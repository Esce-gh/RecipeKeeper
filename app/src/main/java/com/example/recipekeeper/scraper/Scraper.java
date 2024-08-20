package com.example.recipekeeper.scraper;

import android.util.Log;

import com.example.recipekeeper.BuildConfig;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Scraper {
    private Document doc;
    private ArrayList<IngredientsGroup> ingredientsGroups = new ArrayList<>();
    private String name = "";
    private String instructions = "";
    private String notes = "";
    private final String url;

    public Scraper(String link) throws Exception {
        this.url = link;
        try {
            doc = Jsoup.connect(url).get();
        } catch (Exception e) {
            throw new FailedToConnectException();
        }

        try {
            wprmScraper(doc);
        } catch (WebsiteNotSupportedException e) {
            if (!getFromApi()) {
                throw e;
            }
        }
    }

    private void wprmScraper(Document doc) throws WebsiteNotSupportedException {
        Elements ingredientsContainer = doc.getElementsByClass("wprm-recipe-ingredient-group");
        if (ingredientsContainer.isEmpty()) {
            throw new WebsiteNotSupportedException();
        }

        for (Element e : ingredientsContainer) {
            Elements ingredients = e.getElementsByClass("wprm-recipe-ingredients").get(0).children();
            String groupName = Jsoup.parse(e.getElementsByClass("wprm-recipe-group-name").html()).text();
            IngredientsGroup group = new IngredientsGroup(groupName);
            for (Element i : ingredients) {
                group.addIngredient(extractIngredient(i));
            }
            ingredientsGroups.add(group);
        }

        Elements instructionsContainer = doc.getElementsByClass("wprm-recipe-instruction-text");
        for (int i = 0; i < instructionsContainer.size(); i++) {
            instructions += String.format("%d.", i + 1) + Jsoup.parse((instructionsContainer.get(i).html())).text() + "\n\n";
        }

        Elements notesElements = doc.getElementsByClass("wprm-recipe-notes");
        for (Element e : notesElements) {
            notes += Jsoup.parse(e.html()).text() + "\n\n";
        }

        Elements name = doc.getElementsByClass("wprm-recipe-name");
        if (!name.isEmpty()) {
            this.name = Jsoup.parse(name.get(0).html()).text();
        }
    }

    private String extractIngredient(Element i) {
        String ingredient = "";

        for (Element e : i.children()) {
            String className = e.className();
            if (className.contains("name")) {
                ingredient += Jsoup.parse(e.html()).text() + " ";
            } else if (className.contains("unit")) {
                ingredient += Jsoup.parse(e.html()).text() + " ";
            } else if (className.contains("amount")) {
                ingredient += Jsoup.parse(e.html()).text() + " ";
            } else if (className.contains("notes")) {
                ingredient += Jsoup.parse(e.html()).text() + " ";
            }
        }
        return ingredient.trim();
    }

    // This method requests data about recipes from an api that i made for my own use,
    // which utilizes recipe-scrapers repo from github.
    // You have to specify API_URL in local.properties to even use this,
    // tho it's most likely not going to work without changing the code of this method.
    private boolean getFromApi() {
        String api_url = BuildConfig.API_URL;
        if (api_url == null) {
            return false;
        }

        try {
            URL url = new URL(api_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            String request = String.format("{\"url\": \"%s\"}", this.url);
            os.write(request.getBytes(StandardCharsets.UTF_8));
            os.close();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                connection.disconnect();
                return false;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                responseBuilder.append(line);
            }
            parseJson(responseBuilder.toString());
            br.close();
            connection.disconnect();
        } catch (Exception e) {
            Log.e("Failed to retrieve data from api", e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void parseJson(String jsonResponse) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        this.name = jsonObject.get("title").getAsString();
        JsonArray ingredientGroups = jsonObject.getAsJsonArray("ingredient_groups");
        JsonArray instructions = jsonObject.getAsJsonArray("instructions");

        for (int i = 0; i < ingredientGroups.size(); i++) {
            JsonObject group = ingredientGroups.get(i).getAsJsonObject();
            String purpose = !group.get("purpose").isJsonNull() ? group.get("purpose").getAsString() : "";
            JsonArray ingredients = group.getAsJsonArray("ingredients");


            IngredientsGroup newGroup = new IngredientsGroup(purpose);
            for (int j = 0; j < ingredients.size(); j++) {
                newGroup.addIngredient(ingredients.get(j).getAsString());
            }
            ingredientsGroups.add(newGroup);
        }

        StringBuilder newInstructions = new StringBuilder();
        for (int i = 0; i < instructions.size(); i++) {
            String step = instructions.get(i).getAsString();
            newInstructions.append(String.format("%d. %s\n\n", i + 1, step));
        }
        this.instructions = newInstructions.toString();
    }

    public ArrayList<IngredientsGroup> getIngredientsGroups() {
        return ingredientsGroups;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getNotes() {
        return notes;
    }
}
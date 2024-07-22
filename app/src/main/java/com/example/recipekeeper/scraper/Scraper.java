package com.example.recipekeeper.scraper;

import android.app.Activity;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Scraper {
    private Document doc;
    private ArrayList<IngredientsGroup> ingredientsGroups = new ArrayList<>();
    private ArrayList<String> ingredientsList = new ArrayList<>();
    private String name = "";
    private String instructions = "";
    private String notes = "";
    private final String url;

    public Scraper(String link) throws Exception {
        this.url = link;
        if (!url.isEmpty()) {
            doc = Jsoup.connect(url).get();
        }

        if (doc != null) {
            wprmScraper(doc);
        }
    }

    private void wprmScraper(Document doc) {
        Elements ingredientsContainer = doc.getElementsByClass("wprm-recipe-ingredient-group");
        if (ingredientsContainer.isEmpty()) {
            // website doesn't use wprm
            return;
        }

        for (Element e : ingredientsContainer) {
            Elements ingredients = new Elements();
            try {
                ingredients = e.getElementsByClass("wprm-recipe-ingredients").get(0).children();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            String groupName = Jsoup.parse(e.getElementsByClass("wprm-recipe-group-name").html()).text();
            IngredientsGroup group = new IngredientsGroup(groupName);
            for (Element i : ingredients) {
                group.addIngredient(extractIngredient(i));
                ingredientsList.add(extractIngredient(i));
            }
            ingredientsGroups.add(group);
        }

        Elements instructionsContainer = doc.getElementsByClass("wprm-recipe-instruction-text");
        for (int i = 0; i < instructionsContainer.size(); i++) {
            instructions += String.format("%d.", i + 1) + Jsoup.parse((instructionsContainer.get(i).html())).text() + "\n";
        }

        Elements notesElements = doc.getElementsByClass("wprm-recipe-notes");
        for (Element e : notesElements) {
            notes += Jsoup.parse(e.html()).text();
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

    public ArrayList<IngredientsGroup> getIngredientsGroups() {
        return ingredientsGroups;
    }

    public ArrayList<String> getIngredientsList() {
        return ingredientsList;
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

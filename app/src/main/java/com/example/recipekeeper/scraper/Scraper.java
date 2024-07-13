package com.example.recipekeeper.scraper;

import android.app.Activity;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Scraper {
    private Document doc;
    private ArrayList<IngredientsGroup> ingredientsGroups = new ArrayList<>();
    private ArrayList<Ingredient> ingredientsList = new ArrayList<>();
    private String name = "";
    private final String url;

    public Scraper(String link, Activity activity) {
        this.url = link;
        if (!url.isEmpty()){
            try {
                doc = Jsoup.connect(url).get();
            } catch (Exception e) {
                e.printStackTrace();
                activity.runOnUiThread(() -> {
                            Toast.makeText(activity, "Failed to connect", Toast.LENGTH_SHORT).show();
                        }
                );
            }
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
            }
            catch (Exception ex) {
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

        Elements name = doc.getElementsByClass("wprm-recipe-name");
        if (!name.isEmpty()) {
            this.name = Jsoup.parse(name.get(0).html()).text();
        }
    }

    private Ingredient extractIngredient(Element i) {
        String name = "", amount = "", unit = "";

        for (Element e : i.children()) {
            String className = e.className();
            if (className.contains("name")) {
                name = Jsoup.parse(e.html()).text();
            } else if (className.contains("unit")) {
                unit = Jsoup.parse(e.html()).text();
            } else if (className.contains("amount")) {
                amount = Jsoup.parse(e.html()).text();
            } else if (className.contains("notes")) {
                name += Jsoup.parse(e.html()).text();
            }
        }
        return new Ingredient(amount, unit, name);
    }

    public ArrayList<IngredientsGroup> getIngredientsGroups() {
        return ingredientsGroups;
    }

    public ArrayList<Ingredient> getIngredientsList() {
        return ingredientsList;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}

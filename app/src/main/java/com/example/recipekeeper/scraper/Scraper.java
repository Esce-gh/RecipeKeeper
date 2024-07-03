package com.example.recipekeeper.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Scraper {
    private Document doc;
    private ArrayList<IngredientsGroup> ingredientsGroups = new ArrayList<>();
    private ArrayList<Ingredient> ingredientsList = new ArrayList<>();

    public Scraper(String url) {
        try {
            doc = Jsoup.connect(url).get();
        } catch (Exception e) {
            e.printStackTrace();
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
            IngredientsGroup group = new IngredientsGroup(e.child(0).html());
            Elements ingredients = e.child(1).children();
            for (Element i : ingredients) {
                group.addIngredient(extractIngredient(i));
                ingredientsList.add(extractIngredient(i));
            }
            ingredientsGroups.add(group);
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
}

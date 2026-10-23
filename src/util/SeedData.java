package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import model.Recipe;

public class SeedData {

    public static List<Recipe> getInitialRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        Recipe r1 = new Recipe("Lemon Ricotta Pancakes", "Breakfast", "30 mins", "",
                Arrays.asList("2 cups Ricotta", "2 Lemons (zested)", "1.5 cups Flour", "2 Eggs"),
                Arrays.asList("Whisk wet ingredients in a bowl.", "Fold in flour gently until combined.", "Cook on griddle until golden brown."));
        r1.setFavorite(true);
        r1.incrementViews();
        r1.incrementViews();

        Recipe r2 = new Recipe("Slow-Braised Short Ribs", "Dinner", "3 hours", "",
                Arrays.asList("2 kg Short ribs", "1 Onion (diced)", "800g Canned tomatoes", "1 cup Wine"),
                Arrays.asList("Sear short ribs thoroughly until browned.", "Sauté vegetables.", "Simmer covered for 3 hours until tender."));
        r2.setFavorite(true);
        r2.incrementViews();

        Recipe r3 = new Recipe("Miso-Glazed Roasted Aubergine", "Dinner", "25 mins", "",
                Arrays.asList("2 Aubergines", "2 tbsp White miso paste", "1 tbsp Mirin", "1 tbsp Soy sauce"),
                Arrays.asList("Score aubergine flesh cross-hatch pattern.", "Whisk miso, mirin, and soy sauce.", "Broil for 15-20 mins until caramelized."));

        Recipe r4 = new Recipe("Classic French Onion Soup", "Soups & Stews", "1 hour", "",
                Arrays.asList("1 kg Yellow onions", "4 tbsp Butter", "1 L Beef stock", "200g Gruyère cheese"),
                Arrays.asList("Caramelize onions slowly for 45 minutes.", "Add beef stock and simmer.", "Top with toasted bread and Gruyère, then broil."));

        Recipe r5 = new Recipe("Olive Oil Almond Cake", "Dessert", "55 mins", "",
                Arrays.asList("1 cup Extra virgin olive oil", "1.5 cups Almond flour", "1 cup Sugar", "3 Eggs"),
                Arrays.asList("Beat eggs and sugar until pale and fluffy.", "Stream in olive oil while whisking.", "Fold in almond flour and bake at 175°C for 45 mins."));

        Recipe r6 = new Recipe("Green Goddess Salad", "Salads", "15 mins", "",
                Arrays.asList("1 head Green cabbage", "3 Cucumbers", "1 cup Fresh basil", "1/2 cup Cashews"),
                Arrays.asList("Finely chop cabbage and cucumbers.", "Blend basil, spinach, cashews, and vinegar into a creamy dressing.", "Toss chopped veggies with dressing."));

        recipes.add(r1);
        recipes.add(r2);
        recipes.add(r3);
        recipes.add(r4);
        recipes.add(r5);
        recipes.add(r6);

        return recipes;
    }
}

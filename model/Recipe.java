package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Recipe {
    private String id;
    private String name;
    private String category;
    private String prepTime;
    private String imageUrl;
    private List<String> ingredients;
    private List<String> instructions;
    private int baseServings = 2;
    private int viewCount = 0;
    private boolean favorite = false;

    public Recipe(String name, String category, String prepTime, String imageUrl, List<String> ingredients, List<String> instructions) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.category = category;
        this.prepTime = prepTime;
        this.imageUrl = imageUrl;
        this.ingredients = new ArrayList<>(ingredients);
        this.instructions = new ArrayList<>(instructions);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getPrepTime() { return prepTime; }
    public String getImageUrl() { return imageUrl; }
    public List<String> getIngredients() { return ingredients; }
    public List<String> getInstructions() { return instructions; }
    public int getBaseServings() { return baseServings; }
    public int getViewCount() { return viewCount; }
    public void incrementViews() { this.viewCount++; }
    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}
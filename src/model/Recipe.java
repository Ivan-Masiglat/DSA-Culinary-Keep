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
    private int baseServings = 1;
    private int viewCount = 0;
    private boolean favorite = false;

    public Recipe(String name, String category, String prepTime, String imageUrl, List<String> ingredients, List<String> instructions) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.category = category;
        this.prepTime = prepTime;
        this.imageUrl = imageUrl;
        this.ingredients = ingredients != null ? new ArrayList<>(ingredients) : new ArrayList<>();
        this.instructions = instructions != null ? new ArrayList<>(instructions) : new ArrayList<>();
    }

    // --- GETTERS & SETTERS ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPrepTime() { return prepTime; }
    public void setPrepTime(String prepTime) { this.prepTime = prepTime; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public List<String> getInstructions() { return instructions; }
    public void setInstructions(List<String> instructions) { this.instructions = instructions; }

    public int getBaseServings() { return baseServings; }
    public void setBaseServings(int baseServings) { this.baseServings = baseServings; }

    public int getViewCount() { return viewCount; }
    public int getViews() { return viewCount; }
    public void setViews(int views) { this.viewCount = views; }
    public void incrementViews() { this.viewCount++; }

    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}

package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import model.Recipe;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:database/culinary.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        String sql = """
            CREATE TABLE IF NOT EXISTS recipes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                recipe_name TEXT NOT NULL,
                category TEXT,
                prep_time TEXT,
                image_url TEXT,
                ingredients TEXT,
                instructions TEXT
            )
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

            addColumnIfMissing(conn, "category", "TEXT");
            addColumnIfMissing(conn, "prep_time", "TEXT");
            addColumnIfMissing(conn, "image_url", "TEXT");

            System.out.println("Recipes table initialized successfully.");

        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }

    private static void addColumnIfMissing(
            Connection conn, String columnName, String columnType)
            throws SQLException {

        boolean columnExists = false;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(recipes)")) {

            while (rs.next()) {
                if (columnName.equalsIgnoreCase(rs.getString("name"))) {
                    columnExists = true;
                    break;
                }
            }
        }

        if (!columnExists) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(
                    "ALTER TABLE recipes ADD COLUMN "
                    + columnName + " " + columnType
                );
            }
        }
    }

    public static boolean saveRecipe(Recipe recipe) {
        String sql = """
            INSERT INTO recipes
            (recipe_name, category, prep_time, image_url, ingredients, instructions)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, recipe.getName());
            pstmt.setString(2, recipe.getCategory());
            pstmt.setString(3, recipe.getPrepTime());
            pstmt.setString(4, recipe.getImageUrl());
            pstmt.setString(5, String.join("\n", recipe.getIngredients()));
            pstmt.setString(6, String.join("\n", recipe.getInstructions()));

            int rowsInserted = pstmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Recipe saved: " + recipe.getName());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Failed to save recipe: " + e.getMessage());
        }

        return false;
    }

    public static List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        String sql = "SELECT * FROM recipes ORDER BY id DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String ingredientsText = rs.getString("ingredients");
                String instructionsText = rs.getString("instructions");

                List<String> ingredients = ingredientsText == null
                    || ingredientsText.isBlank()
                    ? new ArrayList<>()
                    : new ArrayList<>(Arrays.asList(ingredientsText.split("\n")));

                List<String> instructions = instructionsText == null
                    || instructionsText.isBlank()
                    ? new ArrayList<>()
                    : new ArrayList<>(Arrays.asList(instructionsText.split("\n")));

                Recipe recipe = new Recipe(
                    rs.getString("recipe_name"),
                    rs.getString("category"),
                    rs.getString("prep_time"),
                    rs.getString("image_url"),
                    ingredients,
                    instructions
                );

                recipes.add(recipe);
            }

        } catch (SQLException e) {
            System.err.println("Failed to load recipes: " + e.getMessage());
        }

        System.out.println("Recipes loaded from database: " + recipes.size());
        return recipes;
    }
}


package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.Recipe;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:database/culinary.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {

                stmt.execute(
                    "CREATE TABLE IF NOT EXISTS recipes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "recipe_name TEXT NOT NULL, " +
                    "ingredients TEXT, " +
                    "instructions TEXT)"
                );

                addColumnIfMissing(conn, "category", "TEXT");
                addColumnIfMissing(conn, "prep_time", "TEXT");
                addColumnIfMissing(conn, "image_url", "TEXT");

                System.out.println("Successfully connected to culinary.db!");
                System.out.println("Recipes table initialized successfully.");
            }

        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    private static void addColumnIfMissing(
            Connection conn, String columnName, String columnType)
            throws SQLException {

        boolean exists = false;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(recipes)")) {

            while (rs.next()) {
                if (columnName.equalsIgnoreCase(rs.getString("name"))) {
                    exists = true;
                    break;
                }
            }
        }

        if (!exists) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(
                    "ALTER TABLE recipes ADD COLUMN "
                    + columnName + " " + columnType
                );
            }
        }
    }

    public static boolean saveRecipe(Recipe recipe) {
        initializeDatabase();

        String sql = "INSERT INTO recipes " +
                "(recipe_name, category, prep_time, image_url, ingredients, instructions) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, recipe.getName());
            pstmt.setString(2, recipe.getCategory());
            pstmt.setString(3, recipe.getPrepTime());
            pstmt.setString(4, recipe.getImageUrl());
            pstmt.setString(5, String.join("\n", recipe.getIngredients()));
            pstmt.setString(6, String.join("\n", recipe.getInstructions()));

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Failed to save recipe: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        initializeDatabase();
    }
}

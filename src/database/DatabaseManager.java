package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {


private static final String URL = "jdbc:sqlite:database/culinary.db";

    public static void initializeDatabase() {
        try {
            // Force load the correct driver for version 3.7.2
            Class.forName("org.sqlite.JDBC");
            
            try (Connection conn = DriverManager.getConnection(URL);
                 Statement stmt = conn.createStatement()) {
                
                if (conn != null) {
                    System.out.println("Successfully connected to culinary.db!");
                    
                    String sql = "CREATE TABLE IF NOT EXISTS recipes ("
                               + "  id INTEGER PRIMARY KEY AUTOINCREMENT,"
                               + "  recipe_name TEXT NOT NULL,"
                               + "  ingredients TEXT,"
                               + "  instructions TEXT"
                               + ");";
                    
                    stmt.execute(sql);
                    System.out.println("Recipes table initialized successfully.");
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Driver Class Not Found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        initializeDatabase();
    }
}

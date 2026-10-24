package app;

import database.DatabaseManager;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import model.Recipe;
import util.SeedData;
import view.BackgroundPanel;
import view.SplashScreen;
import view.components.BottomNavBar;
import view.components.RecipeCard;
import view.screens.AddRecipeScreen;
import view.screens.CollectionScreen;
import view.screens.HomeScreen;
import view.screens.MostViewedScreen;
import view.screens.RecipeDetailScreen;

public class CulinaryKeepApp extends JFrame {

    private final List<Recipe> recipes;

    private CardLayout screenCardLayout;
    private JPanel mobileScreenContainer;

    private HomeScreen homeScreen;
    private CollectionScreen collectionScreen;
    private AddRecipeScreen addRecipeScreen;
    private MostViewedScreen mostViewedScreen;

    public CulinaryKeepApp() {
        setTitle("CulinaryKeep");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 840);
        setResizable(false);
        setLocationRelativeTo(null);

        ImageIcon appIcon = new ImageIcon("assets/culinary_keep.jpg");
        if (appIcon.getIconWidth() > 0) {
            setIconImage(appIcon.getImage());
        }

        DatabaseManager.initializeDatabase();

        List<Recipe> savedRecipes = DatabaseManager.getAllRecipes();
        if (savedRecipes.isEmpty()) {
            List<Recipe> initialRecipes = SeedData.getInitialRecipes();
            for (Recipe recipe : initialRecipes) {
                DatabaseManager.saveRecipe(recipe);
            }
            savedRecipes = DatabaseManager.getAllRecipes();
        }

        recipes = new ArrayList<>(savedRecipes);

        BackgroundPanel phoneScreen = new BackgroundPanel();

        screenCardLayout = new CardLayout();
        mobileScreenContainer = new JPanel(screenCardLayout);
        mobileScreenContainer.setOpaque(false);

        RecipeCard.CardActionListener cardListener = new RecipeCard.CardActionListener() {
            @Override
            public void onFavoriteToggle(Recipe recipe) {
                recipe.setFavorite(!recipe.isFavorite());
                DatabaseManager.updateRecipe(recipe);
                if (collectionScreen != null) collectionScreen.refreshGrid();
                if (mostViewedScreen != null) mostViewedScreen.refreshMostViewed();
            }

            @Override
            public void onCardClick(Recipe recipe) {
                recipe.incrementViews();
                DatabaseManager.updateRecipe(recipe);
                if (collectionScreen != null) collectionScreen.refreshGrid();
                if (mostViewedScreen != null) mostViewedScreen.refreshMostViewed();

                RecipeDetailScreen detailScreen = new RecipeDetailScreen(recipe, () ->
                    screenCardLayout.show(mobileScreenContainer, "COLLECTION")
                );

                mobileScreenContainer.add(detailScreen, "DETAIL");
                screenCardLayout.show(mobileScreenContainer, "DETAIL");
            }

            @Override
            public void onDeleteRequested(Recipe recipe) {
                int option = JOptionPane.showConfirmDialog(
                    CulinaryKeepApp.this,
                    "Are you sure you want to delete '" + recipe.getName() + "'?",
                    "Delete Recipe",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );

                if (option == JOptionPane.YES_OPTION) {
                    recipes.remove(recipe);
                    DatabaseManager.deleteRecipe(recipe);
                    if (collectionScreen != null) collectionScreen.refreshGrid();
                    if (mostViewedScreen != null) mostViewedScreen.refreshMostViewed();
                }
            }
        };

        collectionScreen = new CollectionScreen(
            recipes,
            cardListener
        );

        homeScreen = new HomeScreen(
            query -> {
                collectionScreen.setShowFavoritesOnly(false);
                collectionScreen.setSearchQuery(query);
                collectionScreen.refreshGrid();
                screenCardLayout.show(mobileScreenContainer, "COLLECTION");
            }
        );

        addRecipeScreen = new AddRecipeScreen(newRecipe -> {
            recipes.add(0, newRecipe);
            DatabaseManager.saveRecipe(newRecipe);
            collectionScreen.setShowFavoritesOnly(false);
            collectionScreen.setSearchQuery("");
            collectionScreen.refreshGrid();
            if (mostViewedScreen != null) mostViewedScreen.refreshMostViewed();
            screenCardLayout.show(mobileScreenContainer, "COLLECTION");
        });

        mostViewedScreen = new MostViewedScreen(
            recipes,
            cardListener,
            () -> screenCardLayout.show(mobileScreenContainer, "COLLECTION")
        );

        mobileScreenContainer.add(homeScreen, "HOME");
        mobileScreenContainer.add(collectionScreen, "COLLECTION");
        mobileScreenContainer.add(addRecipeScreen, "ADD");
        mobileScreenContainer.add(mostViewedScreen, "MOST_VIEWED");

        screenCardLayout.show(mobileScreenContainer, "HOME");

        BottomNavBar navBar = new BottomNavBar(
            new BottomNavBar.NavListener() {
                @Override
                public void onHomeClicked() {
                    screenCardLayout.show(mobileScreenContainer, "HOME");
                }

                @Override
                public void onFavoritesClicked() {
                    collectionScreen.setShowFavoritesOnly(true);
                    collectionScreen.refreshGrid();
                    screenCardLayout.show(mobileScreenContainer, "COLLECTION");
                }

                @Override
                public void onRecipesClicked() {
                    collectionScreen.setShowFavoritesOnly(false);
                    collectionScreen.setSearchQuery("");
                    collectionScreen.refreshGrid();
                    screenCardLayout.show(mobileScreenContainer, "COLLECTION");
                }

                @Override
                public void onAddClicked() {
                    screenCardLayout.show(mobileScreenContainer, "ADD");
                }

                @Override
                public void onMostViewedClicked() {
                    mostViewedScreen.refreshMostViewed();
                    screenCardLayout.show(mobileScreenContainer, "MOST_VIEWED");
                }
            }
        );

        phoneScreen.add(mobileScreenContainer, BorderLayout.CENTER);
        phoneScreen.add(navBar, BorderLayout.SOUTH);

        add(phoneScreen);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.setVisible(true);

            splash.startSplash(() -> {
                CulinaryKeepApp app = new CulinaryKeepApp();
                app.setVisible(true);
            });
        });
    }
}
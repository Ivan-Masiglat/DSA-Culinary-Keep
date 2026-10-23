
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
import view.screens.RecipeDetailScreen;

public class CulinaryKeepApp extends JFrame {

    private final List<Recipe> recipes;

    private CardLayout screenCardLayout;
    private JPanel mobileScreenContainer;

    private CollectionScreen collectionScreen;
    private AddRecipeScreen addRecipeScreen;

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

        RecipeCard.CardActionListener cardListener =
            new RecipeCard.CardActionListener() {

                @Override
                public void onFavoriteToggle(Recipe recipe) {
                    recipe.setFavorite(!recipe.isFavorite());
                    collectionScreen.refreshGrid();
                }

                @Override
                public void onCardClick(Recipe recipe) {
                    recipe.incrementViews();
                    collectionScreen.refreshGrid();

                    RecipeDetailScreen detailScreen =
                        new RecipeDetailScreen(recipe, () ->
                            screenCardLayout.show(
                                mobileScreenContainer,
                                "COLLECTION"
                            )
                        );

                    mobileScreenContainer.add(detailScreen, "DETAIL");
                    screenCardLayout.show(
                        mobileScreenContainer,
                        "DETAIL"
                    );
                }
            };

        collectionScreen = new CollectionScreen(
            recipes,
            cardListener
        );

        addRecipeScreen = new AddRecipeScreen(newRecipe -> {
            recipes.add(0, newRecipe);

            collectionScreen.setShowFavoritesOnly(false);
            collectionScreen.refreshGrid();

            screenCardLayout.show(
                mobileScreenContainer,
                "COLLECTION"
            );
        });

        mobileScreenContainer.add(
            collectionScreen,
            "COLLECTION"
        );

        mobileScreenContainer.add(
            addRecipeScreen,
            "ADD"
        );

        BottomNavBar navBar = new BottomNavBar(
            new BottomNavBar.NavListener() {

                @Override
                public void onCollectionClicked() {
                    collectionScreen.setShowFavoritesOnly(false);
                    collectionScreen.refreshGrid();

                    screenCardLayout.show(
                        mobileScreenContainer,
                        "COLLECTION"
                    );
                }

                @Override
                public void onAddClicked() {
                    screenCardLayout.show(
                        mobileScreenContainer,
                        "ADD"
                    );
                }

                @Override
                public void onFavoritesClicked() {
                    collectionScreen.setShowFavoritesOnly(true);
                    collectionScreen.refreshGrid();

                    screenCardLayout.show(
                        mobileScreenContainer,
                        "COLLECTION"
                    );
                }
            }
        );

        phoneScreen.add(
            mobileScreenContainer,
            BorderLayout.CENTER
        );

        phoneScreen.add(
            navBar,
            BorderLayout.SOUTH
        );

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

package app;

import model.Recipe;
import util.SeedData;
import view.BackgroundPanel;
import view.components.BottomNavBar;
import view.components.RecipeCard;
import view.screens.AddRecipeScreen;
import view.screens.CollectionScreen;
import view.screens.RecipeDetailScreen;

import javax.swing.*;
import java.awt.*;
import java.util.List;

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

        recipes = SeedData.getInitialRecipes();

        BackgroundPanel phoneScreen = new BackgroundPanel();

        screenCardLayout = new CardLayout();
        mobileScreenContainer = new JPanel(screenCardLayout);
        mobileScreenContainer.setOpaque(false);

        RecipeCard.CardActionListener cardListener = new RecipeCard.CardActionListener() {
            @Override
            public void onFavoriteToggle(Recipe recipe) {
                recipe.setFavorite(!recipe.isFavorite());
                collectionScreen.refreshGrid();
            }

            @Override
            public void onCardClick(Recipe recipe) {
                recipe.incrementViews();
                collectionScreen.refreshGrid();

                // Dynamically add Detail Screen & Switch Card View
                RecipeDetailScreen detailScreen = new RecipeDetailScreen(recipe, () -> {
                    screenCardLayout.show(mobileScreenContainer, "COLLECTION");
                });
                mobileScreenContainer.add(detailScreen, "DETAIL");
                screenCardLayout.show(mobileScreenContainer, "DETAIL");
            }
        };

        collectionScreen = new CollectionScreen(recipes, cardListener);
        addRecipeScreen = new AddRecipeScreen(newRecipe -> {
            recipes.add(0, newRecipe);
            collectionScreen.refreshGrid();
            screenCardLayout.show(mobileScreenContainer, "COLLECTION");
        });

        mobileScreenContainer.add(collectionScreen, "COLLECTION");
        mobileScreenContainer.add(addRecipeScreen, "ADD");

        BottomNavBar navBar = new BottomNavBar(new BottomNavBar.NavListener() {
            @Override
            public void onCollectionClicked() {
                collectionScreen.setShowFavoritesOnly(false);
                screenCardLayout.show(mobileScreenContainer, "COLLECTION");
            }

            @Override
            public void onAddClicked() {
                screenCardLayout.show(mobileScreenContainer, "ADD");
            }

            @Override
            public void onFavoritesClicked() {
                collectionScreen.setShowFavoritesOnly(true);
                screenCardLayout.show(mobileScreenContainer, "COLLECTION");
            }
        });

        phoneScreen.add(mobileScreenContainer, BorderLayout.CENTER);
        phoneScreen.add(navBar, BorderLayout.SOUTH);

        add(phoneScreen);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CulinaryKeepApp().setVisible(true));
    }
}
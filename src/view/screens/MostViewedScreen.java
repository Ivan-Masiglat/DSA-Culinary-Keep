package view.screens;

import config.AppTheme;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Recipe;
import view.components.RecipeCard;

public class MostViewedScreen extends JPanel {

    private final List<Recipe> recipes;
    private final RecipeCard.CardActionListener cardActionListener;
    private final Runnable onBackClicked;

    private JPanel recipeFeedContainer;

    public MostViewedScreen(List<Recipe> recipes, RecipeCard.CardActionListener listener, Runnable onBackClicked) {
        this.recipes = recipes;
        this.cardActionListener = listener;
        this.onBackClicked = onBackClicked;

        setLayout(new BorderLayout());
        setOpaque(false);

        // Header Section
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        // Back Button & Title Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(12, 14, 0, 14));

        JButton backButton = new JButton("← Back");
        backButton.setFont(AppTheme.FONT_BODY_SMALL);
        backButton.setFocusPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setBorder(null);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            if (this.onBackClicked != null) {
                this.onBackClicked.run();
            }
        });

        JLabel titleLabel = new JLabel("🔥 Most Viewed", SwingConstants.CENTER);
        titleLabel.setFont(AppTheme.FONT_TITLE);
        titleLabel.setForeground(AppTheme.COLOR_TEXT_DARK);

        topBar.add(backButton, BorderLayout.WEST);
        topBar.add(titleLabel, BorderLayout.CENTER);
        
        // Empty placeholder on the right to balance the header layout
        JLabel spacer = new JLabel("       ");
        topBar.add(spacer, BorderLayout.EAST);

        headerPanel.add(topBar);

        // Subtitle
        JLabel subTitle = new JLabel("Your most frequently visited recipes");
        subTitle.setFont(AppTheme.FONT_BODY_SMALL);
        subTitle.setForeground(Color.GRAY);
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(Box.createVerticalStrut(6));
        headerPanel.add(subTitle);
        headerPanel.add(Box.createVerticalStrut(14));

        add(headerPanel, BorderLayout.NORTH);

        // Feed Container
        recipeFeedContainer = new JPanel();
        recipeFeedContainer.setLayout(new BoxLayout(recipeFeedContainer, BoxLayout.Y_AXIS));
        recipeFeedContainer.setOpaque(false);

        JScrollPane scroller = new JScrollPane(recipeFeedContainer);
        scroller.setBorder(new EmptyBorder(0, 14, 10, 14));
        scroller.setOpaque(false);
        scroller.getViewport().setOpaque(false);
        scroller.getVerticalScrollBar().setUnitIncrement(12);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scroller, BorderLayout.CENTER);

        refreshMostViewed();
    }

    public void refreshMostViewed() {
        recipeFeedContainer.removeAll();

        // Sort all recipes by view count descending
        List<Recipe> sortedRecipes = recipes.stream()
                .sorted((a, b) -> Integer.compare(b.getViewCount(), a.getViewCount()))
                .collect(Collectors.toList());

        if (sortedRecipes.isEmpty()) {
            JLabel emptyLabel = new JLabel("No recipes available yet.");
            emptyLabel.setFont(AppTheme.FONT_BODY);
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            recipeFeedContainer.add(Box.createVerticalStrut(40));
            recipeFeedContainer.add(emptyLabel);
        } else {
            int rank = 1;
            for (Recipe recipe : sortedRecipes) {
                JPanel cardWrapper = new JPanel(new BorderLayout());
                cardWrapper.setOpaque(false);

                // Rank Badge (#1, #2, #3, etc.)
                JLabel rankBadge = new JLabel(" #" + rank + " ", SwingConstants.CENTER);
                rankBadge.setFont(AppTheme.FONT_BODY_SMALL);
                rankBadge.setOpaque(true);
                
                if (rank == 1) {
                    rankBadge.setBackground(new Color(255, 215, 0)); // Gold
                    rankBadge.setForeground(Color.BLACK);
                } else if (rank == 2) {
                    rankBadge.setBackground(new Color(192, 192, 192)); // Silver
                    rankBadge.setForeground(Color.BLACK);
                } else if (rank == 3) {
                    rankBadge.setBackground(new Color(205, 127, 50)); // Bronze
                    rankBadge.setForeground(Color.WHITE);
                } else {
                    rankBadge.setBackground(new Color(230, 230, 230));
                    rankBadge.setForeground(AppTheme.COLOR_TEXT_DARK);
                }

                RecipeCard card = new RecipeCard(recipe, cardActionListener);

                cardWrapper.add(rankBadge, BorderLayout.NORTH);
                cardWrapper.add(card, BorderLayout.CENTER);

                recipeFeedContainer.add(cardWrapper);
                recipeFeedContainer.add(Box.createVerticalStrut(12));
                rank++;
            }
        }

        recipeFeedContainer.revalidate();
        recipeFeedContainer.repaint();
    }
}
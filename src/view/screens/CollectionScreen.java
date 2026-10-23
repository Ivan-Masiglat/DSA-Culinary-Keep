package view.screens;

import config.AppTheme;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Recipe;
import util.DynamicRowHelper;
import view.components.RecipeCard;

public class CollectionScreen extends JPanel {

    private final List<Recipe> recipes;
    private final RecipeCard.CardActionListener cardActionListener;
    
    private String currentCategoryFilter = "All";
    private boolean showFavoritesOnly = false;
    private String currentSearchQuery = "";

    private JPanel collectionCardFeed;

    public CollectionScreen(List<Recipe> recipes, RecipeCard.CardActionListener listener) {
        this.recipes = recipes;
        this.cardActionListener = listener;

        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel topBox = new JPanel();
        topBox.setLayout(new BoxLayout(topBox, BoxLayout.Y_AXIS));
        topBox.setOpaque(false);

        JLabel logoLabel = createLogoLabel();
        topBox.add(Box.createVerticalStrut(20));
        topBox.add(logoLabel);
        topBox.add(Box.createVerticalStrut(20));

        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.setOpaque(false);
        controlsPanel.setBorder(new EmptyBorder(0, 14, 8, 14));

        JTextField searchBar = DynamicRowHelper.createStyledTextField("🔍 Search...", AppTheme.COLOR_BORDER);
        searchBar.setMaximumSize(new Dimension(380, 40));
        searchBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                currentSearchQuery = searchBar.getText().replace("🔍 Search...", "").trim().toLowerCase();
                refreshGrid();
            }
        });

        JScrollPane pillsScroll = createPillsPanel();

        controlsPanel.add(searchBar);
        controlsPanel.add(Box.createVerticalStrut(10));
        controlsPanel.add(pillsScroll);

        topBox.add(controlsPanel);
        add(topBox, BorderLayout.NORTH);

        collectionCardFeed = new JPanel();
        collectionCardFeed.setLayout(new BoxLayout(collectionCardFeed, BoxLayout.Y_AXIS));
        collectionCardFeed.setOpaque(false);

        JScrollPane feedScroller = new JScrollPane(collectionCardFeed);
        feedScroller.setBorder(new EmptyBorder(10, 14, 0, 14));
        feedScroller.setOpaque(false);
        feedScroller.getViewport().setOpaque(false);
        feedScroller.getVerticalScrollBar().setUnitIncrement(12);
        feedScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        feedScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(feedScroller, BorderLayout.CENTER);
        refreshGrid();
    }

    public void setShowFavoritesOnly(boolean showFavoritesOnly) {
        this.showFavoritesOnly = showFavoritesOnly;
        refreshGrid();
    }

    public void refreshGrid() {
        collectionCardFeed.removeAll();

        List<Recipe> filtered = recipes.stream()
                .filter(r -> !showFavoritesOnly || r.isFavorite())
                .filter(r -> currentCategoryFilter.equals("All") || r.getCategory().equalsIgnoreCase(currentCategoryFilter))
                .filter(r -> currentSearchQuery.isEmpty() || r.getName().toLowerCase().contains(currentSearchQuery))
                .sorted((a, b) -> Integer.compare(b.getViewCount(), a.getViewCount()))
                .collect(Collectors.toList());

        for (Recipe recipe : filtered) {
            collectionCardFeed.add(new RecipeCard(recipe, cardActionListener));
            collectionCardFeed.add(Box.createVerticalStrut(10));
        }

        collectionCardFeed.revalidate();
        collectionCardFeed.repaint();
    }

    private JLabel createLogoLabel() {
        JLabel logoLabel = new JLabel();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        File logoFile = new File("assets/logohome.png");
        ImageIcon rawLogo = null;
        if (logoFile.exists()) {
            rawLogo = new ImageIcon("assets/logohome.png");
        } else {
            java.net.URL logoUrl = getClass().getResource("/assets/logohome.png");
            if (logoUrl != null) rawLogo = new ImageIcon(logoUrl);
        }

        if (rawLogo != null && rawLogo.getIconWidth() > 0) {
            int targetW = 220; 
            int targetH = (int) ((double) rawLogo.getIconHeight() / rawLogo.getIconWidth() * targetW);
            Image scaledLogo = rawLogo.getImage().getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledLogo));
        } else {
            logoLabel.setText("<html><center><font color='#D46430'><b>CULINARY KEEP</b></font><br><font size='5'><b>Your Recipes</b></font></center></html>");
        }
        return logoLabel;
    }

    private JScrollPane createPillsPanel() {
        JPanel pillsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pillsPanel.setOpaque(false);

        String[] tags = {"All", "Breakfast", "Dinner", "Dessert", "Salads", "Soups & Stews"};
        for (String tag : tags) {
            JButton pill = new JButton(tag);
            pill.setFont(AppTheme.FONT_BODY_SMALL);
            pill.setFocusPainted(false);
            pill.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            stylePillButton(pill, tag.equals(currentCategoryFilter));

            pill.addActionListener(e -> {
                showFavoritesOnly = false;
                currentCategoryFilter = tag;
                for (Component c : pillsPanel.getComponents()) {
                    if (c instanceof JButton) {
                        JButton b = (JButton) c;
                        stylePillButton(b, b.getText().equals(currentCategoryFilter));
                    }
                }
                refreshGrid();
            });
            pillsPanel.add(pill);
        }

        JScrollPane pillsScroll = new JScrollPane(pillsPanel);
        pillsScroll.setBorder(null);
        pillsScroll.setOpaque(false);
        pillsScroll.getViewport().setOpaque(false);
        pillsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pillsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        pillsScroll.setPreferredSize(new Dimension(380, 40));
        pillsScroll.getHorizontalScrollBar().setUnitIncrement(10);
        return pillsScroll;
    }

    private void stylePillButton(JButton pill, boolean isActive) {
        if (isActive) {
            pill.setBackground(AppTheme.COLOR_PRIMARY);
            pill.setForeground(Color.WHITE);
        } else {
            pill.setBackground(new Color(255, 255, 255, 220));
            pill.setForeground(AppTheme.COLOR_TEXT_DARK);
        }
    }
}

package view.screens;

import config.AppTheme;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Recipe;
import view.components.RecipeCard;

public class CollectionScreen extends JPanel {

    private final List<Recipe> recipes;
    private final RecipeCard.CardActionListener cardListener;

    private final JPanel gridPanel;
    private JLabel titleLabel;
    private JTextField searchField;
    private JPanel categoriesPanel;

    private boolean showFavoritesOnly = false;
    private String searchQuery = "";
    private String selectedCategory = "All";

    private final String[] categories = {"All", "Breakfast", "Lunch", "Dinner", "Dessert"};
    private final List<JButton> categoryButtons = new ArrayList<>();

    public CollectionScreen(List<Recipe> recipes, RecipeCard.CardActionListener cardListener) {
        this.recipes = recipes;
        this.cardListener = cardListener;

        setLayout(new BorderLayout());
        setOpaque(false);

        // --- TOP HEADER: Logo, Search Bar & Category Pills ---
        JPanel topHeader = new JPanel();
        topHeader.setLayout(new BoxLayout(topHeader, BoxLayout.Y_AXIS));
        topHeader.setOpaque(false);
        topHeader.setBorder(new EmptyBorder(8, 12, 4, 12));

        // 1. Center Logo / Header Banner (assets/logohome.png)
        JPanel logoPanel = new JPanel() {
            private Image logoImg;

            {
                try {
                    File imgFile = new File("assets/logohome.png");
                    if (imgFile.exists()) {
                        logoImg = new ImageIcon(imgFile.getAbsolutePath()).getImage();
                    } else {
                        java.net.URL url = getClass().getResource("/assets/logohome.png");
                        if (url != null) {
                            logoImg = new ImageIcon(url).getImage();
                        }
                    }
                } catch (Exception ignored) {}
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (logoImg != null && logoImg.getWidth(null) > 0) {
                    int imgWidth = Math.min(getWidth() - 20, 220);
                    int imgHeight = (int) ((double) logoImg.getHeight(null) / logoImg.getWidth(null) * imgWidth);
                    int x = (getWidth() - imgWidth) / 2;
                    int y = (getHeight() - imgHeight) / 2;
                    g2.drawImage(logoImg, x, y, imgWidth, imgHeight, this);
                } else {
                    // Vector/Text Fallback Header
                    g2.setFont(new Font("Serif", Font.BOLD, 22));
                    g2.setColor(new Color(60, 30, 15));
                    FontMetrics fm = g2.getFontMetrics();
                    String text = showFavoritesOnly ? "Favorites" : "Culinary Keep";
                    int x = (getWidth() - fm.stringWidth(text)) / 2;
                    int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    g2.drawString(text, x, y);
                }
                g2.dispose();
            }
        };
        logoPanel.setOpaque(false);
        logoPanel.setPreferredSize(new Dimension(300, 50));
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Optional subtitle below logo
        titleLabel = new JLabel(showFavoritesOnly ? "Favorites" : "All Recipes", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 14));
        titleLabel.setForeground(new Color(150, 90, 40));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 2. Search Bar
        searchField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(220, 210, 200));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchField.setBorder(new EmptyBorder(6, 10, 6, 10));
        searchField.setOpaque(false);
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        searchField.setPreferredSize(new Dimension(300, 36));
        searchField.setAlignmentX(Component.CENTER_ALIGNMENT);

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchQuery = searchField.getText().trim();
                refreshGrid();
            }
        });

        // 3. Category Filter Buttons Panel
        categoriesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        categoriesPanel.setOpaque(false);
        categoriesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (String cat : categories) {
            JButton btn = createCategoryButton(cat);
            categoryButtons.add(btn);
            categoriesPanel.add(btn);
        }

        topHeader.add(logoPanel);
        topHeader.add(titleLabel);
        topHeader.add(Box.createVerticalStrut(8));
        topHeader.add(searchField);
        topHeader.add(Box.createVerticalStrut(10));
        topHeader.add(categoriesPanel);

        add(topHeader, BorderLayout.NORTH);

        // --- CENTER GRID: 2-Column Recipe Grid ---
        gridPanel = new JPanel(new GridLayout(0, 2, 12, 12));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(new EmptyBorder(8, 12, 12, 12));

        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setOpaque(false);
        topWrapper.add(gridPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(topWrapper);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        updateCategoryButtonStyles();
        refreshGrid();
    }

    private JButton createCategoryButton(String categoryName) {
        JButton btn = new JButton(categoryName) {
            @Override
            protected void paintComponent(Graphics g) {
                boolean isSelected = categoryName.equalsIgnoreCase(selectedCategory);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isSelected) {
                    g2.setColor(new Color(212, 100, 48));
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(isSelected ? new Color(212, 100, 48) : new Color(200, 190, 180));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                g2.setColor(isSelected ? Color.WHITE : new Color(60, 30, 15));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(categoryName)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(categoryName, x, y);

                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(68, 28));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            selectedCategory = categoryName;
            updateCategoryButtonStyles();
            refreshGrid();
        });

        return btn;
    }

    private void updateCategoryButtonStyles() {
        for (JButton b : categoryButtons) {
            b.repaint();
        }
    }

    public void setShowFavoritesOnly(boolean showFavoritesOnly) {
        this.showFavoritesOnly = showFavoritesOnly;
        if (titleLabel != null) {
            titleLabel.setText(showFavoritesOnly ? "Favorites" : "All Recipes");
        }
        repaint();
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query != null ? query.toLowerCase().trim() : "";
        if (searchField != null) {
            searchField.setText(query);
        }
    }

    public void refreshGrid() {
        gridPanel.removeAll();

        List<Recipe> filtered = new ArrayList<>();
        for (Recipe r : recipes) {
            boolean matchesFav = !showFavoritesOnly || r.isFavorite();
            boolean matchesCategory = selectedCategory.equalsIgnoreCase("All") 
                || r.getCategory().equalsIgnoreCase(selectedCategory);

            String name = r.getName() != null ? r.getName().toLowerCase() : "";
            String cat = r.getCategory() != null ? r.getCategory().toLowerCase() : "";
            boolean matchesSearch = searchQuery.isEmpty() || name.contains(searchQuery) || cat.contains(searchQuery);

            if (matchesFav && matchesCategory && matchesSearch) {
                filtered.add(r);
            }
        }

        for (Recipe recipe : filtered) {
            RecipeCard card = new RecipeCard(recipe, cardListener);
            card.setPreferredSize(new Dimension(170, 180));
            gridPanel.add(card);
        }

        gridPanel.revalidate();
        gridPanel.repaint();

        Container parent = getParent();
        if (parent != null) {
            parent.revalidate();
            parent.repaint();
        }
    }
}
import model.Recipe;
import util.DynamicRowHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class UI extends JFrame {

    // COLOR PALETTE
    private static final Color COLOR_BG_TOP = new Color(250, 245, 238);
    private static final Color COLOR_PRIMARY = new Color(212, 100, 48);        // Warm Terracotta
    private static final Color COLOR_TEXT_DARK = new Color(38, 33, 29);
    private static final Color COLOR_TEXT_MUTED = new Color(135, 125, 116);
    private static final Color COLOR_BORDER = new Color(230, 220, 208);
    private static final Color COLOR_CARD_BG = Color.WHITE;

    // STATE
    private final List<Recipe> recipes = new ArrayList<>();
    private String currentCategoryFilter = "All";
    private boolean showFavoritesOnly = false;
    private String currentSearchQuery = "";

    // UI CONTAINERS
    private JPanel mobileScreenContainer;
    private CardLayout screenCardLayout;
    private JPanel collectionCardFeed;

    // FORM INPUTS
    private JTextField nameField;
    private JTextField imageUrlField;
    private JComboBox<String> categoryBox;
    private JSpinner timeSpinner;
    private JComboBox<String> unitBox;
    private final List<JTextField> ingredientFields = new ArrayList<>();
    private final List<JTextField> instructionFields = new ArrayList<>();
    private JPanel ingredientsContainer;
    private JPanel instructionsContainer;

    public UI() {
        setTitle("CulinaryKeep");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setSize(420, 840);
        setResizable(false);
        setLocationRelativeTo(null);

        seedInitialRecipes();

        JPanel phoneScreen = new JPanel(new BorderLayout());
        phoneScreen.setBackground(COLOR_BG_TOP);

        phoneScreen.add(createMobileStatusBar(), BorderLayout.NORTH);

        screenCardLayout = new CardLayout();
        mobileScreenContainer = new JPanel(screenCardLayout);
        mobileScreenContainer.setOpaque(false);

        mobileScreenContainer.add(createMobileCollectionScreen(), "COLLECTION");
        mobileScreenContainer.add(createMobileAddRecipeScreen(), "ADD");

        phoneScreen.add(mobileScreenContainer, BorderLayout.CENTER);
        phoneScreen.add(createMobileBottomNavBar(), BorderLayout.SOUTH);

        add(phoneScreen);
    }

    private JPanel createMobileStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(420, 28));
        bar.setBorder(new EmptyBorder(6, 16, 0, 16));

        JLabel timeLbl = new JLabel("9:41");
        timeLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        timeLbl.setForeground(COLOR_TEXT_DARK);

        JLabel iconsLbl = new JLabel("📶 🔋");
        iconsLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));

        bar.add(timeLbl, BorderLayout.WEST);
        bar.add(iconsLbl, BorderLayout.EAST);

        return bar;
    }

    private JPanel createMobileBottomNavBar() {
        JPanel navBar = new JPanel(new GridLayout(1, 3));
        navBar.setBackground(Color.WHITE);
        navBar.setPreferredSize(new Dimension(420, 56));
        navBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDER));

        JButton btnCollection = createNavButton("📖", "Collection");
        JButton btnAdd = createNavButton("➕", "Add Recipe");
        JButton btnFavs = createNavButton("♥", "Favorites");

        btnCollection.addActionListener(e -> {
            showFavoritesOnly = false;
            refreshMobileGrid();
            screenCardLayout.show(mobileScreenContainer, "COLLECTION");
        });

        btnAdd.addActionListener(e -> screenCardLayout.show(mobileScreenContainer, "ADD"));

        btnFavs.addActionListener(e -> {
            showFavoritesOnly = true;
            refreshMobileGrid();
            screenCardLayout.show(mobileScreenContainer, "COLLECTION");
        });

        navBar.add(btnCollection);
        navBar.add(btnAdd);
        navBar.add(btnFavs);

        return navBar;
    }

    private JButton createNavButton(String icon, String label) {
        JButton btn = new JButton("<html><center>" + icon + "<br><font size='2'>" + label + "</font></center></html>");
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setForeground(COLOR_TEXT_DARK);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(null);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel createMobileCollectionScreen() {
        JPanel screen = new JPanel(new BorderLayout());
        screen.setOpaque(false);
        screen.setBorder(new EmptyBorder(12, 14, 0, 14));

        JPanel topBox = new JPanel();
        topBox.setLayout(new BoxLayout(topBox, BoxLayout.Y_AXIS));
        topBox.setOpaque(false);

        JLabel subHeader = new JLabel("CULINARY KEEP");
        subHeader.setFont(new Font("SansSerif", Font.BOLD, 10));
        subHeader.setForeground(COLOR_PRIMARY);

        JLabel mainHeader = new JLabel("Your Recipes");
        mainHeader.setFont(new Font("Serif", Font.BOLD, 26));

        JTextField searchBar = DynamicRowHelper.createStyledTextField("🔍 Search...", COLOR_BORDER);
        searchBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                currentSearchQuery = searchBar.getText().replace("🔍 Search...", "").trim().toLowerCase();
                refreshMobileGrid();
            }
        });

        JPanel pillsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pillsPanel.setOpaque(false);

        String[] tags = {"All", "Breakfast", "Dinner", "Dessert", "Salads", "Soups & Stews"};
        for (String tag : tags) {
            JButton pill = new JButton(tag);
            pill.setFont(new Font("SansSerif", Font.PLAIN, 11));
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
                refreshMobileGrid();
            });
            pillsPanel.add(pill);
        }

        JScrollPane pillsScroll = new JScrollPane(pillsPanel);
        pillsScroll.setBorder(null);
        pillsScroll.setOpaque(false);
        pillsScroll.getViewport().setOpaque(false);
        pillsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pillsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        pillsScroll.setPreferredSize(new Dimension(380, 50));
        pillsScroll.getHorizontalScrollBar().setUnitIncrement(10);

        topBox.add(subHeader);
        topBox.add(mainHeader);
        topBox.add(Box.createVerticalStrut(10));
        topBox.add(searchBar);
        topBox.add(Box.createVerticalStrut(10));
        topBox.add(pillsScroll);
        topBox.add(Box.createVerticalStrut(10));

        screen.add(topBox, BorderLayout.NORTH);

        collectionCardFeed = new JPanel();
        collectionCardFeed.setLayout(new BoxLayout(collectionCardFeed, BoxLayout.Y_AXIS));
        collectionCardFeed.setOpaque(false);

        JScrollPane feedScroller = new JScrollPane(collectionCardFeed);
        feedScroller.setBorder(null);
        feedScroller.setOpaque(false);
        feedScroller.getViewport().setOpaque(false);
        feedScroller.getVerticalScrollBar().setUnitIncrement(12);

        screen.add(feedScroller, BorderLayout.CENTER);

        refreshMobileGrid();
        return screen;
    }

    private void stylePillButton(JButton pill, boolean isActive) {
        if (isActive) {
            pill.setBackground(COLOR_PRIMARY);
            pill.setForeground(Color.WHITE);
        } else {
            pill.setBackground(new Color(238, 230, 220));
            pill.setForeground(COLOR_TEXT_DARK);
        }
    }

    private void refreshMobileGrid() {
        collectionCardFeed.removeAll();

        List<Recipe> filtered = recipes.stream()
                .filter(r -> !showFavoritesOnly || r.isFavorite())
                .filter(r -> currentCategoryFilter.equals("All") || r.getCategory().equalsIgnoreCase(currentCategoryFilter))
                .filter(r -> currentSearchQuery.isEmpty() || r.getName().toLowerCase().contains(currentSearchQuery))
                .sorted((a, b) -> Integer.compare(b.getViewCount(), a.getViewCount()))
                .collect(Collectors.toList());

        for (Recipe recipe : filtered) {
            collectionCardFeed.add(createMobileCard(recipe));
            collectionCardFeed.add(Box.createVerticalStrut(10));
        }

        collectionCardFeed.revalidate();
        collectionCardFeed.repaint();
    }

    private JPanel createMobileCard(Recipe recipe) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(COLOR_BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 14, 14));
                g2.dispose();
            }
        };

        card.setLayout(new BorderLayout(10, 0));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 12, 12, 12));
        card.setMaximumSize(new Dimension(380, 95));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel catLbl = new JLabel(recipe.getCategory().toUpperCase());
        catLbl.setFont(new Font("SansSerif", Font.BOLD, 9));
        catLbl.setForeground(COLOR_PRIMARY);

        JLabel titleLbl = new JLabel("<html><b>" + recipe.getName() + "</b></html>");
        titleLbl.setFont(new Font("Serif", Font.PLAIN, 15));

        JLabel metaLbl = new JLabel("⏱ " + recipe.getPrepTime() + " • 👁 " + recipe.getViewCount());
        metaLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        metaLbl.setForeground(COLOR_TEXT_MUTED);

        info.add(catLbl);
        info.add(Box.createVerticalStrut(2));
        info.add(titleLbl);
        info.add(Box.createVerticalStrut(4));
        info.add(metaLbl);

        JButton favBtn = new JButton(recipe.isFavorite() ? "♥" : "♡");
        favBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        favBtn.setForeground(recipe.isFavorite() ? new Color(210, 45, 45) : COLOR_TEXT_MUTED);
        favBtn.setFocusPainted(false);
        favBtn.setContentAreaFilled(false);
        favBtn.setBorder(null);
        favBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        favBtn.addActionListener(e -> {
            recipe.setFavorite(!recipe.isFavorite());
            refreshMobileGrid();
        });

        card.add(info, BorderLayout.CENTER);
        card.add(favBtn, BorderLayout.EAST);

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                recipe.incrementViews();
                refreshMobileGrid();
                showMobileDetailModal(recipe);
            }
        });

        return card;
    }

    private JScrollPane createMobileAddRecipeScreen() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 16, 20, 16));

        JLabel header = new JLabel("Add New Recipe");
        header.setFont(new Font("Serif", Font.BOLD, 24));

        nameField = DynamicRowHelper.createStyledTextField("e.g. Saffron Risotto", COLOR_BORDER);
        imageUrlField = DynamicRowHelper.createStyledTextField("e.g. Image URL (Optional)", COLOR_BORDER);

        categoryBox = new JComboBox<>(new String[]{"Breakfast", "Dinner", "Dessert", "Salads", "Soups & Stews"});
        styleComponent(categoryBox);

        timeSpinner = new JSpinner(new SpinnerNumberModel(30, 5, 300, 5));
        unitBox = new JComboBox<>(new String[]{"mins", "hours"});

        JPanel timeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        timeRow.setOpaque(false);
        timeRow.add(timeSpinner);
        timeRow.add(unitBox);

        ingredientsContainer = new JPanel();
        ingredientsContainer.setLayout(new BoxLayout(ingredientsContainer, BoxLayout.Y_AXIS));
        ingredientsContainer.setOpaque(false);
        DynamicRowHelper.addDynamicRow(ingredientsContainer, ingredientFields, "e.g. 2 cups rice", COLOR_BORDER);

        JButton addIngBtn = createSecondaryButton("+ Ingredient");
        addIngBtn.addActionListener(e -> DynamicRowHelper.addDynamicRow(ingredientsContainer, ingredientFields, "e.g. Next item...", COLOR_BORDER));

        instructionsContainer = new JPanel();
        instructionsContainer.setLayout(new BoxLayout(instructionsContainer, BoxLayout.Y_AXIS));
        instructionsContainer.setOpaque(false);
        DynamicRowHelper.addDynamicRow(instructionsContainer, instructionFields, "e.g. Heat butter on pan...", COLOR_BORDER);

        JButton addStepBtn = createSecondaryButton("+ Step");
        addStepBtn.addActionListener(e -> DynamicRowHelper.addDynamicRow(instructionsContainer, instructionFields, "e.g. Next step...", COLOR_BORDER));

        JButton saveBtn = new JButton("Save Recipe");
        saveBtn.setBackground(COLOR_PRIMARY);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        saveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveBtn.addActionListener(e -> {
            handleSaveRecipe();
            screenCardLayout.show(mobileScreenContainer, "COLLECTION");
        });

        JButton clearBtn = createSecondaryButton("Clear");
        clearBtn.addActionListener(e -> clearForm());

        panel.add(header);
        panel.add(Box.createVerticalStrut(12));
        panel.add(createFieldLabel("NAME"));
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldLabel("CATEGORY"));
        panel.add(categoryBox);
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldLabel("PREP TIME"));
        panel.add(timeRow);
        panel.add(Box.createVerticalStrut(12));
        panel.add(createFieldLabel("INGREDIENTS"));
        panel.add(ingredientsContainer);
        panel.add(addIngBtn);
        panel.add(Box.createVerticalStrut(12));
        panel.add(createFieldLabel("INSTRUCTIONS"));
        panel.add(instructionsContainer);
        panel.add(addStepBtn);
        panel.add(Box.createVerticalStrut(16));
        
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(saveBtn);
        buttonRow.add(clearBtn);
        panel.add(buttonRow);

        JScrollPane scroller = new JScrollPane(panel);
        scroller.setBorder(null);
        scroller.setOpaque(false);
        scroller.getViewport().setOpaque(false);
        return scroller;
    }

    private void showMobileDetailModal(Recipe recipe) {
        JDialog dialog = new JDialog(this, recipe.getName(), true);
        dialog.setSize(360, 580);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_BG_TOP);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("<html><h3>" + recipe.getName() + "</h3></html>");
        title.setFont(new Font("Serif", Font.BOLD, 20));

        final int[] servings = {recipe.getBaseServings()};
        JLabel servVal = new JLabel(String.valueOf(servings[0]));
        servVal.setFont(new Font("SansSerif", Font.BOLD, 14));

        JButton minus = createSecondaryButton("-");
        JButton plus = createSecondaryButton("+");

        JPanel servBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        servBar.setOpaque(false);
        servBar.add(new JLabel("Servings:"));
        servBar.add(minus);
        servBar.add(servVal);
        servBar.add(plus);

        JPanel ingBox = new JPanel();
        ingBox.setLayout(new BoxLayout(ingBox, BoxLayout.Y_AXIS));
        ingBox.setOpaque(false);

        Runnable updateIngs = () -> {
            ingBox.removeAll();
            double factor = (double) servings[0] / recipe.getBaseServings();
            for (String ing : recipe.getIngredients()) {
                ingBox.add(new JLabel("• " + DynamicRowHelper.scaleIngredientAmount(ing, factor)));
            }
            ingBox.revalidate();
            ingBox.repaint();
        };

        minus.addActionListener(e -> {
            if (servings[0] > 1) {
                servings[0]--;
                servVal.setText(String.valueOf(servings[0]));
                updateIngs.run();
            }
        });

        plus.addActionListener(e -> {
            servings[0]++;
            servVal.setText(String.valueOf(servings[0]));
            updateIngs.run();
        });

        updateIngs.run();

        panel.add(title);
        panel.add(servBar);
        panel.add(Box.createVerticalStrut(12));
        panel.add(createFieldLabel("INGREDIENTS"));
        panel.add(ingBox);
        panel.add(Box.createVerticalStrut(12));
        panel.add(createFieldLabel("STEPS"));

        int num = 1;
        for (String step : recipe.getInstructions()) {
            panel.add(new JLabel("<html><b>" + (num++) + ".</b> " + step + "</html>"));
            panel.add(Box.createVerticalStrut(4));
        }

        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }

    private void seedInitialRecipes() {
        Recipe r1 = new Recipe("Lemon Ricotta Pancakes", "Breakfast", "30 mins", "",
                Arrays.asList("2 cups Ricotta", "2 Lemons (zested)", "1.5 cups Flour", "2 Eggs"),
                Arrays.asList("Whisk wet ingredients in a bowl.", "Fold in flour gently until combined.", "Cook on griddle until golden brown."));
        r1.setFavorite(true);
        r1.incrementViews();
        r1.incrementViews();

        Recipe r2 = new Recipe("Slow-Braised Short Ribs", "Dinner", "3 hours", "",
                Arrays.asList("2 kg Short ribs", "1 Onion (diced)", "800g Canned tomatoes", "1 cup Wine"),
                Arrays.asList("Sear short ribs thoroughly until browned.", "Sauté vegetables.", "Simmer covered for 3 hours until tender."));
        r2.setFavorite(true);
        r2.incrementViews();

        Recipe r3 = new Recipe("Miso-Glazed Roasted Aubergine", "Dinner", "25 mins", "",
                Arrays.asList("2 Aubergines", "2 tbsp White miso paste", "1 tbsp Mirin", "1 tbsp Soy sauce"),
                Arrays.asList("Score aubergine flesh cross-hatch pattern.", "Whisk miso, mirin, and soy sauce.", "Broil for 15-20 mins until caramelized."));

        Recipe r4 = new Recipe("Classic French Onion Soup", "Soups & Stews", "1 hour", "",
                Arrays.asList("1 kg Yellow onions", "4 tbsp Butter", "1 L Beef stock", "200g Gruyère cheese"),
                Arrays.asList("Caramelize onions slowly for 45 minutes.", "Add beef stock and simmer.", "Top with toasted bread and Gruyère, then broil."));

        Recipe r5 = new Recipe("Olive Oil Almond Cake", "Dessert", "55 mins", "",
                Arrays.asList("1 cup Extra virgin olive oil", "1.5 cups Almond flour", "1 cup Sugar", "3 Eggs"),
                Arrays.asList("Beat eggs and sugar until pale and fluffy.", "Stream in olive oil while whisking.", "Fold in almond flour and bake at 175°C for 45 mins."));

        Recipe r6 = new Recipe("Green Goddess Salad", "Salads", "15 mins", "",
                Arrays.asList("1 head Green cabbage", "3 Cucumbers", "1 cup Fresh basil", "1/2 cup Cashews"),
                Arrays.asList("Finely chop cabbage and cucumbers.", "Blend basil, spinach, cashews, and vinegar into a creamy dressing.", "Toss chopped veggies with dressing."));

        recipes.add(r1);
        recipes.add(r2);
        recipes.add(r3);
        recipes.add(r4);
        recipes.add(r5);
        recipes.add(r6);
    }

    private void handleSaveRecipe() {
        String name = nameField.getText().trim();
        if (name.isEmpty() || name.startsWith("e.g.")) return;
        List<String> ings = ingredientFields.stream().map(JTextField::getText).map(String::trim).filter(t -> !t.isEmpty() && !t.startsWith("e.g.")).collect(Collectors.toList());
        List<String> steps = instructionFields.stream().map(JTextField::getText).map(String::trim).filter(t -> !t.isEmpty() && !t.startsWith("e.g.")).collect(Collectors.toList());
        recipes.add(0, new Recipe(name, (String) categoryBox.getSelectedItem(), timeSpinner.getValue() + " " + unitBox.getSelectedItem(), "", ings, steps));
        refreshMobileGrid();
        clearForm();
    }

    private void clearForm() {
        nameField.setText("");
        if (imageUrlField != null) imageUrlField.setText("");
        categoryBox.setSelectedIndex(0);
        timeSpinner.setValue(30);

        ingredientsContainer.removeAll();
        ingredientFields.clear();
        DynamicRowHelper.addDynamicRow(ingredientsContainer, ingredientFields, "e.g. 2 cups rice", COLOR_BORDER);

        instructionsContainer.removeAll();
        instructionFields.clear();
        DynamicRowHelper.addDynamicRow(instructionsContainer, instructionFields, "e.g. Heat butter on pan...", COLOR_BORDER);

        ingredientsContainer.revalidate();
        instructionsContainer.revalidate();
    }

    private JLabel createFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 10));
        l.setForeground(COLOR_PRIMARY);
        return l;
    }

    private void styleComponent(JComponent comp) {
        comp.setFont(new Font("SansSerif", Font.PLAIN, 12));
        comp.setBackground(Color.WHITE);
        comp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btn.setFocusPainted(false);
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UI().setVisible(true));
    }
}
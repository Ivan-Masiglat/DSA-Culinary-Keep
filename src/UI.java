import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Recipe;
import util.DynamicRowHelper;

public class UI extends JFrame {

    // COLOR PALETTE
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

        // ROOT PANEL: Paints recipebg.png at the VERY BACK across the entire screen
        JPanel phoneScreen = new JPanel(new BorderLayout()) {
            private Image bgImage;

            {
                File imgFile = new File("assets/recipebg.png");
                if (imgFile.exists()) {
                    bgImage = new ImageIcon("assets/recipebg.png").getImage();
                } else {
                    java.net.URL url = getClass().getResource("/assets/recipebg.png");
                    if (url != null) {
                        bgImage = new ImageIcon(url).getImage();
                    }
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                    g2d.dispose();
                } else {
                    g.setColor(new Color(250, 245, 238));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        screenCardLayout = new CardLayout();
        mobileScreenContainer = new JPanel(screenCardLayout);
        mobileScreenContainer.setOpaque(false);

        mobileScreenContainer.add(createMobileCollectionScreen(), "COLLECTION");
        mobileScreenContainer.add(createMobileAddRecipeScreen(), "ADD");

        phoneScreen.add(mobileScreenContainer, BorderLayout.CENTER);
        phoneScreen.add(createMobileBottomNavBar(), BorderLayout.SOUTH);

        add(phoneScreen);
    }

    private JPanel createMobileBottomNavBar() {
        JPanel navBar = new JPanel(new GridLayout(1, 3)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(255, 255, 255, 240));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(COLOR_BORDER);
                g2.drawLine(0, 0, getWidth(), 0);
                g2.dispose();
            }
        };
        navBar.setOpaque(false);
        navBar.setPreferredSize(new Dimension(420, 56));

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

        JPanel topBox = new JPanel();
        topBox.setLayout(new BoxLayout(topBox, BoxLayout.Y_AXIS));
        topBox.setOpaque(false);

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

        topBox.add(Box.createVerticalStrut(20));
        topBox.add(logoLabel);
        topBox.add(Box.createVerticalStrut(20));

        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.setOpaque(false);
        controlsPanel.setBorder(new EmptyBorder(0, 14, 8, 14));

        JTextField searchBar = DynamicRowHelper.createStyledTextField("🔍 Search...", COLOR_BORDER);
        searchBar.setMaximumSize(new Dimension(380, 40));

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
        pillsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pillsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        pillsScroll.setPreferredSize(new Dimension(380, 40));
        pillsScroll.getHorizontalScrollBar().setUnitIncrement(10);

        controlsPanel.add(searchBar);
        controlsPanel.add(Box.createVerticalStrut(10));
        controlsPanel.add(pillsScroll);

        topBox.add(controlsPanel);
        screen.add(topBox, BorderLayout.NORTH);

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

        screen.add(feedScroller, BorderLayout.CENTER);

        refreshMobileGrid();
        return screen;
    }

    private void stylePillButton(JButton pill, boolean isActive) {
        if (isActive) {
            pill.setBackground(COLOR_PRIMARY);
            pill.setForeground(Color.WHITE);
        } else {
            pill.setBackground(new Color(255, 255, 255, 220));
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
        // Outer wrapper transparent panel
        JPanel outer = new JPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(20, 16, 20, 16));

        // Form Container Card with a solid white rounded background
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(COLOR_BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
            }
        };

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 18, 20, 18));

        JLabel header = new JLabel("Add New Recipe");
        header.setFont(new Font("Serif", Font.BOLD, 22));
        header.setForeground(COLOR_TEXT_DARK);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        nameField = DynamicRowHelper.createStyledTextField("e.g. Saffron Risotto", COLOR_BORDER);
        nameField.setMaximumSize(new Dimension(350, 36));

        imageUrlField = DynamicRowHelper.createStyledTextField("e.g. Image URL (Optional)", COLOR_BORDER);
        imageUrlField.setMaximumSize(new Dimension(350, 36));

        categoryBox = new JComboBox<>(new String[]{"Breakfast", "Dinner", "Dessert", "Salads", "Soups & Stews"});
        styleComponent(categoryBox);
        categoryBox.setMaximumSize(new Dimension(350, 36));

        timeSpinner = new JSpinner(new SpinnerNumberModel(30, 5, 300, 5));
        unitBox = new JComboBox<>(new String[]{"mins", "hours"});

        JPanel timeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        timeRow.setOpaque(false);
        timeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        timeRow.add(timeSpinner);
        timeRow.add(unitBox);

        ingredientsContainer = new JPanel();
        ingredientsContainer.setLayout(new BoxLayout(ingredientsContainer, BoxLayout.Y_AXIS));
        ingredientsContainer.setOpaque(false);
        DynamicRowHelper.addDynamicRow(ingredientsContainer, ingredientFields, "e.g. 2 cups rice", COLOR_BORDER);

        JButton addIngBtn = createSecondaryButton("+ Ingredient");
        addIngBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addIngBtn.addActionListener(e -> DynamicRowHelper.addDynamicRow(ingredientsContainer, ingredientFields, "e.g. Next item...", COLOR_BORDER));

        instructionsContainer = new JPanel();
        instructionsContainer.setLayout(new BoxLayout(instructionsContainer, BoxLayout.Y_AXIS));
        instructionsContainer.setOpaque(false);
        DynamicRowHelper.addDynamicRow(instructionsContainer, instructionFields, "e.g. Heat butter on pan...", COLOR_BORDER);

        JButton addStepBtn = createSecondaryButton("+ Step");
        addStepBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
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

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonRow.setOpaque(false);
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonRow.add(saveBtn);
        buttonRow.add(clearBtn);

        card.add(header);
        card.add(Box.createVerticalStrut(14));
        card.add(createFieldLabel("NAME"));
        card.add(Box.createVerticalStrut(4));
        card.add(nameField);
        card.add(Box.createVerticalStrut(10));
        card.add(createFieldLabel("CATEGORY"));
        card.add(Box.createVerticalStrut(4));
        card.add(categoryBox);
        card.add(Box.createVerticalStrut(10));
        card.add(createFieldLabel("PREP TIME"));
        card.add(Box.createVerticalStrut(4));
        card.add(timeRow);
        card.add(Box.createVerticalStrut(12));
        card.add(createFieldLabel("INGREDIENTS"));
        card.add(Box.createVerticalStrut(4));
        card.add(ingredientsContainer);
        card.add(Box.createVerticalStrut(4));
        card.add(addIngBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(createFieldLabel("INSTRUCTIONS"));
        card.add(Box.createVerticalStrut(4));
        card.add(instructionsContainer);
        card.add(Box.createVerticalStrut(4));
        card.add(addStepBtn);
        card.add(Box.createVerticalStrut(18));
        card.add(buttonRow);

        outer.add(card);

        JScrollPane scroller = new JScrollPane(outer);
        scroller.setBorder(null);
        scroller.setOpaque(false);
        scroller.getViewport().setOpaque(false);
        scroller.getVerticalScrollBar().setUnitIncrement(12);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scroller;
    }

    private void showMobileDetailModal(Recipe recipe) {
        JDialog dialog = new JDialog(this, recipe.getName(), true);
        dialog.setSize(360, 580);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(250, 245, 238));
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
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void styleComponent(JComponent comp) {
        comp.setFont(new Font("SansSerif", Font.PLAIN, 12));
        comp.setBackground(Color.WHITE);
        comp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        comp.setAlignmentX(Component.LEFT_ALIGNMENT);
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
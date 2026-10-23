package view.screens;

import config.AppTheme;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private boolean showMostViewedOnly = false;
    private String currentSearchQuery = "";

    private static final String SEARCH_PLACEHOLDER = "Search recipes...";

    private JPanel collectionCardFeed;
    private JTextField searchBar;

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

        searchBar = DynamicRowHelper.createStyledTextField(SEARCH_PLACEHOLDER, AppTheme.COLOR_BORDER);
        searchBar.setMaximumSize(new Dimension(380, 40));
        searchBar.setText(SEARCH_PLACEHOLDER);
        searchBar.setForeground(Color.GRAY);

        searchBar.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchBar.getText().equals(SEARCH_PLACEHOLDER)) {
                    searchBar.setText("");
                    searchBar.setForeground(AppTheme.COLOR_TEXT_DARK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchBar.getText().trim().isEmpty()) {
                    searchBar.setText(SEARCH_PLACEHOLDER);
                    searchBar.setForeground(Color.GRAY);
                    currentSearchQuery = "";
                    refreshGrid();
                }
            }
        });

        searchBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = searchBar.getText().trim();
                if (text.equals(SEARCH_PLACEHOLDER) || text.isEmpty()) {
                    currentSearchQuery = "";
                } else {
                    currentSearchQuery = text.toLowerCase();
                }
                refreshGrid();
            }
        });

        JScrollPane pillsScroll = createPillsPanel();

        controlsPanel.add(searchBar);
        controlsPanel.add(Box.createVerticalStrut(10));
        controlsPanel.add(pillsScroll);

        topBox.add(controlsPanel);
        add(topBox, BorderLayout.NORTH);

        collectionCardFeed = new JPanel(new GridLayout(0, 2, 12, 12));
        collectionCardFeed.setOpaque(false);

        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);
        gridWrapper.add(collectionCardFeed, BorderLayout.NORTH);

        JScrollPane feedScroller = new JScrollPane(gridWrapper);
        feedScroller.setBorder(new EmptyBorder(10, 14, 10, 14));
        feedScroller.setOpaque(false);
        feedScroller.getViewport().setOpaque(false);
        feedScroller.getVerticalScrollBar().setUnitIncrement(12);
        feedScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        feedScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(feedScroller, BorderLayout.CENTER);
        refreshGrid();
    }

    public void setSearchQuery(String query) {
        if (query != null && !query.trim().isEmpty()) {
            this.currentSearchQuery = query.trim().toLowerCase();
            if (searchBar != null) {
                searchBar.setText(query.trim());
                searchBar.setForeground(AppTheme.COLOR_TEXT_DARK);
            }
        } else {
            this.currentSearchQuery = "";
            if (searchBar != null) {
                searchBar.setText(SEARCH_PLACEHOLDER);
                searchBar.setForeground(Color.GRAY);
            }
        }
    }

    public void setShowFavoritesOnly(boolean showFavoritesOnly) {
        this.showFavoritesOnly = showFavoritesOnly;
        this.showMostViewedOnly = false;
        refreshGrid();
    }

    public void setShowMostViewedOnly(boolean showMostViewedOnly) {
        this.showMostViewedOnly = showMostViewedOnly;
        this.showFavoritesOnly = false;
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
        }

        if (filtered.size() % 2 != 0) {
            JPanel dummy = new JPanel();
            dummy.setOpaque(false);
            collectionCardFeed.add(dummy);
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
        JPanel pillsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pillsPanel.setOpaque(false);

        String[] tags = {"All", "Breakfast", "Lunch", "Dinner", "Dessert", "Salads", "Soups & Stews"};
        for (String tag : tags) {
            JButton pill = new JButton(tag);
            pill.setFont(AppTheme.FONT_BODY_SMALL);
            pill.setFocusPainted(false);
            pill.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            stylePillButton(pill, tag.equals(currentCategoryFilter));

            pill.addActionListener(e -> {
                showFavoritesOnly = false;
                showMostViewedOnly = false;
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

        pillsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pillsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        pillsScroll.setPreferredSize(new Dimension(380, 36));
        pillsScroll.getHorizontalScrollBar().setUnitIncrement(16);

        pillsScroll.getHorizontalScrollBar().setUI(new InvisibleScrollBarUI());
        pillsScroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));

        pillsScroll.addMouseWheelListener(e -> {
            JScrollBar horizontal = pillsScroll.getHorizontalScrollBar();
            int amount = e.getWheelRotation() * horizontal.getUnitIncrement();
            horizontal.setValue(horizontal.getValue() + amount);
        });

        MouseAdapter dragAdapter = new MouseAdapter() {
            private Point origin;

            @Override
            public void mousePressed(MouseEvent e) {
                origin = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (origin != null) {
                    JViewport viewport = pillsScroll.getViewport();
                    Point viewPosition = viewport.getViewPosition();
                    int deltaX = origin.x - e.getX();
                    int newX = viewPosition.x + deltaX;

                    int maxX = pillsPanel.getWidth() - viewport.getWidth();
                    if (maxX < 0) maxX = 0;

                    if (newX < 0) newX = 0;
                    if (newX > maxX) newX = maxX;

                    viewport.setViewPosition(new Point(newX, viewPosition.y));
                }
            }
        };

        pillsPanel.addMouseListener(dragAdapter);
        pillsPanel.addMouseMotionListener(dragAdapter);

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

    private static class InvisibleScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(0, 0, 0, 0);
            this.trackColor = new Color(0, 0, 0, 0);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(0, 0));
            btn.setMinimumSize(new Dimension(0, 0));
            btn.setMaximumSize(new Dimension(0, 0));
            return btn;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {}

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {}
    }
}
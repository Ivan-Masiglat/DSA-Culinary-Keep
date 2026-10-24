package view.components;

import config.AppTheme;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Recipe;

public class RecipeCard extends JPanel {

    public interface CardActionListener {
        void onFavoriteToggle(Recipe recipe);
        void onCardClick(Recipe recipe);
        void onDeleteRequested(Recipe recipe);
    }

    private final Recipe recipe;
    private final CardActionListener listener;

    public RecipeCard(Recipe recipe, CardActionListener listener) {
        this.recipe = recipe;
        this.listener = listener;

        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel cardContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(254, 246, 235));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(AppTheme.COLOR_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cardContainer.setLayout(new BorderLayout());
        cardContainer.setOpaque(false);
        cardContainer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 1. Top Bar Controls (Delete / Favorite)
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(8, 8, 4, 8));

        JButton deleteBtn = createCircleButton("X", new Color(180, 40, 40), () -> {
            if (listener != null) listener.onDeleteRequested(recipe);
        });
        deleteBtn.setToolTipText("Delete Recipe");

        JButton favBtn = createCircleButton("♥", recipe.isFavorite() ? new Color(212, 60, 60) : new Color(180, 170, 160), () -> {
            if (listener != null) listener.onFavoriteToggle(recipe);
        });
        favBtn.setToolTipText("Toggle Favorite");

        topBar.add(deleteBtn, BorderLayout.WEST);
        topBar.add(favBtn, BorderLayout.EAST);

        // 2. Recipe Image Display
        JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
        imgLabel.setPreferredSize(new Dimension(140, 90));

        ImageIcon scaledImg = loadRecipeImage(recipe.getImageUrl(), 140, 90);
        if (scaledImg != null) {
            imgLabel.setIcon(scaledImg);
        } else {
            imgLabel.setIcon(createDefaultPlaceholderIcon(140, 90));
        }

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(topBar, BorderLayout.NORTH);
        centerPanel.add(imgLabel, BorderLayout.CENTER);

        // 3. Card Bottom Metadata Panel with View Count
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(6, 8, 8, 8));

        JLabel titleLabel = new JLabel("<html><body style='width: 110px; text-overflow: ellipsis; overflow: hidden; white-space: nowrap;'>" + recipe.getName().toUpperCase() + "</body></html>");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleLabel.setForeground(new Color(60, 30, 15));

        // Displays Category, Prep Time, and View Count
        String viewText = recipe.getViews() == 1 ? "1 view" : recipe.getViews() + " views";
        JLabel subLabel = new JLabel(recipe.getCategory().toUpperCase() + " • " + recipe.getPrepTime() + " • " + viewText);
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        subLabel.setForeground(new Color(212, 100, 48));

        infoPanel.add(titleLabel);
        infoPanel.add(subLabel);

        cardContainer.add(centerPanel, BorderLayout.CENTER);
        cardContainer.add(infoPanel, BorderLayout.SOUTH);

        // Card Click Listener
        cardContainer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (listener != null) {
                    listener.onCardClick(recipe);
                }
            }
        });

        add(cardContainer, BorderLayout.CENTER);
    }

    private JButton createCircleButton(String text, Color bgColor, Runnable action) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(text, x, y);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(24, 24));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { e.consume(); }
            @Override
            public void mouseReleased(MouseEvent e) { e.consume(); }
            @Override
            public void mouseClicked(MouseEvent e) {
                e.consume();
                if (action != null) {
                    action.run();
                }
            }
        });

        return btn;
    }

    private ImageIcon loadRecipeImage(String path, int targetWidth, int targetHeight) {
        if (path != null && !path.trim().isEmpty()) {
            try {
                File imgFile = new File(path);
                Image img = null;

                if (imgFile.exists()) {
                    img = new ImageIcon(imgFile.getAbsolutePath()).getImage();
                } else {
                    String resourcePath = path.startsWith("/") ? path : "/" + path;
                    java.net.URL imgUrl = getClass().getResource(resourcePath);
                    if (imgUrl != null) {
                        img = new ImageIcon(imgUrl).getImage();
                    }
                }

                if (img != null && img.getWidth(null) > 0) {
                    Image resized = img.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                    return new ImageIcon(resized);
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    private Icon createDefaultPlaceholderIcon(int width, int height) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(245, 130, 32));
                g2.setStroke(new BasicStroke(2f));

                int rectW = 40;
                int rectH = 30;
                int rectX = x + (width - rectW) / 2;
                int rectY = y + (height - rectH) / 2;

                g2.drawRoundRect(rectX, rectY, rectW, rectH, 6, 6);
                g2.drawPolyline(
                    new int[]{rectX + 6, rectX + 16, rectX + 26, rectX + 34},
                    new int[]{rectY + 22, rectY + 12, rectY + 20, rectY + 14},
                    4
                );
                g2.fillOval(rectX + 26, rectY + 8, 5, 5);
                g2.dispose();
            }

            @Override
            public int getIconWidth() { return width; }

            @Override
            public int getIconHeight() { return height; }
        };
    }
}
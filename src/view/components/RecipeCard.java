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
    }

    private final Recipe recipe;
    private final CardActionListener listener;

    private static final String DEFAULT_PLACEHOLDER_PATH = "assets/placeholder.png";
    private static final String HEART_WHITE_ASSET = "assets/heart_white.png";
    private static final String HEART_RED_ASSET = "assets/heart_red.png";

    public RecipeCard(Recipe recipe, CardActionListener listener) {
        this.recipe = recipe;
        this.listener = listener;

        setLayout(new BorderLayout(0, 6));
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(160, 210));

        // Top Image Container Box
        JPanel imageBox = new JPanel() {
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
        imageBox.setLayout(null);
        imageBox.setOpaque(false);
        imageBox.setPreferredSize(new Dimension(160, 135));

        // Recipe Image Label
        JLabel imageLabel = new JLabel("", SwingConstants.CENTER);
        imageLabel.setBounds(0, 0, 160, 135);
        
        String imagePath = getRecipeImagePath(recipe);
        ImageIcon scaledImage = loadRecipeImage(imagePath, 140, 115);

        if (scaledImage == null) {
            scaledImage = loadRecipeImage(DEFAULT_PLACEHOLDER_PATH, 95, 95);
        }

        if (scaledImage != null) {
            imageLabel.setIcon(scaledImage);
        }

        imageBox.add(imageLabel);

        // Prominent Heart Button Container (40x40 circle)
        JLabel heartLabel = new JLabel("", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 70)); // Dark semi-transparent backdrop
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        heartLabel.setBounds(112, 8, 40, 40); // Larger size and positioning
        heartLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        updateHeartIcon(heartLabel, recipe.isFavorite());

        imageBox.add(heartLabel);
        imageBox.setComponentZOrder(heartLabel, 0);

        heartLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                e.consume();
                if (listener != null) {
                    listener.onFavoriteToggle(recipe);
                }
            }
        });

        // Bottom Details Container
        JPanel infoBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(254, 246, 235));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(AppTheme.COLOR_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        infoBox.setLayout(new BoxLayout(infoBox, BoxLayout.Y_AXIS));
        infoBox.setOpaque(false);
        infoBox.setBorder(new EmptyBorder(6, 6, 6, 6));

        // Title
        JLabel titleLabel = new JLabel(recipe.getName().toUpperCase(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleLabel.setForeground(new Color(60, 30, 15));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        int cookingTime = getRecipeCookingTime(recipe);

        String metaText = String.format("<html><center><font size='2' color='#D46430'><b>%s</b></font>&nbsp;&nbsp;<font size='2' color='#888888'>%dmins • %d views</font></center></html>",
                recipe.getCategory().toUpperCase(),
                cookingTime,
                recipe.getViewCount()
        );
        JLabel metaLabel = new JLabel(metaText, SwingConstants.CENTER);
        metaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoBox.add(titleLabel);
        infoBox.add(Box.createVerticalStrut(2));
        infoBox.add(metaLabel);

        add(imageBox, BorderLayout.CENTER);
        add(infoBox, BorderLayout.SOUTH);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (listener != null) {
                    listener.onCardClick(recipe);
                }
            }
        });
    }

    private void updateHeartIcon(JLabel heartLabel, boolean isFav) {
        String assetPath = isFav ? HEART_RED_ASSET : HEART_WHITE_ASSET;
        // Scaled up icon size to 28x28 for clear visibility
        ImageIcon heartIcon = loadRecipeImage(assetPath, 28, 28);

        if (heartIcon != null) {
            heartLabel.setIcon(heartIcon);
            heartLabel.setText("");
        } else {
            heartLabel.setIcon(null);
            heartLabel.setText(isFav ? "[x]" : "[ ]");
            heartLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            heartLabel.setForeground(isFav ? Color.RED : Color.WHITE);
        }
    }

    private String getRecipeImagePath(Recipe recipe) {
        try {
            return (String) recipe.getClass().getMethod("getImagePath").invoke(recipe);
        } catch (Exception e1) {
            try {
                return (String) recipe.getClass().getMethod("getImage").invoke(recipe);
            } catch (Exception e2) {
                return null;
            }
        }
    }

    private int getRecipeCookingTime(Recipe recipe) {
        try {
            return (int) recipe.getClass().getMethod("getCookingTimeMinutes").invoke(recipe);
        } catch (Exception e1) {
            try {
                return (int) recipe.getClass().getMethod("getPrepTime").invoke(recipe);
            } catch (Exception e2) {
                try {
                    return (int) recipe.getClass().getMethod("getCookTime").invoke(recipe);
                } catch (Exception e3) {
                    return 30;
                }
            }
        }
    }

    private ImageIcon loadRecipeImage(String path, int targetWidth, int targetHeight) {
        if (path == null || path.trim().isEmpty()) return null;

        try {
            File imgFile = new File(path);
            Image img = null;
            if (imgFile.exists()) {
                img = new ImageIcon(path).getImage();
            } else {
                java.net.URL imgUrl = getClass().getResource("/" + path);
                if (imgUrl != null) {
                    img = new ImageIcon(imgUrl).getImage();
                }
            }

            if (img != null && img.getWidth(null) > 0) {
                Image resized = img.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                return new ImageIcon(resized);
            }
        } catch (Exception e) {
            // Fallback
        }
        return null;
    }
}
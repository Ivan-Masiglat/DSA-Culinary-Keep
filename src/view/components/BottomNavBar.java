package view.components;

import java.awt.*;
import java.io.File;
import javax.swing.*;

public class BottomNavBar extends JPanel {

    public interface NavListener {
        void onHomeClicked();
        void onFavoritesClicked();
        void onRecipesClicked();
        void onAddClicked();
        void onMostViewedClicked();
    }

    public BottomNavBar(NavListener listener) {
        setLayout(new GridLayout(1, 5, 0, 0));
        setOpaque(false);
        setPreferredSize(new Dimension(420, 64));

        JButton btnHome = createNavButton("assets/icon_home.png", "Home");
        JButton btnFavs = createNavButton("assets/icon_favorites.png", "Favs");
        JButton btnRecipes = createNavButton("assets/icon_recipes.png", "Recipes");
        JButton btnAdd = createNavButton("assets/icon_add.png", "Add");
        JButton btnMostViewed = createNavButton("assets/icon_most_viewed.png", "Views");

        btnHome.addActionListener(e -> listener.onHomeClicked());
        btnFavs.addActionListener(e -> listener.onFavoritesClicked());
        btnRecipes.addActionListener(e -> listener.onRecipesClicked());
        btnAdd.addActionListener(e -> listener.onAddClicked());
        btnMostViewed.addActionListener(e -> listener.onMostViewedClicked());

        add(btnHome);
        add(btnFavs);
        add(btnRecipes);
        add(btnAdd);
        add(btnMostViewed);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Yellow background bar
        g2.setColor(new Color(254, 203, 22));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        g2.dispose();
    }

    private JButton createNavButton(String assetPath, String fallbackText) {
        JButton btn = new JButton();
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        ImageIcon icon = loadScaledIcon(assetPath, 28, 28);
        if (icon != null) {
            btn.setIcon(icon);
        } else {
            btn.setText(fallbackText);
            btn.setFont(new Font("SansSerif", Font.BOLD, 10));
            btn.setForeground(new Color(220, 80, 20));
        }

        return btn;
    }

    private ImageIcon loadScaledIcon(String path, int targetWidth, int targetHeight) {
        if (path == null || path.trim().isEmpty()) return null;

        try {
            File file = new File(path);
            Image img = null;

            if (file.exists()) {
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
            // Fallback to plain text
        }
        return null;
    }
}
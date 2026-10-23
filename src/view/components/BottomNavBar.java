package view.components;

import config.AppTheme;
import java.awt.*;
import javax.swing.*;

public class BottomNavBar extends JPanel {

    public interface NavListener {
        void onCollectionClicked();
        void onAddClicked();
        void onFavoritesClicked();
    }

    public BottomNavBar(NavListener listener) {
        setLayout(new GridLayout(1, 3));
        setOpaque(false);
        setPreferredSize(new Dimension(420, 56));

        JButton btnCollection = createNavButton("📖", "Collection");
        JButton btnAdd = createNavButton("➕", "Add Recipe");
        JButton btnFavs = createNavButton("♥", "Favorites");

        btnCollection.addActionListener(e -> listener.onCollectionClicked());
        btnAdd.addActionListener(e -> listener.onAddClicked());
        btnFavs.addActionListener(e -> listener.onFavoritesClicked());

        add(btnCollection);
        add(btnAdd);
        add(btnFavs);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(255, 255, 255, 240));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(AppTheme.COLOR_BORDER);
        g2.drawLine(0, 0, getWidth(), 0);
        g2.dispose();
    }

    private JButton createNavButton(String icon, String label) {
        JButton btn = new JButton("<html><center>" + icon + "<br><font size='2'>" + label + "</font></center></html>");
        btn.setFont(AppTheme.FONT_BODY);
        btn.setForeground(AppTheme.COLOR_TEXT_DARK);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(null);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}

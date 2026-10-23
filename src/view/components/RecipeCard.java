package view.components;

import config.AppTheme;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Recipe;

public class RecipeCard extends JPanel {

    public interface CardActionListener {
        void onFavoriteToggle(Recipe recipe);
        void onCardClick(Recipe recipe);
    }

    public RecipeCard(Recipe recipe, CardActionListener listener) {
        setLayout(new BorderLayout(10, 0));
        setOpaque(false);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setMaximumSize(new Dimension(380, 95));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel catLbl = new JLabel(recipe.getCategory().toUpperCase());
        catLbl.setFont(new Font("SansSerif", Font.BOLD, 9));
        catLbl.setForeground(AppTheme.COLOR_PRIMARY);

        JLabel titleLbl = new JLabel("<html><b>" + recipe.getName() + "</b></html>");
        titleLbl.setFont(AppTheme.FONT_SERIF_TITLE);

        JLabel metaLbl = new JLabel("⏱ " + recipe.getPrepTime() + " • 👁 " + recipe.getViewCount());
        metaLbl.setFont(AppTheme.FONT_BODY_SMALL);
        metaLbl.setForeground(AppTheme.COLOR_TEXT_MUTED);

        info.add(catLbl);
        info.add(Box.createVerticalStrut(2));
        info.add(titleLbl);
        info.add(Box.createVerticalStrut(4));
        info.add(metaLbl);

        JButton favBtn = new JButton(recipe.isFavorite() ? "♥" : "♡");
        favBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        favBtn.setForeground(recipe.isFavorite() ? new Color(210, 45, 45) : AppTheme.COLOR_TEXT_MUTED);
        favBtn.setFocusPainted(false);
        favBtn.setContentAreaFilled(false);
        favBtn.setBorder(null);
        favBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        favBtn.addActionListener(e -> listener.onFavoriteToggle(recipe));

        add(info, BorderLayout.CENTER);
        add(favBtn, BorderLayout.EAST);

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                listener.onCardClick(recipe);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(AppTheme.COLOR_CARD_BG);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
        g2.setColor(AppTheme.COLOR_BORDER);
        g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 14, 14));
        g2.dispose();
    }
}

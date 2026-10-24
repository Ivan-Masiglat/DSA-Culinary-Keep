package view.screens;

import config.AppTheme;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Recipe;
import view.components.RecipeCard;

public class MostViewedScreen extends JPanel {

    public interface BackListener {
        void onBackClicked();
    }

    private final List<Recipe> recipes;
    private final RecipeCard.CardActionListener cardListener;
    private final BackListener backListener;
    private final JPanel gridPanel;

    public MostViewedScreen(List<Recipe> recipes, RecipeCard.CardActionListener cardListener, BackListener backListener) {
        this.recipes = recipes;
        this.cardListener = cardListener;
        this.backListener = backListener;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(8, 8, 8, 8));

        // Top Header Bar with Back Button and Title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        headerPanel.setOpaque(false);

        JButton backBtn = new JButton("← Back") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(240, 230, 215));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(212, 100, 48));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        backBtn.setForeground(new Color(60, 30, 15));
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.setPreferredSize(new Dimension(80, 28));
        backBtn.addActionListener(e -> {
            if (this.backListener != null) {
                this.backListener.onBackClicked();
            }
        });

        JLabel titleLabel = new JLabel("Most Viewed");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 18));
        titleLabel.setForeground(new Color(60, 30, 15));

        headerPanel.add(backBtn);
        headerPanel.add(titleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Recipe Grid Container
        gridPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

        refreshMostViewed();

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshMostViewed() {
        gridPanel.removeAll();

        List<Recipe> sorted = new ArrayList<>(recipes);

        // Sort by views descending
        sorted.sort((r1, r2) -> Integer.compare(r2.getViews(), r1.getViews()));

        for (Recipe recipe : sorted) {
            gridPanel.add(new RecipeCard(recipe, cardListener));
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }
}
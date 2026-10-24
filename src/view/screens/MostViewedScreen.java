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

    private JPanel gridPanel;

    public MostViewedScreen(List<Recipe> recipes, RecipeCard.CardActionListener cardListener, BackListener backListener) {
        this.recipes = recipes;
        this.cardListener = cardListener;
        this.backListener = backListener;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(12, 16, 12, 16));

        // Top Header Panel
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);

        // High-Contrast Back Button with Drop Shadow
        JButton backBtn = new JButton("← Back") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                // Text drop shadow
                g2.setColor(new Color(0, 0, 0, 160));
                g2.setFont(getFont());
                g2.drawString(getText(), 2, getHeight() - 8);
                // Main white text
                g2.setColor(Color.WHITE);
                g2.drawString(getText(), 1, getHeight() - 9);
                g2.dispose();
            }
        };
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.setPreferredSize(new Dimension(70, 30));
        backBtn.addActionListener(e -> {
            if (backListener != null) backListener.onBackClicked();
        });

        // Center Header Title Box
        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        // Main Title Label ("🔥 Most Viewed")
        JLabel titleLabel = new JLabel("🔥 Most Viewed", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                // Subtle dark outline / shadow for crisp readability
                g2.setColor(new Color(60, 30, 15, 180));
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = fm.getAscent();
                g2.drawString(getText(), x + 1, y + 1);
                g2.setColor(new Color(60, 30, 15));
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        titleLabel.setFont(new Font("Serif", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle Label with Sharp Shadow
        JLabel subtitleLabel = new JLabel("Your most frequently visited recipes", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = fm.getAscent();
                // Shadow
                g2.setColor(new Color(0, 0, 0, 180));
                g2.setFont(getFont());
                g2.drawString(getText(), x + 1, y + 1);
                // Crisp White Text
                g2.setColor(Color.WHITE);
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        subtitleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titleBox.add(titleLabel);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(subtitleLabel);

        topContainer.add(backBtn, BorderLayout.WEST);
        topContainer.add(titleBox, BorderLayout.CENTER);

        add(topContainer, BorderLayout.NORTH);

        // Scrollable Grid Area
        gridPanel = new JPanel();
        gridPanel.setLayout(new BoxLayout(gridPanel, BoxLayout.Y_AXIS));
        gridPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        // Hide visible scrollbars while keeping mouse-wheel scrolling active
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        refreshMostViewed();
    }

    public void refreshMostViewed() {
        gridPanel.removeAll();

        List<Recipe> sorted = new ArrayList<>(recipes);
        sorted.sort((r1, r2) -> Integer.compare(r2.getViewCount(), r1.getViewCount()));

        int rank = 1;
        for (Recipe recipe : sorted) {
            JPanel cardWrapper = new JPanel(new BorderLayout(0, 4));
            cardWrapper.setOpaque(false);
            cardWrapper.setBorder(new EmptyBorder(6, 0, 10, 0));

            JLabel rankLabel = new JLabel("#" + rank, SwingConstants.CENTER);
            rankLabel.setOpaque(true);
            rankLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

            if (rank == 1) {
                rankLabel.setBackground(new Color(255, 204, 0));
                rankLabel.setForeground(Color.BLACK);
            } else if (rank == 2) {
                rankLabel.setBackground(new Color(200, 200, 200));
                rankLabel.setForeground(Color.BLACK);
            } else if (rank == 3) {
                rankLabel.setBackground(new Color(205, 127, 50));
                rankLabel.setForeground(Color.WHITE);
            } else {
                rankLabel.setBackground(new Color(230, 220, 200));
                rankLabel.setForeground(new Color(80, 40, 20));
            }

            rankLabel.setPreferredSize(new Dimension(160, 20));

            RecipeCard card = new RecipeCard(recipe, cardListener);

            cardWrapper.add(rankLabel, BorderLayout.NORTH);
            cardWrapper.add(card, BorderLayout.CENTER);

            gridPanel.add(cardWrapper);
            rank++;
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }
}
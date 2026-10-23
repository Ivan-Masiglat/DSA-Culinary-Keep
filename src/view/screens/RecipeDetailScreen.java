package view.screens;

import config.AppTheme;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Recipe;
import util.DynamicRowHelper;

public class RecipeDetailScreen extends JScrollPane {

    public RecipeDetailScreen(Recipe recipe, Runnable onBackCallback) {
        JPanel outer = new JPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Rounded Card Container
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.COLOR_CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(AppTheme.COLOR_BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
            }
        };

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        // Back Button Row
        JButton backBtn = new JButton("← Back");
        backBtn.setFont(AppTheme.FONT_BODY_SMALL);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        backBtn.addActionListener(e -> onBackCallback.run());

        JLabel title = new JLabel("<html><h3>" + recipe.getName() + "</h3></html>");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.COLOR_TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        final int[] servings = {recipe.getBaseServings()};
        JLabel servVal = new JLabel(String.valueOf(servings[0]));
        servVal.setFont(new Font("SansSerif", Font.BOLD, 14));

        JButton minus = createSecondaryButton("-");
        JButton plus = createSecondaryButton("+");

        JPanel servBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        servBar.setOpaque(false);
        servBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        servBar.add(new JLabel("Servings:"));
        servBar.add(minus);
        servBar.add(servVal);
        servBar.add(plus);

        JPanel ingBox = new JPanel();
        ingBox.setLayout(new BoxLayout(ingBox, BoxLayout.Y_AXIS));
        ingBox.setOpaque(false);
        ingBox.setAlignmentX(Component.LEFT_ALIGNMENT);

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

        card.add(backBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(title);
        card.add(Box.createVerticalStrut(10));
        card.add(servBar);
        card.add(Box.createVerticalStrut(14));
        card.add(createFieldLabel("INGREDIENTS"));
        card.add(Box.createVerticalStrut(6));
        card.add(ingBox);
        card.add(Box.createVerticalStrut(14));
        card.add(createFieldLabel("STEPS"));
        card.add(Box.createVerticalStrut(6));

        int num = 1;
        for (String step : recipe.getInstructions()) {
            JLabel stepLbl = new JLabel("<html><b>" + (num++) + ".</b> " + step + "</html>");
            stepLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(stepLbl);
            card.add(Box.createVerticalStrut(6));
        }

        outer.add(card);

        setViewportView(outer);
        setBorder(null);
        setOpaque(false);
        getViewport().setOpaque(false);
        getVerticalScrollBar().setUnitIncrement(12);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }

    private JLabel createFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppTheme.FONT_LABEL_BOLD);
        l.setForeground(AppTheme.COLOR_PRIMARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(AppTheme.FONT_BODY_SMALL);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
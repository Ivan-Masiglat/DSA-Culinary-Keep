package view.screens;

import config.AppTheme;
import java.awt.*;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Recipe;

public class RecipeDetailScreen extends JPanel {

    public interface BackListener {
        void onBackClicked();
    }

    private final Recipe recipe;
    private final BackListener backListener;
    private int currentServings = 1;

    private JLabel servingsValueLabel;
    private JPanel ingredientsContainer;

    public RecipeDetailScreen(Recipe recipe, BackListener backListener) {
        this.recipe = recipe;
        this.backListener = backListener;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(12, 16, 12, 16));

        // Main Cream Background Card
        JPanel contentCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(254, 246, 235));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(AppTheme.COLOR_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        contentCard.setLayout(new BoxLayout(contentCard, BoxLayout.Y_AXIS));
        contentCard.setOpaque(false);
        contentCard.setBorder(new EmptyBorder(16, 16, 16, 16));

        // 1. Back Button Header Row
        JButton backBtn = new JButton("← Back") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(240, 230, 215));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(AppTheme.COLOR_BORDER);
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
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        backBtn.setMaximumSize(new Dimension(80, 30));
        backBtn.addActionListener(e -> {
            if (backListener != null) backListener.onBackClicked();
        });

        contentCard.add(backBtn);
        contentCard.add(Box.createVerticalStrut(12));

        // 2. Recipe Title
        JLabel titleLabel = new JLabel("<html><body style='width: 250px;'>" + recipe.getName().toUpperCase() + "</body></html>");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(60, 30, 15));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentCard.add(titleLabel);
        contentCard.add(Box.createVerticalStrut(12));

        // 3. Hero Recipe Image Box
        JPanel imageFrame = new JPanel(new BorderLayout());
        imageFrame.setOpaque(false);
        imageFrame.setMaximumSize(new Dimension(360, 150));
        imageFrame.setPreferredSize(new Dimension(360, 150));
        imageFrame.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel imageLabel = new JLabel("", SwingConstants.CENTER);
        ImageIcon scaledImage = loadRecipeImage(recipe.getImageUrl(), 340, 140);
        if (scaledImage == null) {
            scaledImage = loadRecipeImage("assets/placeholder.png", 100, 100);
        }
        if (scaledImage != null) {
            imageLabel.setIcon(scaledImage);
        }

        imageFrame.add(imageLabel, BorderLayout.CENTER);
        contentCard.add(imageFrame);
        contentCard.add(Box.createVerticalStrut(12));

        // 4. Servings Control Bar
        JPanel servingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        servingsPanel.setOpaque(false);
        servingsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel servingsLabel = new JLabel("Servings:");
        servingsLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        servingsLabel.setForeground(new Color(60, 30, 15));

        JButton minusBtn = createStepperButton("-");
        JButton plusBtn = createStepperButton("+");

        servingsValueLabel = new JLabel(String.valueOf(currentServings), SwingConstants.CENTER);
        servingsValueLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        servingsValueLabel.setForeground(new Color(60, 30, 15));
        servingsValueLabel.setPreferredSize(new Dimension(24, 24));

        minusBtn.addActionListener(e -> {
            if (currentServings > 1) {
                currentServings--;
                servingsValueLabel.setText(String.valueOf(currentServings));
                updateIngredientsList();
            }
        });

        plusBtn.addActionListener(e -> {
            currentServings++;
            servingsValueLabel.setText(String.valueOf(currentServings));
            updateIngredientsList();
        });

        servingsPanel.add(servingsLabel);
        servingsPanel.add(minusBtn);
        servingsPanel.add(servingsValueLabel);
        servingsPanel.add(plusBtn);

        contentCard.add(servingsPanel);
        contentCard.add(Box.createVerticalStrut(16));

        // 5. Ingredients Section Header
        JLabel ingredientsHeader = new JLabel("INGREDIENTS");
        ingredientsHeader.setFont(new Font("SansSerif", Font.BOLD, 12));
        ingredientsHeader.setForeground(new Color(212, 100, 48));
        ingredientsHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentCard.add(ingredientsHeader);
        contentCard.add(Box.createVerticalStrut(6));

        // Dynamic Ingredients Container
        ingredientsContainer = new JPanel();
        ingredientsContainer.setLayout(new BoxLayout(ingredientsContainer, BoxLayout.Y_AXIS));
        ingredientsContainer.setOpaque(false);
        ingredientsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentCard.add(ingredientsContainer);
        updateIngredientsList();

        contentCard.add(Box.createVerticalStrut(16));

        // 6. Steps Section Header
        JLabel stepsHeader = new JLabel("STEPS");
        stepsHeader.setFont(new Font("SansSerif", Font.BOLD, 12));
        stepsHeader.setForeground(new Color(212, 100, 48));
        stepsHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentCard.add(stepsHeader);
        contentCard.add(Box.createVerticalStrut(6));

        // Steps List Items formatted with HTML wrapping to fit inside card bounds
        if (recipe.getInstructions() != null && !recipe.getInstructions().isEmpty()) {
            int stepNum = 1;
            for (String step : recipe.getInstructions()) {
                if (!step.trim().isEmpty()) {
                    JLabel stepLabel = new JLabel("<html><body style='width: 250px;'>" + stepNum + ". " + step.trim() + "</body></html>");
                    stepLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
                    stepLabel.setForeground(new Color(40, 20, 10));
                    stepLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    contentCard.add(stepLabel);
                    contentCard.add(Box.createVerticalStrut(6));
                    stepNum++;
                }
            }
        } else {
            JLabel noSteps = new JLabel("No instructions listed");
            noSteps.setFont(new Font("SansSerif", Font.ITALIC, 12));
            noSteps.setForeground(Color.GRAY);
            noSteps.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentCard.add(noSteps);
        }

        // ScrollPane with vertical scrolling only and no horizontal scrollbar
        JScrollPane scrollPane = new JScrollPane(contentCard);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void updateIngredientsList() {
        ingredientsContainer.removeAll();

        if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
            for (String item : recipe.getIngredients()) {
                if (!item.trim().isEmpty()) {
                    String scaledItem = scaleIngredientText(item.trim(), currentServings);
                    JLabel itemLabel = new JLabel("<html><body style='width: 250px;'>• " + scaledItem + "</body></html>");
                    itemLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
                    itemLabel.setForeground(new Color(40, 20, 10));
                    itemLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    ingredientsContainer.add(itemLabel);
                    ingredientsContainer.add(Box.createVerticalStrut(4));
                }
            }
        } else {
            JLabel noIng = new JLabel("• No ingredients listed");
            noIng.setFont(new Font("SansSerif", Font.ITALIC, 12));
            noIng.setForeground(Color.GRAY);
            noIng.setAlignmentX(Component.LEFT_ALIGNMENT);
            ingredientsContainer.add(noIng);
        }

        ingredientsContainer.revalidate();
        ingredientsContainer.repaint();
    }

    private String scaleIngredientText(String ingredient, int multiplier) {
        if (multiplier <= 1) return ingredient;

        Pattern pattern = Pattern.compile("^(\\d+\\/\\d+|\\d+(?:\\.\\d+)?)");
        Matcher matcher = pattern.matcher(ingredient);

        if (matcher.find()) {
            String rawVal = matcher.group(1);
            double val = 0;

            if (rawVal.contains("/")) {
                String[] parts = rawVal.split("/");
                val = Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
            } else {
                val = Double.parseDouble(rawVal);
            }

            double scaledVal = val * multiplier;
            String formattedVal;

            if (scaledVal == Math.floor(scaledVal)) {
                formattedVal = String.valueOf((int) scaledVal);
            } else {
                formattedVal = String.format("%.1f", scaledVal);
            }

            return ingredient.replaceFirst("^" + Pattern.quote(rawVal), formattedVal);
        }

        return ingredient;
    }

    private JButton createStepperButton(String text) {
        JButton btn = new JButton("") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(240, 230, 215));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(AppTheme.COLOR_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

                g2.setColor(new Color(60, 30, 15));
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(text, x, y);

                g2.dispose();
            }
        };
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(28, 28));
        return btn;
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
        } catch (Exception ignored) {}
        return null;
    }
}
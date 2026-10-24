package view.screens;

import config.AppTheme;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    private int currentServings;
    private final int baseServings;

    private JLabel titleLabel;
    private JLabel imgLabel;
    private JLabel servingsValueLabel;
    private JPanel ingredientsContainer;
    private JPanel stepsContainer;

    public RecipeDetailScreen(Recipe recipe, BackListener backListener) {
        this.recipe = recipe;
        this.backListener = backListener;

        this.baseServings = recipe.getBaseServings() > 0 ? recipe.getBaseServings() : 1;
        this.currentServings = this.baseServings;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(12, 16, 12, 16));

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

        // 1. Navigation & Action Header (Back & Edit)
        JPanel headerBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerBar.setOpaque(false);
        headerBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton backBtn = createPillButton("← Back");
        backBtn.addActionListener(e -> {
            if (backListener != null) backListener.onBackClicked();
        });

        JButton editBtn = createPillButton("✎ Edit");
        editBtn.addActionListener(e -> openEditDialog());

        headerBar.add(backBtn);
        headerBar.add(editBtn);

        contentCard.add(headerBar);
        contentCard.add(Box.createVerticalStrut(12));

        // 2. Recipe Title
        titleLabel = new JLabel(recipe.getName().toUpperCase());
        titleLabel.setFont(new Font("Serif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(60, 30, 15));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentCard.add(titleLabel);
        contentCard.add(Box.createVerticalStrut(12));

        // 3. Main Recipe Image
        imgLabel = new JLabel("", SwingConstants.CENTER);
        imgLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        imgLabel.setPreferredSize(new Dimension(340, 180));
        imgLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        updateImageDisplay();
        contentCard.add(imgLabel);
        contentCard.add(Box.createVerticalStrut(14));

        // 4. Servings Stepper Bar (- / +)
        JPanel servingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        servingsPanel.setOpaque(false);
        servingsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel servingsTextLabel = new JLabel("Servings:");
        servingsTextLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        servingsTextLabel.setForeground(new Color(60, 30, 15));

        JButton minusBtn = createStepperButton("-");
        servingsValueLabel = new JLabel(String.valueOf(currentServings), SwingConstants.CENTER);
        servingsValueLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        servingsValueLabel.setPreferredSize(new Dimension(24, 24));

        JButton plusBtn = createStepperButton("+");

        minusBtn.addActionListener(e -> {
            if (currentServings > 1) {
                currentServings--;
                servingsValueLabel.setText(String.valueOf(currentServings));
                renderIngredients();
            }
        });

        plusBtn.addActionListener(e -> {
            currentServings++;
            servingsValueLabel.setText(String.valueOf(currentServings));
            renderIngredients();
        });

        servingsPanel.add(servingsTextLabel);
        servingsPanel.add(minusBtn);
        servingsPanel.add(servingsValueLabel);
        servingsPanel.add(plusBtn);

        contentCard.add(servingsPanel);
        contentCard.add(Box.createVerticalStrut(16));

        // 5. Ingredients Section Header
        JLabel ingHeader = new JLabel("INGREDIENTS");
        ingHeader.setFont(new Font("SansSerif", Font.BOLD, 12));
        ingHeader.setForeground(new Color(212, 100, 48));
        ingHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentCard.add(ingHeader);
        contentCard.add(Box.createVerticalStrut(6));

        ingredientsContainer = new JPanel();
        ingredientsContainer.setLayout(new BoxLayout(ingredientsContainer, BoxLayout.Y_AXIS));
        ingredientsContainer.setOpaque(false);
        ingredientsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentCard.add(ingredientsContainer);
        contentCard.add(Box.createVerticalStrut(16));

        renderIngredients();

        // 6. Steps Section Header
        JLabel stepsHeader = new JLabel("STEPS");
        stepsHeader.setFont(new Font("SansSerif", Font.BOLD, 12));
        stepsHeader.setForeground(new Color(212, 100, 48));
        stepsHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentCard.add(stepsHeader);
        contentCard.add(Box.createVerticalStrut(6));

        stepsContainer = new JPanel();
        stepsContainer.setLayout(new BoxLayout(stepsContainer, BoxLayout.Y_AXIS));
        stepsContainer.setOpaque(false);
        stepsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentCard.add(stepsContainer);
        renderSteps();

        JScrollPane scrollPane = new JScrollPane(contentCard);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    // --- POPUP EDIT DIALOG ---
    private void openEditDialog() {
        Window ancestor = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(ancestor, "Edit Recipe", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(380, 500);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(12, 12, 12, 12));
        form.setBackground(new Color(254, 246, 235));

        JTextField nameField = new JTextField(recipe.getName());
        JTextField timeField = new JTextField(recipe.getPrepTime());
        JComboBox<String> catCombo = new JComboBox<>(new String[]{"Breakfast", "Lunch", "Dinner", "Dessert"});
        catCombo.setSelectedItem(recipe.getCategory());

        JTextArea ingArea = new JTextArea(String.join("\n", recipe.getIngredients()), 6, 20);
        JTextArea stepsArea = new JTextArea(String.join("\n", recipe.getInstructions()), 6, 20);

        form.add(new JLabel("Recipe Name:"));
        form.add(nameField);
        form.add(Box.createVerticalStrut(8));

        form.add(new JLabel("Category:"));
        form.add(catCombo);
        form.add(Box.createVerticalStrut(8));

        form.add(new JLabel("Prep Time:"));
        form.add(timeField);
        form.add(Box.createVerticalStrut(8));

        form.add(new JLabel("Ingredients (1 per line):"));
        form.add(new JScrollPane(ingArea));
        form.add(Box.createVerticalStrut(8));

        form.add(new JLabel("Steps (1 per line):"));
        form.add(new JScrollPane(stepsArea));
        form.add(Box.createVerticalStrut(12));

        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setBackground(new Color(212, 100, 48));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> {
            recipe.setName(nameField.getText().trim());
            recipe.setPrepTime(timeField.getText().trim());
            recipe.setCategory((String) catCombo.getSelectedItem());

            List<String> newIngs = new ArrayList<>();
            for (String line : ingArea.getText().split("\\r?\\n")) {
                if (!line.trim().isEmpty()) newIngs.add(line.trim());
            }
            recipe.getIngredients().clear();
            recipe.getIngredients().addAll(newIngs);

            List<String> newSteps = new ArrayList<>();
            for (String line : stepsArea.getText().split("\\r?\\n")) {
                if (!line.trim().isEmpty()) newSteps.add(line.trim());
            }
            recipe.getInstructions().clear();
            recipe.getInstructions().addAll(newSteps);

            // Refresh UI directly
            titleLabel.setText(recipe.getName().toUpperCase());
            renderIngredients();
            renderSteps();
            dialog.dispose();
        });

        form.add(saveBtn);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        dialog.add(scroll, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void renderIngredients() {
        ingredientsContainer.removeAll();
        double factor = (double) currentServings / baseServings;

        for (String rawIngredient : recipe.getIngredients()) {
            String cleanText = rawIngredient.trim().replaceAll("^[•\\-\\s]+", "");
            String scaledText = scaleIngredientNumbers(cleanText, factor);

            JLabel ingLabel = new JLabel("<html><body style='width: 290px;'>• " + scaledText + "</body></html>");
            ingLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            ingLabel.setForeground(new Color(60, 30, 15));
            ingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            ingredientsContainer.add(ingLabel);
            ingredientsContainer.add(Box.createVerticalStrut(4));
        }

        ingredientsContainer.revalidate();
        ingredientsContainer.repaint();
    }

    private void renderSteps() {
        stepsContainer.removeAll();
        List<String> instructions = recipe.getInstructions();
        for (int i = 0; i < instructions.size(); i++) {
            String rawStep = instructions.get(i).trim();
            String cleanStep = rawStep.replaceAll("^\\d+\\.\\s*\\d+\\.\\s*", "").replaceAll("^\\d+\\.\\s*", "");

            JLabel stepLabel = new JLabel("<html><body style='width: 290px;'>" + (i + 1) + ". " + cleanStep + "</body></html>");
            stepLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            stepLabel.setForeground(new Color(60, 30, 15));
            stepLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            stepsContainer.add(stepLabel);
            stepsContainer.add(Box.createVerticalStrut(6));
        }
        stepsContainer.revalidate();
        stepsContainer.repaint();
    }

    private String scaleIngredientNumbers(String text, double factor) {
        Pattern pattern = Pattern.compile("(\\d+(\\.\\d+)?)\\s*(lb|g|oz|kg|cup|cups|tbsp|tsp|large|small|medium|whole|cloves|slices|grams)?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            try {
                double val = Double.parseDouble(matcher.group(1));
                double scaled = val * factor;

                String formattedVal = (scaled == (long) scaled) ? String.format("%d", (long) scaled) : String.format("%.1f", scaled);
                matcher.appendReplacement(sb, Matcher.quoteReplacement(formattedVal + (matcher.group(3) != null ? " " + matcher.group(3) : "")));
            } catch (Exception e) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private void updateImageDisplay() {
        ImageIcon icon = loadScaledImage(recipe.getImageUrl(), 340, 180);
        if (icon != null) {
            imgLabel.setIcon(icon);
        } else {
            imgLabel.setIcon(createDefaultPlaceholderIcon(340, 180));
        }
    }

    private JButton createPillButton(String text) {
        JButton btn = new JButton(text) {
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
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(new Color(60, 30, 15));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(80, 28));
        return btn;
    }

    private JButton createStepperButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 220, 205));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(new Color(180, 170, 160));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.setFont(new Font("SansSerif", Font.BOLD, 13));
                g2.setColor(new Color(60, 30, 15));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(text, x, y);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(28, 28));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private ImageIcon loadScaledImage(String path, int targetWidth, int targetHeight) {
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

                int rectW = 60;
                int rectH = 40;
                int rectX = x + (width - rectW) / 2;
                int rectY = y + (height - rectH) / 2;

                g2.drawRoundRect(rectX, rectY, rectW, rectH, 8, 8);
                g2.drawPolyline(
                    new int[]{rectX + 10, rectX + 24, rectX + 38, rectX + 50},
                    new int[]{rectY + 30, rectY + 16, rectY + 28, rectY + 20},
                    4
                );
                g2.fillOval(rectX + 38, rectY + 10, 8, 8);
                g2.dispose();
            }

            @Override
            public int getIconWidth() { return width; }

            @Override
            public int getIconHeight() { return height; }
        };
    }
}
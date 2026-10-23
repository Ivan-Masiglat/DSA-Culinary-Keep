package view.screens;

import config.AppTheme;
import database.DatabaseManager;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Recipe;
import util.DynamicRowHelper;

public class AddRecipeScreen extends JScrollPane {

    private JTextField nameField;
    private JTextField imageUrlField;
    private JComboBox<String> categoryBox;
    private JSpinner timeSpinner;
    private JComboBox<String> unitBox;

    private final List<JTextField> ingredientFields = new ArrayList<>();
    private final List<JTextField> instructionFields = new ArrayList<>();

    private JPanel ingredientsContainer;
    private JPanel instructionsContainer;

    public AddRecipeScreen(Consumer<Recipe> onSaveCallback) {
        JPanel outer = new JPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(20, 16, 20, 16));

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(AppTheme.COLOR_CARD_BG);
                g2.fill(new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 16, 16
                ));

                g2.setColor(AppTheme.COLOR_BORDER);
                g2.draw(new RoundRectangle2D.Float(
                    0, 0, getWidth() - 1, getHeight() - 1, 16, 16
                ));

                g2.dispose();
            }
        };

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 18, 20, 18));

        JLabel header = new JLabel("Add New Recipe");
        header.setFont(AppTheme.FONT_TITLE);
        header.setForeground(AppTheme.COLOR_TEXT_DARK);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        nameField = DynamicRowHelper.createStyledTextField(
            "e.g. Saffron Risotto",
            AppTheme.COLOR_BORDER
        );
        nameField.setMaximumSize(new Dimension(350, 36));

        imageUrlField = DynamicRowHelper.createStyledTextField(
            "e.g. Image URL (Optional)",
            AppTheme.COLOR_BORDER
        );
        imageUrlField.setMaximumSize(new Dimension(350, 36));

        categoryBox = new JComboBox<>(new String[]{
            "Breakfast",
            "Dinner",
            "Dessert",
            "Salads",
            "Soups & Stews"
        });
        styleComponent(categoryBox);
        categoryBox.setMaximumSize(new Dimension(350, 36));

        timeSpinner = new JSpinner(
            new SpinnerNumberModel(30, 5, 300, 5)
        );
        unitBox = new JComboBox<>(new String[]{"mins", "hours"});

        JPanel timeRow = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 6, 0)
        );
        timeRow.setOpaque(false);
        timeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        timeRow.add(timeSpinner);
        timeRow.add(unitBox);

        ingredientsContainer = new JPanel();
        ingredientsContainer.setLayout(
            new BoxLayout(ingredientsContainer, BoxLayout.Y_AXIS)
        );
        ingredientsContainer.setOpaque(false);

        DynamicRowHelper.addDynamicRow(
            ingredientsContainer,
            ingredientFields,
            "e.g. 2 cups rice",
            AppTheme.COLOR_BORDER
        );

        JButton addIngBtn = createSecondaryButton("+ Ingredient");
        addIngBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addIngBtn.addActionListener(e -> {
            DynamicRowHelper.addDynamicRow(
                ingredientsContainer,
                ingredientFields,
                "e.g. Next item...",
                AppTheme.COLOR_BORDER
            );
            ingredientsContainer.revalidate();
            ingredientsContainer.repaint();
        });

        instructionsContainer = new JPanel();
        instructionsContainer.setLayout(
            new BoxLayout(instructionsContainer, BoxLayout.Y_AXIS)
        );
        instructionsContainer.setOpaque(false);

        DynamicRowHelper.addDynamicRow(
            instructionsContainer,
            instructionFields,
            "e.g. Heat butter on pan...",
            AppTheme.COLOR_BORDER
        );

        JButton addStepBtn = createSecondaryButton("+ Step");
        addStepBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addStepBtn.addActionListener(e -> {
            DynamicRowHelper.addDynamicRow(
                instructionsContainer,
                instructionFields,
                "e.g. Next step...",
                AppTheme.COLOR_BORDER
            );
            instructionsContainer.revalidate();
            instructionsContainer.repaint();
        });

        JButton saveBtn = new JButton("Save Recipe");
        saveBtn.setBackground(AppTheme.COLOR_PRIMARY);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        saveBtn.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        saveBtn.addActionListener(e -> {
            Recipe newRecipe = buildRecipeFromForm();

            if (newRecipe == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Please enter a recipe name.",
                    "Missing Recipe Name",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (DatabaseManager.saveRecipe(newRecipe)) {
                onSaveCallback.accept(newRecipe);
                clearForm();

                JOptionPane.showMessageDialog(
                    this,
                    "Recipe saved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to save recipe. Please check the database.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        JButton clearBtn = createSecondaryButton("Clear");
        clearBtn.addActionListener(e -> clearForm());

        JPanel buttonRow = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 8, 0)
        );
        buttonRow.setOpaque(false);
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonRow.add(saveBtn);
        buttonRow.add(clearBtn);

        card.add(header);
        card.add(Box.createVerticalStrut(14));
        card.add(createFieldLabel("NAME"));
        card.add(Box.createVerticalStrut(4));
        card.add(nameField);
        card.add(Box.createVerticalStrut(10));
        card.add(createFieldLabel("IMAGE URL (OPTIONAL)"));
        card.add(Box.createVerticalStrut(4));
        card.add(imageUrlField);
        card.add(Box.createVerticalStrut(10));
        card.add(createFieldLabel("CATEGORY"));
        card.add(Box.createVerticalStrut(4));
        card.add(categoryBox);
        card.add(Box.createVerticalStrut(10));
        card.add(createFieldLabel("PREP TIME"));
        card.add(Box.createVerticalStrut(4));
        card.add(timeRow);
        card.add(Box.createVerticalStrut(12));
        card.add(createFieldLabel("INGREDIENTS"));
        card.add(Box.createVerticalStrut(4));
        card.add(ingredientsContainer);
        card.add(Box.createVerticalStrut(4));
        card.add(addIngBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(createFieldLabel("INSTRUCTIONS"));
        card.add(Box.createVerticalStrut(4));
        card.add(instructionsContainer);
        card.add(Box.createVerticalStrut(4));
        card.add(addStepBtn);
        card.add(Box.createVerticalStrut(18));
        card.add(buttonRow);

        outer.add(card);

        setViewportView(outer);
        setBorder(null);
        setOpaque(false);
        getViewport().setOpaque(false);
        getVerticalScrollBar().setUnitIncrement(12);
        setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_NEVER
        );
        setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
    }

    private Recipe buildRecipeFromForm() {
        String name = nameField.getText().trim();

        if (name.isEmpty() || name.startsWith("e.g.")) {
            return null;
        }

        List<String> ings = ingredientFields.stream()
            .map(JTextField::getText)
            .map(String::trim)
            .filter(t -> !t.isEmpty() && !t.startsWith("e.g."))
            .collect(Collectors.toList());

        List<String> steps = instructionFields.stream()
            .map(JTextField::getText)
            .map(String::trim)
            .filter(t -> !t.isEmpty() && !t.startsWith("e.g."))
            .collect(Collectors.toList());

        return new Recipe(
            name,
            (String) categoryBox.getSelectedItem(),
            timeSpinner.getValue() + " " + unitBox.getSelectedItem(),
            imageUrlField.getText().trim(),
            ings,
            steps
        );
    }

    public void clearForm() {
        nameField.setText("");
        imageUrlField.setText("");
        categoryBox.setSelectedIndex(0);
        timeSpinner.setValue(30);
        unitBox.setSelectedIndex(0);

        ingredientsContainer.removeAll();
        ingredientFields.clear();

        DynamicRowHelper.addDynamicRow(
            ingredientsContainer,
            ingredientFields,
            "e.g. 2 cups rice",
            AppTheme.COLOR_BORDER
        );

        instructionsContainer.removeAll();
        instructionFields.clear();

        DynamicRowHelper.addDynamicRow(
            instructionsContainer,
            instructionFields,
            "e.g. Heat butter on pan...",
            AppTheme.COLOR_BORDER
        );

        ingredientsContainer.revalidate();
        ingredientsContainer.repaint();
        instructionsContainer.revalidate();
        instructionsContainer.repaint();
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.FONT_LABEL_BOLD);
        label.setForeground(AppTheme.COLOR_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void styleComponent(JComponent comp) {
        comp.setFont(AppTheme.FONT_BODY);
        comp.setBackground(Color.WHITE);
        comp.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                AppTheme.COLOR_BORDER, 1
            ),
            new EmptyBorder(6, 10, 6, 10)
        ));
        comp.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(AppTheme.FONT_BODY_SMALL);
        btn.setFocusPainted(false);
        return btn;
    }
}

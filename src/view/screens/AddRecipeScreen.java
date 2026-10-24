package view.screens;

import config.AppTheme;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import model.Recipe;

public class AddRecipeScreen extends JPanel {

    public interface RecipeAddListener {
        void onRecipeAdded(Recipe recipe);
    }

    private final RecipeAddListener listener;

    private JTextField nameField;
    private JComboBox<String> categoryCombo;
    private JSpinner prepTimeSpinner;

    private JPanel dropZonePanel;
    private JLabel dropZoneLabel;
    private String selectedImagePath = "";

    private JPanel ingredientsPanel;
    private JPanel stepsPanel;

    private final List<JTextArea> ingredientFields = new ArrayList<>();
    private final List<JTextArea> stepFields = new ArrayList<>();

    public AddRecipeScreen(RecipeAddListener listener) {
        this.listener = listener;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(12, 16, 12, 16));

        // Cream Background Card Container
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

        // Screen Title
        JLabel titleLabel = new JLabel("Add New Recipe");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(60, 30, 15));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentCard.add(titleLabel);
        contentCard.add(Box.createVerticalStrut(12));

        // 1. Recipe Name
        contentCard.add(createSectionHeader("RECIPE NAME"));
        contentCard.add(Box.createVerticalStrut(4));
        nameField = createStyledSingleField();
        contentCard.add(nameField);
        contentCard.add(Box.createVerticalStrut(12));

        // 2. Drag & Drop Image Drop Zone
        contentCard.add(createSectionHeader("RECIPE IMAGE (DRAG & DROP OR CLICK)"));
        contentCard.add(Box.createVerticalStrut(4));

        dropZonePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Dashed Border Paint
                float[] dash = {6.0f};
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                g2.setColor(new Color(200, 150, 100));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        dropZonePanel.setOpaque(false);
        dropZonePanel.setPreferredSize(new Dimension(300, 70));
        dropZonePanel.setMaximumSize(new Dimension(320, 70));
        dropZonePanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dropZonePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        dropZoneLabel = new JLabel("📁 Drag & Drop Image Here or Click to Browse", SwingConstants.CENTER);
        dropZoneLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        dropZoneLabel.setForeground(new Color(150, 100, 50));
        dropZonePanel.add(dropZoneLabel, BorderLayout.CENTER);

        // Click Handler for File Chooser
        dropZonePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JFileChooser chooser = new JFileChooser();
                int option = chooser.showOpenDialog(AddRecipeScreen.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    processUploadedFile(chooser.getSelectedFile());
                }
            }
        });

        // Drag and Drop Handler
        new DropTarget(dropZonePanel, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    @SuppressWarnings("unchecked")
                    List<File> droppedFiles = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (droppedFiles != null && !droppedFiles.isEmpty()) {
                        processUploadedFile(droppedFiles.get(0));
                    }
                } catch (Exception ignored) {}
            }
        });

        contentCard.add(dropZonePanel);
        contentCard.add(Box.createVerticalStrut(12));

        // 3. Category
        contentCard.add(createSectionHeader("CATEGORY"));
        contentCard.add(Box.createVerticalStrut(4));
        categoryCombo = new JComboBox<>(new String[]{"Breakfast", "Lunch", "Dinner", "Dessert"});
        categoryCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        categoryCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentCard.add(categoryCombo);
        contentCard.add(Box.createVerticalStrut(12));

        // 4. Prep Time
        contentCard.add(createSectionHeader("PREP TIME"));
        contentCard.add(Box.createVerticalStrut(4));
        
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        timePanel.setOpaque(false);
        timePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        prepTimeSpinner = new JSpinner(new SpinnerNumberModel(20, 1, 300, 5));
        prepTimeSpinner.setPreferredSize(new Dimension(65, 28));
        
        JLabel minsLabel = new JLabel("mins");
        minsLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        minsLabel.setForeground(new Color(60, 30, 15));

        timePanel.add(prepTimeSpinner);
        timePanel.add(minsLabel);

        contentCard.add(timePanel);
        contentCard.add(Box.createVerticalStrut(12));

        // 5. Dynamic Ingredients Section
        contentCard.add(createSectionHeader("INGREDIENTS"));
        contentCard.add(Box.createVerticalStrut(4));

        ingredientsPanel = new JPanel();
        ingredientsPanel.setLayout(new BoxLayout(ingredientsPanel, BoxLayout.Y_AXIS));
        ingredientsPanel.setOpaque(false);
        ingredientsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentCard.add(ingredientsPanel);

        JButton addIngredientBtn = createSmallButton("+ Ingredient");
        addIngredientBtn.addActionListener(e -> addIngredientField(""));
        contentCard.add(addIngredientBtn);
        contentCard.add(Box.createVerticalStrut(16));

        // 6. Dynamic Instructions / Steps Section
        contentCard.add(createSectionHeader("INSTRUCTIONS"));
        contentCard.add(Box.createVerticalStrut(4));

        stepsPanel = new JPanel();
        stepsPanel.setLayout(new BoxLayout(stepsPanel, BoxLayout.Y_AXIS));
        stepsPanel.setOpaque(false);
        stepsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentCard.add(stepsPanel);

        JButton addStepBtn = createSmallButton("+ Step");
        addStepBtn.addActionListener(e -> addStepField(""));
        contentCard.add(addStepBtn);
        contentCard.add(Box.createVerticalStrut(20));

        // Add 1 default row each
        addIngredientField("");
        addStepField("");

        // 7. Form Action Buttons
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionsPanel.setOpaque(false);
        actionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton saveBtn = new JButton("Save Recipe") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(212, 100, 48));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        saveBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setContentAreaFilled(false);
        saveBtn.setBorderPainted(false);
        saveBtn.setFocusPainted(false);
        saveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveBtn.setPreferredSize(new Dimension(120, 32));
        saveBtn.addActionListener(e -> saveRecipe());

        JButton clearBtn = createSmallButton("Clear");
        clearBtn.setPreferredSize(new Dimension(80, 32));
        clearBtn.addActionListener(e -> resetForm());

        actionsPanel.add(saveBtn);
        actionsPanel.add(clearBtn);

        contentCard.add(actionsPanel);

        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(contentCard);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void processUploadedFile(File file) {
        if (file == null || !file.exists()) return;

        try {
            File assetsDir = new File("assets");
            if (!assetsDir.exists()) {
                assetsDir.mkdirs();
            }

            String fileName = System.currentTimeMillis() + "_" + file.getName();
            File destFile = new File(assetsDir, fileName);

            Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            selectedImagePath = "assets/" + fileName;
            dropZoneLabel.setText("✓ " + file.getName());
            dropZoneLabel.setForeground(new Color(40, 140, 40));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to copy image to assets folder.", "Upload Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createSectionHeader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(new Color(212, 100, 48));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createStyledSingleField() {
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private JTextArea createExpandingTextArea() {
        JTextArea textArea = new JTextArea(1, 15);
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            private void updateHeight() {
                SwingUtilities.invokeLater(() -> {
                    int preferredHeight = textArea.getPreferredSize().height;
                    if (preferredHeight < 28) preferredHeight = 28;
                    textArea.setSize(new Dimension(textArea.getWidth(), preferredHeight));
                    refreshContainerLayout();
                });
            }

            @Override
            public void insertUpdate(DocumentEvent e) { updateHeight(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateHeight(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateHeight(); }
        });

        return textArea;
    }

    private void addIngredientField(String text) {
        JTextArea area = createExpandingTextArea();
        if (!text.isEmpty()) area.setText(text);
        ingredientFields.add(area);

        JPanel rowContainer = new JPanel();
        rowContainer.setLayout(new BoxLayout(rowContainer, BoxLayout.Y_AXIS));
        rowContainer.setOpaque(false);
        rowContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel textWrapper = new JPanel(new BorderLayout());
        textWrapper.setOpaque(false);
        textWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 190, 180), 1),
            new EmptyBorder(3, 6, 3, 6)
        ));
        textWrapper.setBackground(Color.WHITE);
        textWrapper.add(area, BorderLayout.CENTER);

        JButton removeBtn = createRemoveButton();
        removeBtn.addActionListener(e -> {
            ingredientFields.remove(area);
            ingredientsPanel.remove(rowContainer);
            refreshContainerLayout();
        });

        row.add(textWrapper, BorderLayout.CENTER);
        row.add(removeBtn, BorderLayout.EAST);

        rowContainer.add(row);
        rowContainer.add(Box.createVerticalStrut(6));

        ingredientsPanel.add(rowContainer);
        refreshContainerLayout();
    }

    private void addStepField(String text) {
        JTextArea area = createExpandingTextArea();
        if (!text.isEmpty()) area.setText(text);
        stepFields.add(area);

        JPanel rowContainer = new JPanel();
        rowContainer.setLayout(new BoxLayout(rowContainer, BoxLayout.Y_AXIS));
        rowContainer.setOpaque(false);
        rowContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel textWrapper = new JPanel(new BorderLayout());
        textWrapper.setOpaque(false);
        textWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 190, 180), 1),
            new EmptyBorder(3, 6, 3, 6)
        ));
        textWrapper.setBackground(Color.WHITE);
        textWrapper.add(area, BorderLayout.CENTER);

        JButton removeBtn = createRemoveButton();
        removeBtn.addActionListener(e -> {
            stepFields.remove(area);
            stepsPanel.remove(rowContainer);
            refreshContainerLayout();
        });

        row.add(textWrapper, BorderLayout.CENTER);
        row.add(removeBtn, BorderLayout.EAST);

        rowContainer.add(row);
        rowContainer.add(Box.createVerticalStrut(6));

        stepsPanel.add(rowContainer);
        refreshContainerLayout();
    }

    private void refreshContainerLayout() {
        revalidate();
        repaint();
    }

    private JButton createRemoveButton() {
        JButton btn = new JButton("X") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(180, 40, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("X")) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString("X", x, y);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(26, 26));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setToolTipText("Remove Row");
        return btn;
    }

    private JButton createSmallButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setForeground(new Color(60, 30, 15));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    private void saveRecipe() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a recipe name.", "Missing Name", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String category = (String) categoryCombo.getSelectedItem();
        int prepTime = (int) prepTimeSpinner.getValue();

        List<String> ingredients = new ArrayList<>();
        for (JTextArea a : ingredientFields) {
            String val = a.getText().trim();
            if (!val.isEmpty()) ingredients.add(val);
        }

        List<String> steps = new ArrayList<>();
        for (JTextArea a : stepFields) {
            String val = a.getText().trim();
            if (!val.isEmpty()) steps.add(val);
        }

        Recipe recipe = new Recipe(name, category, prepTime + " mins", selectedImagePath, ingredients, steps);

        if (listener != null) {
            listener.onRecipeAdded(recipe);
        }

        resetForm();
        JOptionPane.showMessageDialog(this, "Recipe saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetForm() {
        nameField.setText("");
        selectedImagePath = "";
        dropZoneLabel.setText("📁 Drag & Drop Image Here or Click to Browse");
        dropZoneLabel.setForeground(new Color(150, 100, 50));

        categoryCombo.setSelectedIndex(0);
        prepTimeSpinner.setValue(20);

        ingredientsPanel.removeAll();
        ingredientFields.clear();

        stepsPanel.removeAll();
        stepFields.clear();

        addIngredientField("");
        addStepField("");

        refreshContainerLayout();
    }
}
package util;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicRowHelper {

    public static JTextField createStyledTextField(String placeholder, Color borderColor) {
        JTextField field = new JTextField(placeholder);
        field.setFont(new Font("SansSerif", Font.PLAIN, 12));
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return field;
    }

    public static void addDynamicRow(JPanel container, List<JTextField> list, String placeholder, Color borderColor) {
        JPanel row = new JPanel(new BorderLayout(4, 0));
        row.setOpaque(false);
        JTextField field = createStyledTextField(placeholder, borderColor);
        list.add(field);

        JButton removeBtn = new JButton("×");
        removeBtn.setBorder(null);
        removeBtn.setContentAreaFilled(false);
        removeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        removeBtn.addActionListener(e -> {
            container.remove(row);
            list.remove(field);
            container.revalidate();
            container.repaint();
        });

        row.add(field, BorderLayout.CENTER);
        row.add(removeBtn, BorderLayout.EAST);
        container.add(row);
        container.add(Box.createVerticalStrut(4));
        container.revalidate();
    }

    public static String scaleIngredientAmount(String ingredient, double factor) {
        Pattern pattern = Pattern.compile("^([0-9]+(?:\\.[0-9]+)?)(.*)$");
        Matcher matcher = pattern.matcher(ingredient.trim());
        if (matcher.find()) {
            try {
                double val = Double.parseDouble(matcher.group(1)) * factor;
                String formatted = (val % 1 == 0) ? String.format("%.0f", val) : String.format("%.1f", val);
                return formatted + matcher.group(2);
            } catch (Exception ignored) {}
        }
        return ingredient;
    }
}
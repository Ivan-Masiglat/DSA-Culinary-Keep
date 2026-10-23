package view.screens;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class HomeScreen extends JPanel {

    private final Consumer<String> onSearchSubmitted;
    private static final String SEARCH_PLACEHOLDER = "Search...";
    private static final String BG_ASSET_PATH = "assets/homescreen.png";
    private Image bgImage;

    public HomeScreen(Consumer<String> onSearchSubmitted) {
        this.onSearchSubmitted = onSearchSubmitted;

        setLayout(null);
        setOpaque(false);

        loadBackgroundImage(BG_ASSET_PATH);

        // Dynamic Search Bar floating directly above the static canvas background
        JTextField searchBar = new JTextField(SEARCH_PLACEHOLDER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Cream fill matching the template background
                g2.setColor(new Color(254, 246, 215));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                
                // Brown border outline
                g2.setColor(new Color(120, 50, 20));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        searchBar.setBounds(24, 28, 332, 42);
        searchBar.setOpaque(false);
        searchBar.setBorder(new EmptyBorder(0, 16, 0, 16));
        searchBar.setFont(new Font("SansSerif", Font.PLAIN, 15));
        searchBar.setForeground(Color.GRAY);

        searchBar.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchBar.getText().equals(SEARCH_PLACEHOLDER)) {
                    searchBar.setText("");
                    searchBar.setForeground(new Color(60, 30, 15));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchBar.getText().trim().isEmpty()) {
                    searchBar.setText(SEARCH_PLACEHOLDER);
                    searchBar.setForeground(Color.GRAY);
                }
            }
        });

        searchBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String query = searchBar.getText().trim();
                    if (!query.equals(SEARCH_PLACEHOLDER) && !query.isEmpty()) {
                        if (onSearchSubmitted != null) {
                            onSearchSubmitted.accept(query);
                        }
                    }
                }
            }
        });

        add(searchBar);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void loadBackgroundImage(String path) {
        try {
            File imgFile = new File(path);
            if (imgFile.exists()) {
                bgImage = new ImageIcon(path).getImage();
            } else {
                java.net.URL url = getClass().getResource("/" + path);
                if (url != null) {
                    bgImage = new ImageIcon(url).getImage();
                }
            }
        } catch (Exception e) {
            // Fallback
        }
    }
}
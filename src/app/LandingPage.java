package app;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LandingPage extends JFrame {

    private static final Color COLOR_BTN_BG = new Color(250, 214, 73);
    private static final Color COLOR_BTN_BORDER = new Color(178, 48, 79);
    private static final Color COLOR_TEXT_DARK = new Color(58, 24, 16);

    public LandingPage() {
        setTitle("CulinaryKeep - Welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 840);
        setResizable(false);
        setLocationRelativeTo(null);

        // Single Full-Screen Background Panel
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            private Image bgImage;

            {
                File imgFile = new File("assets/landingbg.png");
                if (imgFile.exists()) {
                    bgImage = new ImageIcon("assets/landingbg.png").getImage();
                } else {
                    java.net.URL url = getClass().getResource("/assets/landingbg.png");
                    if (url != null) {
                        bgImage = new ImageIcon(url).getImage();
                    }
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    // Stretch/fit background image across the entire window frame
                    g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                    g2d.dispose();
                } else {
                    g.setColor(new Color(240, 230, 233));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        JPanel contentOverlay = new JPanel(new BorderLayout());
        contentOverlay.setOpaque(false);

        // Optional Overlay Text
        File textFile = new File("assets/landingtext.png");
        if (textFile.exists() || getClass().getResource("/assets/landingtext.png") != null) {
            JLabel textLabel = new JLabel();
            textLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            ImageIcon rawIcon = textFile.exists() ? 
                    new ImageIcon("assets/landingtext.png") : 
                    new ImageIcon(getClass().getResource("/assets/landingtext.png"));
            
            if (rawIcon.getIconWidth() > 0) {
                int targetW = 340;
                int targetH = (int) ((double) rawIcon.getIconHeight() / rawIcon.getIconWidth() * targetW);
                Image scaledImg = rawIcon.getImage().getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                textLabel.setIcon(new ImageIcon(scaledImg));
            }
            contentOverlay.add(textLabel, BorderLayout.CENTER);
        }

        // Bottom Button Container
        JPanel bottomContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomContainer.setOpaque(false);
        bottomContainer.setBorder(new EmptyBorder(0, 0, 50, 0));

        JButton getStartedBtn = createGetStartedButton();
        getStartedBtn.addActionListener(e -> {
            new CulinaryKeepApp().setVisible(true);
            dispose();
        });

        bottomContainer.add(getStartedBtn);
        contentOverlay.add(bottomContainer, BorderLayout.SOUTH);

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(contentOverlay, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JButton createGetStartedButton() {
        JButton button = new JButton("GET STARTED") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                g2.setColor(COLOR_BTN_BORDER);
                g2.fillRoundRect(0, 0, w, h, 16, 16);

                g2.setColor(getModel().isPressed() ? COLOR_BTN_BG.darker() : COLOR_BTN_BG);
                g2.fillRoundRect(3, 3, w - 6, h - 6, 12, 12);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setForeground(COLOR_TEXT_DARK);
        button.setPreferredSize(new Dimension(280, 52));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.repaint();
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LandingPage().setVisible(true));
    }
}

package view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public class SplashScreen extends JWindow {

    private float alpha = 1.0f;
    private BufferedImage splashImage;

    public SplashScreen() {
        try {
            splashImage = ImageIO.read(
                new File("assets/splashscreen.jpg")
            );
        } catch (Exception e) {
            System.err.println(
                "Failed to load splash image: " + e.getMessage()
            );
        }

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (splashImage != null) {
                    Graphics2D g2 = (Graphics2D) g.create();

                    g2.setComposite(
                        AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER,
                            Math.max(0f, Math.min(1f, alpha))
                        )
                    );

                    g2.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR
                    );

                    g2.drawImage(
                        splashImage,
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        null
                    );

                    g2.dispose();
                }
            }
        };

        panel.setBackground(new Color(235, 220, 218));
        setContentPane(panel);

        setSize(420, 746);
        setLocationRelativeTo(null);
    }

    public void startSplash(Runnable onFinished) {
        Timer displayTimer = new Timer(2000, e -> {
            ((Timer) e.getSource()).stop();
            startFadeOut(onFinished);
        });

        displayTimer.setRepeats(false);
        displayTimer.start();
    }

    private void startFadeOut(Runnable onFinished) {
        Timer fadeTimer = new Timer(30, null);

        fadeTimer.addActionListener(e -> {
            alpha -= 0.06f;

            if (alpha <= 0f) {
                alpha = 0f;
                fadeTimer.stop();
                dispose();
                onFinished.run();
            } else {
                repaint();
            }
        });

        fadeTimer.start();
    }
}

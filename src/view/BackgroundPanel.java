package view;

import config.AppTheme;
import java.awt.*;
import java.io.File;
import javax.swing.*;

public class BackgroundPanel extends JPanel {
    private Image bgImage;

    public BackgroundPanel() {
        super(new BorderLayout());
        File imgFile = new File("assets/recipebg.png");
        if (imgFile.exists()) {
            bgImage = new ImageIcon("assets/recipebg.png").getImage();
        } else {
            java.net.URL url = getClass().getResource("/assets/recipebg.png");
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
            g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            g2d.dispose();
        } else {
            g.setColor(AppTheme.COLOR_SCREEN_BG);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}

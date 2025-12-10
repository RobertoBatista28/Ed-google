package Resources.Dice;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public class DiceDrawer {

    public static ImageIcon drawDieFaces(int value, int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, width, height, 20, 20);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(0, 0, width, height, 20, 20);

        // Draw dots
        g2.setColor(Color.BLACK);
        int dotSize = width / 6;
        int center = width / 2 - dotSize / 2;
        int left = width / 4 - dotSize / 2;
        int right = (width * 3) / 4 - dotSize / 2;
        int top = height / 4 - dotSize / 2;
        int bottom = (height * 3) / 4 - dotSize / 2;

        if (value % 2 != 0) { // 1, 3, 5
            g2.fillOval(center, center, dotSize, dotSize);
        }
        if (value > 1) { // 2, 3, 4, 5, 6
            g2.fillOval(left, top, dotSize, dotSize);
            g2.fillOval(right, bottom, dotSize, dotSize);
        }
        if (value > 3) { // 4, 5, 6
            g2.fillOval(right, top, dotSize, dotSize);
            g2.fillOval(left, bottom, dotSize, dotSize);
        }
        if (value == 6) { // 6
            g2.fillOval(left, center, dotSize, dotSize);
            g2.fillOval(right, center, dotSize, dotSize);
        }

        g2.dispose();
        return new ImageIcon(img);
    }
}

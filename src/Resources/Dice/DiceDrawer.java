package Resources.Dice;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * DiceDrawer is a utility class for rendering die face representations as images.
 * Generates BufferedImage icons of standard 6-sided dice faces (1-6) with
 * appropriately positioned black dots on white background with rounded corners.
 *
 */
public class DiceDrawer {

    /**
     * Draws and returns a die face icon for the specified value.
     * Creates a BufferedImage with rounded white background, black border,
     * and black dots positioned according to standard die face patterns.
     * Supports die values 1-6 with proper dot placement conventions.
     *
     * @param value the die face value (1-6) to render
     * @param width the width in pixels of the generated image
     * @param height the height in pixels of the generated image
     * @return an ImageIcon containing the rendered die face
     */
    public static ImageIcon drawDieFaces(int value, int width, int height) {
        // Create a buffered image with alpha channel support for transparency
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();

        // Enable anti-aliasing for smooth edges on circles and shapes
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw white background with rounded corners (20px radius)
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, width, height, 20, 20);

        // Draw black border with 3-pixel stroke width
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(0, 0, width, height, 20, 20);

        // Configure dot drawing: black filled ovals representing die pips
        g2.setColor(Color.BLACK);

        // Calculate dot size and positions based on image dimensions
        // Dot size is 1/6 of width to ensure proper spacing
        int dotSize = width / 6;

        // Center position: horizontally and vertically centered
        int center = width / 2 - dotSize / 2;

        // Left position: 1/4 from left edge
        int left = width / 4 - dotSize / 2;

        // Right position: 3/4 from left edge
        int right = (width * 3) / 4 - dotSize / 2;

        // Top position: 1/4 from top edge
        int top = height / 4 - dotSize / 2;

        // Bottom position: 3/4 from top edge
        int bottom = (height * 3) / 4 - dotSize / 2;

        // Draw dots based on die value according to standard die face patterns
        // Value 1, 3, 5: center dot
        if (value % 2 != 0) {
            g2.fillOval(center, center, dotSize, dotSize);
        }

        // Value 2, 3, 4, 5, 6: top-left and bottom-right diagonal dots
        if (value > 1) {
            g2.fillOval(left, top, dotSize, dotSize);
            g2.fillOval(right, bottom, dotSize, dotSize);
        }

        // Value 4, 5, 6: top-right and bottom-left opposite diagonal dots
        if (value > 3) {
            g2.fillOval(right, top, dotSize, dotSize);
            g2.fillOval(left, bottom, dotSize, dotSize);
        }

        // Value 6: additional left and right middle dots for total of 6 pips
        if (value == 6) {
            g2.fillOval(left, center, dotSize, dotSize);
            g2.fillOval(right, center, dotSize, dotSize);
        }

        // Dispose of graphics context to free resources
        g2.dispose();

        // Return the buffered image wrapped in an ImageIcon for GUI use
        return new ImageIcon(img);
    }
}

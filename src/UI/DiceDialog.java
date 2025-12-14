package UI;

import Resources.Dice.DiceDrawer;
import Utils.GameConfig;
import java.awt.*;
import javax.swing.*;

/**
 * DiceDialog is a modal dialog that displays an animated dice rolling simulation.
 * Shows two animated dice that roll through random values before settling on
 * the final dice results. Automatically closes after displaying the total.
 *
 */
public class DiceDialog extends JDialog {

    private final JLabel d1Label;
    private final JLabel d2Label;
    private final JLabel totalLabel;
    private final int finalDie1;
    private final int finalDie2;
    private int animationSteps = 0;
    private Timer animationTimer;

    /**
     * Creates a new DiceDialog with animated dice rolling visualization.
     * Displays the specified player name and animates two dice from random values
     * to their final values. The dialog auto-closes after showing the total.
     *
     * @param parent the parent JFrame for modal behavior
     * @param playerName the name of the player rolling the dice
     * @param die1 the final value of the first die
     * @param die2 the final value of the second die
     */
    public DiceDialog(JFrame parent, String playerName, int die1, int die2) {
        super(parent, "Lançamento de Dados", true);
        // Store final dice values to display after animation
        this.finalDie1 = die1;
        this.finalDie2 = die2;

        // Configure dialog with no window decorations for custom appearance
        setUndecorated(true);
        setLayout(new BorderLayout());

        // Create main content panel with custom styling
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        content.setBackground(new Color(40, 40, 40));

        // Set dialog dimensions
        content.setPreferredSize(new Dimension(350, 280));

        // Create title label with player name
        JLabel title = new JLabel(playerName + " está a lançar...");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create panel for dice display with horizontal layout
        JPanel dicePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        dicePanel.setOpaque(false);

        // Create and add both dice labels
        d1Label = createDieLabel();
        d2Label = createDieLabel();

        dicePanel.add(d1Label);
        dicePanel.add(d2Label);

        // Create total label for displaying sum (shown after animation)
        totalLabel = new JLabel("A lançar os dados...");
        totalLabel.setForeground(Color.YELLOW);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 26));
        totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Assemble content panel with proper spacing
        content.add(Box.createVerticalStrut(30));
        content.add(title);
        content.add(Box.createVerticalStrut(20));
        content.add(dicePanel);
        content.add(Box.createVerticalStrut(20));
        content.add(totalLabel);
        content.add(Box.createVerticalStrut(30));

        add(content);
        pack();
        setLocationRelativeTo(parent);

        // Start dice animation
        startAnimation();
    }

    /**
     * Creates a label for displaying a single die face.
     * The label is sized to 80x80 pixels and configured to display die face icons.
     *
     * @return a JLabel configured for die face display
     */
    private JLabel createDieLabel() {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(80, 80));
        label.setBackground(Color.WHITE);
        label.setOpaque(false);
        return label;
    }

    /**
     * Updates a die label to display the icon for the specified die value.
     * Renders the die face (1-6) as a 80x80 pixel icon and displays it on the label.
     *
     * @param label the JLabel to update with the new die face
     * @param value the die value (1-6) to display
     */
    private void updateDie(JLabel label, int value) {
        label.setIcon(DiceDrawer.drawDieFaces(value, 80, 80));
        label.setText("");
        label.setBorder(null);
    }

    /**
     * Starts the dice rolling animation using a Swing Timer.
     * Animates both dice through random values for a specified number of steps,
     * then displays the final values and closes the dialog after a delay.
     * Animation is controlled by GameConfig parameters for timing and duration.
     *
     */
    private void startAnimation() {
        animationTimer = new Timer(GameConfig.DICE_ANIMATION_DELAY, e -> {
            // Continue rolling dice while animation steps remain
            if (animationSteps < GameConfig.DICE_ANIMATION_MAX_STEPS) {
                // Display random die values to simulate rolling
                updateDie(d1Label, (int) (Math.random() * 6) + 1);
                updateDie(d2Label, (int) (Math.random() * 6) + 1);
                animationSteps++;
            } else {
                // Animation complete - stop timer and display final values
                animationTimer.stop();
                updateDie(d1Label, finalDie1);
                updateDie(d2Label, finalDie2);
                // Display total sum of both dice in yellow text
                totalLabel.setText("Total: " + (finalDie1 + finalDie2));

                // Schedule automatic dialog closure after delay
                Timer closeTimer = new Timer(GameConfig.DICE_DIALOG_DELAY, evt -> dispose());
                closeTimer.setRepeats(false);
                closeTimer.start();
            }
        });
        animationTimer.start();
    }
}

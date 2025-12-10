package UI;

import Resources.Dice.DiceDrawer;
import Utils.GameConfig;
import java.awt.*;
import javax.swing.*;

public class DiceDialog extends JDialog {

    private final JLabel d1Label;
    private final JLabel d2Label;
    private final JLabel totalLabel;
    private final int finalDie1;
    private final int finalDie2;
    private int animationSteps = 0;
    private Timer animationTimer;

    public DiceDialog(JFrame parent, String playerName, int die1, int die2) {
        super(parent, "Lançamento de Dados", true);
        this.finalDie1 = die1;
        this.finalDie2 = die2;

        setUndecorated(true);
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        content.setBackground(new Color(40, 40, 40));

        content.setPreferredSize(new Dimension(350, 280));

        JLabel title = new JLabel(playerName + " está a lançar...");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel dicePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        dicePanel.setOpaque(false);

        d1Label = createDieLabel();
        d2Label = createDieLabel();

        dicePanel.add(d1Label);
        dicePanel.add(d2Label);

        totalLabel = new JLabel("A lançar os dados...");
        totalLabel.setForeground(Color.YELLOW);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 26));
        totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

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

        startAnimation();
    }

    private JLabel createDieLabel() {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(80, 80));
        label.setBackground(Color.WHITE);
        label.setOpaque(false);
        return label;
    }

    private void updateDie(JLabel label, int value) {
        label.setIcon(DiceDrawer.drawDieFaces(value, 80, 80));
        label.setText("");
        label.setBorder(null);
    }

    private void startAnimation() {
        animationTimer = new Timer(GameConfig.DICE_ANIMATION_DELAY, e -> {
            if (animationSteps < GameConfig.DICE_ANIMATION_MAX_STEPS) {
                updateDie(d1Label, (int) (Math.random() * 6) + 1);
                updateDie(d2Label, (int) (Math.random() * 6) + 1);
                animationSteps++;
            } else {
                animationTimer.stop();
                updateDie(d1Label, finalDie1);
                updateDie(d2Label, finalDie2);
                totalLabel.setText("Total: " + (finalDie1 + finalDie2));

                Timer closeTimer = new Timer(GameConfig.DICE_DIALOG_DELAY, evt -> dispose());
                closeTimer.setRepeats(false);
                closeTimer.start();
            }
        });
        animationTimer.start();
    }
}

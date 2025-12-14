package UI;

import Models.Question;
import java.awt.*;
import javax.swing.*;

/**
 * QuestionDialog represents a modal dialog that displays a question challenge to the player.
 * The dialog presents a question with four answer options and allows the player to select
 * one answer. The result of the question (correct or incorrect) is stored and can be
 * retrieved after the dialog is closed.
 *
 */
public class QuestionDialog extends JDialog {

    private boolean result = false;

    /**
     * Creates a new QuestionDialog with the specified question.
     * The dialog displays the question text and four answer options in a 2x2 grid layout.
     * When the player selects an answer, the dialog evaluates the correctness, displays
     * the result, and closes automatically.
     *
     * @param parent the parent frame of this dialog
     * @param question the Question object containing the text and answer options
     */
    public QuestionDialog(JFrame parent, Question question) {
        super(parent, "Desafio!", true);
        setUndecorated(true);
        setLayout(new BorderLayout());

        Utils.SoundPlayer.playQuestion();

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
        content.setBackground(new Color(30, 30, 50));
        content.setPreferredSize(new Dimension(400, 300));

        // Dialog title "DESAFIO!" in large yellow font
        JLabel title = new JLabel("DESAFIO!");
        title.setForeground(Color.YELLOW);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Question text area with word wrapping enabled for multi-line display
        JTextArea qText = new JTextArea(question.getText());
        qText.setWrapStyleWord(true);
        qText.setLineWrap(true);
        qText.setOpaque(false);
        qText.setForeground(Color.WHITE);
        qText.setFont(new Font("Arial", Font.BOLD, 16));
        qText.setEditable(false);
        qText.setAlignmentX(Component.CENTER_ALIGNMENT);
        qText.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Answer options panel with 2x2 grid layout for four buttons
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        optionsPanel.setOpaque(false);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] options = question.getOptions();
        for (int i = 0; i < options.length; i++) {
            JButton btn = new JButton(options[i]);
            btn.setFocusPainted(false);
            btn.setBackground(Color.WHITE);
            final int index = i;
            // Each button evaluates if the selected answer is correct and displays result
            btn.addActionListener(e -> {
                Utils.SoundPlayer.playClick();
                if (question.isCorrect(index)) {
                    result = true;
                    JOptionPane.showMessageDialog(this, "Correto!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    result = false;
                    JOptionPane.showMessageDialog(this, "Errado!", "Falha", JOptionPane.ERROR_MESSAGE);
                }
                dispose();
            });
            optionsPanel.add(btn);
        }

        content.add(Box.createVerticalStrut(20));
        content.add(title);
        content.add(Box.createVerticalStrut(20));
        content.add(qText);
        content.add(Box.createVerticalStrut(20));
        content.add(optionsPanel);
        content.add(Box.createVerticalStrut(20));

        add(content);
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Returns the result of the question answer.
     * The result is determined when the player selects an answer and the dialog
     * evaluates whether the selected option is correct.
     *
     * @return true if the answer was correct, false otherwise
     */
    public boolean getResult() {
        return result;
    }
}

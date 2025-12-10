package UI;

import Models.Question;
import java.awt.*;
import javax.swing.*;

public class QuestionDialog extends JDialog {

    private boolean result = false;

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

        JLabel title = new JLabel("DESAFIO!");
        title.setForeground(Color.YELLOW);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea qText = new JTextArea(question.getText());
        qText.setWrapStyleWord(true);
        qText.setLineWrap(true);
        qText.setOpaque(false);
        qText.setForeground(Color.WHITE);
        qText.setFont(new Font("Arial", Font.BOLD, 16));
        qText.setEditable(false);
        qText.setAlignmentX(Component.CENTER_ALIGNMENT);
        qText.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        optionsPanel.setOpaque(false);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] options = question.getOptions();
        for (int i = 0; i < options.length; i++) {
            JButton btn = new JButton(options[i]);
            btn.setFocusPainted(false);
            btn.setBackground(Color.WHITE);
            final int index = i;
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

    public boolean getResult() {
        return result;
    }
}

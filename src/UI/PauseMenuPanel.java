package UI;

import Utils.GameConfig;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class PauseMenuPanel extends JPanel {

    private final JButton resumeBtn;
    private final JButton helpBtn;
    private final JButton mainMenuBtn;

    public PauseMenuPanel(ActionListener resumeAction, ActionListener helpAction, ActionListener mainMenuAction) {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Semi-transparent overlay
        JPanel overlayPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(0, 0, 0, 180)); // Dark semi-transparent
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlayPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        // Top spacer to center vertically
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        overlayPanel.add(Box.createGlue(), gbc);

        // Logo
        BufferedImage logoImg = Utils.ImageLoader.getImage(GameConfig.UI_PATH_TEXTURE + GameConfig.LOGO_TEXTURE);
        if (logoImg != null) {
            double scale = GameConfig.MAIN_MENU_LOGO_SCALE;
            int newWidth = (int) (logoImg.getWidth() * scale);
            int newHeight = (int) (logoImg.getHeight() * scale);
            Image scaledLogo = logoImg.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

            gbc.gridy = 1;
            gbc.weighty = 0;
            gbc.insets = new Insets(0, 10, 20, 10);
            overlayPanel.add(logoLabel, gbc);
        }

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.setOpaque(false);

        GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.gridx = 0;
        btnGbc.fill = GridBagConstraints.HORIZONTAL;
        btnGbc.insets = new Insets(5, 0, 5, 0);

        // Resume Button
        resumeBtn = createButton("VOLTAR AO JOGO", false);
        resumeBtn.addActionListener(e -> {
            Utils.SoundPlayer.playClick();
            resumeAction.actionPerformed(e);
        });
        btnGbc.gridy = 0;
        btnGbc.gridwidth = 2;
        buttonsPanel.add(resumeBtn, btnGbc);

        // Help Button
        helpBtn = createButton("AJUDA E SUPORTE", true);
        helpBtn.addActionListener(e -> {
            Utils.SoundPlayer.playClick();
            helpAction.actionPerformed(e);
        });
        
        btnGbc.gridy = 1;
        btnGbc.gridwidth = 1;
        btnGbc.gridx = 0;
        btnGbc.weightx = 0.5;
        btnGbc.insets = new Insets(5, 0, 5, 5);
        buttonsPanel.add(helpBtn, btnGbc);

        // Main Menu Button
        mainMenuBtn = createButton("MENU PRINCIPAL", true);
        mainMenuBtn.addActionListener(e -> {
            Utils.SoundPlayer.playClick();
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja voltar ao menu principal?\nO progresso do jogo será perdido.",
                "Confirmar Saída",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                mainMenuAction.actionPerformed(e);
            }
        });
        btnGbc.gridx = 1;
        btnGbc.insets = new Insets(5, 5, 5, 0);
        buttonsPanel.add(mainMenuBtn, btnGbc);

        gbc.gridy = 3;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 10, 0, 10);
        overlayPanel.add(buttonsPanel, gbc);

        // Hint Label
        JLabel hintLabel = new JLabel("Pressione ESC para voltar ao jogo");
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        hintLabel.setForeground(new Color(200, 200, 200));
        hintLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 4;
        gbc.weighty = 0;
        gbc.insets = new Insets(30, 10, 0, 10);
        overlayPanel.add(hintLabel, gbc);

        // Bottom spacer to center vertically
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        overlayPanel.add(Box.createGlue(), gbc);

        add(overlayPanel, BorderLayout.CENTER);
    }

    private JButton createButton(String text, boolean isShort) {
        String texture = isShort ? GameConfig.SHORT_BUTTON_TEXTURE : GameConfig.LONG_BUTTON_TEXTURE;
        String hoverTexture = isShort ? GameConfig.HOVER_SHORT_BUTTON_TEXTURE : GameConfig.HOVER_LONG_BUTTON_TEXTURE;

        final BufferedImage btnImg = Utils.ImageLoader.getImage(GameConfig.UI_PATH_TEXTURE + texture);
        final BufferedImage hoverBtnImg = Utils.ImageLoader.getImage(GameConfig.UI_PATH_TEXTURE + hoverTexture);

        JButton btn = new JButton(text) {
            private boolean isHovered = false;

            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        isHovered = true;
                        setForeground(Color.decode(GameConfig.TEXT_COLOR_HOVER_HEX));
                        repaint();
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        isHovered = false;
                        setForeground(Color.WHITE);
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                BufferedImage imgToDraw = isHovered && hoverBtnImg != null ? hoverBtnImg : btnImg;

                if (imgToDraw != null) {
                    g.drawImage(imgToDraw, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(Color.decode(GameConfig.BUTTONS_BACKGROUND_COLOR));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(isShort ? 220 : 450, 50));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}

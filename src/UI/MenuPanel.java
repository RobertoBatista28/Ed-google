package UI;

import Utils.GameConfig;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class MenuPanel extends JPanel {

    private final JButton playBtn;
    private final JButton helpBtn;
    private final JButton exitBtn;

    public MenuPanel(ActionListener playAction, ActionListener helpAction, ActionListener exitAction) {
        setLayout(new BorderLayout());

        // Center Panel for Logo and Buttons
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Logo
        BufferedImage logoImg = Utils.ImageLoader.getImage(GameConfig.UI_PATH_TEXTURE + GameConfig.LOGO_TEXTURE);
        double scale = GameConfig.MAIN_MENU_LOGO_SCALE * 1.25;
        int newWidth = (int) (logoImg.getWidth() * scale);
        int newHeight = (int) (logoImg.getHeight() * scale);
        Image scaledLogo = logoImg.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));

        gbc.gridy = 0;
        gbc.insets = new Insets(80, 10, 30, 10);
        centerPanel.add(logoLabel, gbc);

        // Painel de Botões
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.setOpaque(false);

        GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.gridx = 0;
        btnGbc.fill = GridBagConstraints.HORIZONTAL;
        btnGbc.insets = new Insets(5, 0, 5, 0);

        playBtn = createButton("JOGAR", false);
        playBtn.addActionListener(e -> {
            Utils.SoundPlayer.playClick();
            playAction.actionPerformed(e);
        });
        btnGbc.gridy = 0;
        btnGbc.gridwidth = 2;
        buttonsPanel.add(playBtn, btnGbc);

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

        exitBtn = createButton("SAIR", true);
        exitBtn.addActionListener(e -> {
            Utils.SoundPlayer.playClick();
            exitAction.actionPerformed(e);
        });
        btnGbc.gridx = 1;
        btnGbc.insets = new Insets(5, 5, 5, 0);
        buttonsPanel.add(exitBtn, btnGbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(40, 10, 0, 10);
        JPanel buttonSpacer = new JPanel();
        buttonSpacer.setOpaque(false);
        buttonSpacer.setPreferredSize(new Dimension(1, 60));
        centerPanel.add(buttonSpacer, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 10, 0, 10);
        centerPanel.add(buttonsPanel, gbc);

        // Filler to push content up
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        centerPanel.add(Box.createGlue(), gbc);

        add(centerPanel, BorderLayout.CENTER);

        // South Panel for Version and Copyright
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel versionLabel = new JLabel(GameConfig.GAME_VERSION);
        versionLabel.setForeground(Color.WHITE);
        versionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        southPanel.add(versionLabel, BorderLayout.WEST);

        JLabel copyrightLabel = new JLabel("Copyright João Coelho e Roberto Baptista");
        copyrightLabel.setForeground(Color.WHITE);
        copyrightLabel.setFont(new Font("Arial", Font.BOLD, 14));
        southPanel.add(copyrightLabel, BorderLayout.EAST);

        add(southPanel, BorderLayout.SOUTH);
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage bg = Utils.ImageLoader.getImage(Utils.GameConfig.BACKGROUND_PATH + Utils.GameConfig.MAIN_MENU_BACKGROUND_TEXTURE);
        if (bg != null) {
            // Draw centered background image with scaling
            double zoom = 0.8;
            int imgW = (int) (bg.getWidth() * zoom);
            int imgH = (int) (bg.getHeight() * zoom);
            int panelW = getWidth();
            int panelH = getHeight();
            int x = (panelW - imgW) / 2;
            int y = (panelH - imgH) / 2;

            g.drawImage(bg, x, y, imgW, imgH, this);
        } else {
            g.setColor(Color.decode(GameConfig.STATS_PANEL_BACKGROUND_COLOR));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}

package UI;

import Utils.GameConfig;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.*;

/**
 * MenuPanel represents the main menu interface of the game.
 * The panel displays the game logo, four action buttons (Play, Map Editor, Help, Exit),
 * and version/copyright information. It includes a background image and custom-styled buttons
 * with hover effects.
 *
 */
public class MenuPanel extends JPanel {

    private final JButton playBtn;
    private final JButton mapEditorBtn;
    private final JButton helpBtn;
    private final JButton exitBtn;

    /**
     * Creates a new MenuPanel with the specified action listeners for menu buttons.
     * The panel constructs a GridBagLayout with a centered logo, four buttons arranged
     * vertically, and version/copyright information at the bottom. Each button has an
     * associated ActionListener that is triggered when clicked.
     *
     * @param playAction the ActionListener to execute when the play button is clicked
     * @param mapEditorAction the ActionListener to execute when the map editor button is clicked
     * @param helpAction the ActionListener to execute when the help button is clicked
     * @param exitAction the ActionListener to execute when the exit button is clicked
     */
    public MenuPanel(ActionListener playAction, ActionListener mapEditorAction, ActionListener helpAction, ActionListener exitAction) {
        setLayout(new BorderLayout());

        // Center panel with GridBagLayout for logo and buttons positioning
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Load, scale, and display the game logo
        BufferedImage logoImg = Utils.ImageLoader.getImage(GameConfig.UI_PATH_TEXTURE + GameConfig.LOGO_TEXTURE);
        double scale = GameConfig.MAIN_MENU_LOGO_SCALE * 1.25;
        int newWidth = (int) (logoImg.getWidth() * scale);
        int newHeight = (int) (logoImg.getHeight() * scale);
        Image scaledLogo = logoImg.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));

        gbc.gridy = 0;
        gbc.insets = new Insets(80, 10, 30, 10);
        centerPanel.add(logoLabel, gbc);

        // Buttons panel with GridBagLayout for vertical button arrangement
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.setOpaque(false);

        GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.gridx = 0;
        btnGbc.fill = GridBagConstraints.HORIZONTAL;
        btnGbc.insets = new Insets(5, 0, 5, 0);

        // Play button - full width
        playBtn = createButton("JOGAR", false);
        playBtn.addActionListener(e -> {
            Utils.SoundPlayer.playClick();
            playAction.actionPerformed(e);
        });
        btnGbc.gridy = 0;
        btnGbc.gridwidth = 2;
        buttonsPanel.add(playBtn, btnGbc);

        // Map Editor button - full width
        mapEditorBtn = createButton("EDITOR DE MAPAS", false);
        mapEditorBtn.addActionListener(e -> {
            Utils.SoundPlayer.playClick();
            mapEditorAction.actionPerformed(e);
        });
        btnGbc.gridy = 1;
        btnGbc.gridwidth = 2;
        buttonsPanel.add(mapEditorBtn, btnGbc);

        // Help button - left side (half width)
        helpBtn = createButton("AJUDA E SUPORTE", true);
        helpBtn.addActionListener(e -> {
            Utils.SoundPlayer.playClick();
            helpAction.actionPerformed(e);
        });
        
        btnGbc.gridy = 2;
        btnGbc.gridwidth = 1;
        btnGbc.gridx = 0;
        btnGbc.weightx = 0.5;
        btnGbc.insets = new Insets(5, 0, 5, 5);
        buttonsPanel.add(helpBtn, btnGbc);

        // Exit button - right side (half width)
        exitBtn = createButton("SAIR", true);
        exitBtn.addActionListener(e -> {
            Utils.SoundPlayer.playClick();
            exitAction.actionPerformed(e);
        });
        btnGbc.gridx = 1;
        btnGbc.insets = new Insets(5, 5, 5, 0);
        buttonsPanel.add(exitBtn, btnGbc);

        // Add vertical spacer between logo and buttons
        gbc.gridy = 1;
        gbc.insets = new Insets(40, 10, 0, 10);
        JPanel buttonSpacer = new JPanel();
        buttonSpacer.setOpaque(false);
        buttonSpacer.setPreferredSize(new Dimension(1, 60));
        centerPanel.add(buttonSpacer, gbc);

        // Add buttons panel to center panel
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 10, 0, 10);
        centerPanel.add(buttonsPanel, gbc);

        // Add vertical filler to push content up
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        centerPanel.add(Box.createGlue(), gbc);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for version and copyright information
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

    /**
     * Creates a styled button with image textures and hover effects.
     * The button uses background images for normal and hovered states, with text color
     * that changes when the mouse enters the button area. The button customizes appearance
     * and disables default look-and-feel rendering.
     *
     * @param text the text to display on the button
     * @param isShort determines which button texture to use (true for short buttons, false for long)
     * @return a styled JButton with image background and hover effects
     */
    private JButton createButton(String text, boolean isShort) {
        String texture = isShort ? GameConfig.SHORT_BUTTON_TEXTURE : GameConfig.LONG_BUTTON_TEXTURE;
        String hoverTexture = isShort ? GameConfig.HOVER_SHORT_BUTTON_TEXTURE : GameConfig.HOVER_LONG_BUTTON_TEXTURE;

        final BufferedImage btnImg = Utils.ImageLoader.getImage(GameConfig.UI_PATH_TEXTURE + texture);
        final BufferedImage hoverBtnImg = Utils.ImageLoader.getImage(GameConfig.UI_PATH_TEXTURE + hoverTexture);

        // Custom JButton with internal hover state tracking and custom painting
        JButton btn = new JButton(text) {
            private boolean isHovered = false;

            // Anonymous initializer block that adds mouse listener for hover tracking
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        isHovered = true;
                        // Change text color to hover color when mouse enters
                        setForeground(Color.decode(GameConfig.TEXT_COLOR_HOVER_HEX));
                        repaint();
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        isHovered = false;
                        // Reset text color to white when mouse leaves
                        setForeground(Color.WHITE);
                        repaint();
                    }
                });
            }

            // Override paintComponent to draw background images instead of default button appearance
            @Override
            protected void paintComponent(Graphics g) {
                // Select appropriate image based on hover state
                BufferedImage imgToDraw = isHovered && hoverBtnImg != null ? hoverBtnImg : btnImg;

                // Draw background image or fallback color if image not available
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

    /**
     * Renders the background image for the menu panel.
     * Loads the main menu background texture, scales it to 80% of original size,
     * centers it on the panel, and draws it. If the image is unavailable,
     * fills the panel with a solid background color.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage bg = Utils.ImageLoader.getImage(Utils.GameConfig.BACKGROUND_PATH + Utils.GameConfig.MAIN_MENU_BACKGROUND_TEXTURE);
        if (bg != null) {
            // Calculate scaled dimensions with 80% zoom factor
            double zoom = 0.8;
            int imgW = (int) (bg.getWidth() * zoom);
            int imgH = (int) (bg.getHeight() * zoom);
            int panelW = getWidth();
            int panelH = getHeight();
            // Calculate centered position
            int x = (panelW - imgW) / 2;
            int y = (panelH - imgH) / 2;

            g.drawImage(bg, x, y, imgW, imgH, this);
        } else {
            // Fallback: draw solid background color if image not found
            g.setColor(Color.decode(GameConfig.STATS_PANEL_BACKGROUND_COLOR));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}

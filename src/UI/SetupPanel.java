package UI;

import DataStructures.ArrayList.ArrayUnorderedList;
import Utils.GameConfig;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class SetupPanel extends JPanel {

    private final JComboBox<Integer> numPlayersCombo;
    private final JPanel namesPanel;
    private final ArrayUnorderedList<JTextField> nameFields;
    private final ArrayUnorderedList<JCheckBox> typeCheckboxes;
    private final ArrayUnorderedList<JComboBox<String>> charCombos;
    private final JButton startBtn;
    private final JButton backBtn;

    public SetupPanel(ActionListener startAction, ActionListener backAction) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Top Panel: Title and Number of Players
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Configuração do jogo");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(lblTitle);
        topPanel.add(Box.createVerticalStrut(20));

        // Modern selector panel
        JPanel numPlayersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        numPlayersPanel.setOpaque(false);
        
        JLabel lblNumPlayers = new JLabel("Número de jogadores:");
        lblNumPlayers.setForeground(Color.WHITE);
        lblNumPlayers.setFont(new Font("Arial", Font.BOLD, 16));
        
        Integer[] options = {2, 3, 4};
        numPlayersCombo = new JComboBox<>(options);
        numPlayersCombo.setSelectedItem(2);
        numPlayersCombo.setFont(new Font("Arial", Font.BOLD, 14));
        numPlayersCombo.setPreferredSize(new Dimension(80, 35));
        numPlayersCombo.setBackground(new Color(60, 60, 60));
        numPlayersCombo.setForeground(Color.WHITE);
        numPlayersCombo.setFocusable(false);
        numPlayersCombo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        numPlayersCombo.addActionListener(e -> updateNameFields());
        
        numPlayersPanel.add(lblNumPlayers);
        numPlayersPanel.add(numPlayersCombo);
        topPanel.add(numPlayersPanel);
        topPanel.add(Box.createVerticalStrut(15));
        
        add(topPanel, BorderLayout.NORTH);

        // Center Panel: Name Fields
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        namesPanel = new JPanel();
        namesPanel.setOpaque(false);
        
        nameFields = new ArrayUnorderedList<>();
        typeCheckboxes = new ArrayUnorderedList<>();
        charCombos = new ArrayUnorderedList<>();

        centerPanel.add(namesPanel);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel: Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        backBtn = createButton("Voltar");
        backBtn.addActionListener(e -> {
            Utils.SoundPlayer.playClick();
            backAction.actionPerformed(e);
        });

        startBtn = createButton("Iniciar jogo");
        startBtn.addActionListener(e -> {
            Utils.SoundPlayer.playClick();
            startAction.actionPerformed(e);
        });

        bottomPanel.add(backBtn);
        bottomPanel.add(startBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        updateNameFields();
    }

    private JButton createButton(String text) {
        final BufferedImage btnImg = Utils.ImageLoader.getImage(GameConfig.UI_PATH_TEXTURE + GameConfig.SHORT_BUTTON_TEXTURE);
        final BufferedImage hoverBtnImg = Utils.ImageLoader.getImage(GameConfig.UI_PATH_TEXTURE + GameConfig.HOVER_SHORT_BUTTON_TEXTURE);

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
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(200, 50));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void updateNameFields() {
        namesPanel.setVisible(false);
        namesPanel.removeAll();
        while (!nameFields.isEmpty()) { try { nameFields.removeLast(); } catch (Exception e) {} }
        while (!typeCheckboxes.isEmpty()) { try { typeCheckboxes.removeLast(); } catch (Exception e) {} }
        while (!charCombos.isEmpty()) { try { charCombos.removeLast(); } catch (Exception e) {} }
        
        int num = (Integer) numPlayersCombo.getSelectedItem();
        
        if (num == 2) {
            namesPanel.setLayout(new GridLayout(1, 2, 60, 20));
        } else {
            namesPanel.setLayout(new GridLayout(2, 2, 60, 25));
        }

        for (int i = 1; i <= num; i++) {
            namesPanel.add(createPlayerPanel(i));
        }

        namesPanel.setVisible(true);
        namesPanel.revalidate();
        namesPanel.repaint();
    }

    private JPanel createPlayerPanel(int playerNum) {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setOpaque(false);
        
        // Card panel with rounded corners and semi-transparent background
        JPanel cardPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Semi-transparent dark background
                g2d.setColor(new Color(30, 30, 30, 200));
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                
                // Subtle border
                g2d.setColor(new Color(80, 80, 80, 150));
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(new RoundRectangle2D.Double(1, 1, getWidth()-2, getHeight()-2, 20, 20));
                
                g2d.dispose();
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setBorder(new EmptyBorder(18, 18, 18, 18));
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Player number header
        JLabel playerLabel = new JLabel("Jogador " + playerNum);
        playerLabel.setForeground(new Color(255, 215, 0));
        playerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(playerLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Name Label
        JLabel nameLabel = new JLabel("Nome:");
        nameLabel.setForeground(new Color(200, 200, 200));
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        contentPanel.add(nameLabel, gbc);
        
        // Name Field with modern style
        JTextField nameField = new JTextField("Jogador " + playerNum) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(50, 50, 50));
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                
                super.paintComponent(g);
                g2d.dispose();
            }
        };
        nameField.setPreferredSize(new Dimension(160, 30));
        nameField.setFont(new Font("Arial", Font.PLAIN, 13));
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(Color.WHITE);
        nameField.setBackground(new Color(50, 50, 50));
        nameField.setBorder(new EmptyBorder(5, 10, 5, 10));
        nameField.setOpaque(false);
        nameFields.addToRear(nameField);
        gbc.gridx = 1; gbc.gridy = 1;
        contentPanel.add(nameField, gbc);
        
        // Skin Label
        JLabel skinLabel = new JLabel("Skin:");
        skinLabel.setForeground(new Color(200, 200, 200));
        skinLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 2;
        contentPanel.add(skinLabel, gbc);
        
        // Skin Combo with modern style
        String[] chars = {"Steve", "Alex", "Villager", "Enderman", "Zombie", "Skeleton", "Creeper", "Dragon", "Spider", "Slime", "Wither"};
        JComboBox<String> skinCombo = new JComboBox<>(chars);
        skinCombo.setPreferredSize(new Dimension(160, 30));
        skinCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        skinCombo.setBackground(new Color(50, 50, 50));
        skinCombo.setForeground(Color.WHITE);
        skinCombo.setFocusable(false);
        skinCombo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        charCombos.addToRear(skinCombo);
        gbc.gridx = 1; gbc.gridy = 2;
        contentPanel.add(skinCombo, gbc);
        
        // AI Checkbox with modern style
        JCheckBox aiCheck = new JCheckBox("Jogador IA") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (isSelected()) {
                    g2d.setColor(new Color(255, 215, 0, 50));
                    g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
                }
                
                super.paintComponent(g);
                g2d.dispose();
            }
        };
        aiCheck.setOpaque(false);
        aiCheck.setForeground(new Color(200, 200, 200));
        aiCheck.setFont(new Font("Arial", Font.PLAIN, 13));
        aiCheck.setFocusPainted(false);
        aiCheck.setCursor(new Cursor(Cursor.HAND_CURSOR));
        typeCheckboxes.addToRear(aiCheck);
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(aiCheck, gbc);
        
        cardPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Image Preview with modern frame
        JPanel imagePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        imagePanel.setOpaque(false);
        imagePanel.setPreferredSize(new Dimension(74, 74));
        
        JLabel imageLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Draw border exactly around the image
                if (getIcon() != null) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    int imgSize = 64;
                    int x = (getWidth() - imgSize) / 2;
                    int y = (getHeight() - imgSize) / 2;
                    
                    // Glow effect
                    g2d.setColor(new Color(255, 215, 0, 30));
                    g2d.fillRect(x - 3, y - 3, imgSize + 6, imgSize + 6);
                    
                    // Border exactly around the image
                    g2d.setColor(new Color(255, 215, 0));
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRect(x - 1, y - 1, imgSize + 2, imgSize + 2);
                    
                    g2d.dispose();
                }
            }
        };
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        skinCombo.addActionListener(e -> updatePlayerImage(imageLabel, (String) skinCombo.getSelectedItem()));
        updatePlayerImage(imageLabel, (String) skinCombo.getSelectedItem());
        
        cardPanel.add(imagePanel, BorderLayout.EAST);
        
        outerPanel.add(cardPanel, BorderLayout.CENTER);
        
        return outerPanel;
    }

    private void updatePlayerImage(JLabel label, String skinName) {
        String fileName = switch (skinName) {
            case "Steve" -> GameConfig.HEAD_STEVE;
            case "Alex" -> GameConfig.HEAD_ALEX;
            case "Villager" -> GameConfig.HEAD_VILLAGER;
            case "Enderman" -> GameConfig.HEAD_ENDERMAN;
            case "Zombie" -> GameConfig.HEAD_ZOMBIE;
            case "Skeleton" -> GameConfig.HEAD_SKELETON;
            case "Creeper" -> GameConfig.HEAD_CREEPER;
            case "Dragon" -> GameConfig.HEAD_DRAGON;
            case "Spider" -> GameConfig.HEAD_SPIDER;
            case "Slime" -> GameConfig.HEAD_SLIME;
            case "Wither" -> GameConfig.HEAD_WITHER;
            default -> GameConfig.HEAD_STEVE;
        };
        
        BufferedImage img = Utils.ImageLoader.getImage(GameConfig.SKINS_PATH + fileName);
        if (img != null) {
            Image scaled = img.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaled));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage bg = Utils.ImageLoader.getImage(Utils.GameConfig.BACKGROUND_PATH + Utils.GameConfig.SETUP_BACKGROUND_TEXTURE);
        if (bg != null) {
            int imgW = bg.getWidth();
            int imgH = bg.getHeight();
            int panelW = getWidth();
            int panelH = getHeight();
            int x = 0;
            int y = 0;

            // Center the image
            if (imgW > panelW) {
                x = -(imgW - panelW) / 2;
            } else if (imgW < panelW) {
                x = (panelW - imgW) / 2;
            }
            if (imgH > panelH) {
                y = -(imgH - panelH) / 2;
            } else if (imgH < panelH) {
                y = (panelH - imgH) / 2;
            }
            g.drawImage(bg, x, y, imgW, imgH, this);
        } else {
            g.setColor(new Color(40, 40, 40));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public ArrayUnorderedList<String> getPlayerNames() {
        ArrayUnorderedList<String> names = new ArrayUnorderedList<>();
        DataStructures.Iterator<JTextField> it = nameFields.iterator();
        while (it.hasNext()) {
            names.addToRear(it.next().getText());
        }
        return names;
    }

    public ArrayUnorderedList<Boolean> getPlayerTypes() {
        ArrayUnorderedList<Boolean> types = new ArrayUnorderedList<>();
        DataStructures.Iterator<JCheckBox> it = typeCheckboxes.iterator();
        while (it.hasNext()) {
            types.addToRear(it.next().isSelected());
        }
        return types;
    }

    public ArrayUnorderedList<String> getPlayerCharacters() {
        ArrayUnorderedList<String> chars = new ArrayUnorderedList<>();
        DataStructures.Iterator<JComboBox<String>> it = charCombos.iterator();
        while (it.hasNext()) {
            chars.addToRear(it.next().getSelectedItem().toString());
        }
        return chars;
    }
}
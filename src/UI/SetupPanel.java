package UI;

import DataStructures.ArrayList.ArrayUnorderedList;
import Utils.GameConfig;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.*;

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
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top Panel: Title and Number of Players
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Configuração do Jogo");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(lblTitle);
        topPanel.add(Box.createVerticalStrut(10));

        JPanel numPlayersPanel = new JPanel(new FlowLayout());
        numPlayersPanel.setOpaque(false);
        JLabel lblNumPlayers = new JLabel("Número de Jogadores:");
        lblNumPlayers.setForeground(Color.WHITE);
        lblNumPlayers.setFont(new Font("Arial", Font.BOLD, 16));
        Integer[] options = {2, 3, 4};
        numPlayersCombo = new JComboBox<>(options);
        numPlayersCombo.setSelectedItem(2);
        numPlayersCombo.addActionListener(e -> updateNameFields());
        numPlayersPanel.add(lblNumPlayers);
        numPlayersPanel.add(numPlayersCombo);
        numPlayersPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(numPlayersPanel);
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
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setOpaque(false);

        backBtn = createButton("Voltar");
        backBtn.addActionListener(e -> {
            Utils.SoundPlayer.playClick();
            backAction.actionPerformed(e);
        });

        startBtn = createButton("Iniciar Jogo");
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
        btn.setFont(new Font("Arial", Font.BOLD, 18));
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
            namesPanel.setLayout(new GridLayout(1, 2, 40, 20));
        } else {
            namesPanel.setLayout(new GridLayout(2, 2, 40, 20));
        }

        for (int i = 1; i <= num; i++) {
            namesPanel.add(createPlayerPanel(i));
        }

        namesPanel.setVisible(true);
        namesPanel.revalidate();
        namesPanel.repaint();
    }

    private JPanel createPlayerPanel(int playerNum) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Name Label
        JLabel nameLabel = new JLabel("Nome:");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(nameLabel, gbc);
        
        // Name Field
        JTextField nameField = new JTextField("Jogador " + playerNum);
        nameField.setPreferredSize(new Dimension(120, 25));
        nameFields.addToRear(nameField);
        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(nameField, gbc);
        
        // Skin Label
        JLabel skinLabel = new JLabel("Skin:");
        skinLabel.setForeground(Color.WHITE);
        skinLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(skinLabel, gbc);
        
        // Skin Combo
        String[] chars = {"Steve", "Alex", "Villager", "Enderman", "Zombie", "Skeleton", "Creeper", "Dragon", "Spider", "Slime", "Wither"};
        JComboBox<String> skinCombo = new JComboBox<>(chars);
        skinCombo.setPreferredSize(new Dimension(120, 25));
        charCombos.addToRear(skinCombo);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(skinCombo, gbc);
        
        // AI Checkbox
        JCheckBox aiCheck = new JCheckBox("AI");
        aiCheck.setOpaque(false);
        aiCheck.setForeground(Color.WHITE);
        aiCheck.setFont(new Font("Arial", Font.BOLD, 14));
        typeCheckboxes.addToRear(aiCheck);
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(aiCheck, gbc);
        
        // Image Preview
        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(70, 70));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        
        skinCombo.addActionListener(e -> updatePlayerImage(imageLabel, (String) skinCombo.getSelectedItem()));
        updatePlayerImage(imageLabel, (String) skinCombo.getSelectedItem());
        
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 15, 5, 5);
        panel.add(imageLabel, gbc);
        
        return panel;
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

package UI;

import DataStructures.ArrayList.ArrayUnorderedList;
import DataStructures.Iterator;
import Models.Player;
import Utils.GameConfig;
import Utils.ImageLoader;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class StatsPanel extends JPanel {

    private ArrayUnorderedList<Player> players;
    private final DataStructures.ArrayList.ArrayUnorderedList<PlayerIconPair> playerIcons = new DataStructures.ArrayList.ArrayUnorderedList<>();
    private final BufferedImage iconPickaxe;
    private final BufferedImage iconEnderPearl;

    private static class PlayerIconPair {
        String name;
        BufferedImage icon;
        public PlayerIconPair(String n, BufferedImage i) {
            this.name = n;
            this.icon = i;
        }
    }

    private final double scale;

    public StatsPanel() {
        this.scale = (double) GameConfig.ROOM_SIZE / 60;

        int hGap = (int) (20 * scale);
        setLayout(new FlowLayout(FlowLayout.CENTER, hGap, 5));
        setBackground(Color.decode(GameConfig.STATS_PANEL_BACKGROUND_COLOR));
        setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.GRAY));

        // Load Player Icons
        loadPlayerIcon("STEVE", GameConfig.HEAD_STEVE);
        loadPlayerIcon("ALEX", GameConfig.HEAD_ALEX);
        loadPlayerIcon("VILLAGER", GameConfig.HEAD_VILLAGER);
        loadPlayerIcon("ENDERMAN", GameConfig.HEAD_ENDERMAN);
        loadPlayerIcon("ZOMBIE", GameConfig.HEAD_ZOMBIE);
        loadPlayerIcon("SKELETON", GameConfig.HEAD_SKELETON);
        loadPlayerIcon("CREEPER", GameConfig.HEAD_CREEPER);
        loadPlayerIcon("DRAGON", GameConfig.HEAD_DRAGON);
        loadPlayerIcon("SPIDER", GameConfig.HEAD_SPIDER);
        loadPlayerIcon("SLIME", GameConfig.HEAD_SLIME);
        loadPlayerIcon("WITHER", GameConfig.HEAD_WITHER);

        // Load Item Icons
        iconPickaxe = ImageLoader.getImage(GameConfig.ITENS_PATH + GameConfig.PICKAXE_TEXTURE);
        iconEnderPearl = ImageLoader.getImage(GameConfig.ITENS_PATH + GameConfig.ENDERPEARL_TEXTURE);
    }

    private void loadPlayerIcon(String name, String fileName) {
        playerIcons.addToRear(new PlayerIconPair(name, ImageLoader.getImage(GameConfig.SKINS_PATH + fileName)));
    }

    private BufferedImage getPlayerIcon(String characterType) {
        String key = characterType != null ? characterType.toUpperCase() : "STEVE";
        BufferedImage icon = findIcon(key);
        if (icon == null) {
            icon = findIcon("STEVE");
        }
        return icon;
    }

    private BufferedImage findIcon(String name) {
        DataStructures.Iterator<PlayerIconPair> it = playerIcons.iterator();
        while (it.hasNext()) {
            PlayerIconPair pair = it.next();
            if (pair.name.equals(name)) {
                return pair.icon;
            }
        }
        return null;
    }

    public void updateStats(ArrayUnorderedList<Player> players, Player currentPlayer) {
        this.players = players;
        removeAll();

        // Ensure layout gap is correct
        int hGap = (int) (20 * scale);
        ((FlowLayout) getLayout()).setHgap(hGap);

        Iterator<Player> it = players.iterator();
        while (it.hasNext()) {
            Player p = it.next();
            JPanel pPanel = new JPanel();
            pPanel.setLayout(new BoxLayout(pPanel, BoxLayout.X_AXIS));
            pPanel.setOpaque(false);

            if (p.equals(currentPlayer)) {
                pPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(p.getColor(), 3),
                        BorderFactory.createEmptyBorder((int) (5 * scale), (int) (5 * scale), (int) (5 * scale), (int) (5 * scale))
                ));
                pPanel.setBackground(new Color(60, 60, 60));
                pPanel.setOpaque(true);
            } else {
                pPanel.setBorder(BorderFactory.createEmptyBorder((int) (8 * scale), (int) (8 * scale), (int) (8 * scale), (int) (8 * scale)));
            }

            // Center: Stats
            JPanel statsPanel = new JPanel();
            statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
            statsPanel.setOpaque(false);
            statsPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

            BufferedImage icon = getPlayerIcon(p.getCharacterType());

            if (icon != null) {
                int iconSize = (int) (50 * scale);
                Image scaledIcon = icon.getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
                JLabel iconLbl = new JLabel(new ImageIcon(scaledIcon));
                iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                statsPanel.add(iconLbl);
                statsPanel.add(Box.createVerticalStrut((int) (10 * scale)));
            }

            JLabel nameLbl = new JLabel(p.getName());
            nameLbl.setForeground(p.getColor());
            nameLbl.setFont(new Font("Arial", Font.BOLD, Math.max(12, (int) (18 * scale))));
            nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel movesLbl = new JLabel("Movimentos: " + p.getMoves());
            movesLbl.setForeground(Color.WHITE);
            movesLbl.setFont(new Font("Arial", Font.PLAIN, Math.max(11, (int) (16 * scale))));
            movesLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

            statsPanel.add(nameLbl);
            statsPanel.add(Box.createVerticalStrut((int) (2 * scale)));
            statsPanel.add(movesLbl);

            // Inventory (Horizontal below moves)
            JPanel inventoryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, (int)(5 * scale), 0));
            inventoryPanel.setOpaque(false);
            inventoryPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            Iterator<Models.Item> invIt = p.getInventory().iterator();
            int slot = 1;
            while (invIt.hasNext()) {
                Models.Item item = invIt.next();
                JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                itemPanel.setOpaque(false);

                // Slot number
                JLabel slotLbl = new JLabel(slot + ": ");
                slotLbl.setForeground(Color.LIGHT_GRAY);
                slotLbl.setFont(new Font("Arial", Font.PLAIN, Math.max(10, (int) (14 * scale))));
                itemPanel.add(slotLbl);

                switch (item.getName()) {
                    case "Pickaxe" -> {
                        if (iconPickaxe != null) {
                            int pickaxeSize = (int) (20 * scale);
                            Image scaledPickaxe = iconPickaxe.getScaledInstance(pickaxeSize, pickaxeSize, Image.SCALE_SMOOTH);
                            JLabel pickaxeIconLbl = new JLabel(new ImageIcon(scaledPickaxe));
                            itemPanel.add(pickaxeIconLbl);
                        } else {
                            JLabel itemNameLbl = new JLabel("Pickaxe");
                            itemNameLbl.setForeground(Color.CYAN);
                            itemPanel.add(itemNameLbl);
                        }
                    }
                    case "Ender Pearl" -> {
                        if (iconEnderPearl != null) {
                            int pearlSize = (int) (20 * scale);
                            Image scaledPearl = iconEnderPearl.getScaledInstance(pearlSize, pearlSize, Image.SCALE_SMOOTH);
                            JLabel pearlIconLbl = new JLabel(new ImageIcon(scaledPearl));
                            itemPanel.add(pearlIconLbl);
                        } else {
                            JLabel itemNameLbl = new JLabel("Ender Pearl");
                            itemNameLbl.setForeground(Color.MAGENTA);
                            itemPanel.add(itemNameLbl);
                        }
                    }
                    default -> {
                        JLabel itemNameLbl = new JLabel(item.getName());
                        itemNameLbl.setForeground(Color.WHITE);
                        itemPanel.add(itemNameLbl);
                    }
                }

                inventoryPanel.add(itemPanel);
                slot++;
            }

                // Espaço reservado para inventário horizontal
                int minInventoryHeight = (int) (30 * scale);
                inventoryPanel.setPreferredSize(new Dimension((int)(200 * scale), minInventoryHeight));
                inventoryPanel.setMinimumSize(new Dimension((int)(200 * scale), minInventoryHeight));
                statsPanel.add(Box.createVerticalStrut((int) (5 * scale)));
                statsPanel.add(inventoryPanel);

            pPanel.add(statsPanel);

            add(pPanel);
        }

        revalidate();
        repaint();
    }
}

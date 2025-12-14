package UI;

import DataStructures.ArrayList.ArrayUnorderedList;
import DataStructures.Iterator;
import Models.Player;
import Utils.GameConfig;
import Utils.ImageLoader;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

/**
 * StatsPanel is a custom JPanel that displays player statistics and inventory
 * during gameplay. It shows player names, character icons, remaining moves,
 * and inventory items for all players in the game.
 *
 */
public class StatsPanel extends JPanel {

    private ArrayUnorderedList<Player> players;
    private final DataStructures.ArrayList.ArrayUnorderedList<PlayerIconPair> playerIcons = new DataStructures.ArrayList.ArrayUnorderedList<>();
    private final BufferedImage iconPickaxe;
    private final BufferedImage iconEnderPearl;

    /**
     * PlayerIconPair represents an association between a character name
     * and its corresponding icon image.
     *
     */
    private static class PlayerIconPair {
        String name;
        BufferedImage icon;
        
        /**
         * Creates a new PlayerIconPair with the specified name and icon.
         *
         * @param n the name of the player character
         * @param i the BufferedImage icon for the character
         */
        public PlayerIconPair(String n, BufferedImage i) {
            this.name = n;
            this.icon = i;
        }
    }

    private final double scale;

    /**
     * Creates a new StatsPanel and initializes all player character icons
     * and item icons. Sets up the layout and styling based on game configuration.
     *
     */
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

    /**
     * Loads a player character icon by name and filename.
     * Adds the icon to the playerIcons collection for later retrieval.
     *
     * @param name the character name (e.g., "STEVE", "ALEX")
     * @param fileName the filename of the icon image
     */
    private void loadPlayerIcon(String name, String fileName) {
        playerIcons.addToRear(new PlayerIconPair(name, ImageLoader.getImage(GameConfig.SKINS_PATH + fileName)));
    }

    /**
     * Retrieves the icon image for a given player character type.
     * Defaults to STEVE if the character type is not found or null.
     *
     * @param characterType the type of character to retrieve the icon for
     * @return the BufferedImage icon for the character type
     */
    private BufferedImage getPlayerIcon(String characterType) {
        String key = characterType != null ? characterType.toUpperCase() : "STEVE";
        BufferedImage icon = findIcon(key);
        if (icon == null) {
            icon = findIcon("STEVE");
        }
        return icon;
    }

    /**
     * Searches the playerIcons collection for an icon matching the given name.
     * Iterates through all PlayerIconPair objects to find a match.
     *
     * @param name the character name to search for
     * @return the BufferedImage icon if found, null otherwise
     */
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

    /**
     * Updates the statistics panel with information from all players.
     * Reconstructs the panel display showing current player stats, icons, 
     * remaining moves, and inventory items. Highlights the current player
     * with an orange border and darker background.
     *
     * @param players the list of all players in the game
     * @param currentPlayer the player whose turn it is
     */
    public void updateStats(ArrayUnorderedList<Player> players, Player currentPlayer) {
        this.players = players;
        removeAll();

        // Ensure layout gap is correct
        int hGap = (int) (20 * scale);
        ((FlowLayout) getLayout()).setHgap(hGap);

        // Iterate through all players and create visual representation for each
        Iterator<Player> it = players.iterator();
        while (it.hasNext()) {
            Player p = it.next();
            JPanel pPanel = new JPanel();
            pPanel.setLayout(new BoxLayout(pPanel, BoxLayout.X_AXIS));
            pPanel.setOpaque(false);

            // Highlight current player with orange border and darker background
            if (p.equals(currentPlayer)) {
                pPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.ORANGE, 3), // Cor fixa para destacar o jogador atual
                        BorderFactory.createEmptyBorder((int) (5 * scale), (int) (5 * scale), (int) (5 * scale), (int) (5 * scale))
                ));
                pPanel.setBackground(new Color(60, 60, 60));
                pPanel.setOpaque(true);
            } else {
                pPanel.setBorder(BorderFactory.createEmptyBorder((int) (8 * scale), (int) (8 * scale), (int) (8 * scale), (int) (8 * scale)));
            }

            // Create center stats panel with player icon, name, and moves
            JPanel statsPanel = new JPanel();
            statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
            statsPanel.setOpaque(false);
            statsPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

            // Load and scale player character icon
            BufferedImage icon = getPlayerIcon(p.getCharacterType());

            if (icon != null) {
                int iconSize = (int) (50 * scale);
                Image scaledIcon = icon.getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
                JLabel iconLbl = new JLabel(new ImageIcon(scaledIcon));
                iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                statsPanel.add(iconLbl);
                statsPanel.add(Box.createVerticalStrut((int) (10 * scale)));
            }

            // Create and format player name label
            JLabel nameLbl = new JLabel(p.getName());
            nameLbl.setForeground(Color.WHITE); // Cor padrão
            nameLbl.setFont(new Font("Arial", Font.BOLD, Math.max(12, (int) (18 * scale))));
            nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Create and format remaining moves label
            JLabel movesLbl = new JLabel("Movimentos: " + p.getMoves());
            movesLbl.setForeground(Color.WHITE);
            movesLbl.setFont(new Font("Arial", Font.PLAIN, Math.max(11, (int) (16 * scale))));
            movesLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

            statsPanel.add(nameLbl);
            statsPanel.add(Box.createVerticalStrut((int) (2 * scale)));
            statsPanel.add(movesLbl);

            // Create inventory panel to display items in player's inventory
            JPanel inventoryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, (int)(5 * scale), 0));
            inventoryPanel.setOpaque(false);
            inventoryPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Iterate through inventory and display each item with icon or name
            Iterator<Models.Item> invIt = p.getInventory().iterator();
            int slot = 1;
            while (invIt.hasNext()) {
                Models.Item item = invIt.next();
                JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                itemPanel.setOpaque(false);

                // Display slot number
                JLabel slotLbl = new JLabel(slot + ": ");
                slotLbl.setForeground(Color.LIGHT_GRAY);
                slotLbl.setFont(new Font("Arial", Font.PLAIN, Math.max(10, (int) (14 * scale))));
                itemPanel.add(slotLbl);

                // Display item with appropriate icon or text representation
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

                // Set fixed dimensions for inventory panel to maintain consistent layout
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

package UI;

import DataStructures.ArrayList.ArrayUnorderedList;
import DataStructures.Iterator;
import GameEngine.GameMapGenerator;
import Models.Player;
import Models.Report;
import Models.Report.PlayerPath;
import Models.Report.ReportData;
import Utils.GameConfig;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ReportDialog extends JDialog {

    // Dados do relatório
    private GameMapGenerator gameMap;
    private ArrayUnorderedList<PlayerPath> playerPaths;

    public ReportDialog(JFrame parent, ArrayUnorderedList<Player> players, Player winner, String reportFile) {
        super(parent, "Relatório Final", true);
        setSize(GameConfig.REPORT_WINDOW_WIDTH, GameConfig.REPORT_WINDOW_HEIGHT);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.decode(GameConfig.MAIN_WINDOW_BACKGROUND_COLOR));
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setUndecorated(false);
        setFocusableWindowState(true);
        setType(Type.UTILITY);

        // Carregar dados do relatório (Mapa e Caminhos)
        loadReportData(reportFile);

        // Header com vencedor
        JPanel headerPanel = createHeaderPanel(winner);
        add(headerPanel, BorderLayout.NORTH);

        // Lista de jogadores com scroll
        JPanel playersPanel = createContentPanel(players);
        JScrollPane scrollPane = new JScrollPane(playersPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(Color.decode(GameConfig.MAIN_WINDOW_BACKGROUND_COLOR));
        scrollPane.getViewport().setBackground(Color.decode(GameConfig.MAIN_WINDOW_BACKGROUND_COLOR));
        add(scrollPane, BorderLayout.CENTER);

        // Botão de fechar
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void loadReportData(String reportFile) {
        ReportData data = Report.loadReport(reportFile);
        if (data != null) {
            this.gameMap = data.gameMap;
            this.playerPaths = data.playerPaths;
        } else {
            this.playerPaths = new ArrayUnorderedList<>();
        }
    }

    private JPanel createHeaderPanel(Player winner) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.decode(GameConfig.MAIN_WINDOW_BACKGROUND_COLOR));
        panel.setBorder(new EmptyBorder(25, 30, 20, 30));

        JLabel winnerLabel = new JLabel("VENCEDOR");
        winnerLabel.setForeground(Color.decode(GameConfig.REPORT_TEXT_SECONDARY_COLOR_HEX));
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(winnerLabel);

        panel.add(Box.createVerticalStrut(5));

        JLabel winnerName = new JLabel(winner.getName());
        winnerName.setForeground(Color.decode(GameConfig.REPORT_ACCENT_COLOR_HEX));
        winnerName.setFont(new Font("Arial", Font.BOLD, 32));
        winnerName.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(winnerName);

        return panel;
    }

    private JPanel createContentPanel(ArrayUnorderedList<Player> players) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.decode(GameConfig.MAIN_WINDOW_BACKGROUND_COLOR));
        mainPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        // Adicionar cartões de estatísticas dos jogadores
        Iterator<Player> it = players.iterator();
        while (it.hasNext()) {
            Player p = it.next();
            mainPanel.add(createPlayerCard(p));
            mainPanel.add(Box.createVerticalStrut(15));
        }

        // Adicionar visualização do mapa se disponível
        if (gameMap != null) {
            mainPanel.add(Box.createVerticalStrut(20));
            mainPanel.add(createMapSection());
            mainPanel.add(Box.createVerticalStrut(20));
        }

        return mainPanel;
    }

    private JPanel createMapSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.decode(GameConfig.MAIN_WINDOW_BACKGROUND_COLOR));

        JLabel mapLabel = new JLabel("TRAJETOS PERCORRIDOS");
        mapLabel.setForeground(Color.decode(GameConfig.REPORT_TEXT_SECONDARY_COLOR_HEX));
        mapLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mapLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(mapLabel);

        panel.add(Box.createVerticalStrut(10));

        VisualizationPanel visPanel = new VisualizationPanel();
        visPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(visPanel);

        return panel;
    }

    private JPanel createPlayerCard(Player player) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(15, 15));
        card.setBackground(Color.decode(GameConfig.STATS_PANEL_BACKGROUND_COLOR));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 70), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Nome do jogador no topo
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        namePanel.setBackground(Color.decode(GameConfig.STATS_PANEL_BACKGROUND_COLOR));
        JLabel nameLabel = new JLabel(player.getName());
        nameLabel.setForeground(Color.decode(GameConfig.REPORT_TEXT_PRIMARY_COLOR_HEX));
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        namePanel.add(nameLabel);
        card.add(namePanel, BorderLayout.NORTH);

        // Painel central com as categorias
        JPanel statsContainer = new JPanel();
        statsContainer.setLayout(new BoxLayout(statsContainer, BoxLayout.Y_AXIS));
        statsContainer.setBackground(Color.decode(GameConfig.STATS_PANEL_BACKGROUND_COLOR));

        // Categoria 1: Movimentos
        statsContainer.add(createCategoryPanel("Movimentos", 2,
                new StatItem(null, "Movimentos", String.valueOf(player.getTotalMoves())),
                new StatItem(null, "Interações com alavancas", String.valueOf(player.getLeverInteractions()))
        ));

        statsContainer.add(Box.createVerticalStrut(15));

        // Categoria 2: Perguntas
        statsContainer.add(createCategoryPanel("Perguntas", 2,
                new StatItem(null, "Corretas", String.valueOf(player.getQuestionsCorrect())),
                new StatItem(null, "Incorretas", String.valueOf(player.getQuestionsIncorrect()))
        ));

        statsContainer.add(Box.createVerticalStrut(15));

        // Container horizontal para Itens
        JPanel itemsContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        itemsContainer.setBackground(Color.decode(GameConfig.STATS_PANEL_BACKGROUND_COLOR));
        itemsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Categoria 3: Itens Coletados
        itemsContainer.add(createCategoryPanel("Itens Coletados", 1,
                new StatItem(null, "Total Coletados", String.valueOf(player.getItemsCollected())),
                new StatItem(GameConfig.PICKAXE_TEXTURE, "Picaretas", String.valueOf(player.getPickaxesCollected())),
                new StatItem(GameConfig.ENDERPEARL_TEXTURE, "Pérolas do Fim", String.valueOf(player.getEnderPearlsCollected()))
        ));

        // Categoria 4: Itens Usados
        itemsContainer.add(createCategoryPanel("Itens Usados", 1,
                new StatItem(null, "Total Usados", String.valueOf(player.getItemsUsed())),
                new StatItem(GameConfig.PICKAXE_TEXTURE, "Picaretas", String.valueOf(player.getPickaxesUsed())),
                new StatItem(GameConfig.ENDERPEARL_TEXTURE, "Pérolas do Fim", String.valueOf(player.getEnderPearlsUsed()))
        ));

        statsContainer.add(itemsContainer);
        card.add(statsContainer, BorderLayout.CENTER);

        return card;
    }

    private JPanel createCategoryPanel(String categoryName, int columns, StatItem... stats) {
        JPanel categoryPanel = new JPanel();
        categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.Y_AXIS));
        categoryPanel.setBackground(Color.decode(GameConfig.STATS_PANEL_BACKGROUND_COLOR));

        // Título da categoria
        JLabel categoryLabel = new JLabel(categoryName);
        categoryLabel.setForeground(Color.decode(GameConfig.REPORT_ACCENT_COLOR_HEX));
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        categoryLabel.setBorder(new EmptyBorder(0, 0, 8, 0));
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        categoryPanel.add(categoryLabel);

        // Grid de estatísticas
        JPanel statsGrid = new JPanel(new GridLayout(0, columns, 15, 8));
        statsGrid.setBackground(Color.decode(GameConfig.STATS_PANEL_BACKGROUND_COLOR));
        statsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (StatItem stat : stats) {
            addStatRow(statsGrid, stat);
        }

        categoryPanel.add(statsGrid);

        return categoryPanel;
    }

    private void addStatRow(JPanel panel, StatItem stat) {
        JPanel statPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        statPanel.setBackground(Color.decode(GameConfig.STATS_PANEL_BACKGROUND_COLOR));

        // Adicionar ícone
        if (stat.texture != null) {
            java.awt.image.BufferedImage img = Utils.ImageLoader.getImage(GameConfig.ITENS_PATH + stat.texture);
            if (img != null) {
                ImageIcon icon = new ImageIcon(img.getScaledInstance(16, 16, Image.SCALE_SMOOTH));
                JLabel iconLabel = new JLabel(icon);
                statPanel.add(iconLabel);
            }
        }

        JLabel labelComp = new JLabel(stat.label + ":");
        labelComp.setForeground(Color.decode(GameConfig.REPORT_TEXT_SECONDARY_COLOR_HEX));
        labelComp.setFont(new Font("Arial", Font.PLAIN, 13));

        JLabel valueComp = new JLabel(stat.value);
        valueComp.setForeground(Color.decode(GameConfig.REPORT_TEXT_PRIMARY_COLOR_HEX));
        valueComp.setFont(new Font("Arial", Font.BOLD, 13));

        statPanel.add(labelComp);
        statPanel.add(valueComp);

        panel.add(statPanel);
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.decode(GameConfig.MAIN_WINDOW_BACKGROUND_COLOR));
        panel.setBorder(new EmptyBorder(15, 20, 20, 20));

        JButton closeButton = createButton("Fechar");
        closeButton.addActionListener(e -> dispose());

        panel.add(closeButton);

        return panel;
    }

    private JButton createButton(String text) {
        final java.awt.image.BufferedImage btnImg = Utils.ImageLoader.getImage(GameConfig.UI_PATH_TEXTURE + GameConfig.SHORT_BUTTON_TEXTURE);
        final java.awt.image.BufferedImage hoverBtnImg = Utils.ImageLoader.getImage(GameConfig.UI_PATH_TEXTURE + GameConfig.HOVER_SHORT_BUTTON_TEXTURE);

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
                java.awt.image.BufferedImage imgToDraw = isHovered && hoverBtnImg != null ? hoverBtnImg : btnImg;

                if (imgToDraw != null) {
                    g.drawImage(imgToDraw, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(Color.decode(GameConfig.REPORT_ACCENT_COLOR_HEX));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
                super.paintComponent(g);
            }
        };

        btn.setPreferredSize(new Dimension(120, 40));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    private class VisualizationPanel extends JPanel {

        private final int ROOM_SIZE;
        private final BufferedImage floorTexture;

        public VisualizationPanel() {
            int availableWidth = 692;
            int mapWidth = gameMap != null ? gameMap.getWidth() : 21;
            this.ROOM_SIZE = availableWidth / mapWidth;

            setBackground(Color.BLACK);
            if (gameMap != null) {
                setPreferredSize(new Dimension(gameMap.getWidth() * ROOM_SIZE, gameMap.getHeight() * ROOM_SIZE));
            }
            floorTexture = Utils.ImageLoader.getImage(GameConfig.TEXTURES_PATH + GameConfig.FLOOR_TEXTURE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (gameMap == null) {
                return;
            }

            Graphics2D g2d = (Graphics2D) g;

            // Desenhar salas
            for (int x = 0; x < gameMap.getWidth(); x++) {
                for (int y = 0; y < gameMap.getHeight(); y++) {
                    Models.Room room = gameMap.getRoom(x, y);
                    drawRoom(g2d, room, x, y);
                }
            }

            // Desenhar caminhos dos jogadores
            drawPlayerPaths(g2d);
        }

        private void drawPlayerPaths(Graphics2D g2d) {
            g2d.setStroke(new BasicStroke(Math.max(2, GameConfig.PATH_LINE_THICKNESS / 2)));
            Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN, Color.MAGENTA};
            int colorIndex = 0;

            Iterator<PlayerPath> it = playerPaths.iterator();
            while (it.hasNext()) {
                PlayerPath pp = it.next();
                g2d.setColor(colors[colorIndex % colors.length]);

                Point prev = null;
                Iterator<Point> pathIt = pp.path.iterator();
                while (pathIt.hasNext()) {
                    Point p = pathIt.next();
                    if (prev != null) {
                        int x1 = prev.x * ROOM_SIZE + ROOM_SIZE / 2;
                        int y1 = prev.y * ROOM_SIZE + ROOM_SIZE / 2;
                        int x2 = p.x * ROOM_SIZE + ROOM_SIZE / 2;
                        int y2 = p.y * ROOM_SIZE + ROOM_SIZE / 2;
                        g2d.drawLine(x1, y1, x2, y2);
                    }
                    prev = p;
                }
                colorIndex++;
            }
        }

        private void drawRoom(Graphics2D g, Models.Room room, int x, int y) {
            int px = x * ROOM_SIZE;
            int py = y * ROOM_SIZE;

            if (floorTexture != null) {
                g.drawImage(floorTexture, px, py, ROOM_SIZE, ROOM_SIZE, null);
            } else {
                g.setColor(Color.DARK_GRAY);
                g.fillRect(px, py, ROOM_SIZE, ROOM_SIZE);
            }

            g.setColor(Color.decode(GameConfig.WALL_COLOR_HEX));
            int thickness = Math.max(1, GameConfig.WALL_THICKNESS / 2);

            if (!hasConnection(room, "North")) {
                g.fillRect(px, py, ROOM_SIZE, thickness);
            }
            if (!hasConnection(room, "South")) {
                g.fillRect(px, py + ROOM_SIZE - thickness, ROOM_SIZE, thickness);
            }
            if (!hasConnection(room, "West")) {
                g.fillRect(px, py, thickness, ROOM_SIZE);
            }
            if (!hasConnection(room, "East")) {
                g.fillRect(px + ROOM_SIZE - thickness, py, thickness, ROOM_SIZE);
            }
        }

        private boolean hasConnection(Models.Room room, String direction) {
            int targetX = room.getX();
            int targetY = room.getY();

            switch (direction) {
                case "North" ->
                    targetY--;
                case "South" ->
                    targetY++;
                case "West" ->
                    targetX--;
                case "East" ->
                    targetX++;
            }

            if (targetX < 0 || targetX >= gameMap.getWidth() || targetY < 0 || targetY >= gameMap.getHeight()) {
                return false;
            }

            Iterator<Models.Connection> it = room.getConnections().iterator();
            while (it.hasNext()) {
                Models.Connection c = it.next();
                Models.Room to = c.getTo();
                if (to.getX() == targetX && to.getY() == targetY) {
                    return true;
                }
            }
            return false;
        }
    }

    // Classe auxiliar para representar uma estatística
    private static class StatItem {

        String texture;
        String label;
        String value;

        StatItem(String texture, String label, String value) {
            this.texture = texture;
            this.label = label;
            this.value = value;
        }
    }
}

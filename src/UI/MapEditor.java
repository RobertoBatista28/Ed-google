package UI;

import GameEngine.GameMapGenerator;
import Models.Lever;
import Models.Room;
import Utils.GameConfig;
import Utils.ImageLoader;
import Utils.SoundPlayer;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MapEditor extends JPanel {

    private final int ROWS = GameConfig.MAP_HEIGHT;
    private final int COLS = GameConfig.MAP_WIDTH;
    private final int CELL_SIZE = GameConfig.MAP_EDITOR_CELL_SIZE;

    private GameEngine.GameEditLogic logic;

    private int selectedTool = GameConfig.MAP_EDITOR_TOOL_WALL;
    private ActionListener backAction;

    private JPanel blueprintPanel;
    private JPanel toolsPanel;
    private JLabel statusLabel;

    private BufferedImage pickaxeImg;
    private BufferedImage enderpearlImg;
    private BufferedImage leverImg;
    private BufferedImage questionImg;

    public MapEditor(ActionListener backAction) {
        this.backAction = backAction;
        this.logic = new GameEngine.GameEditLogic();

        setLayout(new BorderLayout());
        setBackground(Color.decode(GameConfig.MAIN_WINDOW_BACKGROUND_COLOR));
        setPreferredSize(new Dimension(GameConfig.MAP_EDITOR_WINDOW_WIDTH, GameConfig.MAP_EDITOR_WINDOW_HEIGHT));

        loadImages();

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setOpaque(false);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        blueprintPanel = new BlueprintPanel();
        blueprintPanel.setPreferredSize(new Dimension(COLS * GameConfig.MAP_EDITOR_CELL_SIZE, ROWS * GameConfig.MAP_EDITOR_CELL_SIZE));

        JPanel blueprintContainer = new JPanel(new GridBagLayout());
        blueprintContainer.setOpaque(false);
        blueprintContainer.add(blueprintPanel);
        mainContainer.add(blueprintContainer, BorderLayout.CENTER);

        toolsPanel = createToolsPanel();
        mainContainer.add(toolsPanel, BorderLayout.EAST);

        add(mainContainer, BorderLayout.CENTER);

        statusLabel = new JLabel("Editor de mapas - Selecione uma ferramenta");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        add(statusLabel, BorderLayout.SOUTH);

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cancelLeverPlacement();
                }
            }
        });

        revalidate();
        repaint();
    }

    private void loadImages() {
        pickaxeImg = ImageLoader.getImage(GameConfig.ITENS_PATH + GameConfig.PICKAXE_TEXTURE);
        enderpearlImg = ImageLoader.getImage(GameConfig.ITENS_PATH + GameConfig.ENDERPEARL_TEXTURE);
        leverImg = ImageLoader.getImage(GameConfig.ITENS_PATH + GameConfig.LEVER_INACTIVE_TEXTURE);
        questionImg = ImageLoader.getImage(GameConfig.ITENS_PATH + GameConfig.QUESTION_MARK_TEXTURE);
    }

    private JPanel createToolsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(GameConfig.MAP_EDITOR_MENU_WIDTH, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Ferramentas");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        JPanel toolsContainer = new JPanel(new GridLayout(3, 2, 10, 10));
        toolsContainer.setOpaque(false);
        toolsContainer.setMaximumSize(new Dimension(110, 170));
        toolsContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        ButtonGroup group = new ButtonGroup();
        toolsContainer.add(createToolButton("Parede", GameConfig.MAP_EDITOR_TOOL_WALL, group));
        toolsContainer.add(createToolButton("Picareta", GameConfig.MAP_EDITOR_TOOL_PICKAXE, group));
        toolsContainer.add(createToolButton("Enderpearl", GameConfig.MAP_EDITOR_TOOL_ENDERPEARL, group));
        toolsContainer.add(createToolButton("Alavanca", GameConfig.MAP_EDITOR_TOOL_LEVER, group));
        toolsContainer.add(createToolButton("Enigma", GameConfig.MAP_EDITOR_TOOL_QUESTION, group));

        panel.add(toolsContainer);
        panel.add(Box.createVerticalStrut(20));

        panel.add(createActionButton("Gerar aleatório", e -> {
            GameMapGenerator newMap = new GameMapGenerator(COLS, ROWS, false);
            newMap.generateMap();
            logic.loadFromGameMap(newMap);
            blueprintPanel.repaint();
        }));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createActionButton("Limpar mapa", e -> {
            int result = JOptionPane.showConfirmDialog(this, "Tem a certeza que pretende limpar o mapa?", "Limpar mapa", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                logic.clearMap();
                blueprintPanel.repaint();
            }
        }));

        panel.add(Box.createVerticalGlue());

        JLabel actionsTitle = new JLabel("Ações");
        actionsTitle.setForeground(Color.WHITE);
        actionsTitle.setFont(new Font("Arial", Font.BOLD, 16));
        actionsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(actionsTitle);
        panel.add(Box.createVerticalStrut(20));

        panel.add(createActionButton("Carregar mapa", e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(GameConfig.MAP_LOADER_PATH));
            fileChooser.setDialogTitle("Carregar mapa");
            fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));

            int userSelection = fileChooser.showOpenDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToLoad = fileChooser.getSelectedFile();
                GameEngine.GameMapLoader loader = new GameEngine.GameMapLoader();
                GameEngine.GameMapGenerator loadedMap = loader.loadMap(fileToLoad);
                logic.loadFromGameMap(loadedMap);
                blueprintPanel.repaint();
                JOptionPane.showMessageDialog(this, "Mapa carregado com sucesso!");
            }
        }));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createActionButton("Guardar mapa", e -> saveMap()));
        panel.add(Box.createVerticalStrut(30));

        JButton backBtn = createActionButton("Voltar ao menu", e -> {
            backAction.actionPerformed(e);
        });
        panel.add(backBtn);

        return panel;
    }

    private JToggleButton createToolButton(String text, int toolId, ButtonGroup group) {
        final BufferedImage btnImg = ImageLoader.getImage(GameConfig.UI_PATH_TEXTURE + GameConfig.TINY_BUTTON_TEXTURE);
        final BufferedImage hoverBtnImg = ImageLoader.getImage(GameConfig.UI_PATH_TEXTURE + GameConfig.HOVER_TINY_BUTTON_TEXTURE);

        JToggleButton btn = new JToggleButton() {
            private boolean isHovered = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                BufferedImage imgToDraw = (isHovered || isSelected()) && hoverBtnImg != null ? hoverBtnImg : btnImg;

                if (imgToDraw != null) {
                    g.drawImage(imgToDraw, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(isSelected() ? Color.DARK_GRAY : Color.GRAY);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }

                drawIcon(g, toolId, getWidth(), getHeight());
            }
        };

        btn.setToolTipText(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(50, 50));
        btn.setPreferredSize(new Dimension(50, 50));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (toolId == selectedTool) {
            btn.setSelected(true);
        }

        btn.addActionListener(e -> {
            SoundPlayer.playClick();
            selectedTool = toolId;
            statusLabel.setText("Ferramenta selecionada: " + text);
        });

        group.add(btn);
        return btn;
    }

    private void drawIcon(Graphics g, int toolId, int w, int h) {
        int iconSize = (int) (w * 0.6);
        int x = (w - iconSize) / 2;
        int y = (h - iconSize) / 2;

        if (toolId == GameConfig.MAP_EDITOR_TOOL_WALL) {
            g.setColor(Color.WHITE);
            g.fillRect(w / 2 - 2, y, 4, iconSize);
        } else if (toolId == GameConfig.MAP_EDITOR_TOOL_PICKAXE) {
            if (pickaxeImg != null) {
                g.drawImage(pickaxeImg, x, y, iconSize, iconSize, null);
            }
        } else if (toolId == GameConfig.MAP_EDITOR_TOOL_ENDERPEARL) {
            if (enderpearlImg != null) {
                g.drawImage(enderpearlImg, x, y, iconSize, iconSize, null);
            }
        } else if (toolId == GameConfig.MAP_EDITOR_TOOL_LEVER) {
            if (leverImg != null) {
                g.drawImage(leverImg, x, y, iconSize, iconSize, null);
            }
        } else if (toolId == GameConfig.MAP_EDITOR_TOOL_QUESTION) {
            if (questionImg != null) {
                g.drawImage(questionImg, x, y, iconSize, iconSize, null);
            }
        }
    }

    private JButton createActionButton(String text, ActionListener action) {
        String texture = GameConfig.SHORT_BUTTON_TEXTURE;
        String hoverTexture = GameConfig.HOVER_SHORT_BUTTON_TEXTURE;

        final BufferedImage btnImg = ImageLoader.getImage(GameConfig.UI_PATH_TEXTURE + texture);
        final BufferedImage hoverBtnImg = ImageLoader.getImage(GameConfig.UI_PATH_TEXTURE + hoverTexture);

        JButton btn = new JButton(text) {
            private boolean isHovered = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        setForeground(Color.decode(GameConfig.TEXT_COLOR_HOVER_HEX));
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
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

        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            SoundPlayer.playClick();
            action.actionPerformed(e);
        });
        return btn;
    }

    private void saveMap() {
        // Save map logic
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("src/Resources/Maps"));
        fileChooser.setDialogTitle("Guardar mapa");

        int randomNum = (int) (Math.random() * 10000);
        String defaultName = String.format("map-21x21-%04d.json", randomNum);
        fileChooser.setSelectedFile(new File(fileChooser.getCurrentDirectory(), defaultName));

        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".json")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".json");
            }

            try {
                GameMapGenerator mapToSave = logic.createGameMapFromEditor();
                Utils.MapSerializer.saveToJson(mapToSave, fileToSave.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Mapa guardado com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao guardar mapa: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void setToolsEnabled(boolean enabled) {
        setPanelEnabled(toolsPanel, enabled);
    }

    private void setPanelEnabled(JPanel panel, boolean enabled) {
        for (Component c : panel.getComponents()) {
            c.setEnabled(enabled);
            if (c instanceof JPanel) {
                setPanelEnabled((JPanel) c, enabled);
            }
        }
    }

    private void cancelLeverPlacement() {
        if (logic.cancelLeverPlacement()) {
            setToolsEnabled(true);
            blueprintPanel.repaint();
        }
    }

    private class BlueprintPanel extends JPanel {

        private Point currentMousePos = new Point(0, 0);

        public BlueprintPanel() {
            setBackground(Color.DARK_GRAY);
            setOpaque(true);

            MouseAdapter mouseHandler = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    MapEditor.this.requestFocusInWindow();
                    if (SwingUtilities.isRightMouseButton(e)) {
                        cancelLeverPlacement();
                        return;
                    }
                    handleMouse(e);
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    currentMousePos = e.getPoint();
                    if (logic.getPendingLeverPos() != null) {
                        repaint();
                    }
                }
            };

            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(COLS * GameConfig.MAP_EDITOR_CELL_SIZE + 1, ROWS * GameConfig.MAP_EDITOR_CELL_SIZE + 1);
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        private void handleMouse(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();

            int gridX = x / GameConfig.MAP_EDITOR_CELL_SIZE;
            int gridY = y / GameConfig.MAP_EDITOR_CELL_SIZE;
            int remX = x % GameConfig.MAP_EDITOR_CELL_SIZE;
            int remY = y % GameConfig.MAP_EDITOR_CELL_SIZE;

            int threshold = 12;

            if (gridX < 0 || gridX >= COLS || gridY < 0 || gridY >= ROWS) {
                return;
            }

            if (logic.getPendingLeverPos() != null) {
                boolean wallClicked = false;

                if (remX < threshold && gridX > 0) {
                    wallClicked = true;
                    logic.linkLeverToWall(logic.getPendingLeverPos(), gridX, gridY, true);
                } else if (remX > GameConfig.MAP_EDITOR_CELL_SIZE - threshold && gridX + 1 < COLS) {
                    wallClicked = true;
                    logic.linkLeverToWall(logic.getPendingLeverPos(), gridX + 1, gridY, true);
                } else if (remY < threshold && gridY > 0) {
                    wallClicked = true;
                    logic.linkLeverToWall(logic.getPendingLeverPos(), gridX, gridY, false);
                } else if (remY > GameConfig.MAP_EDITOR_CELL_SIZE - threshold && gridY + 1 < ROWS) {
                    wallClicked = true;
                    logic.linkLeverToWall(logic.getPendingLeverPos(), gridX, gridY + 1, false);
                }

                if (wallClicked) {
                    setToolsEnabled(true);
                    statusLabel.setText("Editor de mapas - Selecione uma ferramenta");
                    repaint();
                }
            } else if (selectedTool == GameConfig.MAP_EDITOR_TOOL_WALL) {
                if (remX < threshold) {
                    if (gridX > 0) {
                        // Bloquear apenas paredes DENTRO da área central (10 e 11)
                        if (gridY >= 9 && gridY <= 11 && gridX >= 10 && gridX <= 11) {
                            return;
                        }

                        logic.toggleVWall(gridX, gridY);
                        repaint();
                    }
                } else if (remX > GameConfig.MAP_EDITOR_CELL_SIZE - threshold) {
                    if (gridX + 1 < COLS) {
                        // Bloquear apenas paredes DENTRO da área central (10 e 11)
                        if (gridY >= 9 && gridY <= 11 && (gridX + 1) >= 10 && (gridX + 1) <= 11) {
                            return;
                        }

                        logic.toggleVWall(gridX + 1, gridY);
                        repaint();
                    }
                } else if (remY < threshold) {
                    if (gridY > 0) {
                        // Bloquear apenas paredes DENTRO da área central (10 e 11)
                        if (gridX >= 9 && gridX <= 11 && gridY >= 10 && gridY <= 11) {
                            return;
                        }

                        logic.toggleHWall(gridX, gridY);
                        repaint();
                    }
                } else if (remY > GameConfig.MAP_EDITOR_CELL_SIZE - threshold) {
                    if (gridY + 1 < ROWS) {
                        // Bloquear apenas paredes DENTRO da área central (10 e 11)
                        if (gridX >= 9 && gridX <= 11 && (gridY + 1) >= 10 && (gridY + 1) <= 11) {
                            return;
                        }

                        logic.toggleHWall(gridX, gridY + 1);
                        repaint();
                    }
                }
            } else {
                boolean nearLine = remX < threshold || remX > GameConfig.MAP_EDITOR_CELL_SIZE - threshold
                        || remY < threshold || remY > GameConfig.MAP_EDITOR_CELL_SIZE - threshold;

                if (!nearLine) {
                    boolean toolsDisabled = logic.modifyCell(gridX, gridY, selectedTool);
                    if (toolsDisabled) {
                        setToolsEnabled(false);
                    } else {
                        setToolsEnabled(true);
                    }
                    repaint();
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Room[][] grid = logic.getGrid();
            boolean[][] hWalls = logic.getHWalls();
            boolean[][] vWalls = logic.getVWalls();
            Point pendingLeverPos = logic.getPendingLeverPos();

            for (int x = 0; x < COLS; x++) {
                for (int y = 0; y < ROWS; y++) {
                    int px = x * CELL_SIZE;
                    int py = y * CELL_SIZE;

                    Room room = grid[x][y];

                    g2d.setColor(Color.LIGHT_GRAY);
                    g2d.fillRect(px, py, CELL_SIZE, CELL_SIZE);

                    if (room.isEntrance()) {
                        g2d.setColor(new Color(100, 255, 100, 150));
                        g2d.fillRect(px, py, CELL_SIZE, CELL_SIZE);
                    } else if (room.isCenter()) {
                        g2d.setColor(new Color(255, 215, 0, 150));
                        g2d.fillRect(px, py, CELL_SIZE, CELL_SIZE);
                    }

                    if (room.hasPickaxe() && pickaxeImg != null) {
                        g2d.drawImage(pickaxeImg, px + 4, py + 4, CELL_SIZE - 8, CELL_SIZE - 8, null);
                    } else if (room.hasEnderPearl() && enderpearlImg != null) {
                        g2d.drawImage(enderpearlImg, px + 6, py + 6, CELL_SIZE - 12, CELL_SIZE - 12, null);
                    } else if (room.getLever() != null && leverImg != null) {
                        g2d.drawImage(leverImg, px + 6, py + 6, CELL_SIZE - 12, CELL_SIZE - 12, null);
                    } else if (room.hasQuestion() && questionImg != null) {
                        g2d.drawImage(questionImg, px + 6, py + 6, CELL_SIZE - 12, CELL_SIZE - 12, null);
                    }
                }
            }

            g2d.setColor(new Color(100, 100, 100));
            g2d.setStroke(new BasicStroke(1));
            for (int x = 0; x <= COLS; x++) {
                if (x > 9 && x < 12) {
                    g2d.drawLine(x * CELL_SIZE, 0, x * CELL_SIZE, 9 * CELL_SIZE);
                    g2d.drawLine(x * CELL_SIZE, 12 * CELL_SIZE, x * CELL_SIZE, ROWS * CELL_SIZE);
                } else {
                    g2d.drawLine(x * CELL_SIZE, 0, x * CELL_SIZE, ROWS * CELL_SIZE);
                }
            }
            for (int y = 0; y <= ROWS; y++) {
                if (y > 9 && y < 12) {
                    g2d.drawLine(0, y * CELL_SIZE, 9 * CELL_SIZE, y * CELL_SIZE);
                    g2d.drawLine(12 * CELL_SIZE, y * CELL_SIZE, COLS * CELL_SIZE, y * CELL_SIZE);
                } else {
                    g2d.drawLine(0, y * CELL_SIZE, COLS * CELL_SIZE, y * CELL_SIZE);
                }
            }

            g2d.setStroke(new BasicStroke(4));

            for (int x = 0; x < COLS; x++) {
                for (int y = 0; y <= ROWS; y++) {
                    if (hWalls[x][y]) {
                        if (logic.isWallTargeted(x, y, false)) {
                            g2d.setColor(Color.RED);
                        } else {
                            g2d.setColor(Color.BLACK);
                        }
                        g2d.drawLine(x * CELL_SIZE, y * CELL_SIZE, (x + 1) * CELL_SIZE, y * CELL_SIZE);
                    }
                }
            }

            for (int x = 0; x <= COLS; x++) {
                for (int y = 0; y < ROWS; y++) {
                    if (vWalls[x][y]) {
                        if (logic.isWallTargeted(x, y, true)) {
                            g2d.setColor(Color.RED);
                        } else {
                            g2d.setColor(Color.BLACK);
                        }
                        g2d.drawLine(x * CELL_SIZE, y * CELL_SIZE, x * CELL_SIZE, (y + 1) * CELL_SIZE);
                    }
                }
            }

            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRect(0, 0, COLS * CELL_SIZE, ROWS * CELL_SIZE);

            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(GameConfig.LEVER_LINE_THICKNESS));

            for (int x = 0; x < COLS; x++) {
                for (int y = 0; y < ROWS; y++) {
                    Room r = grid[x][y];
                    if (r.hasLever()) {
                        Lever l = r.getLever();
                        DataStructures.Iterator<Models.Connection> it = l.getTargets().iterator();
                        while (it.hasNext()) {
                            Models.Connection c = it.next();
                            int x1 = r.getX() * CELL_SIZE + CELL_SIZE / 2;
                            int y1 = r.getY() * CELL_SIZE + CELL_SIZE / 2;

                            int x2 = (c.getFrom().getX() * CELL_SIZE + CELL_SIZE / 2 + c.getTo().getX() * CELL_SIZE + CELL_SIZE / 2) / 2;
                            int y2 = (c.getFrom().getY() * CELL_SIZE + CELL_SIZE / 2 + c.getTo().getY() * CELL_SIZE + CELL_SIZE / 2) / 2;

                            g2d.drawLine(x1, y1, x2, y2);
                        }
                    }
                }
            }

            if (pendingLeverPos != null) {
                int x1 = pendingLeverPos.x * CELL_SIZE + CELL_SIZE / 2;
                int y1 = pendingLeverPos.y * CELL_SIZE + CELL_SIZE / 2;
                g2d.drawLine(x1, y1, currentMousePos.x, currentMousePos.y);
            }
        }
    }
}

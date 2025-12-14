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

/**
 * MapEditor is a GUI panel that allows users to create and edit game maps.
 * Provides tools for placing walls, items (pickaxe, ender pearl), levers,
 * and questions on a 21x21 grid. Includes functionality to generate random maps,
 * save/load maps to/from JSON files, and visually link levers to walls.
 *
 */
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

    /**
     * Creates a new MapEditor with the specified back action listener.
     *
     * @param backAction the ActionListener to invoke when the back button is clicked
     */
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

    /**
     * Loads texture images for game objects used in the map editor.
     * Retrieves pickaxe, ender pearl, lever, and question mark textures
     * from the configured resource paths.
     *
     */
    private void loadImages() {
        pickaxeImg = ImageLoader.getImage(GameConfig.ITENS_PATH + GameConfig.PICKAXE_TEXTURE);
        enderpearlImg = ImageLoader.getImage(GameConfig.ITENS_PATH + GameConfig.ENDERPEARL_TEXTURE);
        leverImg = ImageLoader.getImage(GameConfig.ITENS_PATH + GameConfig.LEVER_INACTIVE_TEXTURE);
        questionImg = ImageLoader.getImage(GameConfig.ITENS_PATH + GameConfig.QUESTION_MARK_TEXTURE);
    }

    /**
     * Creates the tools panel containing editing tools and action buttons.
     * Includes tool selection buttons (wall, pickaxe, ender pearl, lever, question),
     * map generation and clearing options, and file loading/saving buttons.
     *
     * @return a JPanel containing all editing tools and action buttons
     */
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

    /**
     * Creates a toggle button for tool selection with custom rendering.
     * The button displays an icon for the tool and provides hover state feedback
     * with different button textures and visual indicators.
     *
     * @param text the display text and tooltip for the button
     * @param toolId the integer identifier for this tool
     * @param group the ButtonGroup to manage tool selection exclusivity
     * @return a JToggleButton configured for the specified tool
     */
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
                // Select button texture based on hover state or selection state
                BufferedImage imgToDraw = (isHovered || isSelected()) && hoverBtnImg != null ? hoverBtnImg : btnImg;

                // Draw the background texture or fallback to solid color
                if (imgToDraw != null) {
                    g.drawImage(imgToDraw, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(isSelected() ? Color.DARK_GRAY : Color.GRAY);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }

                // Draw the tool-specific icon on top of the button background
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

    /**
     * Draws the icon representation for a specific tool on the given graphics context.
     * Different tools display different visual representations (wall as line, items as images).
     *
     * @param g the Graphics context to draw on
     * @param toolId the integer identifier of the tool to draw
     * @param w the width of the drawing area
     * @param h the height of the drawing area
     */
    private void drawIcon(Graphics g, int toolId, int w, int h) {
        // Calculate icon size as 60% of button dimensions and center it
        int iconSize = (int) (w * 0.6);
        int x = (w - iconSize) / 2;
        int y = (h - iconSize) / 2;

        // Draw different visual representation based on tool type
        if (toolId == GameConfig.MAP_EDITOR_TOOL_WALL) {
            // Wall tool displays as a vertical white line
            g.setColor(Color.WHITE);
            g.fillRect(w / 2 - 2, y, 4, iconSize);
        } else if (toolId == GameConfig.MAP_EDITOR_TOOL_PICKAXE) {
            // Pickaxe tool displays the pickaxe texture image
            if (pickaxeImg != null) {
                g.drawImage(pickaxeImg, x, y, iconSize, iconSize, null);
            }
        } else if (toolId == GameConfig.MAP_EDITOR_TOOL_ENDERPEARL) {
            // Ender pearl tool displays the ender pearl texture image
            if (enderpearlImg != null) {
                g.drawImage(enderpearlImg, x, y, iconSize, iconSize, null);
            }
        } else if (toolId == GameConfig.MAP_EDITOR_TOOL_LEVER) {
            // Lever tool displays the lever texture image
            if (leverImg != null) {
                g.drawImage(leverImg, x, y, iconSize, iconSize, null);
            }
        } else if (toolId == GameConfig.MAP_EDITOR_TOOL_QUESTION) {
            // Question tool displays the question mark texture image
            if (questionImg != null) {
                g.drawImage(questionImg, x, y, iconSize, iconSize, null);
            }
        }
    }

    /**
     * Creates an action button with custom rendering and hover effects.
     * The button displays text on a textured background and changes appearance
     * when hovered over. Plays a click sound when activated.
     *
     * @param text the display text for the button
     * @param action the ActionListener to invoke when the button is clicked
     * @return a JButton configured with the specified text and action
     */
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
                // Select button texture based on hover state
                BufferedImage imgToDraw = isHovered && hoverBtnImg != null ? hoverBtnImg : btnImg;

                // Draw the background texture or fallback to solid color
                if (imgToDraw != null) {
                    g.drawImage(imgToDraw, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(Color.decode(GameConfig.BUTTONS_BACKGROUND_COLOR));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }

                // Draw the button text on top of the background
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

    /**
     * Saves the current map to a JSON file selected by the user.
     * Generates a default filename with a random 4-digit suffix and allows
     * the user to select a save location. Displays success or error messages.
     *
     */
    private void saveMap() {
        // Create file chooser dialog for selecting save location and filename
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("src/Resources/Maps"));
        fileChooser.setDialogTitle("Guardar mapa");

        // Generate default filename with random suffix to avoid overwrites
        int randomNum = (int) (Math.random() * 10000);
        String defaultName = String.format("map-21x21-%04d.json", randomNum);
        fileChooser.setSelectedFile(new File(fileChooser.getCurrentDirectory(), defaultName));

        // Filter to show only JSON files
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));

        // Show save dialog and handle user selection
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Append .json extension if not already present
            if (!fileToSave.getName().toLowerCase().endsWith(".json")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".json");
            }

            try {
                // Serialize current map state and write to JSON file
                GameMapGenerator mapToSave = logic.createGameMapFromEditor();
                Utils.MapSerializer.saveToJson(mapToSave, fileToSave.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Mapa guardado com sucesso!");
            } catch (Exception ex) {
                // Display error message if save operation fails
                JOptionPane.showMessageDialog(this, "Erro ao guardar mapa: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Enables or disables all tools in the tools panel.
     * Used to prevent tool selection while waiting for user input
     * (such as selecting a wall to link a lever to).
     *
     * @param enabled true to enable all tools, false to disable
     */
    private void setToolsEnabled(boolean enabled) {
        setPanelEnabled(toolsPanel, enabled);
    }

    /**
     * Recursively enables or disables all components in a panel.
     * Processes nested JPanels to ensure all UI controls are affected.
     *
     * @param panel the JPanel containing components to enable or disable
     * @param enabled true to enable all components, false to disable
     */
    private void setPanelEnabled(JPanel panel, boolean enabled) {
        for (Component c : panel.getComponents()) {
            c.setEnabled(enabled);
            if (c instanceof JPanel) {
                setPanelEnabled((JPanel) c, enabled);
            }
        }
    }

    /**
     * Cancels the current lever placement operation.
     * Re-enables the tools panel and refreshes the blueprint display.
     * Called when the user presses ESC or right-clicks during lever placement.
     *
     */
    private void cancelLeverPlacement() {
        if (logic.cancelLeverPlacement()) {
            setToolsEnabled(true);
            blueprintPanel.repaint();
        }
    }

    /**
     * BlueprintPanel is the inner class responsible for rendering the map grid
     * and handling user interactions (clicking to place objects, dragging to link levers).
     * Displays all grid cells, walls, objects, and visual feedback during editing.
     *
     */
    private class BlueprintPanel extends JPanel {

        private Point currentMousePos = new Point(0, 0);

        /**
         * Creates a new BlueprintPanel with mouse event handling for map editing.
         * Sets up listeners for left-click placement and right-click cancellation.
         *
         */
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
            // Convert mouse coordinates to grid cell and intra-cell position
            int x = e.getX();
            int y = e.getY();

            int gridX = x / GameConfig.MAP_EDITOR_CELL_SIZE;
            int gridY = y / GameConfig.MAP_EDITOR_CELL_SIZE;
            int remX = x % GameConfig.MAP_EDITOR_CELL_SIZE;
            int remY = y % GameConfig.MAP_EDITOR_CELL_SIZE;

            // Threshold (12 pixels) defines the edge region where walls can be placed
            int threshold = 12;

            // Bounds check to ensure click is within the grid
            if (gridX < 0 || gridX >= COLS || gridY < 0 || gridY >= ROWS) {
                return;
            }

            // Handle lever linking mode (waiting for wall selection)
            if (logic.getPendingLeverPos() != null) {
                boolean wallClicked = false;

                // Detect which wall edge the user clicked on and link lever to it
                if (remX < threshold && gridX > 0) {
                    // Left edge of cell - link to vertical wall between current and left cell
                    wallClicked = true;
                    logic.linkLeverToWall(logic.getPendingLeverPos(), gridX, gridY, true);
                } else if (remX > GameConfig.MAP_EDITOR_CELL_SIZE - threshold && gridX + 1 < COLS) {
                    // Right edge of cell - link to vertical wall between current and right cell
                    wallClicked = true;
                    logic.linkLeverToWall(logic.getPendingLeverPos(), gridX + 1, gridY, true);
                } else if (remY < threshold && gridY > 0) {
                    // Top edge of cell - link to horizontal wall between current and top cell
                    wallClicked = true;
                    logic.linkLeverToWall(logic.getPendingLeverPos(), gridX, gridY, false);
                } else if (remY > GameConfig.MAP_EDITOR_CELL_SIZE - threshold && gridY + 1 < ROWS) {
                    // Bottom edge of cell - link to horizontal wall between current and bottom cell
                    wallClicked = true;
                    logic.linkLeverToWall(logic.getPendingLeverPos(), gridX, gridY + 1, false);
                }

                if (wallClicked) {
                    // Exit lever linking mode and re-enable tool selection
                    setToolsEnabled(true);
                    statusLabel.setText("Editor de mapas - Selecione uma ferramenta");
                    repaint();
                }
            } else if (selectedTool == GameConfig.MAP_EDITOR_TOOL_WALL) {
                // Wall placement mode - toggle walls at the edges of cells
                if (remX < threshold) {
                    // Attempting to place vertical wall on left edge
                    if (gridX > 0) {
                        // Prevent wall placement in central room boundary (10-11 range)
                        if (gridY >= 9 && gridY <= 11 && gridX >= 10 && gridX <= 11) {
                            return;
                        }

                        logic.toggleVWall(gridX, gridY);
                        repaint();
                    }
                } else if (remX > GameConfig.MAP_EDITOR_CELL_SIZE - threshold) {
                    // Attempting to place vertical wall on right edge
                    if (gridX + 1 < COLS) {
                        // Prevent wall placement in central room boundary (10-11 range)
                        if (gridY >= 9 && gridY <= 11 && (gridX + 1) >= 10 && (gridX + 1) <= 11) {
                            return;
                        }

                        logic.toggleVWall(gridX + 1, gridY);
                        repaint();
                    }
                } else if (remY < threshold) {
                    // Attempting to place horizontal wall on top edge
                    if (gridY > 0) {
                        // Prevent wall placement in central room boundary (10-11 range)
                        if (gridX >= 9 && gridX <= 11 && gridY >= 10 && gridY <= 11) {
                            return;
                        }

                        logic.toggleHWall(gridX, gridY);
                        repaint();
                    }
                } else if (remY > GameConfig.MAP_EDITOR_CELL_SIZE - threshold) {
                    // Attempting to place horizontal wall on bottom edge
                    if (gridY + 1 < ROWS) {
                        // Prevent wall placement in central room boundary (10-11 range)
                        if (gridX >= 9 && gridX <= 11 && (gridY + 1) >= 10 && (gridY + 1) <= 11) {
                            return;
                        }

                        logic.toggleHWall(gridX, gridY + 1);
                        repaint();
                    }
                }
            } else {
                // Item placement mode (pickaxe, ender pearl, lever, question)
                // Only allow placement in cell center, not near edges
                boolean nearLine = remX < threshold || remX > GameConfig.MAP_EDITOR_CELL_SIZE - threshold
                        || remY < threshold || remY > GameConfig.MAP_EDITOR_CELL_SIZE - threshold;

                if (!nearLine) {
                    // Place selected item in grid cell and check if tools should be disabled
                    boolean toolsDisabled = logic.modifyCell(gridX, gridY, selectedTool);
                    if (toolsDisabled) {
                        // Tools disabled when lever placement pending
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

            // Enable anti-aliasing for smoother rendering
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Retrieve grid data structures
            Room[][] grid = logic.getGrid();
            boolean[][] hWalls = logic.getHWalls();
            boolean[][] vWalls = logic.getVWalls();
            Point pendingLeverPos = logic.getPendingLeverPos();

            // Draw all grid cells with their contents
            for (int x = 0; x < COLS; x++) {
                for (int y = 0; y < ROWS; y++) {
                    int px = x * CELL_SIZE;
                    int py = y * CELL_SIZE;

                    Room room = grid[x][y];

                    // Fill cell with light gray background
                    g2d.setColor(Color.LIGHT_GRAY);
                    g2d.fillRect(px, py, CELL_SIZE, CELL_SIZE);

                    // Color entrance cells green and center cells gold
                    if (room.isEntrance()) {
                        g2d.setColor(new Color(100, 255, 100, 150));
                        g2d.fillRect(px, py, CELL_SIZE, CELL_SIZE);
                    } else if (room.isCenter()) {
                        g2d.setColor(new Color(255, 215, 0, 150));
                        g2d.fillRect(px, py, CELL_SIZE, CELL_SIZE);
                    }

                    // Draw objects contained in the cell (pickaxe, ender pearl, lever, question)
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

            // Draw grid lines (with gap in central room area)
            g2d.setColor(new Color(100, 100, 100));
            g2d.setStroke(new BasicStroke(1));

            // Vertical lines with central area gap
            for (int x = 0; x <= COLS; x++) {
                if (x > 9 && x < 12) {
                    // Draw above and below central room only
                    g2d.drawLine(x * CELL_SIZE, 0, x * CELL_SIZE, 9 * CELL_SIZE);
                    g2d.drawLine(x * CELL_SIZE, 12 * CELL_SIZE, x * CELL_SIZE, ROWS * CELL_SIZE);
                } else {
                    // Draw full height for non-central columns
                    g2d.drawLine(x * CELL_SIZE, 0, x * CELL_SIZE, ROWS * CELL_SIZE);
                }
            }

            // Horizontal lines with central area gap
            for (int y = 0; y <= ROWS; y++) {
                if (y > 9 && y < 12) {
                    // Draw left and right of central room only
                    g2d.drawLine(0, y * CELL_SIZE, 9 * CELL_SIZE, y * CELL_SIZE);
                    g2d.drawLine(12 * CELL_SIZE, y * CELL_SIZE, COLS * CELL_SIZE, y * CELL_SIZE);
                } else {
                    // Draw full width for non-central rows
                    g2d.drawLine(0, y * CELL_SIZE, COLS * CELL_SIZE, y * CELL_SIZE);
                }
            }

            // Draw wall segments with thicker stroke
            g2d.setStroke(new BasicStroke(4));

            // Draw horizontal walls (blocking vertical movement)
            for (int x = 0; x < COLS; x++) {
                for (int y = 0; y <= ROWS; y++) {
                    if (hWalls[x][y]) {
                        // Highlight walls that are linked to levers in red
                        if (logic.isWallTargeted(x, y, false)) {
                            g2d.setColor(Color.RED);
                        } else {
                            g2d.setColor(Color.BLACK);
                        }
                        g2d.drawLine(x * CELL_SIZE, y * CELL_SIZE, (x + 1) * CELL_SIZE, y * CELL_SIZE);
                    }
                }
            }

            // Draw vertical walls (blocking horizontal movement)
            for (int x = 0; x <= COLS; x++) {
                for (int y = 0; y < ROWS; y++) {
                    if (vWalls[x][y]) {
                        // Highlight walls that are linked to levers in red
                        if (logic.isWallTargeted(x, y, true)) {
                            g2d.setColor(Color.RED);
                        } else {
                            g2d.setColor(Color.BLACK);
                        }
                        g2d.drawLine(x * CELL_SIZE, y * CELL_SIZE, x * CELL_SIZE, (y + 1) * CELL_SIZE);
                    }
                }
            }

            // Draw border around entire grid
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRect(0, 0, COLS * CELL_SIZE, ROWS * CELL_SIZE);

            // Draw lever connections (red lines linking levers to controlled walls)
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(GameConfig.LEVER_LINE_THICKNESS));

            for (int x = 0; x < COLS; x++) {
                for (int y = 0; y < ROWS; y++) {
                    Room r = grid[x][y];
                    if (r.hasLever()) {
                        Lever l = r.getLever();
                        // Iterate through all wall connections controlled by this lever
                        DataStructures.Iterator<Models.Connection> it = l.getTargets().iterator();
                        while (it.hasNext()) {
                            Models.Connection c = it.next();

                            // Starting point at lever cell center
                            int x1 = r.getX() * CELL_SIZE + CELL_SIZE / 2;
                            int y1 = r.getY() * CELL_SIZE + CELL_SIZE / 2;

                            // Endpoint at midpoint between wall endpoints
                            int x2 = (c.getFrom().getX() * CELL_SIZE + CELL_SIZE / 2 + c.getTo().getX() * CELL_SIZE + CELL_SIZE / 2) / 2;
                            int y2 = (c.getFrom().getY() * CELL_SIZE + CELL_SIZE / 2 + c.getTo().getY() * CELL_SIZE + CELL_SIZE / 2) / 2;

                            // Draw line from lever to wall
                            g2d.drawLine(x1, y1, x2, y2);
                        }
                    }
                }
            }

            // Draw pending lever connection preview (while user is linking lever to wall)
            if (pendingLeverPos != null) {
                int x1 = pendingLeverPos.x * CELL_SIZE + CELL_SIZE / 2;
                int y1 = pendingLeverPos.y * CELL_SIZE + CELL_SIZE / 2;
                g2d.drawLine(x1, y1, currentMousePos.x, currentMousePos.y);
            }
        }
    }
}

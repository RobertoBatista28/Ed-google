package UI;

import DataStructures.ArrayList.ArrayUnorderedList;
import GameEngine.GameController;
import GameEngine.GameEventListener;
import GameEngine.GameManager;
import GameEngine.GameMapGenerator;
import GameEngine.GameMapLoader;
import Models.Player;
import Models.Question;
import Utils.GameConfig;
import java.awt.*;
import javax.swing.*;

/**
 * MainWindow is the main application window that manages all UI panels
 * and game state transitions. It implements a CardLayout to switch between
 * menu, setup, editor, help, and game screens. Serves as the central event
 * listener for all game events and coordinates game flow.
 *
 */
public class MainWindow extends JFrame implements GameEventListener {

    // Layout & Panels
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final MenuPanel menuPanel;
    private SetupPanel setupPanel;
    private final HelpPanel helpPanel;
    private final MapEditor mapEditor;
    private PauseMenuPanel pauseMenuPanel;
    private final JPanel gameContainer;
    private final JLayeredPane gameLayeredPane;
    private final StatsPanel statsPanel;
    private MapPanel mapPanel;

    // Game Logic
    private GameManager gameManager;
    private GameMapGenerator gameMap;
    private GameController gameController;
    private boolean isPaused = false;

    /**
     * Creates a new MainWindow and initializes all UI panels in a CardLayout.
     * Sets up the menu, setup, help, editor, and game screens. Initializes
     * game event listeners and displays the main menu.
     *
     */
    public MainWindow() {
        setTitle(GameConfig.MAIN_WINDOW_TITLE);
        setSize(GameConfig.MAIN_WINDOW_WIDTH, GameConfig.MAIN_WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setExtendedState(JFrame.NORMAL);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 1. Menu Panel
        menuPanel = new MenuPanel(
                e -> showSetup(),
                e -> showEditor(),
                e -> showHelp(),
                e -> System.exit(0)
        );

        // 2. Setup Panel
        setupPanel = null;
        setupPanel = new SetupPanel(
                e -> startGame(setupPanel.getPlayerNames(), setupPanel.getPlayerTypes(), setupPanel.getPlayerCharacters()),
                e -> showMenu()
        );

        helpPanel = new HelpPanel(e -> showMenu());
        mapEditor = new MapEditor(e -> showMenu());

        // 3. Game Container
        gameContainer = new JPanel(new BorderLayout());

        gameLayeredPane = new JLayeredPane();
        gameLayeredPane.setLayout(null);

        statsPanel = new StatsPanel();
        gameContainer.add(statsPanel, BorderLayout.NORTH);
        gameContainer.add(gameLayeredPane, BorderLayout.CENTER);

        setupGameLayeredPane();

        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(setupPanel, "SETUP");
        mainPanel.add(helpPanel, "HELP");
        mainPanel.add(mapEditor, "EDITOR");
        mainPanel.add(gameContainer, "GAME");

        add(mainPanel);

        // Show Menu initially
        cardLayout.show(mainPanel, "MENU");

        setVisible(true);
    }

    /**
     * Displays the main menu screen and resizes the window to menu dimensions.
     * Centers the window on the screen and switches the CardLayout to show the menu panel.
     *
     */
    private void showMenu() {
        setSize(GameConfig.MAIN_WINDOW_WIDTH, GameConfig.MAIN_WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        cardLayout.show(mainPanel, "MENU");
    }

    /**
     * Displays the player setup screen where users configure player names,
     * types (human or bot), and character selections. Resizes window and
     * switches CardLayout to show the setup panel.
     *
     */
    private void showSetup() {
        setSize(GameConfig.MAIN_WINDOW_WIDTH, GameConfig.MAIN_WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        cardLayout.show(mainPanel, "SETUP");
    }

    /**
     * Displays the map editor screen for creating and editing game maps.
     * Resizes window to editor dimensions and switches CardLayout to show the editor panel.
     *
     */
    private void showEditor() {
        setSize(GameConfig.MAP_EDITOR_WINDOW_WIDTH, GameConfig.MAP_EDITOR_WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        cardLayout.show(mainPanel, "EDITOR");
    }

    /**
     * Displays the help and support screen with game rules and information.
     * Resizes window to standard dimensions and switches CardLayout to show the help panel.
     *
     */
    private void showHelp() {
        setSize(GameConfig.MAIN_WINDOW_WIDTH, GameConfig.MAIN_WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        cardLayout.show(mainPanel, "HELP");
    }

    /**
     * Initializes and starts a new game with the specified player configuration.
     * Loads or generates a map, creates the GameManager and GameController,
     * initializes the MapPanel and statistics, and switches to the game view.
     *
     * @param playerNames list of player names
     * @param playerTypes list of player types (true for bot, false for human)
     * @param playerCharacters list of player character types (skins)
     */
    private void startGame(ArrayUnorderedList<String> playerNames, ArrayUnorderedList<Boolean> playerTypes, ArrayUnorderedList<String> playerCharacters) {
        // Load or generate game map
        GameMapLoader loader = new GameMapLoader();
        gameMap = loader.loadRandomMap();
        
        // FORÇAR GERAÇÃO DE NOVO MAPA (Para validar o Grafo)
        // gameMap = new GameMapGenerator(21, 21, true);
        
        // Create GameManager with loaded map
        gameManager = new GameManager(gameMap);
        gameManager.setGameEventListener(this);

        // Iterate through player lists and add each player to the game
        DataStructures.Iterator<String> nameIt = playerNames.iterator();
        DataStructures.Iterator<Boolean> typeIt = playerTypes.iterator();
        DataStructures.Iterator<String> charIt = playerCharacters.iterator();

        int i = 0;
        while (nameIt.hasNext() && typeIt.hasNext() && charIt.hasNext()) {
            String name = nameIt.next();
            boolean isBot = typeIt.next();
            String characterType = charIt.next();

            // Use default name if player left field empty
            if (name.trim().isEmpty()) {
                name = "Player " + (i + 1);
            }
            gameManager.addPlayer(name, isBot, characterType);
            i++;
        }

        // Initialize MapPanel if previously created and remove old instance
        if (mapPanel != null) {
            gameLayeredPane.remove(mapPanel);
        }
        mapPanel = new MapPanel(gameManager);

        // Add MapPanel to layered pane with absolute positioning
        mapPanel.setBounds(0, 0, GameConfig.GAME_WINDOW_WIDTH, GameConfig.GAME_WINDOW_HEIGHT - statsPanel.getPreferredSize().height);
        gameLayeredPane.add(mapPanel, JLayeredPane.DEFAULT_LAYER);

        // Remove old GameController key listener if exists
        if (gameController != null) {
            removeKeyListener(gameController);
        }

        // Remove all existing key listeners to prevent duplicate input handling
        for (java.awt.event.KeyListener kl : getKeyListeners()) {
            removeKeyListener(kl);
        }

        // Create new GameController for handling keyboard input
        gameController = new GameController(gameManager, mapPanel);
        gameController.setStatsPanel(statsPanel);

        // Register key listener for ESC (pause) and game controls (arrow keys, etc.)
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                // ESC toggles pause menu, other keys only work if game is not paused
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    MainWindow.this.togglePauseMenu();
                } else if (!MainWindow.this.isPaused) {
                    gameController.keyPressed(e);
                }
            }
        });

        // Start game logic and initial updates
        gameManager.startGame();

        // Update statistics display with initial game state
        statsPanel.updateStats(gameManager.getPlayers(), gameManager.getCurrentPlayer());

        // Switch to game view with game window dimensions
        setSize(GameConfig.GAME_WINDOW_WIDTH, GameConfig.GAME_WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        cardLayout.show(mainPanel, "GAME");
        requestFocusInWindow();
    }

    /**
     * Toggles the pause menu overlay on and off. When pausing, displays a menu
     * with options to resume, show help, or return to main menu. When resuming,
     * removes the pause menu overlay and resumes game logic.
     *
     */
    private void togglePauseMenu() {
        if (isPaused) {
            // Resume game - remove pause menu overlay and unpause game logic
            if (pauseMenuPanel != null) {
                gameLayeredPane.remove(pauseMenuPanel);
                pauseMenuPanel = null;
            }
            isPaused = false;
            if (gameManager != null) {
                gameManager.setPaused(false);
            }
        } else {
            // Pause game - create pause menu with three options (resume, help, main menu)
            pauseMenuPanel = new PauseMenuPanel(
                    e -> togglePauseMenu(),
                    e -> showHelpFromGame(),
                    e -> returnToMainMenu()
            );

            // Position pause menu to overlay entire game area
            int layerWidth = gameLayeredPane.getWidth();
            int layerHeight = gameLayeredPane.getHeight();
            pauseMenuPanel.setBounds(0, 0, layerWidth, layerHeight);

            // Add pause menu to highest layer (PALETTE_LAYER) to appear above all game elements
            gameLayeredPane.add(pauseMenuPanel, JLayeredPane.PALETTE_LAYER);
            isPaused = true;
            if (gameManager != null) {
                gameManager.setPaused(true);
            }
        }

        // Refresh display and request focus to ensure input works
        gameLayeredPane.revalidate();
        gameLayeredPane.repaint();
        requestFocusInWindow();
    }

    /**
     * Displays a help dialog from within the game (via pause menu).
     * Creates a temporary HelpPanel in a modal JDialog and centers it
     * on the main window. Closes dialog when user selects exit.
     *
     */
    private void showHelpFromGame() {
        JDialog helpDialog = new JDialog(this, "Ajuda e Suporte", true);
        helpDialog.setSize(GameConfig.MAIN_WINDOW_WIDTH, GameConfig.MAIN_WINDOW_HEIGHT);
        helpDialog.setLocationRelativeTo(this);

        HelpPanel tempHelpPanel = new HelpPanel(e -> helpDialog.dispose());
        helpDialog.add(tempHelpPanel);
        helpDialog.setVisible(true);

        requestFocusInWindow();
    }

    /**
     * Returns to main menu from an active game. Stops background music,
     * removes all game components (pause menu, map panel), cleans up game
     * resources (GameManager, GameController), and displays the main menu.
     *
     */
    private void returnToMainMenu() {
        // Stop background cave ambience music
        Utils.SoundPlayer.stopCaveAmbience();

        // Remove GameController key listener to prevent input handling after game ends
        if (gameController != null) {
            removeKeyListener(gameController);
        }

        // Remove pause menu overlay if visible
        if (pauseMenuPanel != null) {
            gameLayeredPane.remove(pauseMenuPanel);
            pauseMenuPanel = null;
        }

        // Remove and dispose of map panel to free resources
        if (mapPanel != null) {
            gameLayeredPane.remove(mapPanel);
            mapPanel = null;
        }

        // Reset game state variables
        isPaused = false;
        gameManager = null;
        gameMap = null;

        // Return to main menu screen
        showMenu();
    }

    /**
     * Sets up a component resize listener for the game layered pane.
     * Automatically resizes map panel and pause menu when the layered pane
     * dimensions change, ensuring they always cover the full game area.
     *
     */
    private void setupGameLayeredPane() {
        gameLayeredPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                // Get new dimensions of the layered pane after resize
                int width = gameLayeredPane.getWidth();
                int height = gameLayeredPane.getHeight();

                // Resize map panel to fill entire layered pane
                if (mapPanel != null) {
                    mapPanel.setBounds(0, 0, width, height);
                }

                // Resize pause menu overlay to cover entire layered pane
                if (pauseMenuPanel != null) {
                    pauseMenuPanel.setBounds(0, 0, width, height);
                }
            }
        });
    }

    /**
     * Event handler called when a player rolls the dice.
     * Displays a DiceDialog showing the roll results and updates player statistics.
     * Invoked on the Swing Event Dispatch Thread for thread safety.
     *
     * @param playerName the name of the player who rolled the dice
     * @param die1 the value of the first die
     * @param die2 the value of the second die
     */
    @Override
    public void onDiceRolled(String playerName, int die1, int die2) {
        SwingUtilities.invokeLater(() -> {
            new DiceDialog(this, playerName, die1, die2).setVisible(true);
            if (statsPanel != null) {
                statsPanel.updateStats(gameManager.getPlayers(), gameManager.getCurrentPlayer());
            }
        });
    }

    /**
     * Event handler called when a player moves on the game map.
     * Refreshes the map panel display and updates player statistics.
     * Invoked on the Swing Event Dispatch Thread for thread safety.
     *
     * @param player the player that moved
     */
    @Override
    public void onPlayerMoved(Player player) {
        SwingUtilities.invokeLater(() -> {
            mapPanel.repaint();
            if (statsPanel != null) {
                statsPanel.updateStats(gameManager.getPlayers(), gameManager.getCurrentPlayer());
            }
        });
    }

    /**
     * Event handler called when the game ends with a winner.
     * Generates a game report, displays a ReportDialog showing game statistics,
     * stops background music, and returns to the main menu.
     * Invoked on the Swing Event Dispatch Thread for thread safety.
     *
     * @param winner the Player object representing the game winner
     */
    @Override
    public void onGameOver(Player winner) {
        Utils.SoundPlayer.stopCaveAmbience();
        SwingUtilities.invokeLater(() -> {
            String reportFile = Utils.GameReport.generateReport(gameManager.getPlayers(), winner, gameManager.getGameMap().getMapName());

            new ReportDialog(this, gameManager.getPlayers(), winner, reportFile).setVisible(true);

            showMenu();
        });
    }

    /**
     * Event handler called when a player encounters a question challenge.
     * Displays a QuestionDialog with the question and answer options.
     * Passes the user's answer result to the GameManager and updates display.
     * Invoked on the Swing Event Dispatch Thread for thread safety.
     *
     * @param question the Question object to display
     */
    @Override
    public void onQuestionEncountered(Question question) {
        SwingUtilities.invokeLater(() -> {
            QuestionDialog dialog = new QuestionDialog(this, question);
            dialog.setVisible(true);
            boolean result = dialog.getResult();
            gameManager.handleQuestionResult(result);
            if (statsPanel != null) {
                statsPanel.updateStats(gameManager.getPlayers(), gameManager.getCurrentPlayer());
            }
            mapPanel.repaint();
        });
    }

    /**
     * Event handler called when game status messages need to be displayed.
     * Sets a temporary status message on the map panel (e.g., "Wall is locked", "Treasure found").
     * Invoked on the Swing Event Dispatch Thread for thread safety.
     *
     * @param message the status message text to display
     */
    @Override
    public void onGameStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            if (mapPanel != null) {
                mapPanel.setStatusMessage(message);
            }
        });
    }
}

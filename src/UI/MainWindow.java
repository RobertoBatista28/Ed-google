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

    private void showMenu() {
        setSize(GameConfig.MAIN_WINDOW_WIDTH, GameConfig.MAIN_WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        cardLayout.show(mainPanel, "MENU");
    }

    private void showSetup() {
        setSize(GameConfig.MAIN_WINDOW_WIDTH, GameConfig.MAIN_WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        cardLayout.show(mainPanel, "SETUP");
    }

    private void showEditor() {
        setSize(GameConfig.MAP_EDITOR_WINDOW_WIDTH, GameConfig.MAP_EDITOR_WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        cardLayout.show(mainPanel, "EDITOR");
    }

    private void showHelp() {
        setSize(GameConfig.MAIN_WINDOW_WIDTH, GameConfig.MAIN_WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        cardLayout.show(mainPanel, "HELP");
    }

    private void startGame(ArrayUnorderedList<String> playerNames, ArrayUnorderedList<Boolean> playerTypes, ArrayUnorderedList<String> playerCharacters) {
        // Initialize Game Logic
        GameMapLoader loader = new GameMapLoader();
        gameMap = loader.loadRandomMap();
        
        // FORÇAR GERAÇÃO DE NOVO MAPA (Para validar o Grafo)
        // gameMap = new GameMapGenerator(21, 21, true);
        
        gameManager = new GameManager(gameMap);
        gameManager.setGameEventListener(this);

        // Add Players
        DataStructures.Iterator<String> nameIt = playerNames.iterator();
        DataStructures.Iterator<Boolean> typeIt = playerTypes.iterator();
        DataStructures.Iterator<String> charIt = playerCharacters.iterator();

        int i = 0;
        while (nameIt.hasNext() && typeIt.hasNext() && charIt.hasNext()) {
            String name = nameIt.next();
            boolean isBot = typeIt.next();
            String characterType = charIt.next();

            if (name.trim().isEmpty()) {
                name = "Player " + (i + 1);
            }
            gameManager.addPlayer(name, isBot, characterType);
            i++;
        }

        // Initialize Map Panel
        if (mapPanel != null) {
            gameLayeredPane.remove(mapPanel);
        }
        mapPanel = new MapPanel(gameManager);

        // Add MapPanel to layered pane
        mapPanel.setBounds(0, 0, GameConfig.GAME_WINDOW_WIDTH, GameConfig.GAME_WINDOW_HEIGHT - statsPanel.getPreferredSize().height);
        gameLayeredPane.add(mapPanel, JLayeredPane.DEFAULT_LAYER);

        // Initialize Controller with ESC key support
        if (gameController != null) {
            removeKeyListener(gameController);
        }

        // Remove old key listeners
        for (java.awt.event.KeyListener kl : getKeyListeners()) {
            removeKeyListener(kl);
        }

        gameController = new GameController(gameManager, mapPanel);
        gameController.setStatsPanel(statsPanel);

        // Add key listener for ESC and game controls
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    MainWindow.this.togglePauseMenu();
                } else if (!MainWindow.this.isPaused) {
                    gameController.keyPressed(e);
                }
            }
        });

        // Start the game logic
        gameManager.startGame();

        // Initial Stats Update
        statsPanel.updateStats(gameManager.getPlayers(), gameManager.getCurrentPlayer());

        // Switch View
        setSize(GameConfig.GAME_WINDOW_WIDTH, GameConfig.GAME_WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        cardLayout.show(mainPanel, "GAME");
        requestFocusInWindow();
    }

    private void togglePauseMenu() {
        if (isPaused) {
            // Close pause menu
            if (pauseMenuPanel != null) {
                gameLayeredPane.remove(pauseMenuPanel);
                pauseMenuPanel = null;
            }
            isPaused = false;
            if (gameManager != null) {
                gameManager.setPaused(false);
            }
        } else {
            // Open pause menu
            pauseMenuPanel = new PauseMenuPanel(
                    e -> togglePauseMenu(),
                    e -> showHelpFromGame(),
                    e -> returnToMainMenu()
            );

            // Set bounds to cover entire game area
            int layerWidth = gameLayeredPane.getWidth();
            int layerHeight = gameLayeredPane.getHeight();
            pauseMenuPanel.setBounds(0, 0, layerWidth, layerHeight);

            gameLayeredPane.add(pauseMenuPanel, JLayeredPane.PALETTE_LAYER);
            isPaused = true;
            if (gameManager != null) {
                gameManager.setPaused(true);
            }
        }

        gameLayeredPane.revalidate();
        gameLayeredPane.repaint();
        requestFocusInWindow();
    }

    private void showHelpFromGame() {
        JDialog helpDialog = new JDialog(this, "Ajuda e Suporte", true);
        helpDialog.setSize(GameConfig.MAIN_WINDOW_WIDTH, GameConfig.MAIN_WINDOW_HEIGHT);
        helpDialog.setLocationRelativeTo(this);

        HelpPanel tempHelpPanel = new HelpPanel(e -> helpDialog.dispose());
        helpDialog.add(tempHelpPanel);
        helpDialog.setVisible(true);

        requestFocusInWindow();
    }

    private void returnToMainMenu() {
        Utils.SoundPlayer.stopCaveAmbience();
        if (gameController != null) {
            removeKeyListener(gameController);
        }

        if (pauseMenuPanel != null) {
            gameLayeredPane.remove(pauseMenuPanel);
            pauseMenuPanel = null;
        }

        if (mapPanel != null) {
            gameLayeredPane.remove(mapPanel);
            mapPanel = null;
        }

        isPaused = false;
        gameManager = null;
        gameMap = null;

        showMenu();
    }

    // Add component listener to handle resizing
    private void setupGameLayeredPane() {
        gameLayeredPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int width = gameLayeredPane.getWidth();
                int height = gameLayeredPane.getHeight();

                if (mapPanel != null) {
                    mapPanel.setBounds(0, 0, width, height);
                }

                if (pauseMenuPanel != null) {
                    pauseMenuPanel.setBounds(0, 0, width, height);
                }
            }
        });
    }

    @Override
    public void onDiceRolled(String playerName, int die1, int die2) {
        SwingUtilities.invokeLater(() -> {
            new DiceDialog(this, playerName, die1, die2).setVisible(true);
            if (statsPanel != null) {
                statsPanel.updateStats(gameManager.getPlayers(), gameManager.getCurrentPlayer());
            }
        });
    }

    @Override
    public void onPlayerMoved(Player player) {
        SwingUtilities.invokeLater(() -> {
            mapPanel.repaint();
            if (statsPanel != null) {
                statsPanel.updateStats(gameManager.getPlayers(), gameManager.getCurrentPlayer());
            }
        });
    }

    @Override
    public void onGameOver(Player winner) {
        Utils.SoundPlayer.stopCaveAmbience();
        SwingUtilities.invokeLater(() -> {
            String reportFile = Utils.GameReport.generateReport(gameManager.getPlayers(), winner, gameManager.getGameMap().getMapName());

            new ReportDialog(this, gameManager.getPlayers(), winner, reportFile).setVisible(true);

            showMenu();
        });
    }

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

    @Override
    public void onGameStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            if (mapPanel != null) {
                mapPanel.setStatusMessage(message);
            }
        });
    }
}

package UI;

import DataStructures.ArrayList.ArrayUnorderedList;
import GameEngine.GameController;
import GameEngine.GameEventListener;
import GameEngine.GameManager;
import GameEngine.GameMap;
import GameEngine.GameMapLoader;
import Models.Player;
import Models.Question;
import Utils.GameConfig;
import java.awt.*;
import javax.swing.*;

public class MainWindow extends JFrame implements GameEventListener {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private MenuPanel menuPanel;
    private SetupPanel setupPanel;
    private HelpPanel helpPanel;
    private JPanel gameContainer;
    private StatsPanel statsPanel;
    private MapPanel mapPanel;
    private GameManager gameManager;
    private GameMap gameMap;
    private GameController gameController;

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
                e -> showHelp(),
                e -> System.exit(0)
        );

        // 2. Setup Panel
        setupPanel = new SetupPanel(
                e -> startGame(setupPanel.getPlayerNames(), setupPanel.getPlayerTypes(), setupPanel.getPlayerCharacters()),
                e -> showMenu()
        );

        // Help Panel
        helpPanel = new HelpPanel(e -> showMenu());

        // 3. Game Container (Stats + Map)
        gameContainer = new JPanel(new BorderLayout());
        statsPanel = new StatsPanel();
        gameContainer.add(statsPanel, BorderLayout.NORTH);

        // Add panels to CardLayout
        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(setupPanel, "SETUP");
        mainPanel.add(helpPanel, "HELP");
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

    private void showHelp() {
        setSize(GameConfig.MAIN_WINDOW_WIDTH, GameConfig.MAIN_WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        cardLayout.show(mainPanel, "HELP");
    }

    private void startGame(ArrayUnorderedList<String> playerNames, ArrayUnorderedList<Boolean> playerTypes, ArrayUnorderedList<String> playerCharacters) {
        // Initialize Game Logic
        GameMapLoader loader = new GameMapLoader();
        gameMap = loader.loadRandomMap();
        gameManager = new GameManager(gameMap);
        gameManager.setGameEventListener(this);

        // Add Players
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
        
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
            gameManager.addPlayer(name, colors[i % colors.length], isBot, characterType);
            i++;
        }

        // Initialize Map Panel
        if (mapPanel != null) {
            gameContainer.remove(mapPanel);
        }
        mapPanel = new MapPanel(gameManager);
        gameContainer.add(mapPanel, BorderLayout.CENTER);

        // Initialize Controller
        if (gameController != null) {
            removeKeyListener(gameController);
        }
        gameController = new GameController(gameManager, mapPanel);
        gameController.setStatsPanel(statsPanel);

        addKeyListener(gameController);

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
        SwingUtilities.invokeLater(() -> {
            // Generate JSON Report
            String reportFile = Utils.GameReport.generateReport(gameManager.getPlayers(), winner, gameManager.getGameMap().getMapName());
            
            // Show Report Dialog
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

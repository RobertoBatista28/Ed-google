package GameEngine;

import UI.MapPanel;
import UI.StatsPanel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameController extends KeyAdapter {

    // ----------------------------------------------------------------
    // Fields
    // ----------------------------------------------------------------
    private GameManager gameManager;
    private MapPanel mapPanel;
    private StatsPanel statsPanel;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    public GameController(GameManager gameManager, MapPanel mapPanel) {
        this.gameManager = gameManager;
        this.mapPanel = mapPanel;
    }

    // ----------------------------------------------------------------
    // Methods
    // ----------------------------------------------------------------
    public void setStatsPanel(StatsPanel statsPanel) {
        this.statsPanel = statsPanel;
    }

    // ----------------------------------------------------------------
    // Key Events
    // ----------------------------------------------------------------
    @Override
    public void keyPressed(KeyEvent e) {
        if (gameManager.getCurrentPlayer() == null || gameManager.getCurrentPlayer().isBot()) {
            return;
        }

        if (gameManager.isEnderPearlSelectionMode()) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT, KeyEvent.VK_UP -> gameManager.cycleEnderPearlTarget(-1);
                case KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN -> gameManager.cycleEnderPearlTarget(1);
                case KeyEvent.VK_ENTER -> gameManager.confirmEnderPearlUse();
            }
            mapPanel.repaint();
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> gameManager.movePlayer("UP");
            case KeyEvent.VK_DOWN -> gameManager.movePlayer("DOWN");
            case KeyEvent.VK_LEFT -> gameManager.movePlayer("LEFT");
            case KeyEvent.VK_RIGHT -> gameManager.movePlayer("RIGHT");
            case KeyEvent.VK_SPACE -> gameManager.interactWithLever();
            case KeyEvent.VK_1 -> gameManager.useItem(0);
            case KeyEvent.VK_2 -> gameManager.useItem(1);
            case KeyEvent.VK_3 -> gameManager.useItem(2);
        }
        mapPanel.repaint();
        if (statsPanel != null) {
            statsPanel.updateStats(gameManager.getPlayers(), gameManager.getCurrentPlayer());
        }
    }
}

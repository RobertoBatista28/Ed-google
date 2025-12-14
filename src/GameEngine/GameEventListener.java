package GameEngine;

import Models.Player;
import Models.Question;

/**
 * Interface for listening to game events.
 */
public interface GameEventListener {
    /**
     * Called when dice are rolled.
     *
     * @param playerName the name of the player who rolled the dice
     * @param die1       the value of the first die
     * @param die2       the value of the second die
     */
    void onDiceRolled(String playerName, int die1, int die2);

    /**
     * Called when a player moves.
     *
     * @param player the player who moved
     */
    void onPlayerMoved(Player player);

    /**
     * Called when the game is over.
     *
     * @param winner the player who won the game
     */
    void onGameOver(Player winner);

    /**
     * Called when a question is encountered.
     *
     * @param question the question encountered
     */
    void onQuestionEncountered(Question question);

    /**
     * Called when the game status changes.
     *
     * @param message the status message
     */
    void onGameStatus(String message);
}

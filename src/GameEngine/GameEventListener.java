package GameEngine;

import Models.Player;
import Models.Question;

public interface GameEventListener {
    void onDiceRolled(String playerName, int die1, int die2);
    void onPlayerMoved(Player player);
    void onGameOver(Player winner);
    void onQuestionEncountered(Question question);
    void onGameStatus(String message);
}

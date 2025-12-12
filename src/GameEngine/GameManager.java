package GameEngine;

import DataStructures.ArrayList.ArrayUnorderedList;
import DataStructures.Exceptions.EmptyCollectionException;
import DataStructures.Iterator;
import DataStructures.Queue.LinkedQueue;
import DataStructures.Queue.QueueADT;
import Models.Connection;
import Models.Player;
import Models.Room;
import Utils.GameConfig;

public class GameManager {

    private final GameMapGenerator gameMap;
    private final ArrayUnorderedList<Player> players;
    private final QueueADT<Player> turnQueue;
    private final QuestionManager questionManager;
    private final RandomEventManager randomEventManager;
    private GameEventListener gameEventListener;
    private boolean isEnderPearlSelectionMode = false;
    private Player selectedTargetPlayer = null;
    private int selectedTargetIndex = -1;
    private boolean isPaused = false;

    // ----------------------------------------------------------------
    // Constructor
    public GameManager(GameMapGenerator gameMap) {
        this.gameMap = gameMap;
        this.players = new ArrayUnorderedList<>();
        this.turnQueue = new LinkedQueue<>();
        this.questionManager = new QuestionManager(GameConfig.QUESTIONS_PATH);
        this.randomEventManager = new RandomEventManager();
    }

    // ----------------------------------------------------------------
    // Event Listener Setter
    public void setGameEventListener(GameEventListener listener) {
        this.gameEventListener = listener;
    }

    // ----------------------------------------------------------------
    // Adding players at starting positions
    public void addPlayer(String name, boolean isBot, String characterType) {
        ArrayUnorderedList<Room> entrances = gameMap.getEntrances();
        ArrayUnorderedList<Room> availableEntrances = new ArrayUnorderedList<>();

        Iterator<Room> it = entrances.iterator();
        while (it.hasNext()) {
            Room entrance = it.next();
            boolean occupied = false;

            Iterator<Player> playerIt = players.iterator();
            while (playerIt.hasNext()) {
                if (playerIt.next().getCurrentRoom().equals(entrance)) {
                    occupied = true;
                    break;
                }
            }

            if (!occupied) {
                availableEntrances.add(entrance);
            }
        }

        Room startRoom;
        if (!availableEntrances.isEmpty()) {
            int randomIndex = (int) (Math.random() * availableEntrances.size());
            startRoom = availableEntrances.get(randomIndex);
        } else {
            int randomIndex = (int) (Math.random() * entrances.size());
            startRoom = entrances.get(randomIndex);
        }

        Player newPlayer = new Player(name, startRoom, isBot, characterType);
        players.add(newPlayer);
        turnQueue.enqueue(newPlayer);
    }

    // ----------------------------------------------------------------
    // Game Flow Control
    public void startGame() {
        if (!players.isEmpty()) {
            rollDiceForCurrentPlayer();
        }
    }

    public void setPaused(boolean paused) {
        this.isPaused = paused;
    }

    // Dice Rolling
    private void rollDiceForCurrentPlayer() {
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer != null) {
            int die1 = (int) (Math.random() * 6) + 1;
            int die2 = (int) (Math.random() * 6) + 1;
            int total = die1 + die2;
            currentPlayer.setMoves(total);
            System.out.println(currentPlayer.getName() + " rolled " + die1 + " + " + die2 + " = " + total);

            if (gameEventListener != null) {
                gameEventListener.onDiceRolled(currentPlayer.getName(), die1, die2);
            }

            if (currentPlayer.isBot()) {
                new Thread(this::executeBotTurn).start();
            }
        }
    }

    // ----------------------------------------------------------------
    // AI Bot logic
    private void executeBotTurn() {
        while (isPaused) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                return;
            }
        }

        try {
            Thread.sleep(GameConfig.AI_INITIAL_DELAY);
        } catch (InterruptedException e) {
            return;
        }

        while (isPaused) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                return;
            }
        }

        Player bot = getCurrentPlayer();
        Room center = gameMap.getRoom(gameMap.getWidth() / 2, gameMap.getHeight() / 2);

        // Pre-calculate distances to center for heuristic
        int[][] distToCenter = gameMap.getDistancesTo(center);
        int currentDist = distToCenter[bot.getCurrentRoom().getX()][bot.getCurrentRoom().getY()];

        while (bot.getMoves() > 0 && bot == getCurrentPlayer()) {
            while (isPaused) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    return;
                }
            }

            // 1. Check for Lever in current room
            if (bot.getCurrentRoom().hasLever()) {
                Models.Lever lever = bot.getCurrentRoom().getLever();
                boolean useful = false;
                Iterator<Connection> targets = lever.getTargets().iterator();
                while (targets.hasNext()) {
                    if (targets.next().isLocked()) {
                        useful = true;
                        break;
                    }
                }

                if (useful) {
                    System.out.println("Bot found a useful lever. Pulling it.");
                    interactWithLever();
                    try {
                        Thread.sleep(GameConfig.AI_WALK_DELAY);
                    } catch (InterruptedException e) {
                    }
                    continue;
                }
            }

            // 2. Check if we should use Ender Pearl
            if (bot.getEnderPearlCount() > 0) {
                Player bestTarget = null;
                int bestTargetDist = currentDist;

                Iterator<Player> playerIt = players.iterator();
                while (playerIt.hasNext()) {
                    Player p = playerIt.next();
                    if (p == bot) {
                        continue;
                    }

                    int pDist = distToCenter[p.getCurrentRoom().getX()][p.getCurrentRoom().getY()];
                    if (pDist < currentDist && pDist < bestTargetDist) {
                        bestTarget = p;
                        bestTargetDist = pDist;
                    }
                }

                if (bestTarget != null) {
                    System.out.println("Bot " + bot.getName() + " using Ender Pearl to swap with " + bestTarget.getName());

                    // Perform swap
                    Room temp = bot.getCurrentRoom();
                    bot.setCurrentRoom(bestTarget.getCurrentRoom());
                    bestTarget.setCurrentRoom(temp);

                    consumeItem(bot, "Ender Pearl");
                    Utils.SoundPlayer.playTeleport();

                    if (gameEventListener != null) {
                        gameEventListener.onGameStatus("Bot " + bot.getName() + " usou Ender Pearl em " + bestTarget.getName() + "!");
                        gameEventListener.onPlayerMoved(bot);
                        gameEventListener.onPlayerMoved(bestTarget);
                    }

                    bot.setMoves(0);

                    try {
                        Thread.sleep(GameConfig.MOVEMENT_DURATION);
                    } catch (InterruptedException e) {
                    }
                    nextTurn();
                    return;
                }
            }

            // 3. Check if we should use Pickaxe
            if (bot.getPickaxeCount() > 0) {
                Room current = bot.getCurrentRoom();
                int bestSavings = 0;
                String bestDir = null;

                int[][] dirs = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
                String[] dirNames = {"UP", "DOWN", "LEFT", "RIGHT"};

                for (int i = 0; i < 4; i++) {
                    int nx = current.getX() + dirs[i][0];
                    int ny = current.getY() + dirs[i][1];
                    Room neighbor = gameMap.getRoom(nx, ny);

                    if (neighbor != null) {
                        Connection conn = getConnection(current, neighbor);
                        if (conn == null) {
                            int neighborDist = distToCenter[nx][ny];
                            if (neighborDist != Integer.MAX_VALUE) {
                                int savings = currentDist - neighborDist;
                                if (savings > 5 && savings > bestSavings) {
                                    bestSavings = savings;
                                    bestDir = dirNames[i];
                                }
                            }
                        }
                    }
                }

                if (bestDir != null) {
                    System.out.println("Bot deciding to break wall " + bestDir);
                    movePlayer(bestDir);
                    usePickaxe();
                    return;
                }
            }

            // 4. Check if we should go for a Pickaxe
            Room target = center;
            if (bot.getPickaxeCount() == 0 && currentDist > 10) {
                int[][] distFromBot = gameMap.getDistancesTo(bot.getCurrentRoom());
                ArrayUnorderedList<Room> pickaxes = gameMap.getPickaxeRooms();
                Iterator<Room> it = pickaxes.iterator();

                Room bestPickaxe = null;
                int minPickaxeDist = Integer.MAX_VALUE;

                while (it.hasNext()) {
                    Room p = it.next();
                    int d = distFromBot[p.getX()][p.getY()];
                    if (d < 5 && d < minPickaxeDist) {
                        minPickaxeDist = d;
                        bestPickaxe = p;
                    }
                }

                if (bestPickaxe != null) {
                    target = bestPickaxe;
                    System.out.println("Bot going for pickaxe at " + target.getX() + "," + target.getY());
                }
            }

            // 5. Calculate Path
            Iterator<Room> pathIt = gameMap.getShortestPathDijkstra(bot.getCurrentRoom(), target);

            if (pathIt.hasNext()) {
                pathIt.next();
            }

            if (!pathIt.hasNext()) {
                // Try to find a lever to go to
                Room leverTarget = findNearestUsefulLever(bot.getCurrentRoom());
                if (leverTarget != null) {
                    System.out.println("Bot " + bot.getName() + " is stuck! Going to lever at " + leverTarget.getX() + "," + leverTarget.getY());
                    pathIt = gameMap.getShortestPathDijkstra(bot.getCurrentRoom(), leverTarget);
                    if (pathIt.hasNext()) {
                        pathIt.next();
                    }
                }
            }

            if (!pathIt.hasNext()) {
                System.out.println("Bot " + bot.getName() + " is stuck! Trying random move.");
                pathIt = getRandomNeighbor(bot.getCurrentRoom());
            }

            if (pathIt.hasNext()) {
                try {
                    Thread.sleep(GameConfig.AI_WALK_DELAY);
                } catch (InterruptedException e) {
                    break;
                }

                Room next = pathIt.next();
                String direction = getDirection(bot.getCurrentRoom(), next);

                if (direction != null) {
                    movePlayer(direction);
                }
            } else {
                break;
            }
        }

        if (bot.getMoves() > 0 && bot == getCurrentPlayer()) {
            nextTurn();
        }
    }

    private Room findNearestUsefulLever(Room start) {
        int[][] distances = gameMap.getDistancesTo(start);
        Room bestLever = null;
        int minDistance = Integer.MAX_VALUE;

        for (int x = 0; x < gameMap.getWidth(); x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                Room r = gameMap.getRoom(x, y);
                if (r.hasLever()) {
                    int d = distances[x][y];
                    if (d != Integer.MAX_VALUE && d < minDistance) {
                        // Check if useful
                        boolean useful = false;
                        Iterator<Connection> targets = r.getLever().getTargets().iterator();
                        while (targets.hasNext()) {
                            if (targets.next().isLocked()) {
                                useful = true;
                                break;
                            }
                        }

                        if (useful) {
                            minDistance = d;
                            bestLever = r;
                        }
                    }
                }
            }
        }
        return bestLever;
    }

    // Get Random Neighbor (for stuck bots)
    private Iterator<Room> getRandomNeighbor(Room current) {
        ArrayUnorderedList<Room> neighbors = new ArrayUnorderedList<>();
        Iterator<Connection> it = current.getConnections().iterator();
        while (it.hasNext()) {
            Connection c = it.next();
            if (!c.isLocked()) {
                neighbors.add(c.getTo());
            }
        }

        if (!neighbors.isEmpty()) {
            int idx = (int) (Math.random() * neighbors.size());

            Iterator<Room> nIt = neighbors.iterator();
            for (int i = 0; i < idx; i++) {
                nIt.next();
            }

            ArrayUnorderedList<Room> path = new ArrayUnorderedList<>();
            path.add(nIt.next());
            return path.iterator();
        }
        return new ArrayUnorderedList<Room>().iterator();
    }

    // ----------------------------------------------------------------
    // Player movement and interaction Logic
    private String getDirection(Room from, Room to) {
        if (to.getX() > from.getX()) {
            return "RIGHT";
        }
        if (to.getX() < from.getX()) {
            return "LEFT";
        }
        if (to.getY() > from.getY()) {
            return "DOWN";
        }
        if (to.getY() < from.getY()) {
            return "UP";
        }
        return null;
    }

    public void movePlayer(String direction) {
        if (players.isEmpty()) {
            return;
        }

        Player currentPlayer = getCurrentPlayer();

        if (currentPlayer.getMoves() <= 0) {
            System.out.println("No moves left!");
            return;
        }

        Room current = currentPlayer.getCurrentRoom();
        boolean movingFromSoulSand = current.isSoulSand();
        int targetX = current.getX();
        int targetY = current.getY();

        switch (direction.toUpperCase()) {
            case "UP" ->
                targetY--;
            case "DOWN" ->
                targetY++;
            case "LEFT" ->
                targetX--;
            case "RIGHT" ->
                targetX++;
        }

        Room targetRoom = gameMap.getRoom(targetX, targetY);
        if (targetRoom == null) {
            return;
        }

        currentPlayer.setLastDirection(direction.toUpperCase());

        Connection conn = getConnection(current, targetRoom);
        if (conn != null) {
            if (conn.isLocked()) {
                System.out.println("Blocked! Wall is active.");
                return;
            }

            currentPlayer.setCurrentRoom(targetRoom);
            currentPlayer.moveTaken();
            currentPlayer.addToPath(targetRoom);

            if (movingFromSoulSand) {
                int currentMoves = currentPlayer.getMoves();
                int newMoves = currentMoves - 2;
                if (newMoves < 0) {
                    newMoves = 0;
                }
                currentPlayer.setMoves(newMoves);

                System.out.println("Lost 3 moves due to Soul Sand!");
                if (gameEventListener != null) {
                    gameEventListener.onGameStatus("Areia das almas, exigindo 3 movimentos!");
                }
            }

            System.out.println(currentPlayer.getName() + " moved to: " + targetRoom.getX() + "," + targetRoom.getY());

            if (gameEventListener != null) {
                gameEventListener.onPlayerMoved(currentPlayer);
            }

            // Interaction Handlers
            handlePickaxePickup(currentPlayer, targetRoom);
            handleEnderPearlPickup(currentPlayer, targetRoom);

            // Random Event Check
            Models.Event eventDef = randomEventManager.checkForRandomEvent(currentPlayer, gameMap, targetRoom);
            if (eventDef != null) {
                System.out.println("Random Event Triggered: " + eventDef.getName());
                if (gameEventListener != null) {
                    gameEventListener.onGameStatus("Evento: " + eventDef.getName());
                }

                if (eventDef.isStopGame()) {
                    currentPlayer.setMoves(0);
                    System.out.println("Event stopped the game for current player.");
                }
            }

            if (handleWinCondition(currentPlayer, targetRoom)) {
                return;
            }

            if (handleQuestionEncounter(currentPlayer, targetRoom)) {
                return;
            }

            if (currentPlayer.getMoves() <= 0) {
                final int delay = movingFromSoulSand ? (int) (GameConfig.MOVEMENT_DURATION * 6.0) : GameConfig.MOVEMENT_DURATION;

                new Thread(() -> {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    while (isPaused) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            return;
                        }
                    }

                    nextTurn();
                }).start();
            }
        } else {
            System.out.println("Blocked! No connection.");
        }
    }

    public void useItem(int index) {
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer == null || currentPlayer.getMoves() <= 0) {
            return;
        }

        Models.Item item = currentPlayer.getItem(index);
        if (item == null) {
            return;
        }

        if (item.getName().equals("Ender Pearl")) {
            startEnderPearlSelection();
            return;
        }

        if (item.getName().equals("Pickaxe")) {
            String dir = currentPlayer.getLastDirection();
            Room currentRoom = currentPlayer.getCurrentRoom();

            int targetX = currentRoom.getX();
            int targetY = currentRoom.getY();

            switch (dir) {
                case "UP" ->
                    targetY--;
                case "DOWN" ->
                    targetY++;
                case "LEFT" ->
                    targetX--;
                case "RIGHT" ->
                    targetX++;
            }

            Room targetRoom = gameMap.getRoom(targetX, targetY);
            if (targetRoom != null) {
                Connection conn = getConnection(currentRoom, targetRoom);
                if (conn == null) {
                    gameMap.breakWall(currentRoom, dir);
                    currentPlayer.useItem(index);
                    currentPlayer.incrementItemsUsed();
                    currentPlayer.incrementPickaxesUsed();
                    currentPlayer.setMoves(0);
                    Utils.SoundPlayer.playBreak();
                    System.out.println(currentPlayer.getName() + " used a pickaxe to break a wall " + dir);

                    if (gameEventListener != null) {
                        gameEventListener.onGameStatus(currentPlayer.getName() + " partiu uma parede!");
                        gameEventListener.onPlayerMoved(currentPlayer);
                    }

                    nextTurn();
                } else {
                    System.out.println("No wall to break there.");
                    if (gameEventListener != null) {
                        gameEventListener.onGameStatus("Não há parede para partir!");
                    }
                }
            } else {
                System.out.println("Cannot break outer bounds.");
                if (gameEventListener != null) {
                    gameEventListener.onGameStatus("Não podes partir os limites do mapa!");
                }
            }
        }
    }

    // ==================================================================================
    // OBJECT INTERACTION LOGIC (Rules & Responsiveness)
    // ----------------------------------------------------------------
    // Logic for Pickaxe: Pickup and Usage
    private void handlePickaxePickup(Player player, Room room) {
        if (room.hasPickaxe()) {
            Models.Item item = new Models.Item("Pickaxe", "Tool");
            if (player.addItem(item)) {
                player.incrementItemsCollected();
                player.incrementPickaxesCollected();
                room.setHasPickaxe(false);
                Utils.SoundPlayer.playPickup();
                System.out.println(player.getName() + " picked up a pickaxe!");
                if (gameEventListener != null) {
                    gameEventListener.onGameStatus(player.getName() + " apanhou uma picareta!");
                }
            } else {
                System.out.println("Inventory full!");
                if (gameEventListener != null) {
                    gameEventListener.onGameStatus("Inventário cheio!");
                }
            }
        }
    }

    public void usePickaxe() {
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer == null) {
            return;
        }

        Iterator<Models.Item> it = currentPlayer.getInventory().iterator();
        int index = 0;
        int foundIndex = -1;
        while (it.hasNext()) {
            if (it.next().getName().equals("Pickaxe")) {
                foundIndex = index;
                break;
            }
            index++;
        }

        if (foundIndex != -1) {
            useItem(foundIndex);
        } else {
            System.out.println("No pickaxes!");
            if (gameEventListener != null) {
                gameEventListener.onGameStatus("Não tens picaretas!");
            }
        }
    }

    // ----------------------------------------------------------------
    // Logic for Ender Pearl
    private void handleEnderPearlPickup(Player player, Room room) {
        if (room.hasEnderPearl()) {
            Models.Item item = new Models.Item("Ender Pearl", "Item");
            if (player.addItem(item)) {
                player.incrementItemsCollected();
                player.incrementEnderPearlsCollected();
                room.setHasEnderPearl(false);
                Utils.SoundPlayer.playPickup();
                System.out.println(player.getName() + " picked up an ender pearl!");
                if (gameEventListener != null) {
                    gameEventListener.onGameStatus(player.getName() + " apanhou uma ender pearl!");
                }
            } else {
                System.out.println("Inventory full!");
                if (gameEventListener != null) {
                    gameEventListener.onGameStatus("Inventário cheio!");
                }
            }
        }
    }

    public boolean isEnderPearlSelectionMode() {
        return isEnderPearlSelectionMode;
    }

    public Player getSelectedTargetPlayer() {
        return selectedTargetPlayer;
    }

    public void startEnderPearlSelection() {
        if (players.size() <= 1) {
            if (gameEventListener != null) {
                gameEventListener.onGameStatus("Não há outros jogadores!");
            }
            return;
        }
        isEnderPearlSelectionMode = true;
        cycleEnderPearlTarget(1);
        if (gameEventListener != null) {
            gameEventListener.onGameStatus("Selecione um alvo e pressione ENTER");
        }
    }

    private int getCurrentPlayerIndex() {
        Player current = getCurrentPlayer();
        if (current == null) {
            return -1;
        }
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i) == current) {
                return i;
            }
        }
        return -1;
    }

    public void cycleEnderPearlTarget(int direction) {
        if (!isEnderPearlSelectionMode) {
            return;
        }

        int count = players.size();
        int currentPlayerIndex = getCurrentPlayerIndex();
        int startIdx = (selectedTargetIndex == -1) ? currentPlayerIndex : selectedTargetIndex;
        int current = startIdx;

        int attempts = 0;
        do {
            current = (current + direction + count) % count;
            attempts++;
        } while (current == currentPlayerIndex && attempts < count);

        if (current != currentPlayerIndex) {
            selectedTargetIndex = current;
            selectedTargetPlayer = players.get(selectedTargetIndex);
        }
    }

    public void confirmEnderPearlUse() {
        if (!isEnderPearlSelectionMode || selectedTargetPlayer == null) {
            return;
        }

        Player currentPlayer = getCurrentPlayer();
        Player target = selectedTargetPlayer;

        Room temp = currentPlayer.getCurrentRoom();
        currentPlayer.setCurrentRoom(target.getCurrentRoom());
        target.setCurrentRoom(temp);

        currentPlayer.addToPath(currentPlayer.getCurrentRoom());
        target.addToPath(target.getCurrentRoom());

        consumeItem(currentPlayer, "Ender Pearl");
        currentPlayer.incrementItemsUsed();
        currentPlayer.incrementEnderPearlsUsed();
        Utils.SoundPlayer.playTeleport();

        isEnderPearlSelectionMode = false;
        selectedTargetPlayer = null;
        selectedTargetIndex = -1;

        currentPlayer.setMoves(0);

        if (gameEventListener != null) {
            gameEventListener.onGameStatus("Troca realizada com " + target.getName() + "!");
            gameEventListener.onPlayerMoved(currentPlayer);
            gameEventListener.onPlayerMoved(target);
        }

        nextTurn();
    }

    private void consumeItem(Player p, String itemName) {
        Iterator<Models.Item> it = p.getInventory().iterator();
        int index = 0;
        while (it.hasNext()) {
            if (it.next().getName().equals(itemName)) {
                p.useItem(index);
                return;
            }
            index++;
        }
    }

    // ----------------------------------------------------------------
    // Logic for Questions
    private boolean handleQuestionEncounter(Player player, Room room) {
        if (room.hasQuestion()) {
            Models.Question q = questionManager.getNextQuestion();

            if (player.isBot()) {
                if (gameEventListener != null) {
                    gameEventListener.onGameStatus("Bot " + player.getName() + " está a pensar...");
                }

                while (isPaused) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        return true;
                    }
                }

                try {
                    Thread.sleep((long) GameConfig.AI_QUESTIONS_THINKING);
                } catch (InterruptedException e) {
                }

                while (isPaused) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        return true;
                    }
                }

                boolean correct = Math.random() < GameConfig.AI_QUESTIONS_RATE;
                if (correct) {
                    if (gameEventListener != null) {
                        gameEventListener.onGameStatus("Bot " + player.getName() + " acertou na pergunta!");
                    }
                } else {
                    if (gameEventListener != null) {
                        gameEventListener.onGameStatus("Bot " + player.getName() + " errou na pergunta!");
                    }
                }
                handleQuestionResult(correct);
                return true;
            }

            if (gameEventListener != null && q != null) {
                gameEventListener.onQuestionEncountered(q);
                return true;
            }
        }
        return false;
    }

    public void handleQuestionResult(boolean correct) {
        Player currentPlayer = getCurrentPlayer();
        Room currentRoom = currentPlayer.getCurrentRoom();

        currentRoom.setHasQuestion(false);

        if (correct) {
            currentPlayer.incrementQuestionsCorrect();
            System.out.println("Correct answer! Continue turn.");
            if (currentPlayer.getMoves() == 0) {
                nextTurn();
            }
        } else {
            currentPlayer.incrementQuestionsIncorrect();
            System.out.println("Wrong answer! Turn over.");
            currentPlayer.setMoves(0);
            nextTurn();
        }
    }

    // ----------------------------------------------------------------
    // Logic for Levers
    public void interactWithLever() {
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer == null || currentPlayer.getMoves() <= 0) {
            return;
        }

        Room currentRoom = currentPlayer.getCurrentRoom();
        if (currentRoom.hasLever()) {
            currentRoom.getLever().toggle();
            currentPlayer.incrementLeverInteractions();
            if (currentRoom.getLever().isActive()) {
                Utils.SoundPlayer.playLeverOn();
            } else {
                Utils.SoundPlayer.playLeverOff();
            }
            currentPlayer.moveTaken();
            System.out.println(currentPlayer.getName() + " toggled a lever!");

            if (gameEventListener != null) {
                gameEventListener.onPlayerMoved(currentPlayer);
            }

            if (currentPlayer.getMoves() == 0) {
                nextTurn();
            }
        }
    }

    // ==================================================================================
    // GAME LOGIC HELPERS
    // ----------------------------------------------------------------
    // Logic for Win Condition
    private boolean handleWinCondition(Player player, Room room) {
        if (room.isCenter()) {
            if (gameEventListener != null) {
                gameEventListener.onGameOver(player);
            }
            player.setMoves(0);
            return true;
        }
        return false;
    }

    // Get Connection Helper
    private Connection getConnection(Room from, Room to) {
        Iterator<Connection> it = from.getConnections().iterator();
        while (it.hasNext()) {
            Connection c = it.next();
            if (c.getTo().equals(to)) {
                return c;
            }
        }
        return null;
    }

    // Get Current Player Helper
    public Player getCurrentPlayer() {
        if (turnQueue.isEmpty()) {
            return null;
        }
        try {
            return turnQueue.first();
        } catch (EmptyCollectionException e) {
            return null;
        }
    }

    public void nextTurn() {
        if (turnQueue.isEmpty()) {
            return;
        }
        try {
            Player current = turnQueue.dequeue();
            turnQueue.enqueue(current);
        } catch (EmptyCollectionException e) {
            System.err.println("Error rotating turn queue: " + e.getMessage());
        }
        rollDiceForCurrentPlayer();
    }

    public ArrayUnorderedList<Player> getPlayers() {
        return players;
    }

    public GameMapGenerator getGameMap() {
        return gameMap;
    }
}

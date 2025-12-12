package GameEngine;

import Models.Lever;
import Models.Room;
import Utils.GameConfig;
import java.awt.Point;

public class GameEditLogic {

    private final int ROWS = GameConfig.MAP_HEIGHT;
    private final int COLS = GameConfig.MAP_WIDTH;

    private Room[][] grid;
    private boolean[][] hWalls;
    private boolean[][] vWalls;

    private Point pendingLeverPos = null;

    public GameEditLogic() {
        this.grid = new Room[COLS][ROWS];
        this.hWalls = new boolean[COLS][ROWS + 1];
        this.vWalls = new boolean[COLS + 1][ROWS];
        initializeGrid();
    }

    public Room[][] getGrid() {
        return grid;
    }

    public boolean[][] getHWalls() {
        return hWalls;
    }

    public boolean[][] getVWalls() {
        return vWalls;
    }

    public Point getPendingLeverPos() {
        return pendingLeverPos;
    }

    public void setPendingLeverPos(Point pendingLeverPos) {
        this.pendingLeverPos = pendingLeverPos;
    }

    public void initializeGrid() {
        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                boolean isCorner = (x == 0 && y == 0)
                        || (x == COLS - 1 && y == 0)
                        || (x == 0 && y == ROWS - 1)
                        || (x == COLS - 1 && y == ROWS - 1);

                boolean isCenter = (x >= 9 && x <= 11) && (y >= 9 && y <= 11);

                grid[x][y] = new Room("Room " + x + "-" + y, x, y, isCorner, isCenter);
            }
        }
    }

    public GameMapGenerator createGameMapFromEditor() {
        GameMapGenerator map = new GameMapGenerator(COLS, ROWS, false);

        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                Room editorRoom = grid[x][y];
                Room newRoom = new Room(editorRoom.getName(), x, y, editorRoom.isEntrance(), editorRoom.isCenter());
                newRoom.setHasPickaxe(editorRoom.hasPickaxe());
                newRoom.setHasEnderPearl(editorRoom.hasEnderPearl());
                newRoom.setHasQuestion(editorRoom.hasQuestion());
                map.setRoom(x, y, newRoom);
            }
        }

        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y <= ROWS; y++) {
                boolean isWall = hWalls[x][y];
                boolean isTarget = isWallTargeted(x, y, false);

                if (!isWall || isTarget) {
                    if (y > 0 && y < ROWS) {
                        map.addConnection(x, y - 1, x, y, isTarget);
                        map.addConnection(x, y, x, y - 1, isTarget);
                    }
                }
            }
        }

        for (int x = 0; x <= COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                boolean isWall = vWalls[x][y];
                boolean isTarget = isWallTargeted(x, y, true);

                if (!isWall || isTarget) {
                    if (x > 0 && x < COLS) {
                        map.addConnection(x - 1, y, x, y, isTarget);
                        map.addConnection(x, y, x - 1, y, isTarget);
                    }
                }
            }
        }

        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                Room editorRoom = grid[x][y];
                if (editorRoom.getLever() != null) {
                    Lever editorLever = editorRoom.getLever();
                    Lever newLever = new Lever();

                    DataStructures.Iterator<Models.Connection> it = editorLever.getTargets().iterator();
                    while (it.hasNext()) {
                        Models.Connection c = it.next();
                        Room from = map.getRoom(c.getFrom().getX(), c.getFrom().getY());
                        Room to = map.getRoom(c.getTo().getX(), c.getTo().getY());

                        Models.Connection mapConn = map.getNetwork().getConnection(from, to);
                        if (mapConn != null) {
                            newLever.addTarget(mapConn);
                        }
                        
                        Models.Connection mapConnReverse = map.getNetwork().getConnection(to, from);
                        if (mapConnReverse != null) {
                            newLever.addTarget(mapConnReverse);
                        }
                    }
                    map.getRoom(x, y).setLever(newLever);
                }
            }
        }

        return map;
    }

    public boolean doesLeverTargetWall(Lever lever, int wallX, int wallY, boolean isVertical) {
        DataStructures.Iterator<Models.Connection> it = lever.getTargets().iterator();
        while (it.hasNext()) {
            Models.Connection c = it.next();
            int x1 = c.getFrom().getX();
            int y1 = c.getFrom().getY();
            int x2 = c.getTo().getX();
            int y2 = c.getTo().getY();

            if (isVertical) {
                if (y1 == wallY && y2 == wallY && ((x1 == wallX - 1 && x2 == wallX) || (x1 == wallX && x2 == wallX - 1))) {
                    return true;
                }
            } else {
                if (x1 == wallX && x2 == wallX && ((y1 == wallY - 1 && y2 == wallY) || (y1 == wallY && y2 == wallY - 1))) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isWallTargeted(int wallX, int wallY, boolean isVertical) {
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS; j++) {
                Room r = grid[i][j];
                if (r.hasLever() && doesLeverTargetWall(r.getLever(), wallX, wallY, isVertical)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void removeLeverLinkedToWall(int wallX, int wallY, boolean isVertical) {
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS; j++) {
                Room r = grid[i][j];
                if (r.hasLever() && doesLeverTargetWall(r.getLever(), wallX, wallY, isVertical)) {
                    r.setLever(null);
                }
            }
        }
    }

    public void clearMap() {
        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                Room r = grid[x][y];
                if (r != null) {
                    r.setHasPickaxe(false);
                    r.setHasEnderPearl(false);
                    r.setHasQuestion(false);
                    r.setLever(null);
                }
            }
        }
        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y <= ROWS; y++) {
                hWalls[x][y] = false;
            }
        }
        for (int x = 0; x <= COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                vWalls[x][y] = false;
            }
        }
    }

    public void loadFromGameMap(GameMapGenerator map) {
        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y <= ROWS; y++) {
                hWalls[x][y] = true;
            }
        }
        for (int x = 0; x <= COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                vWalls[x][y] = true;
            }
        }

        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                Room gameRoom = map.getRoom(x, y);
                Room editorRoom = grid[x][y];

                if (gameRoom == null) {
                    continue;
                }

                editorRoom.setHasPickaxe(gameRoom.hasPickaxe());
                editorRoom.setHasEnderPearl(gameRoom.hasEnderPearl());
                editorRoom.setHasQuestion(gameRoom.hasQuestion());
                editorRoom.setLever(gameRoom.getLever());

                java.util.List<Room> accessibleNeighbors = map.getNetwork().getAccessibleNeighbors(gameRoom);
                for (Room target : accessibleNeighbors) {
                    int tx = target.getX();
                    int ty = target.getY();

                    if (tx == x + 1 && ty == y) {
                        vWalls[x + 1][y] = false;
                    } else if (tx == x - 1 && ty == y) {
                        vWalls[x][y] = false;
                    } else if (tx == x && ty == y + 1) {
                        hWalls[x][y + 1] = false;
                    } else if (tx == x && ty == y - 1) {
                        hWalls[x][y] = false;
                    }
                }
            }
        }
    }

    public boolean cancelLeverPlacement() {
        if (pendingLeverPos != null) {
            grid[pendingLeverPos.x][pendingLeverPos.y].setLever(null);
            pendingLeverPos = null;
            return true;
        }
        return false;
    }

    public boolean modifyCell(int x, int y, int selectedTool) {
        boolean isCorner = (x == 0 && y == 0) || (x == 0 && y == ROWS - 1)
                || (x == COLS - 1 && y == 0) || (x == COLS - 1 && y == ROWS - 1);
        boolean isCenter = (x >= 9 && x <= 11) && (y >= 9 && y <= 11);

        if (isCorner || isCenter) {
            return false;
        }
        Room room = grid[x][y];
        if (room == null) {
            return false;
        }

        boolean toolsDisabled = false;

        switch (selectedTool) {
            case GameConfig.MAP_EDITOR_TOOL_PICKAXE -> {
                room.setHasPickaxe(!room.hasPickaxe());
                room.setHasEnderPearl(false);
                room.setHasQuestion(false);
                room.setLever(null);
            }
            case GameConfig.MAP_EDITOR_TOOL_ENDERPEARL -> {
                room.setHasEnderPearl(!room.hasEnderPearl());
                room.setHasPickaxe(false);
                room.setHasQuestion(false);
                room.setLever(null);
            }
            case GameConfig.MAP_EDITOR_TOOL_LEVER -> {
                if (room.getLever() == null) {
                    room.setLever(new Lever());
                    pendingLeverPos = new Point(x, y);
                    toolsDisabled = true;
                } else {
                    room.setLever(null);
                    pendingLeverPos = null;
                    toolsDisabled = false;
                }
                room.setHasPickaxe(false);
                room.setHasEnderPearl(false);
                room.setHasQuestion(false);
            }
            case GameConfig.MAP_EDITOR_TOOL_QUESTION -> {
                room.setHasQuestion(!room.hasQuestion());
                room.setHasPickaxe(false);
                room.setHasEnderPearl(false);
                room.setLever(null);
            }
            default -> {
            }
        }
        return toolsDisabled;
    }

    public boolean linkLeverToWall(Point leverPos, int wallX, int wallY, boolean isVertical) {
        // Verifica se a parede existe
        if (isVertical) {
            if (!vWalls[wallX][wallY]) {
                return false;
            }
        } else {
            if (!hWalls[wallX][wallY]) {
                return false;
            }
        }

        // VERIFICAÇÃO IMPORTANTE: Se a parede já está ligada a OUTRA alavanca
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS; j++) {
                // Ignora a alavanca atual (pendente)
                if (i == leverPos.x && j == leverPos.y) {
                    continue;
                }

                Room r = grid[i][j];
                if (r.hasLever() && doesLeverTargetWall(r.getLever(), wallX, wallY, isVertical)) {
                    // Parede já conectada a OUTRA alavanca - cancela operação
                    grid[leverPos.x][leverPos.y].setLever(null);
                    pendingLeverPos = null;
                    return true; // Retorna true para desbloquear o menu
                }
            }
        }

        Room leverRoom = grid[leverPos.x][leverPos.y];
        Lever lever = leverRoom.getLever();
        if (lever == null) {
            pendingLeverPos = null;
            return true;
        }

        Room r1, r2;
        if (isVertical) {
            r1 = grid[wallX - 1][wallY];
            r2 = grid[wallX][wallY];
        } else {
            r1 = grid[wallX][wallY - 1];
            r2 = grid[wallX][wallY];
        }

        Models.Connection c1 = new Models.Connection(r1, r2, true, null);
        Models.Connection c2 = new Models.Connection(r2, r1, true, null);

        lever.addTarget(c1);
        lever.addTarget(c2);

        pendingLeverPos = null;
        return true;
    }

    public void toggleVWall(int x, int y) {
        vWalls[x][y] = !vWalls[x][y];
        if (!vWalls[x][y]) {
            removeLeverLinkedToWall(x, y, true);
        }
    }

    public void toggleHWall(int x, int y) {
        hWalls[x][y] = !hWalls[x][y];
        if (!hWalls[x][y]) {
            removeLeverLinkedToWall(x, y, false);
        }
    }
}

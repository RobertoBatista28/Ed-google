package GameEngine;

import Models.Lever;
import Models.Room;
import Utils.GameConfig;
import java.awt.Point;

/**
 * Logic for the map editor, handling grid initialization and map creation.
 */
public class GameEditLogic {

    private final int ROWS = GameConfig.MAP_HEIGHT;
    private final int COLS = GameConfig.MAP_WIDTH;

    private Room[][] grid;
    private boolean[][] hWalls;
    private boolean[][] vWalls;

    private Point pendingLeverPos = null;

    /**
     * Constructor for GameEditLogic.
     * Initializes the grid and walls.
     */
    public GameEditLogic() {
        this.grid = new Room[COLS][ROWS];
        this.hWalls = new boolean[COLS][ROWS + 1];
        this.vWalls = new boolean[COLS + 1][ROWS];
        initializeGrid();
    }

    /**
     * Retrieves the room grid representing the current map layout.
     * Each cell contains room properties and item/lever placements.
     *
     * @return the 2D array of rooms in the editor grid
     */
    public Room[][] getGrid() {
        return grid;
    }

    /**
     * Retrieves the horizontal walls array representing walls between vertically adjacent rooms.
     * hWalls[x][y] represents a wall between rooms (x, y-1) and (x, y).
     *
     * @return the 2D boolean array of horizontal walls
     */
    public boolean[][] getHWalls() {
        return hWalls;
    }

    /**
     * Retrieves the vertical walls array representing walls between horizontally adjacent rooms.
     * vWalls[x][y] represents a wall between rooms (x-1, y) and (x, y).
     *
     * @return the 2D boolean array of vertical walls
     */
    public boolean[][] getVWalls() {
        return vWalls;
    }

    /**
     * Retrieves the current pending lever position waiting for wall link confirmation.
     * When a lever is placed, it enters "pending" state until linked to a wall.
     *
     * @return the pending lever position as a Point, or null if no lever is pending
     */
    public Point getPendingLeverPos() {
        return pendingLeverPos;
    }

    /**
     * Sets the pending lever position for the active lever awaiting wall link confirmation.
     *
     * @param pendingLeverPos the position of the lever being placed, or null to clear
     */
    public void setPendingLeverPos(Point pendingLeverPos) {
        this.pendingLeverPos = pendingLeverPos;
    }

    /**
     * Initializes the editor grid with default rooms for a blank map.
     * Creates rooms for all grid positions and marks corners as entrance rooms
     * and the center 3x3 area as the central zone. Walls are not initialized here.
     */
    public void initializeGrid() {
        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                // Mark corner rooms as entrance points for player spawn
                boolean isCorner = (x == 0 && y == 0)
                        || (x == COLS - 1 && y == 0)
                        || (x == 0 && y == ROWS - 1)
                        || (x == COLS - 1 && y == ROWS - 1);

                // Mark center 3x3 area (from 9-11) as central zone
                boolean isCenter = (x >= 9 && x <= 11) && (y >= 9 && y <= 11);

                grid[x][y] = new Room("Room " + x + "-" + y, x, y, isCorner, isCenter);
            }
        }
    }

    /**
     * Converts the current editor state into a playable GameMapGenerator instance.
     * Creates a new map with all rooms, applies item and lever placements, and
     * converts wall states into bidirectional connections. Reconstructs lever-to-wall
     * relationships to ensure puzzle mechanics remain intact.
     *
     * @return a new GameMapGenerator configured from the current editor state
     */
    public GameMapGenerator createGameMapFromEditor() {
        // Create a new map without automatic generation
        GameMapGenerator map = new GameMapGenerator(COLS, ROWS, false);

        // Copy all rooms from editor grid to the new map
        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                Room editorRoom = grid[x][y];
                Room newRoom = new Room(editorRoom.getName(), x, y, editorRoom.isEntrance(), editorRoom.isCenter());
                // Copy item placements from editor to new room
                newRoom.setHasPickaxe(editorRoom.hasPickaxe());
                newRoom.setHasEnderPearl(editorRoom.hasEnderPearl());
                newRoom.setHasQuestion(editorRoom.hasQuestion());
                map.setRoom(x, y, newRoom);
            }
        }

        // Convert horizontal walls to vertical connections
        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y <= ROWS; y++) {
                boolean isWall = hWalls[x][y];
                boolean isTarget = isWallTargeted(x, y, false);

                // Create connection if wall is open or if wall is targeted by a lever
                if (!isWall || isTarget) {
                    if (y > 0 && y < ROWS) {
                        // Create bidirectional connection between vertically adjacent rooms
                        map.addConnection(x, y - 1, x, y, isTarget);
                        map.addConnection(x, y, x, y - 1, isTarget);
                    }
                }
            }
        }

        // Convert vertical walls to horizontal connections
        for (int x = 0; x <= COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                boolean isWall = vWalls[x][y];
                boolean isTarget = isWallTargeted(x, y, true);

                // Create connection if wall is open or if wall is targeted by a lever
                if (!isWall || isTarget) {
                    if (x > 0 && x < COLS) {
                        // Create bidirectional connection between horizontally adjacent rooms
                        map.addConnection(x - 1, y, x, y, isTarget);
                        map.addConnection(x, y, x - 1, y, isTarget);
                    }
                }
            }
        }

        // Reconstruct lever-to-wall relationships in the new map
        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                Room editorRoom = grid[x][y];
                if (editorRoom.getLever() != null) {
                    Lever editorLever = editorRoom.getLever();
                    Lever newLever = new Lever();

                    // Iterate through all target connections of the editor lever
                    DataStructures.Iterator<Models.Connection> it = editorLever.getTargets().iterator();
                    while (it.hasNext()) {
                        Models.Connection c = it.next();
                        // Get corresponding rooms in new map
                        Room from = map.getRoom(c.getFrom().getX(), c.getFrom().getY());
                        Room to = map.getRoom(c.getTo().getX(), c.getTo().getY());

                        // Find the corresponding connection in the new map and add it to new lever
                        DataStructures.Iterator<Models.Connection> mapConnIt = map.getGraph().getConnections(from).iterator();
                        while (mapConnIt.hasNext()) {
                            Models.Connection mapConn = mapConnIt.next();
                            if (mapConn.getTo() == to) {
                                newLever.addTarget(mapConn);
                                break;
                            }
                        }

                        // Also add reverse connection if it exists
                        DataStructures.Iterator<Models.Connection> mapConnIt2 = map.getGraph().getConnections(to).iterator();
                        while (mapConnIt2.hasNext()) {
                            Models.Connection mapConn = mapConnIt2.next();
                            if (mapConn.getTo() == from) {
                                newLever.addTarget(mapConn);
                                break;
                            }
                        }
                    }
                    map.getRoom(x, y).setLever(newLever);
                }
            }
        }

        return map;
    }

    /**
     * Determines whether a specific lever targets a particular wall.
     * Checks if any of the lever's target connections correspond to the given wall
     * by comparing room coordinates and wall orientation.
     *
     * @param lever the lever to check
     * @param wallX the x-coordinate of the wall
     * @param wallY the y-coordinate of the wall
     * @param isVertical true if checking vertical wall, false for horizontal wall
     * @return true if the lever targets this wall, false otherwise
     */
    public boolean doesLeverTargetWall(Lever lever, int wallX, int wallY, boolean isVertical) {
        // Iterate through all connections that this lever can toggle
        DataStructures.Iterator<Models.Connection> it = lever.getTargets().iterator();
        while (it.hasNext()) {
            Models.Connection c = it.next();
            int x1 = c.getFrom().getX();
            int y1 = c.getFrom().getY();
            int x2 = c.getTo().getX();
            int y2 = c.getTo().getY();

            if (isVertical) {
                // Vertical wall: check if connection spans horizontally at wallY between x1 and x2
                if (y1 == wallY && y2 == wallY && ((x1 == wallX - 1 && x2 == wallX) || (x1 == wallX && x2 == wallX - 1))) {
                    return true;
                }
            } else {
                // Horizontal wall: check if connection spans vertically at wallX between y1 and y2
                if (x1 == wallX && x2 == wallX && ((y1 == wallY - 1 && y2 == wallY) || (y1 == wallY && y2 == wallY - 1))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks whether any lever in the map targets a specific wall.
     * Scans all rooms in the editor grid to find levers and checks if any
     * target the given wall using doesLeverTargetWall method.
     *
     * @param wallX the x-coordinate of the wall
     * @param wallY the y-coordinate of the wall
     * @param isVertical true if checking vertical wall, false for horizontal wall
     * @return true if any lever in the map targets this wall, false otherwise
     */
    public boolean isWallTargeted(int wallX, int wallY, boolean isVertical) {
        // Iterate through all rooms in the editor grid
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS; j++) {
                Room r = grid[i][j];
                // Check if this room has a lever and if it targets the specified wall
                if (r.hasLever() && doesLeverTargetWall(r.getLever(), wallX, wallY, isVertical)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Removes any lever that targets a specific wall when that wall is toggled.
     * This maintains puzzle validity by preventing levers from controlling
     * non-existent connections when walls change states.
     *
     * @param wallX the x-coordinate of the wall
     * @param wallY the y-coordinate of the wall
     * @param isVertical true if toggling vertical wall, false for horizontal wall
     */
    public void removeLeverLinkedToWall(int wallX, int wallY, boolean isVertical) {
        // Scan all rooms to find and remove levers that target this wall
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS; j++) {
                Room r = grid[i][j];
                // If this room has a lever and it targets the specified wall, remove the lever
                if (r.hasLever() && doesLeverTargetWall(r.getLever(), wallX, wallY, isVertical)) {
                    r.setLever(null);
                }
            }
        }
    }

    /**
     * Clears all map content by removing items, levers, and walls.
     * Resets the map to a blank slate with all walls removed (complete maze with all passages open).
     * Used when starting a fresh map design in the editor.
     */
    public void clearMap() {
        // Clear all items and levers from rooms
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
        // Remove all horizontal walls (set to false means passages are open)
        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y <= ROWS; y++) {
                hWalls[x][y] = false;
            }
        }
        // Remove all vertical walls (set to false means passages are open)
        for (int x = 0; x <= COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                vWalls[x][y] = false;
            }
        }
    }

    /**
     * Initializes editor grid and walls from an existing GameMapGenerator.
     * Reconstructs editor state by setting walls based on available connections
     * in the game map, then overlays item and lever states to preserve puzzle configuration.
     * Walls default to true (complete maze) and are opened only where connections exist.
     *
     * @param map the GameMapGenerator to load editor state from
     */
    public void loadFromGameMap(GameMapGenerator map) {
        // Initialize all walls to true (complete maze with no connections)
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

        // Iterate through each room in the game map to reconstruct editor state
        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                Room gameRoom = map.getRoom(x, y);
                Room editorRoom = grid[x][y];

                if (gameRoom == null) {
                    continue;
                }

                // Copy item placements and levers from game map to editor
                editorRoom.setHasPickaxe(gameRoom.hasPickaxe());
                editorRoom.setHasEnderPearl(gameRoom.hasEnderPearl());
                editorRoom.setHasQuestion(gameRoom.hasQuestion());
                editorRoom.setLever(gameRoom.getLever());

                // Iterate through all outgoing connections from current room
                var conns = map.getGraph().getConnections(gameRoom);
                var it = conns.iterator();
                while (it.hasNext()) {
                    var conn = it.next();
                    Room target = conn.getTo();
                    int tx = target.getX();
                    int ty = target.getY();

                    // Open walls where unlocked connections exist; keep locked walls closed
                    if (!conn.isLocked()) {
                        if (tx == x + 1 && ty == y) {
                            // Connection to the right: open vertical wall between (x,y) and (x+1,y)
                            vWalls[x + 1][y] = false;
                        } else if (tx == x - 1 && ty == y) {
                            // Connection to the left: open vertical wall between (x-1,y) and (x,y)
                            vWalls[x][y] = false;
                        } else if (tx == x && ty == y + 1) {
                            // Connection downward: open horizontal wall between (x,y) and (x,y+1)
                            hWalls[x][y + 1] = false;
                        } else if (tx == x && ty == y - 1) {
                            // Connection upward: open horizontal wall between (x,y-1) and (x,y)
                            hWalls[x][y] = false;
                        }
                    }
                }
            }
        }
    }

    /**
     * Cancels the pending lever placement in the editor.
     * Removes the lever from its temporary location and clears the pending state.
     *
     * @return true if a pending lever placement was cancelled, false if no pending placement existed
     */
    public boolean cancelLeverPlacement() {
        if (pendingLeverPos != null) {
            // Remove the lever from the room it was temporarily placed in
            grid[pendingLeverPos.x][pendingLeverPos.y].setLever(null);
            // Clear the pending position tracker
            pendingLeverPos = null;
            return true;
        }
        return false;
    }

    /**
     * Modifies a cell's content based on the selected editor tool.
     * Enforces mutual exclusivity among items and levers - placing one type removes others.
     * Protected cells (corners and center area) cannot be modified.
     *
     * @param x the x-coordinate of the cell to modify
     * @param y the y-coordinate of the cell to modify
     * @param selectedTool the editor tool identifier (pickaxe, ender pearl, lever, or question)
     * @return true if lever placement mode should be enabled (waiting for wall link), false otherwise
     */
    public boolean modifyCell(int x, int y, int selectedTool) {
        // Check if attempting to modify protected corner or center rooms
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

        // Tool-specific handling with mutual exclusivity enforcement
        switch (selectedTool) {
            case GameConfig.MAP_EDITOR_TOOL_PICKAXE -> {
                // Toggle pickaxe and clear all other items/levers
                room.setHasPickaxe(!room.hasPickaxe());
                room.setHasEnderPearl(false);
                room.setHasQuestion(false);
                room.setLever(null);
            }
            case GameConfig.MAP_EDITOR_TOOL_ENDERPEARL -> {
                // Toggle ender pearl and clear all other items/levers
                room.setHasEnderPearl(!room.hasEnderPearl());
                room.setHasPickaxe(false);
                room.setHasQuestion(false);
                room.setLever(null);
            }
            case GameConfig.MAP_EDITOR_TOOL_LEVER -> {
                // Lever placement enters pending state waiting for wall link
                if (room.getLever() == null) {
                    // Place new lever and set pending position
                    room.setLever(new Lever());
                    pendingLeverPos = new Point(x, y);
                    toolsDisabled = true;
                } else {
                    // Remove existing lever
                    room.setLever(null);
                    pendingLeverPos = null;
                    toolsDisabled = false;
                }
                // Clear all items when placing/removing lever
                room.setHasPickaxe(false);
                room.setHasEnderPearl(false);
                room.setHasQuestion(false);
            }
            case GameConfig.MAP_EDITOR_TOOL_QUESTION -> {
                // Toggle question marker and clear all other items/levers
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

    /**
     * Links a pending lever to a wall, creating bidirectional locked connections.
     * Validates that the wall exists and is not already controlled by another lever.
     * Only one lever may target any given wall to maintain clear puzzle relationships.
     *
     * @param leverPos the position of the lever to link
     * @param wallX the x-coordinate of the wall
     * @param wallY the y-coordinate of the wall
     * @param isVertical true if linking to vertical wall, false for horizontal wall
     * @return true if linking succeeded or was cancelled (tools should unlock), false if invalid wall
     */
    public boolean linkLeverToWall(Point leverPos, int wallX, int wallY, boolean isVertical) {
        // Check if the target wall actually exists and is a wall (not already open)
        if (isVertical) {
            if (!vWalls[wallX][wallY]) {
                return false;
            }
        } else {
            if (!hWalls[wallX][wallY]) {
                return false;
            }
        }

        // CRITICAL VALIDATION: Ensure no other lever already controls this wall
        // This prevents conflicting lever assignments and maintains puzzle clarity
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS; j++) {
                // Skip the current pending lever being placed
                if (i == leverPos.x && j == leverPos.y) {
                    continue;
                }

                Room r = grid[i][j];
                // Check if another room has a lever targeting this wall
                if (r.hasLever() && doesLeverTargetWall(r.getLever(), wallX, wallY, isVertical)) {
                    // Wall already controlled by different lever - cancel placement and unlock tools
                    grid[leverPos.x][leverPos.y].setLever(null);
                    pendingLeverPos = null;
                    return true; // Return true to unlock editor tools
                }
            }
        }

        // Get the lever and the two rooms adjacent to the wall
        Room leverRoom = grid[leverPos.x][leverPos.y];
        Lever lever = leverRoom.getLever();
        if (lever == null) {
            pendingLeverPos = null;
            return true;
        }

        // Determine which rooms are separated by this wall
        Room r1, r2;
        if (isVertical) {
            // Vertical wall at (wallX, wallY) separates (wallX-1, wallY) and (wallX, wallY)
            r1 = grid[wallX - 1][wallY];
            r2 = grid[wallX][wallY];
        } else {
            // Horizontal wall at (wallX, wallY) separates (wallX, wallY-1) and (wallX, wallY)
            r1 = grid[wallX][wallY - 1];
            r2 = grid[wallX][wallY];
        }

        // Create bidirectional locked connections (wall can only be opened by lever toggle)
        Models.Connection c1 = new Models.Connection(r1, r2, true, null);
        Models.Connection c2 = new Models.Connection(r2, r1, true, null);

        // Add both directions to lever's target list
        lever.addTarget(c1);
        lever.addTarget(c2);

        // Clear pending state after successful link
        pendingLeverPos = null;
        return true;
    }

    /**
     * Toggles the state of a vertical wall between two rooms.
     * When a wall opens (is removed), any lever that was controlling it is also removed
     * to prevent invalid puzzle configurations with non-existent walls.
     *
     * @param x the x-coordinate of the vertical wall
     * @param y the y-coordinate of the vertical wall
     */
    public void toggleVWall(int x, int y) {
        // Toggle wall state between true (wall exists) and false (passage open)
        vWalls[x][y] = !vWalls[x][y];
        // If wall is being opened, remove any lever that was controlling it
        if (!vWalls[x][y]) {
            removeLeverLinkedToWall(x, y, true);
        }
    }

    /**
     * Toggles the state of a horizontal wall between two rooms.
     * When a wall opens (is removed), any lever that was controlling it is also removed
     * to prevent invalid puzzle configurations with non-existent walls.
     *
     * @param x the x-coordinate of the horizontal wall
     * @param y the y-coordinate of the horizontal wall
     */
    public void toggleHWall(int x, int y) {
        // Toggle wall state between true (wall exists) and false (passage open)
        hWalls[x][y] = !hWalls[x][y];
        // If wall is being opened, remove any lever that was controlling it
        if (!hWalls[x][y]) {
            removeLeverLinkedToWall(x, y, false);
        }
    }
}

package GameEngine;

import DataStructures.ArrayList.ArrayUnorderedList;
import DataStructures.Graph.GameGraph;
import DataStructures.Stack.LinkedStack;
import Models.Connection;
import Models.Lever;
import Models.Random;
import Models.Room;
import Utils.GameConfig;

/**
 * GameMapGenerator generates and manages the complete game map structure,
 * including rooms, connections between rooms, and item placement. Implements
 * a depth-first search based maze generation algorithm with optional braiding
 * to create cycles, ensuring varied and interconnected gameplay maps. Supports
 * automatic placement of levers, questions, pickaxes, and ender pearls, as well
 * as manual room and connection manipulation for map editing.
 */
public class GameMapGenerator {

    // ----------------------------------------------------------------
    // Fields
    // ----------------------------------------------------------------
    private Room[][] grid;
    private GameGraph graph;
    private int width;
    private int height;
    private String mapName;

    /**
     * Constructor for GameMapGenerator.
     * Generates a new map by default.
     *
     * @param width the width of the map grid
     * @param height the height of the map grid
     */
    public GameMapGenerator(int width, int height) {
        this(width, height, true);
    }

    /**
     * Creates a new GameMapGenerator with the specified dimensions. If generate is true,
     * automatically generates the maze structure, item placements, and levers. If false,
     * creates an empty map ready for manual configuration or loading from a file.
     *
     * @param width the width of the map grid
     * @param height the height of the map grid
     * @param generate true to automatically generate map content, false to create empty map
     */
    public GameMapGenerator(int width, int height, boolean generate) {
        this.width = width;
        this.height = height;
        this.mapName = "Generated";
        this.grid = new Room[width][height];
        this.graph = new GameGraph();
        if (generate) {
            generateMap();
            generateLevers();
            generateQuestions();
            generatePickaxes();
            generateEnderPearls();
        }
    }

    /**
     * Randomly places ender pearls throughout the map in unoccupied rooms.
     * Continues placing pearls until the configured maximum count is reached.
     * Ensures pearls are not placed in entrance rooms, center area, or rooms
     * that already contain levers, questions, or other items.
     */
    private void generateEnderPearls() {
        int count = 0;
        int maxEnderPearls = GameConfig.ENDERPEARLS_COUNT;
        Random rand = new Random();

        // Randomly select rooms until we've placed the maximum number of pearls
        while (count < maxEnderPearls) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            Room r = grid[x][y];

            // Only place if room is empty and not special (entrance/center)
            if (!r.isEntrance() && !r.isCenter() && !r.hasLever() && !r.hasQuestion() && !r.hasPickaxe() && !r.hasEnderPearl()) {
                r.setHasEnderPearl(true);
                count++;
            }
        }
    }

    /**
     * Randomly places pickaxes throughout the map in unoccupied rooms.
     * Continues placing pickaxes until the configured maximum count is reached.
     * Ensures pickaxes are not placed in entrance rooms, center area, or rooms
     * that already contain levers or questions.
     */
    private void generatePickaxes() {
        int count = 0;
        int maxPickaxes = GameConfig.PICKAXES_COUNT;
        Random rand = new Random();

        // Randomly select rooms until we've placed the maximum number of pickaxes
        while (count < maxPickaxes) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            Room r = grid[x][y];

            // Only place if room is empty and not special (entrance/center)
            if (!r.isEntrance() && !r.isCenter() && !r.hasLever() && !r.hasQuestion() && !r.hasPickaxe()) {
                r.setHasPickaxe(true);
                count++;
            }
        }
    }

    /**
     * Randomly places questions throughout the map in unoccupied rooms.
     * Continues placing questions until the configured maximum count is reached.
     * Ensures questions are not placed in entrance rooms, center area, or rooms
     * that already contain levers or other questions.
     */
    private void generateQuestions() {
        int count = 0;
        int maxQuestions = GameConfig.QUESTIONS_COUNT;
        Random rand = new Random();

        // Randomly select rooms until we've placed the maximum number of questions
        while (count < maxQuestions) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            Room r = grid[x][y];

            // Only place if room is empty and not special (entrance/center)
            if (!r.isEntrance() && !r.isCenter() && !r.hasLever() && !r.hasQuestion()) {
                r.setHasQuestion(true);
                count++;
            }
        }
    }

    /**
     * Randomly places levers throughout the map with connected target connections.
     * Attempts to place levers outside the central area with biased selection towards
     * connections near the map center. Each lever controls forward and reverse connections,
     * creating puzzle locks that control player movement. The process validates lever
     * placement constraints to avoid entrance areas and invalid target connections.
     */
    private void generateLevers() {
        int count = 0;
        Random rand = new Random();

        while (count < GameConfig.LEVERS_COUNT) {
            // 1. Pick a random room for the lever, preferring areas away from center
            int lx = rand.nextInt(width);
            int ly = rand.nextInt(height);

            // Calculate distance from center using Euclidean distance formula
            double dist = Math.sqrt(Math.pow(lx - width / 2.0, 2) + Math.pow(ly - height / 2.0, 2));
            // Skip if lever position is too close to center (within quarter of map width)
            if (dist < width / 4.0) {
                continue;
            }

            Room leverRoom = grid[lx][ly];

            // Skip if room already has a lever or is a special area (entrance/center)
            if (leverRoom.hasLever() || leverRoom.isEntrance() || leverRoom.isCenter()) {
                continue;
            }

            // 2. Pick a random connection to lock (target connection near map center)
            int centerX = width / 2;
            int centerY = height / 2;
            int range = Math.max(3, width / 3);

            // Generate target coordinates within a range around the map center
            int tx = centerX + (rand.nextInt(range * 2 + 1) - range);
            int ty = centerY + (rand.nextInt(range * 2 + 1) - range);

            // Clamp target coordinates to valid map bounds
            if (tx < 0) tx = 0;
            if (tx >= width) tx = width - 1;
            if (ty < 0) ty = 0;
            if (ty >= height) ty = height - 1;

            Room targetRoom1 = grid[tx][ty];

            // Skip if target room has no connections
            if (graph.getConnections(targetRoom1).isEmpty()) {
                continue;
            }

            // Randomly select one of the connections from target room
            ArrayUnorderedList<Connection> conns = graph.getConnections(targetRoom1);
            int cIdx = rand.nextInt(conns.size());
            Connection targetConn = null;

            DataStructures.Iterator<Connection> it = conns.iterator();
            for (int i = 0; i <= cIdx; i++) {
                if (it.hasNext()) {
                    targetConn = it.next();
                }
            }

            if (targetConn == null) {
                continue;
            }

            Room targetRoom2 = targetConn.getTo();

            // Skip if target connection leads to edge rooms (entrance areas)
            if (targetRoom2.getX() == 0 || targetRoom2.getX() == width - 1
                    || targetRoom2.getY() == 0 || targetRoom2.getY() == height - 1) {
                continue;
            }

            // Skip if either target room is the center area
            if (targetRoom1.isCenter() || targetRoom2.isCenter()) {
                continue;
            }

            // 3. Create Lever and link both forward and reverse connections
            Lever lever = new Lever();
            // Add the forward connection (targetRoom1 -> targetRoom2)
            lever.addTarget(targetConn);

            // Find and add the reverse connection (targetRoom2 -> targetRoom1)
            DataStructures.Iterator<Connection> itRev = graph.getConnections(targetRoom2).iterator();
            while (itRev.hasNext()) {
                Connection rev = itRev.next();
                if (rev.getTo().equals(targetRoom1)) {
                    lever.addTarget(rev);
                    break;
                }
            }

            leverRoom.setLever(lever);
            count++;
        }
    }

    /**
     * Generates the complete map structure including rooms, maze layout, and connections.
     * Uses depth-first search starting from the center to create a spanning tree (perfect maze),
     * then opens the center area for easier navigation, and finally adds cycles (braiding)
     * to create multiple solution paths. The process ensures all rooms are connected while
     * maintaining maze characteristics.
     */
    public void generateMap() {
        // 1. Create all rooms in the grid and add them to the graph
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Mark rooms in the center 3x3 area as central rooms
                boolean isCenter = (x >= width / 2 - 1 && x <= width / 2 + 1) && (y >= height / 2 - 1 && y <= height / 2 + 1);
                // Mark corner rooms as entrance points
                boolean isEntrance = (x == 0 && y == 0) || (x == width - 1 && y == 0) || (x == 0 && y == height - 1) || (x == width - 1 && y == height - 1);

                Room room = new Room(x + "," + y, x, y, isEntrance, isCenter);
                grid[x][y] = room;
                graph.addVertex(room);
            }
        }

        // 2. Generate Maze using Depth-First Search (DFS) starting from center
        int centerX = width / 2;
        int centerY = height / 2;
        Room startRoom = grid[centerX][centerY];

        boolean[][] visited = new boolean[width][height];
        LinkedStack<Room> stack = new LinkedStack<>();
        Random rand = new Random();

        stack.push(startRoom);
        visited[centerX][centerY] = true;

        // DFS algorithm: visit unvisited neighbors and create connections
        while (!stack.isEmpty()) {
            try {
                Room current = stack.peek();
                // Get all unvisited neighbors (up, down, left, right)
                ArrayUnorderedList<Room> neighbors = getUnvisitedNeighbors(current, visited);

                if (!neighbors.isEmpty()) {
                    // Randomly select one unvisited neighbor to carve a path to
                    int idx = rand.nextInt(neighbors.size());
                    Room next = neighbors.get(idx);

                    // Create bidirectional connection (maze path)
                    graph.addEdge(current, next, new Connection(current, next, false, null));

                    visited[next.getX()][next.getY()] = true;
                    stack.push(next);
                } else {
                    // Backtrack when no unvisited neighbors remain
                    stack.pop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 3. Open Center Area - connect all center rooms to each other
        for (int x = width / 2 - 1; x <= width / 2 + 1; x++) {
            for (int y = height / 2 - 1; y <= height / 2 + 1; y++) {
                Room r = grid[x][y];
                // Open rightward connections in center area
                if (x < width / 2 + 1) {
                    Room right = grid[x + 1][y];
                    if (!isConnected(r, right)) {
                        graph.addEdge(r, right, new Connection(r, right, false, null));
                    }
                }
                // Open downward connections in center area
                if (y < height / 2 + 1) {
                    Room down = grid[x][y + 1];
                    if (!isConnected(r, down)) {
                        graph.addEdge(r, down, new Connection(r, down, false, null));
                    }
                }
            }
        }

        // 4. Add Cycles (Braiding) - create alternative paths to reduce dead ends
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Room r = grid[x][y];
                // Check and potentially add rightward connection
                if (x < width - 1) {
                    Room right = grid[x + 1][y];
                    if (!isConnected(r, right)) {
                        // Add connection with 10% probability, but only if it won't create a small square
                        if (rand.nextDouble() < 0.1 && !createsSquare(r, right)) {
                            graph.addEdge(r, right, new Connection(r, right, false, null));
                        }
                    }
                }
                // Check and potentially add downward connection
                if (y < height - 1) {
                    Room down = grid[x][y + 1];
                    if (!isConnected(r, down)) {
                        // Add connection with 10% probability, but only if it won't create a small square
                        if (rand.nextDouble() < 0.1 && !createsSquare(r, down)) {
                            graph.addEdge(r, down, new Connection(r, down, false, null));
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks if adding a connection between two adjacent rooms would create a small
     * 2x2 square cycle in the maze. Used during braiding to avoid excessive loops
     * in local areas while still creating alternative paths.
     *
     * @param r1 the first room
     * @param r2 the second room (must be adjacent to r1)
     * @return true if adding connection would create a 2x2 square cycle, false otherwise
     */
    private boolean createsSquare(Room r1, Room r2) {
        int x1 = r1.getX();
        int y1 = r1.getY();
        int x2 = r2.getX();
        int y2 = r2.getY();

        // Horizontal connection (r1 is left, r2 is right)
        if (x1 == x2 - 1 && y1 == y2) {
            // Check above for existing square pattern
            if (y1 > 0) {
                Room r1Up = grid[x1][y1 - 1];
                Room r2Up = grid[x2][y2 - 1];
                // If rooms above are connected to each other and to their respective bottom rooms, square would form
                if (isConnected(r1, r1Up) && isConnected(r2, r2Up) && isConnected(r1Up, r2Up)) {
                    return true;
                }
            }
            // Check below for existing square pattern
            if (y1 < height - 1) {
                Room r1Down = grid[x1][y1 + 1];
                Room r2Down = grid[x2][y2 + 1];
                if (isConnected(r1, r1Down) && isConnected(r2, r2Down) && isConnected(r1Down, r2Down)) {
                    return true;
                }
            }
        } // Vertical connection (r1 is up, r2 is down)
        else if (x1 == x2 && y1 == y2 - 1) {
            // Check left for existing square pattern
            if (x1 > 0) {
                Room r1Left = grid[x1 - 1][y1];
                Room r2Left = grid[x2 - 1][y2];
                if (isConnected(r1, r1Left) && isConnected(r2, r2Left) && isConnected(r1Left, r2Left)) {
                    return true;
                }
            }
            // Check right for existing square pattern
            if (x1 < width - 1) {
                Room r1Right = grid[x1 + 1][y1];
                Room r2Right = grid[x2 + 1][y2];
                if (isConnected(r1, r1Right) && isConnected(r2, r2Right) && isConnected(r1Right, r2Right)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Retrieves all unvisited neighboring rooms for a given room.
     * Checks in four cardinal directions (up, down, left, right).
     *
     * @param r the room to check neighbors for
     * @param visited a 2D boolean array tracking visited rooms
     * @return a list of unvisited neighboring rooms
     */
    private ArrayUnorderedList<Room> getUnvisitedNeighbors(Room r, boolean[][] visited) {
        ArrayUnorderedList<Room> list = new ArrayUnorderedList<>();
        int x = r.getX();
        int y = r.getY();
        // Define four cardinal directions: down, up, right, left
        int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        for (int[] d : dirs) {
            int nx = x + d[0];
            int ny = y + d[1];
            // Check if neighbor is in bounds and not yet visited
            if (nx >= 0 && nx < width && ny >= 0 && ny < height && !visited[nx][ny]) {
                list.add(grid[nx][ny]);
            }
        }
        return list;
    }

    /**
     * Checks if two rooms are directly connected by a path.
     *
     * @param r1 the first room
     * @param r2 the second room
     * @return true if a connection exists between the rooms, false otherwise
     */
    private boolean isConnected(Room r1, Room r2) {
        return graph.getConnection(r1, r2) != null;
    }

    /**
     * Retrieves the room at the specified grid coordinates.
     *
     * @param x the x-coordinate (column)
     * @param y the y-coordinate (row)
     * @return the room at the specified coordinates, or null if coordinates are out of bounds
     */
    public Room getRoom(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return grid[x][y];
        }
        return null;
    }

    /**
     * Retrieves all entrance rooms (located at the four corners of the map).
     *
     * @return a list containing the four entrance room corners
     */
    public ArrayUnorderedList<Room> getEntrances() {
        ArrayUnorderedList<Room> entrances = new ArrayUnorderedList<>();
        entrances.add(grid[0][0]);
        entrances.add(grid[width - 1][0]);
        entrances.add(grid[0][height - 1]);
        entrances.add(grid[width - 1][height - 1]);
        return entrances;
    }

    /**
     * Retrieves the underlying game graph containing all rooms and connections.
     *
     * @return the GameGraph representing the map structure
     */
    public GameGraph getGraph() {
        return graph;
    }

    // ----------------------------------------------------------------
    // Methods for Loading/Saving Maps
    // ----------------------------------------------------------------
    /**
     * Sets the room at the specified grid coordinates and adds it to the graph.
     * Used when loading maps from files to reconstruct the map structure.
     *
     * @param x the x-coordinate (column)
     * @param y the y-coordinate (row)
     * @param room the room to place at these coordinates
     */
    public void setRoom(int x, int y, Room room) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            grid[x][y] = room;
            graph.addVertex(room);
        }
    }

    /**
     * Adds a directional connection between two rooms specified by coordinates.
     * Used when loading maps from files to reconstruct the path network.
     *
     * @param fromX the x-coordinate of the starting room
     * @param fromY the y-coordinate of the starting room
     * @param toX the x-coordinate of the destination room
     * @param toY the y-coordinate of the destination room
     * @param isLocked true if the connection should start locked, false if open
     */
    public void addConnection(int fromX, int fromY, int toX, int toY, boolean isLocked) {
        Room from = grid[fromX][fromY];
        Room to = grid[toX][toY];
        if (from != null && to != null) {
            graph.addEdge(from, to, new Connection(from, to, isLocked, null));
        }
    }

    /**
     * Creates a new connection (breaks a wall) between two adjacent rooms in the
     * specified direction. Used in map editors to allow manual maze modification.
     *
     * @param room the room to create the connection from
     * @param direction the direction to create the connection ("UP", "DOWN", "LEFT", "RIGHT")
     */
    public void breakWall(Room room, String direction) {
        int x = room.getX();
        int y = room.getY();
        int targetX = x;
        int targetY = y;

        // Calculate target room coordinates based on direction
        switch (direction.toUpperCase()) {
            case "UP":
                targetY--;
                break;
            case "DOWN":
                targetY++;
                break;
            case "LEFT":
                targetX--;
                break;
            case "RIGHT":
                targetX++;
                break;
        }

        // Verify target is in bounds and create connection if not already existing
        if (targetX >= 0 && targetX < width && targetY >= 0 && targetY < height) {
            Room targetRoom = grid[targetX][targetY];

            // Check if connection already exists
            if (!isConnected(room, targetRoom)) {
                graph.addEdge(room, targetRoom, new Connection(room, targetRoom, false, null));
            }
        }
    }

    /**
     * Retrieves the width of the map grid.
     *
     * @return the number of columns in the map
     */
    public int getWidth() {
        return width;
    }

    /**
     * Retrieves the height of the map grid.
     *
     * @return the number of rows in the map
     */
    public int getHeight() {
        return height;
    }

    /**
     * Retrieves the name of the map.
     *
     * @return the map name identifier
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * Sets the name of the map.
     *
     * @param mapName the new name to assign to this map
     */
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    /**
     * Retrieves all rooms containing pickaxes.
     *
     * @return a list of rooms that have pickaxes available
     */
    public ArrayUnorderedList<Room> getPickaxeRooms() {
        ArrayUnorderedList<Room> pickaxes = new ArrayUnorderedList<>();
        // Iterate through entire grid to find rooms with pickaxes
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y].hasPickaxe()) {
                    pickaxes.add(grid[x][y]);
                }
            }
        }
        return pickaxes;
    }

    /**
     * Calculates the shortest distances from all rooms to a target room using
     * the breadth-first search (BFS) algorithm. Only traverses unlocked connections,
     * making it useful for pathfinding when some doors are locked. Returns a 2D
     * array where each element represents the distance to that room, or Integer.MAX_VALUE
     * if unreachable.
     *
     * @param target the target room to calculate distances from
     * @return a 2D array where dist[x][y] is the shortest distance to room at (x,y),
     *         or Integer.MAX_VALUE if the room is unreachable from target
     */
    public int[][] getDistancesTo(Room target) {
        // Initialize distance array with maximum values (unreachable)
        int[][] dist = new int[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                dist[x][y] = Integer.MAX_VALUE;
            }
        }

        // Set target distance to 0 and start BFS from target room
        dist[target.getX()][target.getY()] = 0;
        DataStructures.Queue.LinkedQueue<Room> queue = new DataStructures.Queue.LinkedQueue<>();
        queue.enqueue(target);

        // Process rooms in queue, calculating distances to neighbors
        while (!queue.isEmpty()) {
            try {
                Room u = queue.dequeue();
                
                // Check all connections from current room
                DataStructures.Iterator<Connection> it = graph.getConnections(u).iterator();
                while (it.hasNext()) {
                    Connection c = it.next();
                    // Skip locked connections - they cannot be traversed
                    if (c.isLocked()) continue;
                    Room v = c.getTo();
                    // If neighbor hasn't been visited, calculate its distance and enqueue it
                    if (dist[v.getX()][v.getY()] == Integer.MAX_VALUE) {
                        dist[v.getX()][v.getY()] = dist[u.getX()][u.getY()] + 1;
                        queue.enqueue(v);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dist;
    }

    public static void main(String[] args) {
        System.out.println("Generating maps...");
        for (int i = 0; i < 5; i++) {
            GameMapGenerator map = new GameMapGenerator(21, 21, true);
            String filename = String.format("src/Resources/Maps/map-21x21-%04d.json", i);
            Utils.MapSerializer.saveToJson(map, filename);
        }
        System.out.println("Done.");
    }
}

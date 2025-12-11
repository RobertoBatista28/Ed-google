package GameEngine;

import DataStructures.ArrayList.ArrayUnorderedList;
import DataStructures.Graph.Graph;
import DataStructures.Stack.LinkedStack;
import Models.Connection;
import Models.Lever;
import Models.Random;
import Models.Room;
import Utils.GameConfig;

public class GameMap {

    // ----------------------------------------------------------------
    // Fields
    // ----------------------------------------------------------------
    private Room[][] grid;
    private Graph<Room> graph;  // ADT Grafo - estrutura principal para navegação
    private int width;
    private int height;
    private String mapName;

    public GameMap(int width, int height) {
        this(width, height, true);
    }

    public GameMap(int width, int height, boolean generate) {
        this.width = width;
        this.height = height;
        this.mapName = "Generated";
        this.graph = new Graph<>();  // Inicializar o grafo
        this.grid = new Room[width][height];
        if (generate) {
            generateMap();
            generateLevers();
            generateQuestions();
            generatePickaxes();
            generateEnderPearls();
        }
    }

    private void generateEnderPearls() {
        int count = 0;
        int maxEnderPearls = GameConfig.ENDERPEARLS_COUNT;
        Random rand = new Random();

        while (count < maxEnderPearls) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            Room r = grid[x][y];

            if (!r.isEntrance() && !r.isCenter() && !r.hasLever() && !r.hasQuestion() && !r.hasPickaxe() && !r.hasEnderPearl()) {
                r.setHasEnderPearl(true);
                count++;
            }
        }
    }

    private void generatePickaxes() {
        int count = 0;
        int maxPickaxes = GameConfig.PICKAXES_COUNT;
        Random rand = new Random();

        while (count < maxPickaxes) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            Room r = grid[x][y];

            if (!r.isEntrance() && !r.isCenter() && !r.hasLever() && !r.hasQuestion() && !r.hasPickaxe()) {
                r.setHasPickaxe(true);
                count++;
            }
        }
    }

    private void generateQuestions() {
        int count = 0;
        int maxQuestions = GameConfig.QUESTIONS_COUNT;
        Random rand = new Random();

        while (count < maxQuestions) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            Room r = grid[x][y];

            if (!r.isEntrance() && !r.isCenter() && !r.hasLever() && !r.hasQuestion()) {
                r.setHasQuestion(true);
                count++;
            }
        }
    }

    private void generateLevers() {
        int count = 0;
        Random rand = new Random();

        while (count < GameConfig.LEVERS_COUNT) {
            // 1. Pick a random room for the lever
            int lx = rand.nextInt(width);
            int ly = rand.nextInt(height);

            double dist = Math.sqrt(Math.pow(lx - width / 2.0, 2) + Math.pow(ly - height / 2.0, 2));
            if (dist < width / 4.0) {
                continue;
            }

            Room leverRoom = grid[lx][ly];

            if (leverRoom.hasLever() || leverRoom.isEntrance() || leverRoom.isCenter()) {
                continue;
            }

            // 2. Pick a random connection to lock (Target)
            int centerX = width / 2;
            int centerY = height / 2;
            int range = Math.max(3, width / 3);

            int tx = centerX + (rand.nextInt(range * 2 + 1) - range);
            int ty = centerY + (rand.nextInt(range * 2 + 1) - range);

            if (tx < 0) {
                tx = 0;
            }
            if (tx >= width) {
                tx = width - 1;
            }
            if (ty < 0) {
                ty = 0;
            }
            if (ty >= height) {
                ty = height - 1;
            }

            Room targetRoom1 = grid[tx][ty];

            if (targetRoom1.getConnections().isEmpty()) {
                continue;
            }

            ArrayUnorderedList<Connection> conns = targetRoom1.getConnections();
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

            if (targetRoom2.getX() == 0 || targetRoom2.getX() == width - 1
                    || targetRoom2.getY() == 0 || targetRoom2.getY() == height - 1) {
                continue;
            }

            if (targetRoom1.isCenter() || targetRoom2.isCenter()) {
                continue;
            }

            // 3. Create Lever and link connections
            Lever lever = new Lever();
            lever.addTarget(targetConn);

            DataStructures.Iterator<Connection> itRev = targetRoom2.getConnections().iterator();
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

    private void generateMap() {

        // 1. Create Rooms
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                boolean isCenter = (x >= width / 2 - 1 && x <= width / 2 + 1) && (y >= height / 2 - 1 && y <= height / 2 + 1);
                boolean isEntrance = (x == 0 && y == 0) || (x == width - 1 && y == 0) || (x == 0 && y == height - 1) || (x == width - 1 && y == height - 1);

                Room room = new Room(x + "," + y, x, y, isEntrance, isCenter);
                grid[x][y] = room;
            }
        }

        // 2. Generate Maze (DFS from Center)
        int centerX = width / 2;
        int centerY = height / 2;
        Room startRoom = grid[centerX][centerY];

        boolean[][] visited = new boolean[width][height];
        LinkedStack<Room> stack = new LinkedStack<>();
        Random rand = new Random();

        stack.push(startRoom);
        visited[centerX][centerY] = true;

        while (!stack.isEmpty()) {
            try {
                Room current = stack.peek();
                ArrayUnorderedList<Room> neighbors = getUnvisitedNeighbors(current, visited);

                if (!neighbors.isEmpty()) {
                    int idx = rand.nextInt(neighbors.size());
                    Room next = neighbors.get(idx);

                    current.addConnection(new Connection(current, next, false, null));
                    next.addConnection(new Connection(next, current, false, null));

                    visited[next.getX()][next.getY()] = true;
                    stack.push(next);
                } else {
                    stack.pop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 3. Open Center Area
        for (int x = width / 2 - 1; x <= width / 2 + 1; x++) {
            for (int y = height / 2 - 1; y <= height / 2 + 1; y++) {
                Room r = grid[x][y];
                if (x < width / 2 + 1) {
                    Room right = grid[x + 1][y];
                    if (!isConnected(r, right)) {
                        r.addConnection(new Connection(r, right, false, null));
                        right.addConnection(new Connection(right, r, false, null));
                    }
                }
                if (y < height / 2 + 1) {
                    Room down = grid[x][y + 1];
                    if (!isConnected(r, down)) {
                        r.addConnection(new Connection(r, down, false, null));
                        down.addConnection(new Connection(down, r, false, null));
                    }
                }
            }
        }

        // 4. Add Cycles (Braiding)
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Room r = grid[x][y];
                // Check Right
                if (x < width - 1) {
                    Room right = grid[x + 1][y];
                    if (!isConnected(r, right)) {
                        if (rand.nextDouble() < 0.1 && !createsSquare(r, right)) {
                            r.addConnection(new Connection(r, right, false, null));
                            right.addConnection(new Connection(right, r, false, null));
                        }
                    }
                }
                // Check Down
                if (y < height - 1) {
                    Room down = grid[x][y + 1];
                    if (!isConnected(r, down)) {
                        if (rand.nextDouble() < 0.1 && !createsSquare(r, down)) {
                            r.addConnection(new Connection(r, down, false, null));
                            down.addConnection(new Connection(down, r, false, null));
                        }
                    }
                }
            }
        }

        // 5. Popular o Grafo com todos os vértices (Rooms)
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                graph.addVertex(grid[x][y]);
            }
        }

        // 6. Popular o Grafo com todas as arestas (Connections)
        // Usamos as conexões já criadas na Room para popular o grafo
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Room current = grid[x][y];
                DataStructures.Iterator<Connection> connIt = current.getConnections().iterator();
                while (connIt.hasNext()) {
                    Connection conn = connIt.next();
                    Room neighbor = conn.getTo();
                    // Adicionar aresta ao grafo (evita duplicação pois addEdge é simétrico)
                    if (current.getX() < neighbor.getX() || 
                        (current.getX() == neighbor.getX() && current.getY() < neighbor.getY())) {
                        graph.addEdge(current, neighbor);
                    }
                }
            }
        }
    }

    private boolean createsSquare(Room r1, Room r2) {
        int x1 = r1.getX();
        int y1 = r1.getY();
        int x2 = r2.getX();
        int y2 = r2.getY();

        // Horizontal connection (r1 is left, r2 is right)
        if (x1 == x2 - 1 && y1 == y2) {
            if (y1 > 0) {
                Room r1Up = grid[x1][y1 - 1];
                Room r2Up = grid[x2][y2 - 1];
                if (isConnected(r1, r1Up) && isConnected(r2, r2Up) && isConnected(r1Up, r2Up)) {
                    return true;
                }
            }
            if (y1 < height - 1) {
                Room r1Down = grid[x1][y1 + 1];
                Room r2Down = grid[x2][y2 + 1];
                if (isConnected(r1, r1Down) && isConnected(r2, r2Down) && isConnected(r1Down, r2Down)) {
                    return true;
                }
            }
        } // Vertical connection (r1 is up, r2 is down)
        else if (x1 == x2 && y1 == y2 - 1) {
            if (x1 > 0) {
                Room r1Left = grid[x1 - 1][y1];
                Room r2Left = grid[x2 - 1][y2];
                if (isConnected(r1, r1Left) && isConnected(r2, r2Left) && isConnected(r1Left, r2Left)) {
                    return true;
                }
            }
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

    private ArrayUnorderedList<Room> getUnvisitedNeighbors(Room r, boolean[][] visited) {
        ArrayUnorderedList<Room> list = new ArrayUnorderedList<>();
        int x = r.getX();
        int y = r.getY();
        int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        for (int[] d : dirs) {
            int nx = x + d[0];
            int ny = y + d[1];
            if (nx >= 0 && nx < width && ny >= 0 && ny < height && !visited[nx][ny]) {
                list.add(grid[nx][ny]);
            }
        }
        return list;
    }

    private boolean isConnected(Room r1, Room r2) {
        DataStructures.Iterator<Connection> it = r1.getConnections().iterator();
        while (it.hasNext()) {
            if (it.next().getTo().equals(r2)) {
                return true;
            }
        }
        return false;
    }

    public Room getRoom(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return grid[x][y];
        }
        return null;
    }

    public ArrayUnorderedList<Room> getEntrances() {
        ArrayUnorderedList<Room> entrances = new ArrayUnorderedList<>();
        entrances.add(grid[0][0]);
        entrances.add(grid[width - 1][0]);
        entrances.add(grid[0][height - 1]);
        entrances.add(grid[width - 1][height - 1]);
        return entrances;
    }

    /**
     * Método correto: Delega o cálculo do caminho mais curto para o ADT Graph
     * Usa BFS implementado no Graph.iteratorShortestPath()
     */
    public DataStructures.Iterator<Room> getShortestPath(Room start, Room end) {
        return graph.iteratorShortestPath(start, end);
    }

    /**
     * @deprecated Usar getShortestPath() que delega ao ADT Graph
     */
    @Deprecated
    public DataStructures.Iterator<Room> getShortestPathDijkstra(Room start, Room end) {
        // Redireciona para o método correto que usa o grafo
        return getShortestPath(start, end);
    }

    // Methods for Loading/Saving Maps
    public void setRoom(int x, int y, Room room) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            grid[x][y] = room;
        }
    }

    public void addConnection(int fromX, int fromY, int toX, int toY, boolean isLocked) {
        Room from = grid[fromX][fromY];
        Room to = grid[toX][toY];
        if (from != null && to != null) {
            from.addConnection(new Connection(from, to, isLocked, null));
        }
    }

    public void breakWall(Room room, String direction) {
        int x = room.getX();
        int y = room.getY();
        int targetX = x;
        int targetY = y;

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

        if (targetX >= 0 && targetX < width && targetY >= 0 && targetY < height) {
            Room targetRoom = grid[targetX][targetY];

            // Check if connection already exists
            if (!isConnected(room, targetRoom)) {
                room.addConnection(new Connection(room, targetRoom, false, null));
                targetRoom.addConnection(new Connection(targetRoom, room, false, null));
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public ArrayUnorderedList<Room> getPickaxeRooms() {
        ArrayUnorderedList<Room> pickaxes = new ArrayUnorderedList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y].hasPickaxe()) {
                    pickaxes.add(grid[x][y]);
                }
            }
        }
        return pickaxes;
    }

    public int[][] getDistancesTo(Room target) {
        // 1. Inicializar matriz de distâncias
        int[][] distGrid = new int[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                distGrid[x][y] = Integer.MAX_VALUE;
            }
        }

        // 2. Pedir ao grafo as distâncias (Lógica delegada ao ADT)
        // Nota: Num grafo não direcionado, dist(A->B) == dist(B->A), 
        // por isso podemos pedir "distâncias A PARTIR do target"
        int[] graphDistances = graph.getDistancesFrom(target);
        
        // 3. Mapear os resultados do grafo de volta para a grelha 2D
        // Como o grafo usa um array interno, iteramos pelos vértices pela ordem
        ArrayUnorderedList<Room> allRooms = graph.getVertices();
        DataStructures.Iterator<Room> it = allRooms.iterator();
        
        int index = 0;
        while(it.hasNext()) {
            Room r = it.next();
            // Só preenche se o vértice for alcançável
            if (graphDistances[index] != Integer.MAX_VALUE) {
                distGrid[r.getX()][r.getY()] = graphDistances[index];
            }
            index++;
        }
        
        return distGrid;
    }

    public static void main(String[] args) {
        System.out.println("Generating maps...");
        for (int i = 0; i < 5; i++) {
            GameMap map = new GameMap(21, 21, true);
            String filename = String.format("src/Resources/Maps/map-21x21-%04d.json", i);
            Utils.MapSerializer.saveToJson(map, filename);
        }
        System.out.println("Done.");
    }

    /**
     * Retorna o grafo (ADT) usado para navegação no mapa
     */
    public Graph<Room> getGraph() {
        return graph;
    }
}

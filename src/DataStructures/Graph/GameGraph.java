package DataStructures.Graph;

import DataStructures.Iterator;
import DataStructures.ArrayList.ArrayUnorderedList;
import Models.Connection;
import Models.Room;

/**
 * GameGraph extends Graph to represent a game map structure where vertices are
 * Room objects and edges are Connection objects. Maintains a dual representation:
 * an undirected adjacency matrix for connectivity and a directed connection matrix
 * storing Connection objects with locking and key information. Supports weighted
 * shortest path calculations using Dijkstra's algorithm with locked connection skipping.
 */
public class GameGraph extends Graph<Room> {
    
    private Connection[][] connectionMatrix;

    /**
     * Creates a new empty GameGraph with default capacity.
     * Initializes both the adjacency matrix (inherited) and connection matrix
     * to store Connection objects between rooms.
     */
    public GameGraph() {
        super();
        this.connectionMatrix = new Connection[DEFAULT_CAPACITY][DEFAULT_CAPACITY];
    }

    /**
     * Expands the capacity of this graph by doubling both the inherited adjacency
     * matrix and the connection matrix. Ensures both structures grow in parallel
     * to maintain synchronized room and connection data.
     */
    @Override
    public void expandCapacity() {
        // Call parent to expand vertices array and adjacency matrix
        super.expandCapacity();
        // Create a new connection matrix with expanded capacity
        Connection[][] largerConnectionMatrix = new Connection[vertices.length][vertices.length];
        // Copy all existing connections to the larger matrix
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                largerConnectionMatrix[i][j] = connectionMatrix[i][j];
            }
        }
        connectionMatrix = largerConnectionMatrix;
    }

    /**
     * Adds a directed edge between two rooms with an associated Connection object.
     * Creates both the undirected adjacency entry (inherited behavior) and stores
     * the Connection objects in the connection matrix. Automatically creates a
     * reverse Connection for bidirectional traversal while maintaining directional
     * semantics in the Connection object itself (from→to relationship).
     *
     * @param vertex1 the first room (source)
     * @param vertex2 the second room (destination)
     * @param connection the Connection object describing the link from vertex1 to vertex2
     */
    public void addEdge(Room vertex1, Room vertex2, Connection connection) {
        // Call parent to add undirected edge in adjacency matrix
        super.addEdge(vertex1, vertex2);
        int index1 = getIndex(vertex1);
        int index2 = getIndex(vertex2);
        if (indexIsValid(index1) && indexIsValid(index2)) {
            // Store the connection from vertex1 to vertex2
            connectionMatrix[index1][index2] = connection;
            
            // Create reverse connection automatically to ensure undirected behavior in the graph
            // but directed behavior in the Connection object (from->to)
            Connection reverseConnection = new Connection(vertex2, vertex1, connection.isLocked(), connection.getKey());
            connectionMatrix[index2][index1] = reverseConnection; 
        }
    }
    
    /**
     * Removes the edge between two rooms from both the adjacency matrix and
     * connection matrix. Clears both directions of the connection to ensure
     * complete removal from the undirected graph structure.
     *
     * @param vertex1 the first room
     * @param vertex2 the second room
     */
    @Override
    public void removeEdge(Room vertex1, Room vertex2) {
        // Call parent to remove undirected edge in adjacency matrix
        super.removeEdge(vertex1, vertex2);
        int index1 = getIndex(vertex1);
        int index2 = getIndex(vertex2);
        if (indexIsValid(index1) && indexIsValid(index2)) {
            // Clear both directions of the connection
            connectionMatrix[index1][index2] = null;
            connectionMatrix[index2][index1] = null;
        }
    }
    
    /**
     * Returns the Connection object between two rooms if an edge exists.
     * Retrieves the directional connection from vertex1 to vertex2 with all
     * locking and key information preserved.
     *
     * @param vertex1 the source room
     * @param vertex2 the destination room
     * @return the Connection from vertex1 to vertex2, or null if no edge exists
     */
    public Connection getConnection(Room vertex1, Room vertex2) {
        int index1 = getIndex(vertex1);
        int index2 = getIndex(vertex2);
        if (indexIsValid(index1) && indexIsValid(index2)) {
            return connectionMatrix[index1][index2];
        }
        return null;
    }

    /**
     * Returns all outgoing Connection objects from a specific room.
     * Collects all Connection objects where edges exist in the adjacency matrix
     * and Connection objects are stored in the connection matrix.
     *
     * @param room the source room
     * @return an ArrayUnorderedList containing all outgoing Connections from this room
     */
    public ArrayUnorderedList<Connection> getConnections(Room room) {
        ArrayUnorderedList<Connection> connections = new ArrayUnorderedList<>();
        int index = getIndex(room);
        if (indexIsValid(index)) {
            // Iterate through all vertices and collect connections from current room
            for (int i = 0; i < numVertices; i++) {
                if (adjMatrix[index][i] && connectionMatrix[index][i] != null) {
                    connections.add(connectionMatrix[index][i]);
                }
            }
        }
        return connections;
    }

    /**
     * Returns an iterator containing the shortest path between two rooms using
     * Dijkstra's algorithm. Skips locked connections to find paths only through
     * passable connections. Returns an empty iterator if no path exists.
     *
     * @param startVertex the starting room
     * @param targetVertex the destination room
     * @return an iterator containing rooms along the shortest path from start to target
     */
    @Override
    public Iterator<Room> iteratorShortestPath(Room startVertex, Room targetVertex) {
        // Dijkstra implementation
        int startIndex = getIndex(startVertex);
        int targetIndex = getIndex(targetVertex);
        
        if (!indexIsValid(startIndex) || !indexIsValid(targetIndex)) {
            return new ArrayUnorderedList<Room>().iterator();
        }

        // Initialize distance array: all vertices unreachable except start vertex
        double[] dist = new double[numVertices];
        int[] prev = new int[numVertices];
        boolean[] visited = new boolean[numVertices];

        for (int i = 0; i < numVertices; i++) {
            dist[i] = Double.MAX_VALUE;
            prev[i] = -1;
            visited[i] = false;
        }

        // Distance from start to itself is zero
        dist[startIndex] = 0;

        // Main Dijkstra loop: relax edges from unvisited vertex with minimum distance
        for (int i = 0; i < numVertices; i++) {
            int u = -1;
            double minDist = Double.MAX_VALUE;

            // Find unvisited vertex with minimum distance (greedy selection)
            for (int j = 0; j < numVertices; j++) {
                if (!visited[j] && dist[j] < minDist) {
                    minDist = dist[j];
                    u = j;
                }
            }

            // If no reachable vertex found or target reached, terminate early
            if (u == -1 || u == targetIndex) {
                break;
            }

            visited[u] = true;

            // Relax edges to all unvisited neighbors of current vertex
            for (int v = 0; v < numVertices; v++) {
                if (adjMatrix[u][v] && !visited[v]) {
                    Connection conn = connectionMatrix[u][v];
                    // Skip locked connections: they cannot be traversed
                    if (conn != null && conn.isLocked()) {
                        continue;
                    }
                    
                    // Update distance if path through u to v is shorter
                    double alt = dist[u] + 1; // Weight 1 for now
                    if (alt < dist[v]) {
                        dist[v] = alt;
                        prev[v] = u;
                    }
                }
            }
        }

        // Reconstruct path from start to target using predecessor array
        ArrayUnorderedList<Room> path = new ArrayUnorderedList<>();
        int curr = targetIndex;
        
        // Check if target is reachable (distance not infinity)
        if (dist[targetIndex] == Double.MAX_VALUE) {
             return path.iterator();
        }

        // Backtrack from target to start using predecessor links
        if (prev[curr] != -1 || curr == startIndex) {
            while (curr != -1) {
                path.addToFront((Room) vertices[curr]);
                curr = prev[curr];
            }
        }
        
        return path.iterator();
    }
}

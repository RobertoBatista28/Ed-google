package Models;

import DataStructures.Graph.Network;

/**
 * GameNetwork extends Network to handle game-specific logic with Connection objects.
 * This class resolves the problem of locked doors and provides the single source of truth
 * for the game's topology, as required by Aula 12.
 */
public class GameNetwork extends Network<Room> {
    
    // Parallel matrix to store Connection objects (which have locked status)
    private Connection[][] connections;

    /**
     * Creates an empty game network.
     */
    public GameNetwork() {
        super();
        this.connections = new Connection[DEFAULT_CAPACITY][DEFAULT_CAPACITY];
    }

    /**
     * Adds a connection between two rooms with game logic.
     * The weight is based on the destination room's movement cost (Soul Sand = 3.0, normal = 1.0).
     */
    public void addConnection(Room from, Room to, Connection connection) {
        // Add the rooms as vertices if they don't exist
        if (getIndex(from) == -1) {
            addVertex(from);
        }
        if (getIndex(to) == -1) {
            addVertex(to);
        }
        
        // The weight is the cost to enter the destination room
        double weight = to.getMovementCost();
        
        // Add to the network structure (topology + weight)
        super.addEdge(from, to, weight);
        
        // Store the game connection object
        int idx1 = getIndex(from);
        int idx2 = getIndex(to);
        if (indexIsValid(idx1) && indexIsValid(idx2)) {
            connections[idx1][idx2] = connection;
            connections[idx2][idx1] = connection; // Bidirectional
        }
    }

    /**
     * Checks if a player can move between two rooms.
     * Movement is allowed if there's an edge AND the connection is not locked.
     */
    public boolean canMove(Room from, Room to) {
        int idx1 = getIndex(from);
        int idx2 = getIndex(to);
        
        // Check if vertices exist and are adjacent
        if (!indexIsValid(idx1) || !indexIsValid(idx2) || !adjMatrix[idx1][idx2]) {
            return false;
        }
        
        // Check if the connection is not locked
        Connection conn = connections[idx1][idx2];
        return conn != null && !conn.isLocked();
    }
    
    /**
     * Gets the Connection object between two rooms.
     */
    public Connection getConnection(Room from, Room to) {
        int idx1 = getIndex(from);
        int idx2 = getIndex(to);
        if (indexIsValid(idx1) && indexIsValid(idx2)) {
            return connections[idx1][idx2];
        }
        return null;
    }

    /**
     * Gets all accessible neighbors from a room (unlocked connections only).
     */
    public java.util.List<Room> getAccessibleNeighbors(Room room) {
        java.util.List<Room> neighbors = new java.util.ArrayList<>();
        int roomIndex = getIndex(room);
        
        if (indexIsValid(roomIndex)) {
            for (int i = 0; i < numVertices; i++) {
                if (adjMatrix[roomIndex][i]) {
                    Connection conn = connections[roomIndex][i];
                    if (conn != null && !conn.isLocked()) {
                        neighbors.add(vertices[i]);
                    }
                }
            }
        }
        
        return neighbors;
    }

    /**
     * Expands the capacity of the network, including the connections matrix.
     */
    protected void expandCapacity() {
        super.expandCapacity();
        
        int newCapacity = connections.length * 2;
        Connection[][] largerConnectionsMatrix = new Connection[newCapacity][newCapacity];
        
        // Copy existing connections
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                largerConnectionsMatrix[i][j] = connections[i][j];
            }
        }
        
        connections = largerConnectionsMatrix;
    }
}

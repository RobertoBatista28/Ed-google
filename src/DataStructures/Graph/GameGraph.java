package DataStructures.Graph;

import DataStructures.Iterator;
import DataStructures.ArrayList.ArrayUnorderedList;
import Models.Connection;
import Models.Room;

public class GameGraph extends Graph<Room> {
    
    private Connection[][] connectionMatrix;

    public GameGraph() {
        super();
        this.connectionMatrix = new Connection[DEFAULT_CAPACITY][DEFAULT_CAPACITY];
    }

    @Override
    public void expandCapacity() {
        super.expandCapacity();
        Connection[][] largerConnectionMatrix = new Connection[vertices.length][vertices.length];
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                largerConnectionMatrix[i][j] = connectionMatrix[i][j];
            }
        }
        connectionMatrix = largerConnectionMatrix;
    }

    public void addEdge(Room vertex1, Room vertex2, Connection connection) {
        super.addEdge(vertex1, vertex2);
        int index1 = getIndex(vertex1);
        int index2 = getIndex(vertex2);
        if (indexIsValid(index1) && indexIsValid(index2)) {
            connectionMatrix[index1][index2] = connection;
            
            // Create reverse connection automatically to ensure undirected behavior in the graph
            // but directed behavior in the Connection object (from->to)
            Connection reverseConnection = new Connection(vertex2, vertex1, connection.isLocked(), connection.getKey());
            connectionMatrix[index2][index1] = reverseConnection; 
        }
    }
    
    @Override
    public void removeEdge(Room vertex1, Room vertex2) {
        super.removeEdge(vertex1, vertex2);
        int index1 = getIndex(vertex1);
        int index2 = getIndex(vertex2);
        if (indexIsValid(index1) && indexIsValid(index2)) {
            connectionMatrix[index1][index2] = null;
            connectionMatrix[index2][index1] = null;
        }
    }
    
    public Connection getConnection(Room vertex1, Room vertex2) {
        int index1 = getIndex(vertex1);
        int index2 = getIndex(vertex2);
        if (indexIsValid(index1) && indexIsValid(index2)) {
            return connectionMatrix[index1][index2];
        }
        return null;
    }

    public ArrayUnorderedList<Connection> getConnections(Room room) {
        ArrayUnorderedList<Connection> connections = new ArrayUnorderedList<>();
        int index = getIndex(room);
        if (indexIsValid(index)) {
            for (int i = 0; i < numVertices; i++) {
                if (adjMatrix[index][i] && connectionMatrix[index][i] != null) {
                    connections.add(connectionMatrix[index][i]);
                }
            }
        }
        return connections;
    }

    @Override
    public Iterator<Room> iteratorShortestPath(Room startVertex, Room targetVertex) {
        // Dijkstra implementation
        int startIndex = getIndex(startVertex);
        int targetIndex = getIndex(targetVertex);
        
        if (!indexIsValid(startIndex) || !indexIsValid(targetIndex)) {
            return new ArrayUnorderedList<Room>().iterator();
        }

        double[] dist = new double[numVertices];
        int[] prev = new int[numVertices];
        boolean[] visited = new boolean[numVertices];

        for (int i = 0; i < numVertices; i++) {
            dist[i] = Double.MAX_VALUE;
            prev[i] = -1;
            visited[i] = false;
        }

        dist[startIndex] = 0;

        for (int i = 0; i < numVertices; i++) {
            int u = -1;
            double minDist = Double.MAX_VALUE;

            // Find min dist vertex
            for (int j = 0; j < numVertices; j++) {
                if (!visited[j] && dist[j] < minDist) {
                    minDist = dist[j];
                    u = j;
                }
            }

            if (u == -1 || u == targetIndex) {
                break;
            }

            visited[u] = true;

            // Neighbors
            for (int v = 0; v < numVertices; v++) {
                if (adjMatrix[u][v] && !visited[v]) {
                    Connection conn = connectionMatrix[u][v];
                    // Check if locked
                    if (conn != null && conn.isLocked()) {
                        continue;
                    }
                    
                    double alt = dist[u] + 1; // Weight 1 for now
                    if (alt < dist[v]) {
                        dist[v] = alt;
                        prev[v] = u;
                    }
                }
            }
        }

        // Reconstruct path
        ArrayUnorderedList<Room> path = new ArrayUnorderedList<>();
        int curr = targetIndex;
        
        // Check if path exists
        if (dist[targetIndex] == Double.MAX_VALUE) {
             return path.iterator();
        }

        if (prev[curr] != -1 || curr == startIndex) {
            while (curr != -1) {
                path.addToFront((Room) vertices[curr]);
                curr = prev[curr];
            }
        }
        
        return path.iterator();
    }
}

package DataStructures.Graph;

import DataStructures.ArrayList.ArrayUnorderedList;
import DataStructures.Iterator;
import DataStructures.Heap.PriorityQueue;
import DataStructures.Stack.LinkedStack;
import DataStructures.Exceptions.EmptyCollectionException;

/**
 * Network represents a weighted graph implementation.
 * Based on Aula 12 - Networks with Dijkstra's Algorithm
 */
public class Network<T> extends Graph<T> implements WeightedGraphADT<T> {
    
    protected double[][] adjMatrixWeight; // Weight matrix

    /**
     * Creates an empty network.
     */
    public Network() {
        super();
        this.adjMatrixWeight = new double[DEFAULT_CAPACITY][DEFAULT_CAPACITY];
        
        // Initialize weight matrix with infinity
        for (int i = 0; i < DEFAULT_CAPACITY; i++) {
            for (int j = 0; j < DEFAULT_CAPACITY; j++) {
                adjMatrixWeight[i][j] = Double.POSITIVE_INFINITY;
            }
        }
    }

    /**
     * Inserts an edge between two vertices with a weight.
     */
    public void addEdge(T vertex1, T vertex2, double weight) {
        super.addEdge(vertex1, vertex2); // Add to boolean matrix
        setEdgeWeight(vertex1, vertex2, weight);
    }
    
    /**
     * Sets the weight of an edge between two vertices.
     */
    public void setEdgeWeight(T vertex1, T vertex2, double weight) {
        int index1 = getIndex(vertex1);
        int index2 = getIndex(vertex2);
        if (indexIsValid(index1) && indexIsValid(index2)) {
            adjMatrixWeight[index1][index2] = weight;
            adjMatrixWeight[index2][index1] = weight; // Undirected graph
        }
    }

    /**
     * Returns the weight of an edge between two vertices.
     */
    public double getEdgeWeight(T vertex1, T vertex2) {
        int index1 = getIndex(vertex1);
        int index2 = getIndex(vertex2);
        if (indexIsValid(index1) && indexIsValid(index2)) {
            return adjMatrixWeight[index1][index2];
        }
        return Double.POSITIVE_INFINITY;
    }

    /**
     * Dijkstra's Algorithm implementation for shortest path.
     * Based on Aula 12 - Shortest Path in Networks
     */
    public Iterator<T> iteratorShortestPath(T startVertex, T targetVertex) {
        int startIndex = getIndex(startVertex);
        int targetIndex = getIndex(targetVertex);
        
        if (!indexIsValid(startIndex) || !indexIsValid(targetIndex)) {
            return new ArrayUnorderedList<T>().iterator();
        }

        // Arrays for Dijkstra's algorithm
        double[] pathWeight = new double[numVertices];
        int[] predecessor = new int[numVertices];
        boolean[] visited = new boolean[numVertices];

        // Initialize arrays
        for (int i = 0; i < numVertices; i++) {
            pathWeight[i] = Double.POSITIVE_INFINITY;
            predecessor[i] = -1;
            visited[i] = false;
        }

        // Priority Queue for Dijkstra (min-heap by distance)
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        
        pathWeight[startIndex] = 0;
        pq.addElement(startIndex, 0);

        while (!pq.isEmpty()) {
            Integer u = pq.removeNext(); // Get vertex with minimum distance
            
            if (u == null || visited[u]) continue;
            visited[u] = true;

            if (u == targetIndex) break; // Found target

            // Relax adjacent edges
            for (int v = 0; v < numVertices; v++) {
                if (adjMatrix[u][v] && !visited[v]) {
                    double edgeWeight = adjMatrixWeight[u][v];
                    if (pathWeight[u] + edgeWeight < pathWeight[v]) {
                        pathWeight[v] = pathWeight[u] + edgeWeight;
                        predecessor[v] = u;
                        pq.addElement(v, (int)(pathWeight[v] * 100)); // Scale for int priority
                    }
                }
            }
        }

        // Reconstruct path
        if (pathWeight[targetIndex] == Double.POSITIVE_INFINITY) {
            return new ArrayUnorderedList<T>().iterator();
        }

        LinkedStack<T> stack = new LinkedStack<>();
        int current = targetIndex;
        while (current != -1) {
            stack.push(vertices[current]);
            current = predecessor[current];
        }

        ArrayUnorderedList<T> resultList = new ArrayUnorderedList<>();
        try {
            while (!stack.isEmpty()) {
                resultList.add(stack.pop());
            }
        } catch (EmptyCollectionException e) {
            // Should not happen since we check isEmpty()
        }

        return resultList.iterator();
    }

    /**
     * Returns the weight of the shortest path between two vertices.
     */
    public double shortestPathWeight(T vertex1, T vertex2) {
        int startIndex = getIndex(vertex1);
        int targetIndex = getIndex(vertex2);
        
        if (!indexIsValid(startIndex) || !indexIsValid(targetIndex)) {
            return Double.POSITIVE_INFINITY;
        }

        // Run simplified Dijkstra just for weight
        double[] pathWeight = new double[numVertices];
        boolean[] visited = new boolean[numVertices];

        for (int i = 0; i < numVertices; i++) {
            pathWeight[i] = Double.POSITIVE_INFINITY;
            visited[i] = false;
        }

        pathWeight[startIndex] = 0;
        
        for (int count = 0; count < numVertices - 1; count++) {
            int u = minDistance(pathWeight, visited);
            if (u == -1) break;
            
            visited[u] = true;
            
            if (u == targetIndex) break;

            for (int v = 0; v < numVertices; v++) {
                if (!visited[v] && adjMatrix[u][v] && pathWeight[u] != Double.POSITIVE_INFINITY &&
                    pathWeight[u] + adjMatrixWeight[u][v] < pathWeight[v]) {
                    pathWeight[v] = pathWeight[u] + adjMatrixWeight[u][v];
                }
            }
        }

        return pathWeight[targetIndex];
    }

    /**
     * Helper method to find vertex with minimum distance.
     */
    private int minDistance(double[] dist, boolean[] visited) {
        double min = Double.POSITIVE_INFINITY;
        int minIndex = -1;

        for (int v = 0; v < numVertices; v++) {
            if (!visited[v] && dist[v] <= min) {
                min = dist[v];
                minIndex = v;
            }
        }

        return minIndex;
    }

    /**
     * Expands the capacity of the graph, including weight matrix.
     */
    protected void expandCapacity() {
        super.expandCapacity();
        
        double[][] largerWeightMatrix = new double[vertices.length][vertices.length];
        
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                largerWeightMatrix[i][j] = adjMatrixWeight[i][j];
            }
        }
        
        // Initialize new positions with infinity
        for (int i = 0; i < vertices.length; i++) {
            for (int j = numVertices; j < vertices.length; j++) {
                largerWeightMatrix[i][j] = Double.POSITIVE_INFINITY;
                largerWeightMatrix[j][i] = Double.POSITIVE_INFINITY;
            }
        }
        
        adjMatrixWeight = largerWeightMatrix;
    }
}

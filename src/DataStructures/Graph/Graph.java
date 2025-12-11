package DataStructures.Graph;

import DataStructures.ArrayList.ArrayUnorderedList;
import DataStructures.Iterator;
import DataStructures.Queue.LinkedQueue;
import DataStructures.Stack.LinkedStack;

public class Graph<T> implements GraphADT<T> {

    protected final int DEFAULT_CAPACITY = 10;
    protected int numVertices;
    protected boolean[][] adjMatrix;
    protected T[] vertices;

    /**
     * Creates an empty graph.
     */
    @SuppressWarnings("unchecked")
    public Graph() {
        numVertices = 0;
        this.adjMatrix = new boolean[DEFAULT_CAPACITY][DEFAULT_CAPACITY];
        this.vertices = (T[]) (new Object[DEFAULT_CAPACITY]);
    }

    /**
     * Adds a vertex to the graph, expanding the capacity of the graph if
     * necessary. It also associates an object with the vertex.
     *
     * @param vertex the vertex to add to the graph
     */
    @Override
    public void addVertex(T vertex) {
        if (numVertices == vertices.length) {
            expandCapacity();
        }

        vertices[numVertices] = vertex;
        for (int i = 0; i <= numVertices; i++) {
            adjMatrix[numVertices][i] = false;
            adjMatrix[i][numVertices] = false;
        }
        numVertices++;
    }

    /**
     * Removes a single vertex with the given value from this graph.
     *
     * @param vertex the vertex to be removed from this graph
     */
    @Override
    public void removeVertex(T vertex) {
        int index = getIndex(vertex);
        if (indexIsValid(index)) {
            numVertices--;

            for (int i = index; i < numVertices; i++) {
                vertices[i] = vertices[i + 1];
            }

            for (int i = index; i < numVertices; i++) {
                System.arraycopy(adjMatrix[i + 1], 0, adjMatrix[i], 0, numVertices + 1);
            }

            for (int i = index; i < numVertices; i++) {
                for (int j = 0; j < numVertices; j++) {
                    adjMatrix[j][i] = adjMatrix[j][i + 1];
                }
            }
        }
    }

    /**
     * Inserts an edge between two vertices of the graph.
     *
     * @param vertex1 the first vertex
     * @param vertex2 the second vertex
     */
    @Override
    public void addEdge(T vertex1, T vertex2) {
        addEdge(getIndex(vertex1), getIndex(vertex2));
    }

    /**
     * Inserts an edge between two vertices of the graph.
     *
     * @param index1 the first index
     * @param index2 the second index
     */
    public void addEdge(int index1, int index2) {
        if (indexIsValid(index1) && indexIsValid(index2)) {
            adjMatrix[index1][index2] = true;
            adjMatrix[index2][index1] = true;
        }
    }

    /**
     * Removes an edge between two vertices of the graph.
     *
     * @param vertex1 the first vertex
     * @param vertex2 the second vertex
     */
    @Override
    public void removeEdge(T vertex1, T vertex2) {
        removeEdge(getIndex(vertex1), getIndex(vertex2));
    }

    /**
     * Removes an edge between two vertices of the graph.
     *
     * @param index1 the first index
     * @param index2 the second index
     */
    public void removeEdge(int index1, int index2) {
        if (indexIsValid(index1) && indexIsValid(index2)) {
            adjMatrix[index1][index2] = false;
            adjMatrix[index2][index1] = false;
        }
    }

    /**
     * Returns an iterator that performs a breadth first search traversal
     * starting at the given vertex.
     *
     * @param startVertex the starting vertex
     * @return an iterator that performs a breadth first traversal
     */
    @Override
    public Iterator<T> iteratorBFS(T startVertex) {
        return iteratorBFS(getIndex(startVertex));
    }

    /**
     * Returns an iterator that performs a breadth first search traversal
     * starting at the given index.
     *
     * @param startIndex the index to begin the search from
     * @return an iterator that performs a breadth first traversal
     */
    public Iterator<T> iteratorBFS(int startIndex) {
        Integer x;
        LinkedQueue<Integer> traversalQueue = new LinkedQueue<>();
        ArrayUnorderedList<T> resultList = new ArrayUnorderedList<>();

        if (!indexIsValid(startIndex)) {
            return resultList.iterator();
        }

        boolean[] visited = new boolean[numVertices];
        for (int i = 0; i < numVertices; i++) {
            visited[i] = false;
        }

        traversalQueue.enqueue(startIndex);
        visited[startIndex] = true;

        while (!traversalQueue.isEmpty()) {
            try {
                x = traversalQueue.dequeue();
                resultList.add(vertices[x]);

                for (int i = 0; i < numVertices; i++) {
                    if (adjMatrix[x][i] && !visited[i]) {
                        traversalQueue.enqueue(i);
                        visited[i] = true;
                    }
                }
            } catch (Exception e) {
                // Should not happen
            }
        }
        return resultList.iterator();
    }

    /**
     * Returns an iterator that performs a depth first search traversal starting
     * at the given vertex.
     *
     * @param startVertex the starting vertex
     * @return an iterator that performs a depth first traversal
     */
    @Override
    public Iterator<T> iteratorDFS(T startVertex) {
        return iteratorDFS(getIndex(startVertex));
    }

    /**
     * Returns an iterator that performs a depth first search traversal starting
     * at the given index.
     *
     * @param startIndex the index to begin the search traversal from
     * @return an iterator that performs a depth first traversal
     */
    public Iterator<T> iteratorDFS(int startIndex) {
        Integer x;
        boolean found;
        LinkedStack<Integer> traversalStack = new LinkedStack<>();
        ArrayUnorderedList<T> resultList = new ArrayUnorderedList<>();
        boolean[] visited = new boolean[numVertices];

        if (!indexIsValid(startIndex)) {
            return resultList.iterator();
        }

        for (int i = 0; i < numVertices; i++) {
            visited[i] = false;
        }

        traversalStack.push(startIndex);
        resultList.add(vertices[startIndex]);
        visited[startIndex] = true;

        while (!traversalStack.isEmpty()) {
            try {
                x = traversalStack.peek();
                found = false;

                for (int i = 0; (i < numVertices) && !found; i++) {
                    if (adjMatrix[x][i] && !visited[i]) {
                        traversalStack.push(i);
                        resultList.add(vertices[i]);
                        visited[i] = true;
                        found = true;
                    }
                }
                if (!found && !traversalStack.isEmpty()) {
                    traversalStack.pop();
                }
            } catch (Exception e) {
                // Should not happen
            }
        }
        return resultList.iterator();
    }

    /**
     * Returns an iterator that contains the shortest path between the two
     * vertices.
     *
     * @param startVertex the starting vertex
     * @param targetVertex the ending vertex
     * @return an iterator that contains the shortest path between the two
     * vertices
     */
    @Override
    public Iterator<T> iteratorShortestPath(T startVertex, T targetVertex) {
        int startIndex = getIndex(startVertex);
        int targetIndex = getIndex(targetVertex);
        ArrayUnorderedList<T> resultList = new ArrayUnorderedList<>();

        if (!indexIsValid(startIndex) || !indexIsValid(targetIndex)) {
            return resultList.iterator();
        }

        // BFS with predecessor tracking
        LinkedQueue<Integer> traversalQueue = new LinkedQueue<>();
        boolean[] visited = new boolean[numVertices];
        int[] parent = new int[numVertices];

        // Initialize
        for (int i = 0; i < numVertices; i++) {
            visited[i] = false;
            parent[i] = -1;
        }

        traversalQueue.enqueue(startIndex);
        visited[startIndex] = true;

        boolean found = false;

        // BFS to find the target and build parent relationships
        while (!traversalQueue.isEmpty() && !found) {
            try {
                int current = traversalQueue.dequeue();

                if (current == targetIndex) {
                    found = true;
                    break;
                }

                // Explore adjacent vertices
                for (int i = 0; i < numVertices; i++) {
                    if (adjMatrix[current][i] && !visited[i]) {
                        traversalQueue.enqueue(i);
                        visited[i] = true;
                        parent[i] = current;
                    }
                }
            } catch (Exception e) {
                // Error handling
            }
        }

        // If target was not found, return empty iterator
        if (!found) {
            return resultList.iterator();
        }

        // Backtrack from target to start using parent array
        LinkedStack<Integer> pathStack = new LinkedStack<>();
        int current = targetIndex;

        while (current != -1) {
            pathStack.push(current);
            current = parent[current];
        }

        // Build result list from stack (reversing the path)
        while (!pathStack.isEmpty()) {
            try {
                resultList.add(vertices[pathStack.pop()]);
            } catch (Exception e) {
                // Should not happen
            }
        }

        return resultList.iterator();
    }

    /**
     * Returns true if this graph is empty, false otherwise.
     *
     * @return true if this graph is empty
     */
    @Override
    public boolean isEmpty() {
        return (numVertices == 0);
    }

    /**
     * Returns true if this graph is connected, false otherwise.
     *
     * @return true if this graph is connected
     */
    @Override
    public boolean isConnected() {
        if (isEmpty()) {
            return false;
        }

        Iterator<T> it = iteratorBFS(0);
        int count = 0;

        while (it.hasNext()) {
            it.next();
            count++;
        }

        return (count == numVertices);
    }

    /**
     * Returns the number of vertices in this graph.
     *
     * @return the integer number of vertices in this graph
     */
    @Override
    public int size() {
        return numVertices;
    }

    /**
     * Returns a string representation of the adjacency matrix.
     *
     * @return a string representation of the adjacency matrix
     */
    @Override
    public String toString() {
        if (numVertices == 0) {
            return "Graph is empty";
        }

        String result = "";

        result += "Adjacency Matrix\n";
        result += "----------------\n";
        result += "index\t";

        for (int i = 0; i < numVertices; i++) {
            result += "" + i;
            if (i < 10) {
                result += " ";
            }
        }
        result += "\n\n";

        for (int i = 0; i < numVertices; i++) {
            result += "" + i + "\t";

            for (int j = 0; j < numVertices; j++) {
                if (adjMatrix[i][j]) {
                    result += "1 ";
                } else {
                    result += "0 ";
                }
            }
            result += "\n";
        }

        result += "\n\nVertex Values";
        result += "\n-------------\n";
        result += "index\tvalue\n\n";

        for (int i = 0; i < numVertices; i++) {
            result += "" + i + "\t";
            result += vertices[i].toString() + "\n";
        }
        result += "\n";
        return result;
    }

    @SuppressWarnings("unchecked")
    protected void expandCapacity() {
        T[] largerVertices = (T[]) (new Object[vertices.length * 2]);
        boolean[][] largerAdjMatrix = new boolean[vertices.length * 2][vertices.length * 2];

        System.arraycopy(vertices, 0, largerVertices, 0, numVertices);

        for (int i = 0; i < numVertices; i++) {
            System.arraycopy(adjMatrix[i], 0, largerAdjMatrix[i], 0, numVertices);
        }

        vertices = largerVertices;
        adjMatrix = largerAdjMatrix;
    }

    protected int getIndex(T vertex) {
        for (int i = 0; i < numVertices; i++) {
            if (vertices[i].equals(vertex)) {
                return i;
            }
        }
        return -1;
    }

    protected boolean indexIsValid(int index) {
        return ((index >= 0) && (index < numVertices));
    }

    public ArrayUnorderedList<T> getVertices() {
        ArrayUnorderedList<T> list = new ArrayUnorderedList<>();
        for (int i = 0; i < numVertices; i++) {
            list.add(vertices[i]);
        }
        return list;
    }

    /**
     * Calcula a distância (em número de arestas) do startVertex para todos os outros.
     * Retorna um array de inteiros onde o índice corresponde ao índice do vértice.
     */
    public int[] getDistancesFrom(T startVertex) {
        int startIndex = getIndex(startVertex);
        int[] distances = new int[numVertices];
        
        // Inicializar com valor máximo (infinito)
        for (int i = 0; i < numVertices; i++) {
            distances[i] = Integer.MAX_VALUE;
        }

        if (!indexIsValid(startIndex)) {
            return distances;
        }

        // BFS para calcular distâncias
        LinkedQueue<Integer> queue = new LinkedQueue<>();
        distances[startIndex] = 0;
        queue.enqueue(startIndex);

        while (!queue.isEmpty()) {
            try {
                int current = queue.dequeue();
                
                for (int i = 0; i < numVertices; i++) {
                    // Se existe aresta e ainda não foi visitado
                    if (adjMatrix[current][i] && distances[i] == Integer.MAX_VALUE) {
                        distances[i] = distances[current] + 1;
                        queue.enqueue(i);
                    }
                }
            } catch (Exception e) { }
        }
        return distances;
    }
}

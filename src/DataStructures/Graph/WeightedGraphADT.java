package DataStructures.Graph;

/**
 * WeightedGraphADT defines the interface to a weighted graph.
 * Based on Aula 12 - Networks (Weighted Graphs)
 */
public interface WeightedGraphADT<T> extends GraphADT<T> {
    
    /**
     * Inserts an edge between two vertices of this graph with a weight.
     *
     * @param vertex1 the first vertex
     * @param vertex2 the second vertex
     * @param weight the weight of the edge
     */
    public void addEdge(T vertex1, T vertex2, double weight);

    /**
     * Returns the weight of the shortest path between two vertices.
     *
     * @param vertex1 the first vertex
     * @param vertex2 the second vertex
     * @return the weight of the shortest path
     */
    public double shortestPathWeight(T vertex1, T vertex2);
}

package DataStructures;

/**
 * Iterator represents a generic iterator interface for traversing
 * elements in a collection sequentially. Provides standard operations
 * to check for remaining elements and retrieve the next element.
 *
 * @param <T> the type of elements returned by this iterator
 */
public interface Iterator<T> {
    /**
     * Returns true if the iteration has more elements.
     * In other words, returns true if next() would return an element
     * rather than throwing an exception.
     *
     * @return true if the iterator has more elements to traverse, false otherwise
     */
    boolean hasNext();

    /**
     * Returns the next element in the iteration.
     * Should only be called if hasNext() returns true.
     *
     * @return the next element in the iteration
     */
    T next();
}

package DataStructures.ArrayList;

import DataStructures.Exceptions.NoSuchElementException;
import DataStructures.Iterator;

/**
 * ArrayIterator represents an iterator for traversing elements in an array-based
 * collection sequentially. Maintains a current position pointer and the total count
 * of elements to enable hasNext() and next() operations. Implements the Iterator
 * interface for generic collection traversal support.
 *
 * @param <T> the type of elements returned by this iterator
 */
public class ArrayIterator<T> implements Iterator<T> {

    private int current;
    private T[] items;
    private int count;

    /**
     * Creates a new ArrayIterator for the specified array with the given size.
     * Initializes the current position to 0 and stores references to the array
     * and its valid element count.
     *
     * @param collection the array containing elements to iterate over
     * @param size the number of valid elements in the array
     */
    public ArrayIterator(T[] collection, int size) {
        items = collection;
        count = size;
        current = 0;
    }

    /**
     * Returns true if there are more elements to traverse in the collection.
     * Compares the current position with the total element count.
     *
     * @return true if the next element exists, false if iteration is complete
     */
    @Override
    public boolean hasNext() {
        return current < count;
    }

    /**
     * Returns the next element in the iteration and advances the current position.
     * Should only be called if hasNext() returns true.
     *
     * @return the next element in the collection
     * @throws NoSuchElementException if there are no more elements to iterate
     */
    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        // Return current element and advance position pointer
        return items[current++];
    }
}

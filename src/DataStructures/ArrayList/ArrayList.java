package DataStructures.ArrayList;

import DataStructures.Exceptions.EmptyCollectionException;
import DataStructures.Exceptions.NoSuchElementException;
import DataStructures.Iterator;

/**
 * ArrayList is an abstract base class representing a list implemented using a
 * dynamic array. Maintains a rear pointer tracking the number of elements and
 * provides common operations for adding, removing, and accessing elements.
 * Subclasses implement specific insertion strategies (ordered vs unordered).
 * The underlying array has a default capacity of 100 elements.
 *
 * @param <T> the type of elements stored in this list
 */
public abstract class ArrayList<T> implements ListADT<T> {

    protected final int DEFAULT_CAPACITY = 100;
    protected int rear;
    protected T[] list;

    /**
     * Creates a new empty ArrayList with default capacity.
     * Initializes the list array with DEFAULT_CAPACITY elements and sets rear to zero.
     */
    @SuppressWarnings("unchecked")
    public ArrayList() {
        list = (T[]) (new Object[DEFAULT_CAPACITY]);
        rear = 0;
    }

    /**
     * Removes and returns the element at the front of this list.
     * Shifts all remaining elements one position to the left and decrements rear.
     * Clears the old rear position to allow garbage collection.
     * Operates in O(n) time due to element shifting.
     *
     * @return the element that was at the front of this list
     * @throws EmptyCollectionException if the list is empty
     */
    @Override
    public T removeFirst() throws EmptyCollectionException {
        if (isEmpty()) {
            throw new EmptyCollectionException("Lista vazia.");
        }
        T result = list[0];
        // Shift all elements one position to the left, filling the gap at index 0
        for (int i = 0; i < rear - 1; i++) {
            list[i] = list[i + 1];
        }
        list[--rear] = null;
        return result;
    }

    /**
     * Removes and returns the element at the rear of this list.
     * Simply decrements rear and clears the position to allow garbage collection.
     * No element shifting required. Operates in O(1) time.
     *
     * @return the element that was at the rear of this list
     * @throws EmptyCollectionException if the list is empty
     */
    @Override
    public T removeLast() throws EmptyCollectionException {
        if (isEmpty()) {
            throw new EmptyCollectionException("Lista vazia.");
        }
        T result = list[--rear];
        list[rear] = null;
        return result;
    }

    /**
     * Removes and returns the first occurrence of the specified element from this list.
     * Searches for element using equals() comparison, shifts subsequent elements,
     * and clears the old rear position. Operates in O(n) time due to searching and shifting.
     *
     * @param element the element to be removed from this list
     * @return the element that was removed
     * @throws EmptyCollectionException if the list is empty
     * @throws NoSuchElementException if the element is not found in this list
     */
    @Override
    public T remove(T element) throws EmptyCollectionException {
        if (isEmpty()) {
            throw new EmptyCollectionException("Lista vazia.");
        }
        // Search for the first occurrence of the element
        int index = -1;
        for (int i = 0; i < rear; i++) {
            if (list[i].equals(element)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new NoSuchElementException("Elemento não encontrado.");
        }
        T result = list[index];
        // Shift all elements after the removed element one position to the left
        for (int i = index; i < rear - 1; i++) {
            list[i] = list[i + 1];
        }
        list[--rear] = null;
        return result;
    }

    /**
     * Removes and returns the element at the specified index in this list.
     * Shifts all subsequent elements one position to the left and decrements rear.
     * Clears the old rear position to allow garbage collection. Operates in O(n) time.
     *
     * @param index the index of the element to be removed (0-based)
     * @return the element that was at the specified index
     * @throws IndexOutOfBoundsException if index is negative or greater than or equal to rear
     */
    // Method added for compatibility
    public T remove(int index) {
        if (index < 0 || index >= rear) {
            throw new IndexOutOfBoundsException();
        }
        T result = list[index];
        // Shift all elements after the removed element one position to the left
        for (int i = index; i < rear - 1; i++) {
            list[i] = list[i + 1];
        }
        list[--rear] = null;
        return result;
    }

    /**
     * Returns the element at the specified index in this list without removing it.
     * Valid indices range from 0 to rear-1. Operates in O(1) time.
     *
     * @param index the index of the element to return (0-based)
     * @return the element at the specified index
     * @throws IndexOutOfBoundsException if index is negative or greater than or equal to rear
     */
    public T get(int index) {
        if (index < 0 || index >= rear) {
            throw new IndexOutOfBoundsException();
        }
        return list[index];
    }

    /**
     * Returns the element at the front of this list without removing it.
     * Operates in O(1) time.
     *
     * @return the element at the front of this list
     * @throws EmptyCollectionException if the list is empty
     */
    @Override
    public T first() throws EmptyCollectionException {
        if (isEmpty()) {
            throw new EmptyCollectionException("Lista vazia.");
        }
        return list[0];
    }

    /**
     * Returns the element at the rear of this list without removing it.
     * Operates in O(1) time.
     *
     * @return the element at the rear of this list
     * @throws EmptyCollectionException if the list is empty
     */
    @Override
    public T last() throws EmptyCollectionException {
        if (isEmpty()) {
            throw new EmptyCollectionException("Lista vazia.");
        }
        return list[rear - 1];
    }

    /**
     * Returns true if this list contains the specified element.
     * Searches for element using equals() comparison. Operates in O(n) time.
     *
     * @param target the element to search for
     * @return true if the element is found in this list, false otherwise
     */
    @Override
    public boolean contains(T target) {
        // Linear search through all elements in the list
        for (int i = 0; i < rear; i++) {
            if (list[i].equals(target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if this list is empty, false otherwise.
     *
     * @return true if this list contains no elements, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return rear == 0;
    }

    /**
     * Returns the number of elements currently in this list.
     *
     * @return the number of elements in this list
     */
    @Override
    public int size() {
        return rear;
    }

    /**
     * Returns an iterator for traversing elements in this list sequentially.
     * Creates an ArrayIterator passing the list array and rear pointer.
     *
     * @return an Iterator for this list
     */
    @Override
    public Iterator<T> iterator() {
        return new ArrayIterator<>(list, rear);
    }

    /**
     * Returns a string representation of this list.
     * Constructs a formatted string enclosed in brackets with comma-separated elements.
     * Operates in O(n) time to iterate through all elements.
     *
     * @return a string representation of this list
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        // Append each element with comma separators between elements
        for (int i = 0; i < rear; i++) {
            sb.append(list[i]);
            if (i < rear - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}

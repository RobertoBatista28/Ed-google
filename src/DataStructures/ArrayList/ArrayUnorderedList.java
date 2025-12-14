package DataStructures.ArrayList;

import DataStructures.Exceptions.NoSuchElementException;

/**
 * ArrayUnorderedList represents an unordered list implemented using a dynamic array.
 * Extends ArrayList and provides operations to add elements at the front, rear, or
 * after a specific target element. Maintains element order based on insertion position
 * rather than value comparison. Dynamically expands capacity when reaching array limits.
 *
 * @param <T> the type of elements stored in this list
 */
public class ArrayUnorderedList<T> extends ArrayList<T> implements UnorderedListADT<T> {

    /**
     * Creates a new empty ArrayUnorderedList with default capacity.
     * Delegates initialization to parent ArrayList class.
     */
    public ArrayUnorderedList() {
        super();
    }

    /**
     * Inserts the specified element at the front of this list.
     * Shifts all existing elements one position to the right to make room.
     * Expands capacity if necessary. Operates in O(n) time due to shifting.
     *
     * @param element the element to be inserted at the front
     */
    @Override
    public void addToFront(T element) {
        if (rear == list.length) {
            expandCapacity();
        }

        // Shift all elements one position to the right, starting from the end
        for (int i = rear; i > 0; i--) {
            list[i] = list[i - 1];
        }

        list[0] = element;
        rear++;
    }

    /**
     * Appends the specified element to the rear of this list.
     * Places the element at the position indicated by rear and increments rear.
     * Expands capacity if necessary. Operates in O(1) time (amortized).
     *
     * @param element the element to be appended to the rear
     */
    @Override
    public void addToRear(T element) {
        if (rear == list.length) {
            expandCapacity();
        }
        list[rear++] = element;
    }

    /**
     * Inserts the specified element immediately after the first occurrence of
     * the target element. Searches for target using equals() comparison, then
     * shifts subsequent elements to create space for insertion. Expands capacity
     * if necessary. Operates in O(n) time due to shifting.
     *
     * @param element the element to be inserted
     * @param target the element after which the new element should be placed
     * @throws NoSuchElementException if target element is not found in the list
     */
    @Override
    public void addAfter(T element, T target) {
        if (rear == list.length) {
            expandCapacity();
        }

        // Search for the target element in the list
        int index = -1;
        for (int i = 0; i < rear; i++) {
            if (list[i].equals(target)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new NoSuchElementException("Elemento não encontrado.");
        }

        // Shift elements starting from the end to index+1, creating space at index+1
        for (int i = rear; i > index + 1; i--) {
            list[i] = list[i - 1];
        }

        list[index + 1] = element;
        rear++;
    }

    /**
     * Adds the specified element to this list.
     * Delegates to addToRear() method, appending the element to the end.
     *
     * @param element the element to be added to this list
     */
    public void add(T element) {
        addToRear(element);
    }

    /**
     * Returns the element at the specified index in this list.
     * Valid indices range from 0 to rear-1. Operates in O(1) time.
     *
     * @param index the index of the element to return
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
     * Expands the capacity of this list by doubling its current size.
     * Copies all existing elements to the larger array while preserving their order.
     * Called automatically when the list reaches capacity during insertion operations.
     */
    private void expandCapacity() {
        // Create new array with doubled capacity
        T[] newList = (T[]) new Object[list.length * 2];
        // Copy all existing elements to the new larger array
        for (int i = 0; i < rear; i++) {
            newList[i] = list[i];
        }
        list = newList;
    }
}

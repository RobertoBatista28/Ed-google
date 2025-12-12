package DataStructures.Heap;

import DataStructures.Exceptions.EmptyCollectionException;

/**
 * HeapADT defines the interface to a heap data structure.
 * Based on Aula 11 - Heaps and Priority Queues
 */
public interface HeapADT<T> {
    
    /**
     * Adds the specified object to this heap.
     *
     * @param obj the element to added to this head
     */
    public void addElement(T obj);

    /**
     * Removes element with the lowest value from this heap.
     *
     * @return the element with the lowest value from this heap
     */
    public T removeMin() throws EmptyCollectionException;

    /**
     * Returns a reference to the element with the lowest value in
     * this heap.
     *
     * @return a reference to the element with the lowest value
     * in this heap
     */
    public T findMin();
    
    /**
     * Returns true if this heap is empty, false otherwise.
     */
    public boolean isEmpty();
    
    /**
     * Returns the number of elements in this heap.
     */
    public int size();
}

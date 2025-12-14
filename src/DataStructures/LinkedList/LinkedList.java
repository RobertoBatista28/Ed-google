package DataStructures.LinkedList;

import DataStructures.Exceptions.EmptyCollectionException;
import DataStructures.Iterator;
import DataStructures.Exceptions.NoSuchElementException;

/**
 * LinkedList represents a generic doubly-linked list that maintains
 * references to both the head and tail nodes for efficient insertion
 * and removal at both ends. Each node contains bidirectional pointers
 * enabling traversal in both directions. Tracks list size for O(1)
 * size queries and supports iteration through anonymous inner classes.
 *
 * @param <T> the type of elements stored in this list
 */
public class LinkedList<T> {

    private DoubleNode<T> head;
    private DoubleNode<T> tail;
    private int size;

    /**
     * Creates a new empty LinkedList with no elements.
     * Initializes head and tail to null and size to zero.
     */
    public LinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * Appends the specified element to the end of this list.
     * If the list is empty, the element becomes both head and tail.
     * Otherwise, links the element to the current tail and updates tail reference.
     * Operates in O(1) time.
     *
     * @param element the element to be appended to this list
     */
    public void add(T element) {
        DoubleNode<T> newNode = new DoubleNode<>(element);
        if (size == 0) {
            // First element: initialize both head and tail to this node
            head = newNode;
            tail = newNode;
        } else {
            // Append to end: link new node to current tail and update tail
            newNode.setPrevious(this.tail);
            this.tail.setNext(newNode);
            this.tail = newNode;
        }
        size++;
    }

    /**
     * Appends the specified pre-constructed node to the end of this list.
     * Useful for reusing existing node structures from other data structures.
     * Performs identical linking logic as add(T element) but accepts a node directly.
     * Operates in O(1) time.
     *
     * @param newNode the DoubleNode to be appended to this list
     */
    // Method from PL2 structure
    public void add(DoubleNode<T> newNode) {
        if (size == 0) {
            // First node: initialize both head and tail to this node
            head = newNode;
            tail = newNode;
        } else {
            // Append to end: link new node to current tail and update tail
            newNode.setPrevious(this.tail);
            this.tail.setNext(newNode);
            this.tail = newNode;
        }
        size++;
    }

    /**
     * Removes and discards the element at the head of this list.
     * Updates head reference to the next node and clears its previous pointer.
     * If removal empties the list, sets tail to null for consistency.
     * Operates in O(1) time.
     *
     * @throws EmptyCollectionException if the list is empty
     */
    public void removeFirst() throws EmptyCollectionException {
        if (isEmpty()) {
            throw new EmptyCollectionException("List is empty");
        }
        // Move head pointer to next node
        this.head = this.head.getNext();
        if (this.head != null) {
            // Clear backward pointer from new head
            this.head.setPrevious(null);
        }
        size--;
        // If list becomes empty after removal, clear tail reference
        if (size == 0) {
            tail = null;
        }
    }

    /**
     * Removes and discards the element at the tail of this list.
     * Handles the special case of single-element lists by clearing both head and tail.
     * For multi-element lists, updates tail to previous node and clears its next pointer.
     * Operates in O(1) time.
     *
     * @throws EmptyCollectionException if the list is empty
     */
    public void removeLast() throws EmptyCollectionException {
        if (isEmpty()) {
            throw new EmptyCollectionException("List is empty");
        }

        if (size == 1) {
            // Special case: only one element - clear both head and tail
            head = null;
            tail = null;
        } else {
            // Multi-element list: move tail to previous node and clear forward pointer
            this.tail = this.tail.getPrevious();
            this.tail.setNext(null);
        }
        size--;
    }

    /**
     * Returns the element at the specified index in this list.
     * Traverses from head node sequentially to reach the target index.
     * Valid indices range from 0 (head) to size-1 (tail).
     * Operates in O(n) time due to linear traversal requirement.
     *
     * @param index the index of the element to return (0-based)
     * @return the element at the specified index
     * @throws IndexOutOfBoundsException if index is negative or greater than or equal to size
     */
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        // Traverse from head to reach the target index position
        DoubleNode<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.getNext();
        }
        return current.getData();
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements currently in this list
     */
    public int size() {
        return size;
    }

    /**
     * Returns true if this list is empty, false otherwise.
     *
     * @return true if the list contains no elements, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns an iterator for traversing elements in this list from head to tail.
     * Creates an anonymous inner class implementing the Iterator interface.
     * The iterator maintains a current node reference that advances with each next() call.
     *
     * @return an Iterator that traverses this list from beginning to end
     */
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            // Maintain current position for sequential traversal
            private DoubleNode<T> current = head;

            /**
             * Returns true if there are more elements to traverse.
             *
             * @return true if current node is not null, false if traversal is complete
             */
            @Override
            public boolean hasNext() {
                return current != null;
            }

            /**
             * Returns the element at the current position and advances to the next node.
             *
             * @return the element at current position
             * @throws NoSuchElementException if there are no more elements to iterate
             */
            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                // Retrieve data before advancing to maintain correct return value
                T data = current.getData();
                current = current.getNext();
                return data;
            }
        };
    }

    /**
     * Returns a string representation of this list.
     * Constructs a string by concatenating the string representation of each element
     * separated by newlines, traversing from head to tail. Operates in O(n) time.
     *
     * @return a string containing all elements in this list, one per line
     */
    @Override
    public String toString() {
        String str = "";
        DoubleNode<T> current = head;

        // Traverse the entire list, appending each element's string representation
        for (int i = 0; i < size; i++) {
            str += current.getData().toString() + "\n";
            current = current.getNext();
        }

        return str;
    }
}

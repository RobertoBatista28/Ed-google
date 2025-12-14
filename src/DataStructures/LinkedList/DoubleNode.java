package DataStructures.LinkedList;

/**
 * DoubleNode represents a node in a doubly-linked list structure,
 * containing a generic data element and bidirectional pointers to
 * adjacent nodes. Enables traversal in both forward and backward
 * directions throughout the linked list.
 *
 * @param <T> the type of data stored in this node
 */
public class DoubleNode<T> {

    private T data;
    private DoubleNode<T> next;
    private DoubleNode<T> previous;

    /**
     * Creates a new DoubleNode with the specified data.
     * Initializes next and previous pointers to null.
     *
     * @param data the element to be stored in this node
     */
    public DoubleNode(T data) {
        this.data = data;
        this.next = null;
        this.previous = null;
    }

    /**
     * Returns the element stored in this node.
     *
     * @return the data contained in this node
     */
    public T getData() {
        return data;
    }

    /**
     * Sets the element stored in this node.
     *
     * @param data the element to be stored in this node
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * Returns the next node in the doubly-linked list.
     *
     * @return the next DoubleNode, or null if this is the last node
     */
    public DoubleNode<T> getNext() {
        return next;
    }

    /**
     * Sets the next node in the doubly-linked list.
     *
     * @param next the DoubleNode to link as the next node, or null if this is the last node
     */
    public void setNext(DoubleNode<T> next) {
        this.next = next;
    }

    /**
     * Returns the previous node in the doubly-linked list.
     *
     * @return the previous DoubleNode, or null if this is the first node
     */
    public DoubleNode<T> getPrevious() {
        return previous;
    }

    /**
     * Sets the previous node in the doubly-linked list.
     *
     * @param previous the DoubleNode to link as the previous node, or null if this is the first node
     */
    public void setPrevious(DoubleNode<T> previous) {
        this.previous = previous;
    }

    /**
     * Returns a string representation of this node.
     * Delegates to the data element's toString method.
     *
     * @return the string representation of the data stored in this node
     */
    @Override
    public String toString() {
        return data.toString();
    }
}

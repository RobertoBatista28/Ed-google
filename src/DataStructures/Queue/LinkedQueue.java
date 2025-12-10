package DataStructures.Queue;

import DataStructures.Exceptions.EmptyCollectionException;
import DataStructures.Stack.LinearNode;

public class LinkedQueue<T> implements QueueADT<T> {

    /**
     * int that represents both the number of elements and the next available
     * position in the array
     */
    private int size;

    /**
     * reference to the front of the queue
     */
    private LinearNode<T> front;

    /**
     * reference to the rear of the queue
     */
    private LinearNode<T> rear;

    /**
     * Creates an empty queue.
     */
    public LinkedQueue() {
        this.size = 0;
        this.front = null;
        this.rear = null;
    }

    /**
     * Adds the specified element to the rear of this queue.
     *
     * @param element the element to be added to the rear of the queue
     */
    @Override
    public void enqueue(T element) {
        LinearNode<T> node = new LinearNode<>(element);

        if (isEmpty()) {
            front = node;
        } else {
            rear.setNext(node);
        }

        rear = node;
        size++;
    }

    /**
     * Removes the element at the front of this queue and returns a reference to
     * it. Throws an EmptyCollectionException if the queue is empty.
     *
     * @return the element at the front of the queue
     * @throws EmptyCollectionException if the queue is empty
     */
    @Override
    public T dequeue() throws EmptyCollectionException {
        if (isEmpty()) {
            throw new EmptyCollectionException("The Queue Is Empty");
        }

        T result = front.getElement();
        front = front.getNext();
        size--;

        if (isEmpty()) {
            rear = null;
        }

        return result;
    }

    /**
     * Returns a reference to the element at the front of this queue. The
     * element is not removed from the queue. Throws an EmptyCollectionException
     * if the queue is empty.
     *
     * @return the first element in the queue
     * @throws EmptyCollectionException if the queue is empty
     */
    @Override
    public T first() throws EmptyCollectionException {
        if (isEmpty()) {
            throw new EmptyCollectionException("The Queue Is Empty");
        }
        return front.getElement();
    }

    /**
     * Returns true if this queue contains no elements.
     *
     * @return true if this queue is empty
     */
    @Override
    public boolean isEmpty() {
        return (size == 0);
    }

    /**
     * Returns the number of elements in this queue.
     *
     * @return the integer representation of the size of the queue
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns a string representation of this queue.
     *
     * @return the string representation of the queue
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        LinearNode<T> current = front;

        while (current != null) {
            sb.append(current.getElement()).append("\n");
            current = current.getNext();
        }

        return sb.toString();
    }
}

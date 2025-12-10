package DataStructures.LinkedList;

import DataStructures.Exceptions.EmptyCollectionException;
import DataStructures.Iterator;
import DataStructures.Exceptions.NoSuchElementException;

public class LinkedList<T> {

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public LinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    public void add(T element) {
        Node<T> newNode = new Node<>(element);
        if (size == 0) {
            head = newNode;
            tail = newNode;
        } else {
            this.tail.setNext(newNode);
            this.tail = newNode;
        }
        size++;
    }

    // Method from PL2 structure
    public void add(Node<T> newNode) {
        if (size == 0) {
            head = newNode;
            tail = newNode;
        } else {
            this.tail.setNext(newNode);
            this.tail = newNode;
        }
        size++;
    }

    public void removeFirst() throws EmptyCollectionException {
        if (isEmpty()) {
            throw new EmptyCollectionException("List is empty");
        }
        this.head = this.head.getNext();
        size--;
        if (size == 0) {
            tail = null;
        }
    }

    public void removeLast() throws EmptyCollectionException {
        if (isEmpty()) {
            throw new EmptyCollectionException("List is empty");
        }

        if (size == 1) {
            head = null;
            tail = null;
        } else {
            Node<T> current = this.head;
            while (current.getNext() != tail) {
                current = current.getNext();
            }
            this.tail = current;
            this.tail.setNext(null);
        }
        size--;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.getNext();
        }
        return current.getData();
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T data = current.getData();
                current = current.getNext();
                return data;
            }
        };
    }

    @Override
    public String toString() {
        String str = "";
        Node<T> current = head;

        for (int i = 0; i < size; i++) {
            str += current.getData().toString() + "\n";
            current = current.getNext();
        }

        return str;
    }
}

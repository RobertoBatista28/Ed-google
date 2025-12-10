package DataStructures.LinkedList;

import DataStructures.Exceptions.EmptyCollectionException;
import DataStructures.Iterator;
import DataStructures.Exceptions.NoSuchElementException;

public class LinkedList<T> {

    private DoubleNode<T> head;
    private DoubleNode<T> tail;
    private int size;

    public LinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    public void add(T element) {
        DoubleNode<T> newNode = new DoubleNode<>(element);
        if (size == 0) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.setPrevious(this.tail);
            this.tail.setNext(newNode);
            this.tail = newNode;
        }
        size++;
    }

    // Method from PL2 structure
    public void add(DoubleNode<T> newNode) {
        if (size == 0) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.setPrevious(this.tail);
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
        if (this.head != null) {
            this.head.setPrevious(null);
        }
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
            this.tail = this.tail.getPrevious();
            this.tail.setNext(null);
        }
        size--;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        DoubleNode<T> current = head;
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
            private DoubleNode<T> current = head;

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
        DoubleNode<T> current = head;

        for (int i = 0; i < size; i++) {
            str += current.getData().toString() + "\n";
            current = current.getNext();
        }

        return str;
    }
}

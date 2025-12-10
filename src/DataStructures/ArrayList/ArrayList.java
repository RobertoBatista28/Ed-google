package DataStructures.ArrayList;

import DataStructures.Exceptions.EmptyCollectionException;
import DataStructures.Exceptions.NoSuchElementException;
import DataStructures.Iterator;

public abstract class ArrayList<T> implements ListADT<T> {

    protected final int DEFAULT_CAPACITY = 100;
    protected int rear;
    protected T[] list;

    @SuppressWarnings("unchecked")
    public ArrayList() {
        list = (T[]) (new Object[DEFAULT_CAPACITY]);
        rear = 0;
    }

    @Override
    public T removeFirst() throws EmptyCollectionException {
        if (isEmpty()) {
            throw new EmptyCollectionException("Lista vazia.");
        }
        T result = list[0];
        for (int i = 0; i < rear - 1; i++) {
            list[i] = list[i + 1];
        }
        list[--rear] = null;
        return result;
    }

    @Override
    public T removeLast() throws EmptyCollectionException {
        if (isEmpty()) {
            throw new EmptyCollectionException("Lista vazia.");
        }
        T result = list[--rear];
        list[rear] = null;
        return result;
    }

    @Override
    public T remove(T element) throws EmptyCollectionException {
        if (isEmpty()) {
            throw new EmptyCollectionException("Lista vazia.");
        }
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
        for (int i = index; i < rear - 1; i++) {
            list[i] = list[i + 1];
        }
        list[--rear] = null;
        return result;
    }

    // Method added for compatibility
    public T remove(int index) {
        if (index < 0 || index >= rear) {
            throw new IndexOutOfBoundsException();
        }
        T result = list[index];
        for (int i = index; i < rear - 1; i++) {
            list[i] = list[i + 1];
        }
        list[--rear] = null;
        return result;
    }

    public T get(int index) {
        if (index < 0 || index >= rear) {
            throw new IndexOutOfBoundsException();
        }
        return list[index];
    }

    @Override
    public T first() throws EmptyCollectionException {
        if (isEmpty()) {
            throw new EmptyCollectionException("Lista vazia.");
        }
        return list[0];
    }

    @Override
    public T last() throws EmptyCollectionException {
        if (isEmpty()) {
            throw new EmptyCollectionException("Lista vazia.");
        }
        return list[rear - 1];
    }

    @Override
    public boolean contains(T target) {
        for (int i = 0; i < rear; i++) {
            if (list[i].equals(target)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return rear == 0;
    }

    @Override
    public int size() {
        return rear;
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayIterator<>(list, rear);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
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

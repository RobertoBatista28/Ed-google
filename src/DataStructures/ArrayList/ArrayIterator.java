package DataStructures.ArrayList;

import DataStructures.Exceptions.NoSuchElementException;
import DataStructures.Iterator;

public class ArrayIterator<T> implements Iterator<T> {

    private int current;
    private T[] items;
    private int count;

    public ArrayIterator(T[] collection, int size) {
        items = collection;
        count = size;
        current = 0;
    }

    @Override
    public boolean hasNext() {
        return current < count;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return items[current++];
    }
}

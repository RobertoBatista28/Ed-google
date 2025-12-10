package DataStructures.ArrayList;

import DataStructures.Exceptions.NoSuchElementException;

public class ArrayUnorderedList<T> extends ArrayList<T> implements UnorderedListADT<T> {

    public ArrayUnorderedList() {
        super();
    }

    @Override
    public void addToFront(T element) {
        if (rear == list.length) {
            expandCapacity();
        }

        for (int i = rear; i > 0; i--) {
            list[i] = list[i - 1];
        }

        list[0] = element;
        rear++;
    }

    @Override
    public void addToRear(T element) {
        if (rear == list.length) {
            expandCapacity();
        }
        list[rear++] = element;
    }

    @Override
    public void addAfter(T element, T target) {
        if (rear == list.length) {
            expandCapacity();
        }

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

        for (int i = rear; i > index + 1; i--) {
            list[i] = list[i - 1];
        }

        list[index + 1] = element;
        rear++;
    }

    public void add(T element) {
        addToRear(element);
    }

    public T get(int index) {
        if (index < 0 || index >= rear) {
            throw new IndexOutOfBoundsException();
        }
        return list[index];
    }

    private void expandCapacity() {
        T[] newList = (T[]) new Object[list.length * 2];
        for (int i = 0; i < rear; i++) {
            newList[i] = list[i];
        }
        list = newList;
    }
}

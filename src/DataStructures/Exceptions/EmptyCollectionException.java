package DataStructures.Exceptions;

public class EmptyCollectionException extends Exception {
    public EmptyCollectionException() {
        super();
    }

    public EmptyCollectionException(String message) {
        super(message);
    }
}

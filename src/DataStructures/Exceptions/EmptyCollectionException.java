package DataStructures.Exceptions;

/**
 * EmptyCollectionException is thrown when an attempt is made to perform an
 * operation on an empty collection that requires at least one element. Extends
 * Exception to enforce explicit handling through catch or throws declaration.
 */
public class EmptyCollectionException extends Exception {
    /**
     * Creates a new EmptyCollectionException with no detail message.
     */
    public EmptyCollectionException() {
        super();
    }

    /**
     * Creates a new EmptyCollectionException with the specified detail message.
     *
     * @param message a descriptive message about the exception
     */
    public EmptyCollectionException(String message) {
        super(message);
    }
}

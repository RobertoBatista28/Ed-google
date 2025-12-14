package DataStructures.Exceptions;

/**
 * NoSuchElementException is thrown when an attempt is made to access an element
 * that does not exist in a collection or iterator. Extends RuntimeException to
 * allow unchecked throwing without explicit catch or throws declaration.
 */
public class NoSuchElementException extends RuntimeException {
    /**
     * Creates a new NoSuchElementException with no detail message.
     */
    public NoSuchElementException() {
        super();
    }

    /**
     * Creates a new NoSuchElementException with the specified detail message.
     *
     * @param message a descriptive message about the exception
     */
    public NoSuchElementException(String message) {
        super(message);
    }
}

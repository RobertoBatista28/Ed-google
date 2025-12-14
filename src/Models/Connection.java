package Models;

/**
 * Connection represents a link between two rooms in the game map,
 * allowing player movement between connected rooms. Each connection
 * can be locked and may require a specific item as a key to unlock
 * and traverse.
 */
public class Connection {

    // ----------------------------------------------------------------
    // Fields
    // ----------------------------------------------------------------
    private Room from;
    private Room to;
    private boolean isLocked;
    private Item key;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    /**
     * Creates a new Connection between two rooms with a locked state
     * and an optional key item.
     *
     * @param from the room where the connection starts
     * @param to the room where the connection leads
     * @param isLocked true if the connection is locked, false otherwise
     * @param key the item required to unlock this connection, or null if no key is needed
     */
    public Connection(Room from, Room to, boolean isLocked, Item key) {
        this.from = from;
        this.to = to;
        this.isLocked = isLocked;
        this.key = key;
    }

    // ----------------------------------------------------------------
    // Getters & Setters
    // ----------------------------------------------------------------
    /**
     * Returns the room where this connection starts.
     *
     * @return the source room of the connection
     */
    public Room getFrom() {
        return from;
    }

    /**
     * Returns the room where this connection leads.
     *
     * @return the destination room of the connection
     */
    public Room getTo() {
        return to;
    }

    /**
     * Returns whether this connection is currently locked.
     *
     * @return true if the connection is locked, false otherwise
     */
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * Sets the locked state of this connection.
     *
     * @param locked true to lock the connection, false to unlock it
     */
    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    /**
     * Returns the item that serves as a key to unlock this connection.
     *
     * @return the key item, or null if no key is required
     */
    public Item getKey() {
        return key;
    }
}

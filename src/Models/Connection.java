package Models;

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
    public Connection(Room from, Room to, boolean isLocked, Item key) {
        this.from = from;
        this.to = to;
        this.isLocked = isLocked;
        this.key = key;
    }

    // ----------------------------------------------------------------
    // Getters & Setters
    // ----------------------------------------------------------------
    public Room getFrom() {
        return from;
    }

    public Room getTo() {
        return to;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public Item getKey() {
        return key;
    }
}

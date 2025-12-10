package Models;

public class Connection {

    private Room from;
    private Room to;
    private boolean isLocked;
    private int keyId;

    public Connection(Room from, Room to, boolean isLocked, int keyId) {
        this.from = from;
        this.to = to;
        this.isLocked = isLocked;
        this.keyId = keyId;
    }

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

    public int getKeyId() {
        return keyId;
    }
}

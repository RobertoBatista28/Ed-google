package Models;

// REMOVIDO: import DataStructures.ArrayList.ArrayUnorderedList;

/**
 * Room represents a room in the game labyrinth.
 * Refactored according to Aula 12 - Graphs: Room only stores room data,
 * NOT topology connections. The GameNetwork is the single source of truth for topology.
 */
public class Room {

    // ----------------------------------------------------------------
    // Fields
    // ----------------------------------------------------------------
    private String name;
    private int x;
    private int y;
    private boolean isEntrance;
    private boolean isCenter;
    // REMOVED: private ArrayUnorderedList<Connection> connections;
    private Lever lever;
    private boolean hasQuestion;
    private boolean hasPickaxe;
    private boolean hasEnderPearl;
    private boolean isSoulSand;
    // TODO: Replace these with String representations for API/UI separation
    private java.awt.Color customFloorColor;
    private java.awt.image.BufferedImage customFloorImage;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    public Room(String name, int x, int y, boolean isEntrance, boolean isCenter) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.isEntrance = isEntrance;
        this.isCenter = isCenter;
        // REMOVED: this.connections = new ArrayUnorderedList<>();
        this.lever = null;
        this.hasQuestion = false;
        this.hasPickaxe = false;
        this.hasEnderPearl = false;
        this.isSoulSand = false;
        this.customFloorColor = null;
        this.customFloorImage = null;
    }

    // ----------------------------------------------------------------
    // Movement Cost Method (for Weighted Graph)
    // ----------------------------------------------------------------
    
    /**
     * Returns the movement cost for entering this room.
     * Used by the GameNetwork to set edge weights.
     * Soul Sand rooms cost 3.0, normal rooms cost 1.0.
     */
    public double getMovementCost() {
        return isSoulSand ? 3.0 : 1.0;
    }

    // ----------------------------------------------------------------
    // Getters & Setters
    // ----------------------------------------------------------------
    public void setSoulSand(boolean isSoulSand) {
        this.isSoulSand = isSoulSand;
    }

    public boolean isSoulSand() {
        return isSoulSand;
    }

    public void setCustomFloorColor(java.awt.Color color) {
        this.customFloorColor = color;
        this.customFloorImage = null;
    }

    public java.awt.Color getCustomFloorColor() {
        return customFloorColor;
    }

    public void setCustomFloorImage(java.awt.image.BufferedImage image) {
        this.customFloorImage = image;
        this.customFloorColor = null;
    }

    public java.awt.image.BufferedImage getCustomFloorImage() {
        return customFloorImage;
    }

    public void setHasPickaxe(boolean hasPickaxe) {
        this.hasPickaxe = hasPickaxe;
    }

    public boolean hasPickaxe() {
        return hasPickaxe;
    }

    public void setHasEnderPearl(boolean hasEnderPearl) {
        this.hasEnderPearl = hasEnderPearl;
    }

    public boolean hasEnderPearl() {
        return hasEnderPearl;
    }

    public void setHasQuestion(boolean hasQuestion) {
        this.hasQuestion = hasQuestion;
    }

    public boolean hasQuestion() {
        return hasQuestion;
    }

    public void setLever(Lever lever) {
        this.lever = lever;
    }

    public Lever getLever() {
        return lever;
    }

    public boolean hasLever() {
        return lever != null;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isEntrance() {
        return isEntrance;
    }

    public boolean isCenter() {
        return isCenter;
    }

    // REMOVED: getConnections() and addConnection() methods
    // These are now handled by GameNetwork as the single source of truth

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Room room = (Room) obj;
        return x == room.x && y == room.y;
    }
}

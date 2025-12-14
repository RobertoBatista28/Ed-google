package Models;

/**
 * Room represents a single room/cell in the game map grid.
 * Each room has a position (x, y), optional items (pickaxe, ender pearl, question),
 * a lever for controlling walls, and properties like entrance, center, and soul sand.
 * Supports custom floor colors and images for visual customization.
 *
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
    private Lever lever;
    private boolean hasQuestion;
    private boolean hasPickaxe;
    private boolean hasEnderPearl;
    private boolean isSoulSand;
    private java.awt.Color customFloorColor;
    private java.awt.image.BufferedImage customFloorImage;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    /**
     * Creates a new Room with the specified position and properties.
     * Initializes all item fields to false and custom floor fields to null.
     *
     * @param name the name identifier for this room
     * @param x the x-coordinate of this room in the map grid
     * @param y the y-coordinate of this room in the map grid
     * @param isEntrance true if this is the game entrance room
     * @param isCenter true if this is the center room containing the treasure
     */
    public Room(String name, int x, int y, boolean isEntrance, boolean isCenter) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.isEntrance = isEntrance;
        this.isCenter = isCenter;
        this.lever = null;
        this.hasQuestion = false;
        this.hasPickaxe = false;
        this.hasEnderPearl = false;
        this.isSoulSand = false;
        this.customFloorColor = null;
        this.customFloorImage = null;
    }

    // ----------------------------------------------------------------
    // Getters & Setters
    // ----------------------------------------------------------------
    /**
     * Sets whether this room contains soul sand (slowing material).
     * Soul sand makes players move slower through the room.
     *
     * @param isSoulSand true if this room has soul sand, false otherwise
     */
    public void setSoulSand(boolean isSoulSand) {
        this.isSoulSand = isSoulSand;
    }

    /**
     * Determines whether this room contains soul sand.
     *
     * @return true if this room has soul sand, false otherwise
     */
    public boolean isSoulSand() {
        return isSoulSand;
    }

    /**
     * Sets a custom floor color for this room.
     * Setting a color clears any previously set custom floor image.
     *
     * @param color the Color to use for the floor, or null to clear custom color
     */
    public void setCustomFloorColor(java.awt.Color color) {
        this.customFloorColor = color;
        this.customFloorImage = null;
    }

    /**
     * Returns the custom floor color set for this room.
     *
     * @return the Color used for the floor, or null if no custom color is set
     */
    public java.awt.Color getCustomFloorColor() {
        return customFloorColor;
    }

    /**
     * Sets a custom floor image for this room.
     * Setting an image clears any previously set custom floor color.
     *
     * @param image the BufferedImage to use for the floor, or null to clear custom image
     */
    public void setCustomFloorImage(java.awt.image.BufferedImage image) {
        this.customFloorImage = image;
        this.customFloorColor = null;
    }

    /**
     * Returns the custom floor image set for this room.
     *
     * @return the BufferedImage used for the floor, or null if no custom image is set
     */
    public java.awt.image.BufferedImage getCustomFloorImage() {
        return customFloorImage;
    }

    /**
     * Sets whether this room contains a pickaxe item.
     * Pickaxes allow players to break through breakable walls.
     *
     * @param hasPickaxe true if this room has a pickaxe, false otherwise
     */
    public void setHasPickaxe(boolean hasPickaxe) {
        this.hasPickaxe = hasPickaxe;
    }

    /**
     * Determines whether this room contains a pickaxe.
     *
     * @return true if this room has a pickaxe, false otherwise
     */
    public boolean hasPickaxe() {
        return hasPickaxe;
    }

    /**
     * Sets whether this room contains an ender pearl item.
     * Ender pearls allow players to teleport to random or specific locations.
     *
     * @param hasEnderPearl true if this room has an ender pearl, false otherwise
     */
    public void setHasEnderPearl(boolean hasEnderPearl) {
        this.hasEnderPearl = hasEnderPearl;
    }

    /**
     * Determines whether this room contains an ender pearl.
     *
     * @return true if this room has an ender pearl, false otherwise
     */
    public boolean hasEnderPearl() {
        return hasEnderPearl;
    }

    /**
     * Sets whether this room contains a question challenge.
     * When players enter a room with a question, they must answer correctly.
     *
     * @param hasQuestion true if this room has a question challenge, false otherwise
     */
    public void setHasQuestion(boolean hasQuestion) {
        this.hasQuestion = hasQuestion;
    }

    /**
     * Determines whether this room contains a question challenge.
     *
     * @return true if this room has a question, false otherwise
     */
    public boolean hasQuestion() {
        return hasQuestion;
    }

    /**
     * Sets the lever associated with this room.
     * A lever allows players to control wall connections on the map.
     *
     * @param lever the Lever object to associate with this room, or null to remove
     */
    public void setLever(Lever lever) {
        this.lever = lever;
    }

    /**
     * Returns the lever associated with this room.
     *
     * @return the Lever object, or null if no lever is in this room
     */
    public Lever getLever() {
        return lever;
    }

    /**
     * Determines whether this room contains a lever.
     * Checks if the lever field is not null.
     *
     * @return true if this room has a lever, false otherwise
     */
    public boolean hasLever() {
        return lever != null;
    }

    /**
     * Returns the name identifier for this room.
     *
     * @return the name of this room
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the x-coordinate of this room in the game map.
     *
     * @return the x-coordinate position
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of this room in the game map.
     *
     * @return the y-coordinate position
     */
    public int getY() {
        return y;
    }

    /**
     * Determines whether this is the entrance room where players start.
     *
     * @return true if this room is the game entrance, false otherwise
     */
    public boolean isEntrance() {
        return isEntrance;
    }

    /**
     * Determines whether this is the center room containing the treasure.
     * The goal of the game is to reach this room.
     *
     * @return true if this room is the center treasure room, false otherwise
     */
    public boolean isCenter() {
        return isCenter;
    }

    /**
     * Returns the string representation of this room using its name.
     *
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Compares this room with another object for equality based on coordinates.
     * Two rooms are equal if they have the same x and y positions in the map.
     * This implementation checks for same object reference, null values, and type compatibility.
     *
     * @param obj the object to compare with this room
     * @return true if the objects represent the same room position, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        // Check if comparing to the same object instance
        if (this == obj) {
            return true;
        }

        // Check if object is null or different class type
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        // Cast to Room and compare x and y coordinates
        Room room = (Room) obj;
        return x == room.x && y == room.y;
    }
}

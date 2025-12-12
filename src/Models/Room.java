package Models;

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

package Models;

/**
 * Item represents a collectible object in the game with a name and type.
 * Items can be picked up and stored in a player's inventory, such as
 * pickaxes or ender pearls. The class provides methods to retrieve item
 * information and implements equals and hashCode for proper object comparison.
 */
public class Item {

    // ----------------------------------------------------------------
    // Fields
    // ----------------------------------------------------------------
    private String name;
    private String type;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    /**
     * Creates a new Item with the specified name and type.
     *
     * @param name the name of the item
     * @param type the type or category of the item
     */
    public Item(String name, String type) {
        this.name = name;
        this.type = type;
    }

    // ----------------------------------------------------------------
    // Getters & Overrides
    // ----------------------------------------------------------------
    /**
     * Returns the name of this item.
     *
     * @return the item's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type or category of this item.
     *
     * @return the item's type
     */
    public String getType() {
        return type;
    }

    /**
     * Returns a string representation of this item.
     *
     * @return the item's name as a string
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Compares this item with another object for equality based on
     * name and type. Two items are equal if they have the same name
     * and type values.
     *
     * @param obj the object to compare with this item
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Item item = (Item) obj;
        return name.equals(item.name) && type.equals(item.type);
    }

    /**
     * Returns a hash code for this item based on its name and type.
     * The hash code is computed using the formula: 31 * hashCode(name) + hashCode(type).
     *
     * @return the hash code value for this item
     */
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}

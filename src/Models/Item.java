package Models;

public class Item {
    // ----------------------------------------------------------------
    // Fields
    // ----------------------------------------------------------------
    private String name;
    private String type;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    public Item(String name, String type) {
        this.name = name;
        this.type = type;
    }

    // ----------------------------------------------------------------
    // Getters & Overrides
    // ----------------------------------------------------------------
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Item item = (Item) obj;
        return name.equals(item.name) && type.equals(item.type);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}

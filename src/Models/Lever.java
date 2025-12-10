package Models;

import DataStructures.ArrayList.ArrayUnorderedList;
import DataStructures.Iterator;

public class Lever {

    // ----------------------------------------------------------------
    // Fields
    // ----------------------------------------------------------------
    private ArrayUnorderedList<Connection> targets;
    private boolean active;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    public Lever() {
        this.targets = new ArrayUnorderedList<>();
        this.active = false;
    }

    // ----------------------------------------------------------------
    // Methods
    // ----------------------------------------------------------------
    public void addTarget(Connection c) {
        targets.add(c);
        c.setLocked(!active);
    }

    public void toggle() {
        active = !active;
        Iterator<Connection> it = targets.iterator();
        while (it.hasNext()) {
            Connection c = it.next();
            c.setLocked(!active);
        }
    }

    public boolean isActive() {
        return active;
    }

    public ArrayUnorderedList<Connection> getTargets() {
        return targets;
    }
}

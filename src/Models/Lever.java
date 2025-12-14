package Models;

import DataStructures.ArrayList.ArrayUnorderedList;
import DataStructures.Iterator;

/**
 * Lever represents a puzzle element in the game that can be toggled
 * to control the locked state of multiple connections. When a lever
 * is activated or deactivated, all connected doors or passages are
 * updated accordingly, allowing players to open or close routes through
 * the game map.
 */
public class Lever {

    // ----------------------------------------------------------------
    // Fields
    // ----------------------------------------------------------------
    private ArrayUnorderedList<Connection> targets;
    private boolean active;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    /**
     * Creates a new Lever with no targets and an inactive state.
     * Initializes the targets list as an empty ArrayUnorderedList.
     */
    public Lever() {
        this.targets = new ArrayUnorderedList<>();
        this.active = false;
    }

    // ----------------------------------------------------------------
    // Methods
    // ----------------------------------------------------------------
    /**
     * Adds a connection to this lever's list of targets and sets the
     * connection's locked state based on the lever's current active state.
     * If the lever is active, the connection is unlocked; if inactive,
     * the connection is locked.
     *
     * @param c the connection to add as a target
     */
    public void addTarget(Connection c) {
        targets.add(c);
        c.setLocked(!active);
    }

    /**
     * Toggles the active state of this lever and updates all connected
     * targets. When toggled, iterates through all connections and sets
     * their locked state to the inverse of the new active state.
     */
    public void toggle() {
        active = !active;
        Iterator<Connection> it = targets.iterator();
        while (it.hasNext()) {
            Connection c = it.next();
            c.setLocked(!active);
        }
    }

    /**
     * Returns whether this lever is currently in an active state.
     *
     * @return true if the lever is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Returns the list of all connections controlled by this lever.
     *
     * @return the ArrayUnorderedList containing all target connections
     */
    public ArrayUnorderedList<Connection> getTargets() {
        return targets;
    }
}

package Models;

/**
 * Event represents a special event that can occur during gameplay,
 * containing event details and game impact information. Each event has
 * a unique code identifier, a name, a description, and a flag indicating
 * whether the event should stop the game immediately when triggered.
 */
public class Event {

    // ----------------------------------------------------------------
    // Fields
    // ----------------------------------------------------------------
    private final String code;
    private final String name;
    private final String description;
    private final boolean stopGame;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    /**
     * Creates a new Event with the specified code, name, description,
     * and game stop flag.
     *
     * @param code the unique identifier for this event
     * @param name the display name of the event
     * @param description the detailed description of the event
     * @param stopGame true if this event should stop the game, false otherwise
     */
    public Event(String code, String name, String description, boolean stopGame) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.stopGame = stopGame;
    }

    // ----------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------
    /**
     * Returns the unique code identifier for this event.
     *
     * @return the event code
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the display name of this event.
     *
     * @return the event name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the detailed description of this event.
     *
     * @return the event description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this event should stop the game when triggered.
     *
     * @return true if the event stops the game, false otherwise
     */
    public boolean isStopGame() {
        return stopGame;
    }
}

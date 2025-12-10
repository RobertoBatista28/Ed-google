package Models;

public class Event {

    private final String code;
    private final String name;
    private final String description;
    private final boolean stopGame;

    public Event(String code, String name, String description, boolean stopGame) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.stopGame = stopGame;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isStopGame() {
        return stopGame;
    }
}

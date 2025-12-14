package GameEngine;

import DataStructures.ArrayList.ArrayUnorderedList;
import DataStructures.Exceptions.EmptyCollectionException;
import Models.Event;
import Models.Lever;
import Models.Player;
import Models.Random;
import Models.Room;
import Utils.GameConfig;
import Utils.ImageLoader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * RandomEventManager manages random events that can occur during gameplay.
 * Loads events from a JSON configuration file and handles event triggering,
 * including special effects such as lever toggling, movement penalties, and
 * inventory loss. Each event is randomly selected based on a configured
 * probability and may affect the game state through room modifications,
 * player status changes, or map-wide updates.
 */
public class RandomEventManager {

    private final Random random;
    private final ArrayUnorderedList<Event> events;

    /**
     * Creates a new RandomEventManager and initializes the random event list
     * by loading events from the game configuration file path.
     */
    public RandomEventManager() {
        this.random = new Random();
        this.events = new ArrayUnorderedList<>();
        loadEvents(GameConfig.RANDOM_EVENTS_PATH);
    }

    /**
     * Loads events from a JSON file and parses them into Event objects.
     * Reads the entire file content, removes the outer JSON array brackets,
     * splits the JSON objects by closing braces, and parses each event.
     * Handles IOException gracefully by printing error message.
     *
     * @param filePath the path to the JSON file containing event definitions
     */
    private void loadEvents(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            StringBuilder sb = new StringBuilder();
            String line;
            // Read entire file content into StringBuilder
            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }

            String json = sb.toString();
            // Remove outer JSON array brackets if present
            if (json.startsWith("[") && json.endsWith("]")) {
                json = json.substring(1, json.length() - 1);
            }

            if (json.isEmpty()) {
                return;
            }

            // Split JSON objects by closing brace and comma delimiter
            String[] objects = json.split("},");

            // Parse each JSON object and add event to list
            for (String obj : objects) {
                if (!obj.endsWith("}")) {
                    obj += "}";
                }
                parseAndAddEvent(obj);
            }

        } catch (IOException e) {
            System.err.println("Error loading random events: " + e.getMessage());
        }
    }

    /**
     * Parses a JSON object string and creates an Event from its properties.
     * Extracts the code, name, description, and stop-game flag from the JSON,
     * then creates a new Event object if both code and name are present.
     *
     * @param jsonObj the JSON object string representing an event definition
     */
    private void parseAndAddEvent(String jsonObj) {
        String code = extractValue(jsonObj, "code");
        String name = extractValue(jsonObj, "name");
        String description = extractValue(jsonObj, "description");
        boolean stopGame = extractBoolean(jsonObj, "stop-game");

        if (code != null && name != null) {
            events.add(new Event(code, name, description, stopGame));
        }
    }

    /**
     * Extracts a string value from a JSON object for a given key.
     * Locates the key pattern, advances past the colon and whitespace,
     * and extracts the value until a closing quote, comma, or closing brace.
     * Handles quoted and unquoted values.
     *
     * @param json the JSON string to search in
     * @param key the key name to search for
     * @return the extracted value as a string, or null if the key is not found
     */
    private String extractValue(String json, String key) {
        // Build the key pattern to search for in the JSON string
        String keyPattern = "\"" + key + "\":";
        int startIndex = json.indexOf(keyPattern);
        if (startIndex == -1) {
            return null;
        }

        // Advance past the key pattern and skip whitespace and opening quotes
        startIndex += keyPattern.length();
        while (startIndex < json.length() && (json.charAt(startIndex) == ' ' || json.charAt(startIndex) == '\"')) {
            startIndex++;
        }

        // Find the end of the value by looking for closing quote, comma, or brace
        int endIndex = json.indexOf("\"", startIndex);
        if (endIndex == -1) {
            endIndex = json.indexOf(",", startIndex);
            if (endIndex == -1) {
                endIndex = json.indexOf("}", startIndex);
            }
        }

        // Extract and return the substring between start and end indices
        if (endIndex != -1) {
            return json.substring(startIndex, endIndex);
        }
        return null;
    }

    /**
     * Extracts a boolean value from a JSON object for a given key.
     * Locates the key pattern, skips whitespace, and checks if the
     * value starts with "true".
     *
     * @param json the JSON string to search in
     * @param key the key name to search for
     * @return true if the value is "true", false if key not found or value is "false"
     */
    private boolean extractBoolean(String json, String key) {
        // Build the key pattern to search for
        String keyPattern = "\"" + key + "\":";
        int startIndex = json.indexOf(keyPattern);
        if (startIndex == -1) {
            return false;
        }

        // Advance past the key pattern and skip whitespace
        startIndex += keyPattern.length();
        while (startIndex < json.length() && json.charAt(startIndex) == ' ') {
            startIndex++;
        }

        // Check if the value at this position starts with "true"
        return json.startsWith("true", startIndex);
    }

    /**
     * Checks if a random event should be triggered based on configured probability.
     * If an event is selected, dispatches to the appropriate event handler based on
     * the event code. Supports REDSTONE_BLOCK, SOUL_SAND, and CREEPER events.
     *
     * @param player the player encountering the potential event
     * @param gameMap the game map for map-wide effects
     * @param currentRoom the room where the event occurs
     * @return the triggered Event object, or null if no event is triggered
     */
    public Event checkForRandomEvent(Player player, GameMapGenerator gameMap, Room currentRoom) {
        // Check if random event should occur based on configured probability
        if (random.nextDouble() < GameConfig.RANDOM_EVENT_PROBABILITY) {
            if (events.isEmpty()) {
                return null;
            }

            // Select a random event from the loaded events list
            int randomIndex = random.nextInt(events.size());
            Event def = events.get(randomIndex);

            if (def != null) {
                // Dispatch to appropriate event handler based on event code
                return switch (def.getCode()) {
                    case "REDSTONE_BLOCK" -> {
                        triggerRedstoneBlockEvent(gameMap, currentRoom);
                        yield def;
                    }
                    case "SOUL_SAND" -> {
                        triggerSoulSandEvent(currentRoom);
                        yield def;
                    }
                    case "CREEPER" -> {
                        triggerCreeperEvent(player, currentRoom);
                        yield def;
                    }
                    default ->
                        def;
                };
            }
        }
        return null;
    }

    // ----------------------------------------------------------------
    // Event: Redstone Block
    // Power: Inverts the state of all levers on the board.
    // ----------------------------------------------------------------
    /**
     * Triggers the Redstone Block event which toggles all levers on the map.
     * Applies the redstone block texture to the current room and iterates
     * through all map rooms to toggle any lever present. Plays redstone sound effect.
     *
     * @param gameMap the game map containing all rooms
     * @param currentRoom the room where the event occurs (receives texture effect)
     * @return the event code "REDSTONE_BLOCK"
     */
    private String triggerRedstoneBlockEvent(GameMapGenerator gameMap, Room currentRoom) {
        // Apply redstone block texture to current room
        String texturePath = GameConfig.TEXTURES_PATH + GameConfig.REDSTONE_BLOCK_TEXTURE;
        java.awt.image.BufferedImage redstoneTexture = ImageLoader.getImage(texturePath);
        currentRoom.setCustomFloorImage(redstoneTexture);

        int width = GameConfig.MAP_WIDTH;
        int height = GameConfig.MAP_HEIGHT;

        // Iterate through all rooms and toggle any lever found
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Room room = gameMap.getRoom(x, y);
                if (room != null && room.hasLever()) {
                    Lever lever = room.getLever();
                    lever.toggle();
                }
            }
        }
        Utils.SoundPlayer.playRedstoneBlock();
        return "REDSTONE_BLOCK";
    }

    // ----------------------------------------------------------------
    // Event: Soul Sand
    // Power: Reduces player's movement by 3 turns.
    // ----------------------------------------------------------------
    /**
     * Triggers the Soul Sand event which reduces player movement capability.
     * Applies the soul sand texture to the current room and sets the soul sand
     * flag to true. Plays soul sand sound effect.
     *
     * @param currentRoom the room where the event occurs
     * @return the event code "SOUL_SAND"
     */
    private String triggerSoulSandEvent(Room currentRoom) {
        // Apply soul sand texture and flag to current room
        String texturePath = GameConfig.TEXTURES_PATH + GameConfig.SOUL_SAND_TEXTURE;
        java.awt.image.BufferedImage soulSandTexture = ImageLoader.getImage(texturePath);
        currentRoom.setCustomFloorImage(soulSandTexture);
        currentRoom.setSoulSand(true);
        Utils.SoundPlayer.playSoulSand();
        return "SOUL_SAND";
    }

    // ----------------------------------------------------------------
    // Event: Creeper
    // Power: Removes all items from player inventory.
    // ----------------------------------------------------------------
    /**
     * Triggers the Creeper event which removes all items from the player's inventory.
     * Clears inventory by repeatedly removing the last item until empty, applies
     * cobblestone texture to the current room, and plays explosion sound effect.
     *
     * @param player the player losing their inventory items
     * @param currentRoom the room where the event occurs
     */
    private void triggerCreeperEvent(Player player, Room currentRoom) {
        // Clear all items from player inventory
        while (!player.getInventory().isEmpty()) {
            try {
                player.getInventory().removeLast();
            } catch (EmptyCollectionException e) {
                break;
            }
        }

        // Apply cobblestone texture to current room for visual effect
        String texturePath = GameConfig.TEXTURES_PATH + GameConfig.COBBLESTONE_TEXTURE;
        java.awt.image.BufferedImage cobblestone = Utils.ImageLoader.getImage(texturePath);
        if (cobblestone != null) {
            currentRoom.setCustomFloorImage(cobblestone);
        }

        Utils.SoundPlayer.playExplosion();
    }
}

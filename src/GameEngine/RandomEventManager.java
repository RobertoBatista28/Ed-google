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

public class RandomEventManager {

    private final Random random;
    private final ArrayUnorderedList<Event> events;

    public RandomEventManager() {
        this.random = new Random();
        this.events = new ArrayUnorderedList<>();
        loadEvents(GameConfig.RANDOM_EVENTS_PATH);
    }

    private void loadEvents(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }

            String json = sb.toString();
            if (json.startsWith("[") && json.endsWith("]")) {
                json = json.substring(1, json.length() - 1);
            }

            if (json.isEmpty()) {
                return;
            }

            String[] objects = json.split("},");

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

    private void parseAndAddEvent(String jsonObj) {
        String code = extractValue(jsonObj, "code");
        String name = extractValue(jsonObj, "name");
        String description = extractValue(jsonObj, "description");
        boolean stopGame = extractBoolean(jsonObj, "stop-game");

        if (code != null && name != null) {
            events.add(new Event(code, name, description, stopGame));
        }
    }

    private String extractValue(String json, String key) {
        String keyPattern = "\"" + key + "\":";
        int startIndex = json.indexOf(keyPattern);
        if (startIndex == -1) {
            return null;
        }

        startIndex += keyPattern.length();

        while (startIndex < json.length() && (json.charAt(startIndex) == ' ' || json.charAt(startIndex) == '\"')) {
            startIndex++;
        }

        int endIndex = json.indexOf("\"", startIndex);
        if (endIndex == -1) {
            endIndex = json.indexOf(",", startIndex);
            if (endIndex == -1) {
                endIndex = json.indexOf("}", startIndex);
            }
        }

        if (endIndex != -1) {
            return json.substring(startIndex, endIndex);
        }
        return null;
    }

    private boolean extractBoolean(String json, String key) {
        String keyPattern = "\"" + key + "\":";
        int startIndex = json.indexOf(keyPattern);
        if (startIndex == -1) {
            return false;
        }

        startIndex += keyPattern.length();
        while (startIndex < json.length() && json.charAt(startIndex) == ' ') {
            startIndex++;
        }

        return json.startsWith("true", startIndex);
    }

    public Event checkForRandomEvent(Player player, GameMap gameMap, Room currentRoom) {
        if (random.nextDouble() < GameConfig.RANDOM_EVENT_PROBABILITY) {
            if (events.isEmpty()) {
                return null;
            }

            int randomIndex = random.nextInt(events.size());
            Event def = events.get(randomIndex);

            if (def != null) {
                return switch (def.getCode()) {
                    case "REDSTONE_BLOCK" -> {
                        triggerRedstoneBlockEvent(gameMap, currentRoom);
                        yield def;
                    }
                    case "SOUL_SAND" -> {
                        triggerSoulSandEvent(gameMap, currentRoom);
                        yield def;
                    }
                    case "CREEPER" -> {
                        triggerCreeperEvent(player, currentRoom);
                        yield def;
                    }
                    default -> def;
                };
            }
        }
        return null;
    }

    // ----------------------------------------------------------------
    // Event: Redstone Block
    // Power: Inverts the state of all levers on the board.
    private String triggerRedstoneBlockEvent(GameMap gameMap, Room currentRoom) {
        String texturePath = GameConfig.TEXTURES_PATH + GameConfig.REDSTONE_BLOCK_TEXTURE;
        java.awt.image.BufferedImage redstoneTexture = ImageLoader.getImage(texturePath);
        currentRoom.setCustomFloorImage(redstoneTexture);

        int width = GameConfig.MAP_WIDTH;
        int height = GameConfig.MAP_HEIGHT;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Room room = gameMap.getRoom(x, y);
                if (room != null && room.hasLever()) {
                    Lever lever = room.getLever();
                    lever.toggle();
                }
            }
        }
        return "REDSTONE_BLOCK";
    }

    // ----------------------------------------------------------------
    // Event: Soul Sand
    // Power: Reduces player's movement by 3 turns.
    private String triggerSoulSandEvent(GameMap gameMap, Room currentRoom) {
        String texturePath = GameConfig.TEXTURES_PATH + GameConfig.SOUL_SAND_TEXTURE;
        java.awt.image.BufferedImage soulSandTexture = ImageLoader.getImage(texturePath);
        currentRoom.setCustomFloorImage(soulSandTexture);
        currentRoom.setSoulSand(true);
        return "SOUL_SAND";
    }

    // ----------------------------------------------------------------
    // Event: Creeper
    // Power: Removes all items from player inventory.
    private void triggerCreeperEvent(Player player, Room currentRoom) {
        // Remove all items
        while (!player.getInventory().isEmpty()) {
            try {
                player.getInventory().removeLast();
            } catch (EmptyCollectionException e) {
                break;
            }
        }

        // Change floor texture
        java.awt.image.BufferedImage cobblestone = Utils.ImageLoader.getImage("src/Resources/Assets/Textures/cobblestone.jpg");
        if (cobblestone != null) {
            currentRoom.setCustomFloorImage(cobblestone);
        }

        // Play sound
        Utils.SoundPlayer.playExplosion();
    }
}

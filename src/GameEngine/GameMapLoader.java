package GameEngine;

import DataStructures.ArrayList.ArrayUnorderedList;
import Models.Connection;
import Models.Lever;
import Models.Random;
import Models.Room;
import Utils.GameConfig;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * GameMapLoader loads game maps from JSON configuration files. Provides methods
 * to load a random map from the configured directory, load a specific map by name,
 * or load a map from a File object. Parses JSON structure containing room definitions,
 * connections between rooms, and lever configurations with their target connections.
 */
public class GameMapLoader {

    /**
     * Loads a random map file from the configured map directory.
     * Filters files matching the naming pattern "map-{width}x{height}-{id}.json",
     * randomly selects one, and loads it. Returns a default 21x21 map if no files found.
     *
     * @return a GameMapGenerator with the loaded random map, or a default 21x21 map if loading fails
     */
    public GameMapGenerator loadRandomMap() {
        File dir = new File(GameConfig.MAP_LOADER_PATH);
        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("Map directory not found: " + dir.getAbsolutePath());
            return new GameMapGenerator(21, 21, true);
        }

        File[] files = dir.listFiles((d, name) -> name.matches("map-\\d+x\\d+-\\d{4}\\.json"));

        if (files == null || files.length == 0) {
            System.err.println("No map files found in: " + dir.getAbsolutePath());
            return new GameMapGenerator(21, 21, true);
        }

        Random rand = new Random();
        File selectedFile = files[rand.nextInt(files.length)];
        System.out.println("Loading map: " + selectedFile.getName());

        GameMapGenerator map = loadMapFromFile(selectedFile);
        map.setMapName(selectedFile.getName());
        return map;
    }

    /**
     * Loads a specific map by name from the configured map directory.
     * Searches for the map file by appending the configured path, verifies
     * it exists, and parses the JSON content to build the game map.
     *
     * @param mapName the name of the map file to load
     * @return a GameMapGenerator with the loaded map, or a default 21x21 map if file not found
     */
    public GameMapGenerator loadMap(String mapName) {
        File file = new File(GameConfig.MAP_LOADER_PATH + File.separator + mapName);
        if (!file.exists()) {
            System.err.println("Map file not found: " + mapName);
            return new GameMapGenerator(21, 21, true);
        }
        GameMapGenerator map = loadMapFromFile(file);
        map.setMapName(mapName);
        return map;
    }

    /**
     * Loads a specific map from a File object. Verifies the file exists,
     * parses the JSON content, and builds the game map structure.
     *
     * @param file the map File object to load
     * @return a GameMapGenerator with the loaded map, or a default 21x21 map if file not found
     */
    public GameMapGenerator loadMap(File file) {
        if (!file.exists()) {
            System.err.println("Map file not found: " + file.getAbsolutePath());
            return new GameMapGenerator(21, 21, true);
        }
        GameMapGenerator map = loadMapFromFile(file);
        map.setMapName(file.getName());
        return map;
    }

    /**
     * Internal method to parse a map file and construct the complete game map structure.
     * Reads the JSON file content, extracts map dimensions, parses all rooms, connections,
     * and levers, then reconstructs the lever-target relationships. Returns a default 21x21
     * map if IOException occurs during file reading.
     *
     * @param file the map file to parse
     * @return a fully constructed GameMapGenerator with all map elements
     */
    private GameMapGenerator loadMapFromFile(File file) {
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            // Read entire file content and trim each line to remove formatting
            while ((line = br.readLine()) != null) {
                jsonBuilder.append(line.trim());
            }
        } catch (IOException e) {
            return new GameMapGenerator(21, 21, true);
        }

        String json = jsonBuilder.toString();

        // Parse map dimensions (width and height)
        String wStr = extractValue(json, "width");
        String hStr = extractValue(json, "height");

        int width = (wStr != null) ? Integer.parseInt(wStr) : 21;
        int height = (hStr != null) ? Integer.parseInt(hStr) : 21;

        GameMapGenerator map = new GameMapGenerator(width, height, false);

        // Parse Rooms array and populate map grid
        String roomsJson = extractArray(json, "rooms");
        if (roomsJson != null) {
            String[] roomObjs = splitObjects(roomsJson);
            for (String obj : roomObjs) {
                // Extract room properties from JSON object
                int x = Integer.parseInt(extractValue(obj, "x"));
                int y = Integer.parseInt(extractValue(obj, "y"));
                boolean isEntrance = Boolean.parseBoolean(extractValue(obj, "isEntrance"));
                boolean isCenter = Boolean.parseBoolean(extractValue(obj, "isCenter"));
                boolean hasQuestion = Boolean.parseBoolean(extractValue(obj, "hasQuestion"));
                boolean hasPickaxe = Boolean.parseBoolean(extractValue(obj, "hasPickaxe"));
                boolean hasEnderPearl = Boolean.parseBoolean(extractValue(obj, "hasEnderPearl"));

                // Create room and set location-specific properties
                Room room = new Room(x + "," + y, x, y, isEntrance, isCenter);
                room.setHasQuestion(hasQuestion);
                room.setHasPickaxe(hasPickaxe);
                room.setHasEnderPearl(hasEnderPearl);
                map.setRoom(x, y, room);
            }
        }

        // Parse Connections array and add them to the map
        String connsJson = extractArray(json, "connections");
        if (connsJson != null) {
            String[] connObjs = splitObjects(connsJson);
            for (String obj : connObjs) {
                // Extract connection properties: source and destination coordinates
                int fromX = Integer.parseInt(extractValue(obj, "fromX"));
                int fromY = Integer.parseInt(extractValue(obj, "fromY"));
                int toX = Integer.parseInt(extractValue(obj, "toX"));
                int toY = Integer.parseInt(extractValue(obj, "toY"));
                boolean isLocked = Boolean.parseBoolean(extractValue(obj, "isLocked"));

                map.addConnection(fromX, fromY, toX, toY, isLocked);
            }
        }

        // Parse Levers array and connect them to their target connections
        String leversJson = extractArray(json, "levers");
        if (leversJson != null) {
            String[] leverObjs = splitObjects(leversJson);
            for (String obj : leverObjs) {
                // Extract lever position
                int x = Integer.parseInt(extractValue(obj, "x"));
                int y = Integer.parseInt(extractValue(obj, "y"));

                Lever lever = new Lever();
                Room room = map.getRoom(x, y);
                if (room != null) {
                    room.setLever(lever);
                }

                // Parse lever targets: the connections controlled by this lever
                String targetsJson = extractArray(obj, "targets");
                if (targetsJson != null) {
                    String[] targetObjs = splitObjects(targetsJson);
                    for (String tObj : targetObjs) {
                        // Extract target connection properties
                        int fromX = Integer.parseInt(extractValue(tObj, "fromX"));
                        int fromY = Integer.parseInt(extractValue(tObj, "fromY"));
                        int toX = Integer.parseInt(extractValue(tObj, "toX"));
                        int toY = Integer.parseInt(extractValue(tObj, "toY"));

                        Room from = map.getRoom(fromX, fromY);
                        Room to = map.getRoom(toX, toY);

                        // Find the connection matching the target rooms and add it to lever
                        if (from != null && to != null) {
                            DataStructures.Iterator<Connection> it = map.getGraph().getConnections(from).iterator();
                            while (it.hasNext()) {
                                Connection c = it.next();
                                if (c.getTo().equals(to)) {
                                    lever.addTarget(c);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        return map;
    }

    // ----------------------------------------------------------------
    // Helper Methods for JSON Parsing
    // ----------------------------------------------------------------
    /**
     * Extracts a value from a JSON object for a given key. Handles three value types:
     * quoted strings (extracted between quotes), arrays (extracted with bracket tracking),
     * and primitive values (extracted until comma or closing brace). Returns null if
     * the key is not found in the JSON string.
     *
     * @param json the JSON string to search in
     * @param key the key name to search for
     * @return the extracted value as a string, or null if the key is not found
     */
    private String extractValue(String json, String key) {
        // Build the key pattern to search for in the JSON string
        String keyPattern = "\"" + key + "\":";
        int start = json.indexOf(keyPattern);
        if (start == -1) {
            return null;
        }
        start += keyPattern.length();

        // Skip whitespace and newlines after the colon
        while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == '\n')) {
            start++;
        }

        int end = start;
        // Determine value type and extract accordingly
        switch (json.charAt(start)) {
            case '"' -> {
                // Quoted string value: extract text between quotes
                start++;
                end = json.indexOf("\"", start);
                return json.substring(start, end);
            }
            case '[' -> {
                // Array value: use bracket counting to find matching closing bracket
                int bracketCount = 1;
                end = start + 1;
                while (bracketCount > 0 && end < json.length()) {
                    if (json.charAt(end) == '[') {
                        bracketCount++;
                    }
                    if (json.charAt(end) == ']') {
                        bracketCount--;
                    }
                    end++;
                }
                return json.substring(start, end);
            }
            default -> {
                // Primitive value (number, boolean): extract until delimiter
                while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}' && json.charAt(end) != ']') {
                    end++;
                }
                return json.substring(start, end).trim();
            }
        }
    }

    /**
     * Extracts an array value from a JSON object for a given key.
     * Uses extractValue to get the raw array string, then removes the outer
     * brackets to return only the array content. Returns null if the key
     * is not found or the value is not a valid array.
     *
     * @param json the JSON string to search in
     * @param key the key name to search for
     * @return the array content as a string (without brackets), or null if not found or invalid
     */
    private String extractArray(String json, String key) {
        String val = extractValue(json, key);
        if (val != null && val.startsWith("[") && val.endsWith("]")) {
            return val.substring(1, val.length() - 1);
        }
        return null;
    }

    /**
     * Splits a JSON array string into individual JSON object strings.
     * Uses bracket counting to correctly identify object boundaries, handling
     * nested objects and whitespace/newline delimiters. Returns an empty array
     * if the input is empty. Each returned string represents a complete JSON object.
     *
     * @param jsonArray the JSON array content string (without outer brackets)
     * @return an array of individual JSON object strings
     */
    private String[] splitObjects(String jsonArray) {
        if (jsonArray.trim().isEmpty()) {
            return new String[0];
        }

        ArrayUnorderedList<String> list = new ArrayUnorderedList<>();
        int start = 0;
        int bracketCount = 0;
        // Iterate through each character to track object boundaries
        for (int i = 0; i < jsonArray.length(); i++) {
            char c = jsonArray.charAt(i);
            // Increment bracket count on opening brace
            if (c == '{') {
                bracketCount++;
            }
            // Decrement bracket count on closing brace
            if (c == '}') {
                bracketCount--;
            }

            // When bracket count reaches zero and we encounter closing brace, 
            // we have found a complete object boundary
            if (bracketCount == 0 && c == '}') {
                // Extract the complete JSON object from start to current position (inclusive)
                list.add(jsonArray.substring(start, i + 1));
                // Skip any delimiters (commas, spaces, newlines) to find start of next object
                int nextStart = i + 1;
                while (nextStart < jsonArray.length() && (jsonArray.charAt(nextStart) == ',' || jsonArray.charAt(nextStart) == ' ' || jsonArray.charAt(nextStart) == '\n')) {
                    nextStart++;
                }
                start = nextStart;
                i = start - 1;
            }
        }

        // Convert ArrayList to array for return
        String[] result = new String[list.size()];
        DataStructures.Iterator<String> it = list.iterator();
        int i = 0;
        while (it.hasNext()) {
            result[i++] = it.next();
        }
        return result;
    }
}

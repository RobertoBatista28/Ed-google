package Models;

import DataStructures.ArrayList.ArrayUnorderedList;
import DataStructures.Iterator;
import GameEngine.GameMapGenerator;
import GameEngine.GameMapLoader;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Report is a utility class for loading game session reports from JSON files.
 * Parses JSON report data containing game map information and player paths.
 * Provides static methods for report loading and JSON parsing with custom parsing logic.
 *
 */
public class Report {

    /**
     * ReportData is a container class holding the parsed report information.
     * Contains the game map and list of player paths traversed during gameplay.
     *
     */
    public static class ReportData {

        public GameMapGenerator gameMap;
        public ArrayUnorderedList<PlayerPath> playerPaths;

        /**
         * Creates a new ReportData with the specified map and player paths.
         *
         * @param gameMap the GameMapGenerator containing the map configuration
         * @param playerPaths list of PlayerPath objects for each player
         */
        public ReportData(GameMapGenerator gameMap, ArrayUnorderedList<PlayerPath> playerPaths) {
            this.gameMap = gameMap;
            this.playerPaths = playerPaths;
        }
    }

    /**
     * PlayerPath is a container class holding a player's movement path during gameplay.
     * Stores the player's name, character type, and list of coordinates visited.
     *
     */
    public static class PlayerPath {

        public String name;
        public String character;
        public ArrayUnorderedList<Point> path;

        /**
         * Creates a new PlayerPath with the specified player information.
         *
         * @param name the player's name
         * @param character the player's character type/skin
         * @param path list of Point objects representing visited coordinates
         */
        public PlayerPath(String name, String character, ArrayUnorderedList<Point> path) {
            this.name = name;
            this.character = character;
            this.path = path;
        }
    }

    /**
     * Loads a game report from a JSON file and parses all contained data.
     * Extracts map information and player paths from the JSON structure.
     * Returns null if file cannot be read or parsed.
     *
     * @param reportFile the file path to the JSON report file
     * @return ReportData object containing parsed map and player paths, or null on error
     */
    public static ReportData loadReport(String reportFile) {
        GameMapGenerator gameMap = null;
        ArrayUnorderedList<PlayerPath> playerPaths = new ArrayUnorderedList<>();

        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(reportFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                jsonBuilder.append(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        String json = jsonBuilder.toString();

        // Carregar o mapa
        String mapName = extractValue(json, "mapName");
        if (mapName != null) {
            GameMapLoader loader = new GameMapLoader();
            gameMap = loader.loadMap(mapName);
        }

        // Carregar os jogadores e seus caminhos
        String playersJson = extractArray(json, "players");
        if (playersJson != null) {
            String[] players = splitObjects(playersJson);
            for (String pJson : players) {
                String name = extractValue(pJson, "name");
                String character = extractValue(pJson, "character");
                String pathJson = extractArray(pJson, "path");

                ArrayUnorderedList<Point> path = new ArrayUnorderedList<>();
                if (pathJson != null) {
                    String[] points = splitObjects(pathJson);
                    for (String pointJson : points) {
                        String xStr = extractValue(pointJson, "x");
                        String yStr = extractValue(pointJson, "y");
                        if (xStr != null && yStr != null) {
                            path.add(new Point(Integer.parseInt(xStr), Integer.parseInt(yStr)));
                        }
                    }
                }
                playerPaths.add(new PlayerPath(name, character, path));
            }
        }

        return new ReportData(gameMap, playerPaths);
    }

    // Métodos auxiliares de parsing JSON (mantidos privados para encapsulamento)
    /**
     * Extracts a value from JSON string for the specified key.
     * Handles string values, numeric values, and arrays within the JSON structure.
     * Returns null if the key is not found.
     *
     * @param json the JSON string to parse
     * @param key the key name to search for
     * @return the extracted value as a string, or null if not found
     */
    private static String extractValue(String json, String key) {
        // Search for the key pattern in JSON string
        String searchKey = "\"" + key + "\":";
        int start = json.indexOf(searchKey);
        if (start == -1) {
            return null;
        }

        // Move start pointer past the key and colon
        start += searchKey.length();

        // Skip whitespace and opening quotes
        while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == '\"')) {
            start++;
        }

        int end = start;

        // Handle quoted string values (ends with closing quote)
        if (json.charAt(start - 1) == '\"') {
            while (end < json.length() && json.charAt(end) != '\"') {
                end++;
            }
        } else {
            // Handle array values (starts with [ and count brackets)
            if (json.charAt(start) == '[') {
                int bracketCount = 1;
                end++;
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

            // Handle numeric values or identifiers (ends at comma, brace, or bracket)
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}' && json.charAt(end) != ']') {
                end++;
            }
        }
        return json.substring(start, end);
    }

    /**
     * Extracts an array from JSON string for the specified key.
     * Returns the array contents without the outer brackets.
     * Returns null if the key is not found or value is not an array.
     *
     * @param json the JSON string to parse
     * @param key the key name of the array to extract
     * @return the array contents as a string, or null if not found
     */
    private static String extractArray(String json, String key) {
        String val = extractValue(json, key);
        if (val != null && val.startsWith("[") && val.endsWith("]")) {
            return val.substring(1, val.length() - 1);
        }
        return null;
    }

    /**
     * Splits a JSON array string containing objects into individual object strings.
     * Properly handles nested brackets to identify object boundaries.
     * Returns an array of individual JSON object strings without the outer array brackets.
     *
     * @param jsonArray the JSON array string containing objects
     * @return array of strings, each containing a complete JSON object
     */
    private static String[] splitObjects(String jsonArray) {
        // Use list to collect individual JSON objects as they are identified
        ArrayUnorderedList<String> list = new ArrayUnorderedList<>();

        // Track brace depth to identify when a complete object ends
        int bracketCount = 0;
        int start = 0;

        // Iterate through each character in the array string
        for (int i = 0; i < jsonArray.length(); i++) {
            char c = jsonArray.charAt(i);

            // Increment bracket count when opening brace encountered
            if (c == '{') {
                bracketCount++;
            }

            // Decrement bracket count when closing brace encountered
            if (c == '}') {
                bracketCount--;
            }

            // When bracket count returns to 0 and closing brace found, object is complete
            if (bracketCount == 0 && c == '}') {
                // Extract substring from start position to current position (inclusive)
                list.add(jsonArray.substring(start, i + 1));

                // Skip past the closing brace and any separators (commas, spaces)
                int next = i + 1;
                while (next < jsonArray.length() && (jsonArray.charAt(next) == ',' || jsonArray.charAt(next) == ' ')) {
                    next++;
                }

                // Update start position for next object
                start = next;
                i = start - 1;
            }
        }

        // Convert list to array for return
        String[] res = new String[list.size()];
        int idx = 0;
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            res[idx++] = it.next();
        }
        return res;
    }
}

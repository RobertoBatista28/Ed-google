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

public class GameMapLoader {

    public GameMapGenerator loadRandomMap() {
        File dir = new File(GameConfig.MAP_LOADER_PATH);
        File[] files = dir.listFiles((d, name) -> name.matches("map-\\d+x\\d+-\\d{4}\\.json"));

        Random rand = new Random();
        File selectedFile = files[rand.nextInt(files.length)];
        System.out.println("Loading map: " + selectedFile.getName());

        GameMapGenerator map = loadMapFromFile(selectedFile);
        map.setMapName(selectedFile.getName());
        return map;
    }

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

    public GameMapGenerator loadMap(File file) {
        if (!file.exists()) {
            System.err.println("Map file not found: " + file.getAbsolutePath());
            return new GameMapGenerator(21, 21, true);
        }
        GameMapGenerator map = loadMapFromFile(file);
        map.setMapName(file.getName());
        return map;
    }

    private GameMapGenerator loadMapFromFile(File file) {
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                jsonBuilder.append(line.trim());
            }
        } catch (IOException e) {
            return new GameMapGenerator(21, 21, true);
        }

        String json = jsonBuilder.toString();

        // Parse width and height
        String wStr = extractValue(json, "width");
        String hStr = extractValue(json, "height");

        int width = (wStr != null) ? Integer.parseInt(wStr) : 21;
        int height = (hStr != null) ? Integer.parseInt(hStr) : 21;

        GameMapGenerator map = new GameMapGenerator(width, height, false);

        // Parse Rooms
        String roomsJson = extractArray(json, "rooms");
        if (roomsJson != null) {
            String[] roomObjs = splitObjects(roomsJson);
            for (String obj : roomObjs) {
                int x = Integer.parseInt(extractValue(obj, "x"));
                int y = Integer.parseInt(extractValue(obj, "y"));
                boolean isEntrance = Boolean.parseBoolean(extractValue(obj, "isEntrance"));
                boolean isCenter = Boolean.parseBoolean(extractValue(obj, "isCenter"));
                boolean hasQuestion = Boolean.parseBoolean(extractValue(obj, "hasQuestion"));
                boolean hasPickaxe = Boolean.parseBoolean(extractValue(obj, "hasPickaxe"));
                boolean hasEnderPearl = Boolean.parseBoolean(extractValue(obj, "hasEnderPearl"));

                Room room = new Room(x + "," + y, x, y, isEntrance, isCenter);
                room.setHasQuestion(hasQuestion);
                room.setHasPickaxe(hasPickaxe);
                room.setHasEnderPearl(hasEnderPearl);
                map.setRoom(x, y, room);
            }
        }

        // Parse Connections
        String connsJson = extractArray(json, "connections");
        if (connsJson != null) {
            String[] connObjs = splitObjects(connsJson);
            for (String obj : connObjs) {
                int fromX = Integer.parseInt(extractValue(obj, "fromX"));
                int fromY = Integer.parseInt(extractValue(obj, "fromY"));
                int toX = Integer.parseInt(extractValue(obj, "toX"));
                int toY = Integer.parseInt(extractValue(obj, "toY"));
                boolean isLocked = Boolean.parseBoolean(extractValue(obj, "isLocked"));

                map.addConnection(fromX, fromY, toX, toY, isLocked);
            }
        }

        // Parse Levers
        String leversJson = extractArray(json, "levers");
        if (leversJson != null) {
            String[] leverObjs = splitObjects(leversJson);
            for (String obj : leverObjs) {
                int x = Integer.parseInt(extractValue(obj, "x"));
                int y = Integer.parseInt(extractValue(obj, "y"));

                Lever lever = new Lever();
                Room room = map.getRoom(x, y);
                if (room != null) {
                    room.setLever(lever);
                }

                String targetsJson = extractArray(obj, "targets");
                if (targetsJson != null) {
                    String[] targetObjs = splitObjects(targetsJson);
                    for (String tObj : targetObjs) {
                        int fromX = Integer.parseInt(extractValue(tObj, "fromX"));
                        int fromY = Integer.parseInt(extractValue(tObj, "fromY"));
                        int toX = Integer.parseInt(extractValue(tObj, "toX"));
                        int toY = Integer.parseInt(extractValue(tObj, "toY"));

                        Room from = map.getRoom(fromX, fromY);
                        Room to = map.getRoom(toX, toY);

                        if (from != null && to != null) {
                            DataStructures.Iterator<Connection> it = from.getConnections().iterator();
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

    // Helper methods for JSON parsing
    private String extractValue(String json, String key) {
        String keyPattern = "\"" + key + "\":";
        int start = json.indexOf(keyPattern);
        if (start == -1) {
            return null;
        }
        start += keyPattern.length();

        while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == '\n')) {
            start++;
        }

        int end = start;
        switch (json.charAt(start)) {
            case '"' -> {
                start++;
                end = json.indexOf("\"", start);
                return json.substring(start, end);
            }
            case '[' -> {
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
                while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}' && json.charAt(end) != ']') {
                    end++;
                }
                return json.substring(start, end).trim();
            }
        }
    }

    private String extractArray(String json, String key) {
        String val = extractValue(json, key);
        if (val != null && val.startsWith("[") && val.endsWith("]")) {
            return val.substring(1, val.length() - 1);
        }
        return null;
    }

    private String[] splitObjects(String jsonArray) {
        if (jsonArray.trim().isEmpty()) {
            return new String[0];
        }

        ArrayUnorderedList<String> list = new ArrayUnorderedList<>();
        int start = 0;
        int bracketCount = 0;
        for (int i = 0; i < jsonArray.length(); i++) {
            char c = jsonArray.charAt(i);
            if (c == '{') {
                bracketCount++;
            }
            if (c == '}') {
                bracketCount--;
            }

            if (bracketCount == 0 && c == '}') {
                list.add(jsonArray.substring(start, i + 1));
                int nextStart = i + 1;
                while (nextStart < jsonArray.length() && (jsonArray.charAt(nextStart) == ',' || jsonArray.charAt(nextStart) == ' ' || jsonArray.charAt(nextStart) == '\n')) {
                    nextStart++;
                }
                start = nextStart;
                i = start - 1;
            }
        }

        String[] result = new String[list.size()];
        DataStructures.Iterator<String> it = list.iterator();
        int i = 0;
        while (it.hasNext()) {
            result[i++] = it.next();
        }
        return result;
    }
}

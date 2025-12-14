package Utils;

import DataStructures.Iterator;
import GameEngine.GameMapGenerator;
import Models.Connection;
import Models.Room;
import java.io.FileWriter;
import java.io.IOException;

/**
 * MapSerializer provides functionality to serialize a game map into a JSON file.
 * It manually constructs the JSON string representing the map's dimensions, rooms,
 * connections, and lever configurations.
 */
public class MapSerializer {

    /**
     * Saves the specified GameMapGenerator state to a JSON file.
     *
     * @param map      the GameMapGenerator containing the map data to serialize
     * @param filename the name of the file to write the JSON data to
     */
    public static void saveToJson(GameMapGenerator map, String filename) {
        // Initialize StringBuilder to construct the JSON string
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"width\": ").append(map.getWidth()).append(",\n");
        sb.append("  \"height\": ").append(map.getHeight()).append(",\n");

        // Serialize Rooms section
        sb.append("  \"rooms\": [\n");
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Room r = map.getRoom(x, y);
                sb.append("    {");
                sb.append("\"x\": ").append(x).append(", ");
                sb.append("\"y\": ").append(y).append(", ");
                sb.append("\"isEntrance\": ").append(r.isEntrance()).append(", ");
                sb.append("\"isCenter\": ").append(r.isCenter()).append(", ");
                sb.append("\"hasQuestion\": ").append(r.hasQuestion()).append(", ");
                sb.append("\"hasPickaxe\": ").append(r.hasPickaxe()).append(", ");
                sb.append("\"hasEnderPearl\": ").append(r.hasEnderPearl()).append(", ");
                sb.append("\"hasLever\": ").append(r.hasLever());
                sb.append("}");
                
                // Add a comma if this is not the last room
                if (x != map.getWidth() - 1 || y != map.getHeight() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
        }
        sb.append("  ],\n");

        // Serialize Connections section
        sb.append("  \"connections\": [\n");
        boolean firstConn = true;
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Room r = map.getRoom(x, y);
                
                // Iterate over connections for the current room
                Iterator<Connection> it = map.getGraph().getConnections(r).iterator();
                while (it.hasNext()) {
                    Connection c = it.next();
                    if (!firstConn) {
                        sb.append(",\n");
                    }
                    sb.append("    {");
                    sb.append("\"fromX\": ").append(c.getFrom().getX()).append(", ");
                    sb.append("\"fromY\": ").append(c.getFrom().getY()).append(", ");
                    sb.append("\"toX\": ").append(c.getTo().getX()).append(", ");
                    sb.append("\"toY\": ").append(c.getTo().getY()).append(", ");
                    sb.append("\"isLocked\": ").append(c.isLocked());
                    sb.append("}");
                    firstConn = false;
                }
            }
        }
        sb.append("\n  ],\n");

        // Serialize Levers section
        sb.append("  \"levers\": [\n");
        boolean firstLev = true;
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Room r = map.getRoom(x, y);
                
                // Only process if the room actually has a lever
                if (r.hasLever()) {
                    if (!firstLev) {
                        sb.append(",\n");
                    }
                    sb.append("    {");
                    sb.append("\"x\": ").append(x).append(", ");
                    sb.append("\"y\": ").append(y).append(", ");
                    sb.append("\"targets\": [");

                    // Iterate over the target connections controlled by this lever
                    Iterator<Connection> it = r.getLever().getTargets().iterator();
                    boolean firstTarget = true;
                    while (it.hasNext()) {
                        Connection c = it.next();
                        if (!firstTarget) {
                            sb.append(", ");
                        }
                        sb.append("{");
                        sb.append("\"fromX\": ").append(c.getFrom().getX()).append(", ");
                        sb.append("\"fromY\": ").append(c.getFrom().getY()).append(", ");
                        sb.append("\"toX\": ").append(c.getTo().getX()).append(", ");
                        sb.append("\"toY\": ").append(c.getTo().getY());
                        sb.append("}");
                        firstTarget = false;
                    }

                    sb.append("]");
                    sb.append("}");
                    firstLev = false;
                }
            }
        }
        sb.append("\n  ]\n");
        sb.append("}");

        // Write the constructed JSON string to the file
        try (FileWriter fw = new FileWriter(filename)) {
            fw.write(sb.toString());
            System.out.println("Map saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

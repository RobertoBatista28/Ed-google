package Utils;

import DataStructures.ArrayList.ArrayUnorderedList;
import DataStructures.Iterator;
import Models.Player;
import Models.Room;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * GameReport is responsible for generating the final game report in JSON format.
 * It compiles game statistics, player information, and movement history into a structured file.
 */
public class GameReport {

    /**
     * Generates a JSON report containing game statistics and saves it to a file.
     * The report includes the timestamp, map name, winner information, and detailed
     * statistics for every player in the session.
     *
     * @param players  the list of players involved in the game
     * @param winner   the player who won the game
     * @param mapName  the name of the map played
     * @return a string containing the filename of the generated report, or null if an error occurs
     */
    public static String generateReport(ArrayUnorderedList<Player> players, Player winner, String mapName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String directory = "Reports";
        String filename = directory + "/game_report_" + timestamp + ".json";

        // Check if the directory exists, otherwise create it
        java.io.File dir = new java.io.File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Build the JSON structure manually using StringBuilder for efficiency
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"timestamp\": \"").append(timestamp).append("\",\n");
        sb.append("  \"mapName\": \"").append(mapName).append("\",\n");
        sb.append("  \"winner\": \"").append(winner.getName()).append("\",\n");
        sb.append("  \"players\": [\n");

        // Iterate through the custom list of players
        Iterator<Player> it = players.iterator();
        boolean firstPlayer = true;
        while (it.hasNext()) {
            Player p = it.next();
            if (!firstPlayer) {
                sb.append(",\n");
            }
            firstPlayer = false;

            // Append player specific statistics to the JSON string
            sb.append("    {\n");
            sb.append("      \"name\": \"").append(p.getName()).append("\",\n");
            sb.append("      \"character\": \"").append(p.getCharacterType()).append("\",\n");
            sb.append("      \"isBot\": ").append(p.isBot()).append(",\n");
            sb.append("      \"totalMoves\": ").append(p.getTotalMoves()).append(",\n");
            sb.append("      \"leverInteractions\": ").append(p.getLeverInteractions()).append(",\n");
            sb.append("      \"questionsCorrect\": ").append(p.getQuestionsCorrect()).append(",\n");
            sb.append("      \"questionsIncorrect\": ").append(p.getQuestionsIncorrect()).append(",\n");
            sb.append("      \"itemsCollected\": ").append(p.getItemsCollected()).append(",\n");
            sb.append("      \"pickaxesCollected\": ").append(p.getPickaxesCollected()).append(",\n");
            sb.append("      \"enderPearlsCollected\": ").append(p.getEnderPearlsCollected()).append(",\n");
            sb.append("      \"itemsUsed\": ").append(p.getItemsUsed()).append(",\n");
            sb.append("      \"pickaxesUsed\": ").append(p.getPickaxesUsed()).append(",\n");
            sb.append("      \"enderPearlsUsed\": ").append(p.getEnderPearlsUsed()).append(",\n");

            // Serialize the path (linked list of rooms) visited by the player
            sb.append("      \"path\": [");
            Iterator<Room> pathIt = p.getPath().iterator();
            boolean firstRoom = true;
            while (pathIt.hasNext()) {
                Room r = pathIt.next();
                if (!firstRoom) {
                    sb.append(", ");
                }
                firstRoom = false;
                sb.append("{\"x\": ").append(r.getX()).append(", \"y\": ").append(r.getY()).append("}");
            }
            sb.append("]\n");

            sb.append("    }");
        }
        sb.append("\n  ]\n");
        sb.append("}");

        // Write the constructed string to the file
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(sb.toString());
            System.out.println("Report saved to " + filename);
            return filename;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
package Utils;

import DataStructures.ArrayList.ArrayUnorderedList;
import DataStructures.Iterator;
import Models.Player;
import Models.Room;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GameReport {

    public static String generateReport(ArrayUnorderedList<Player> players, Player winner, String mapName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String directory = "Reports";
        String filename = directory + "/game_report_" + timestamp + ".json";

        java.io.File dir = new java.io.File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"timestamp\": \"").append(timestamp).append("\",\n");
        sb.append("  \"mapName\": \"").append(mapName).append("\",\n");
        sb.append("  \"winner\": \"").append(winner.getName()).append("\",\n");
        sb.append("  \"players\": [\n");

        Iterator<Player> it = players.iterator();
        boolean firstPlayer = true;
        while (it.hasNext()) {
            Player p = it.next();
            if (!firstPlayer) {
                sb.append(",\n");
            }
            firstPlayer = false;

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

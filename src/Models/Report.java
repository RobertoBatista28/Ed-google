package Models;

import DataStructures.ArrayList.ArrayUnorderedList;
import DataStructures.Iterator;
import GameEngine.GameMap;
import GameEngine.GameMapLoader;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Classe utilitária responsável por carregar e processar o ficheiro de
 * relatório (JSON). Separa a lógica de leitura de dados da lógica de interface
 * gráfica.
 */
public class Report {

    /**
     * Estrutura para armazenar os dados carregados do relatório.
     */
    public static class ReportData {

        public GameMap gameMap;
        public ArrayUnorderedList<PlayerPath> playerPaths;

        public ReportData(GameMap gameMap, ArrayUnorderedList<PlayerPath> playerPaths) {
            this.gameMap = gameMap;
            this.playerPaths = playerPaths;
        }
    }

    /**
     * Estrutura para armazenar o caminho percorrido por um jogador.
     */
    public static class PlayerPath {

        public String name;
        public String character;
        public ArrayUnorderedList<Point> path;

        public PlayerPath(String name, String character, ArrayUnorderedList<Point> path) {
            this.name = name;
            this.character = character;
            this.path = path;
        }
    }

    /**
     * Carrega o relatório a partir de um ficheiro JSON.
     *
     * @param reportFile Caminho do ficheiro de relatório.
     * @return Objeto ReportData contendo o mapa e os caminhos dos jogadores.
     */
    public static ReportData loadReport(String reportFile) {
        GameMap gameMap = null;
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
    private static String extractValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int start = json.indexOf(searchKey);
        if (start == -1) {
            return null;
        }

        start += searchKey.length();
        while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == '\"')) {
            start++;
        }

        int end = start;
        if (json.charAt(start - 1) == '\"') {
            while (end < json.length() && json.charAt(end) != '\"') {
                end++;
            }
        } else {
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
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}' && json.charAt(end) != ']') {
                end++;
            }
        }
        return json.substring(start, end);
    }

    private static String extractArray(String json, String key) {
        String val = extractValue(json, key);
        if (val != null && val.startsWith("[") && val.endsWith("]")) {
            return val.substring(1, val.length() - 1);
        }
        return null;
    }

    private static String[] splitObjects(String jsonArray) {
        ArrayUnorderedList<String> list = new ArrayUnorderedList<>();
        int bracketCount = 0;
        int start = 0;
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
                int next = i + 1;
                while (next < jsonArray.length() && (jsonArray.charAt(next) == ',' || jsonArray.charAt(next) == ' ')) {
                    next++;
                }
                start = next;
                i = start - 1;
            }
        }

        String[] res = new String[list.size()];
        int idx = 0;
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            res[idx++] = it.next();
        }
        return res;
    }
}

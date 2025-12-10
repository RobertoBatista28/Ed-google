package GameEngine;

import DataStructures.ArrayList.ArrayUnorderedList;
import DataStructures.Queue.LinkedQueue;
import Models.Question;
import Models.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class QuestionManager {

    private final LinkedQueue<Question> questionQueue;
    private final ArrayUnorderedList<Question> allQuestions;

    public QuestionManager(String filePath) {
        questionQueue = new LinkedQueue<>();
        allQuestions = new ArrayUnorderedList<>();
        loadQuestions(filePath);
        shuffleAndReload();
    }

    private void loadQuestions(String filePath) {
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

            String[] objects = json.split("},");

            for (String obj : objects) {
                if (!obj.endsWith("}")) {
                    obj += "}";
                }
                parseAndAddQuestion(obj);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseAndAddQuestion(String jsonObj) {
        try {
            String questionText = extractValue(jsonObj, "question");
            String optionsStr = extractArray(jsonObj, "options");
            String correctStr = extractValue(jsonObj, "correct");

            if (questionText != null && optionsStr != null && correctStr != null) {
                String[] rawOptions = optionsStr.split(",");
                String[] options = new String[rawOptions.length];
                
                for (int i = 0; i < rawOptions.length; i++) {
                    String opt = rawOptions[i].trim();
                    if (opt.startsWith("\"") && opt.endsWith("\"")) {
                        opt = opt.substring(1, opt.length() - 1);
                    }
                    options[i] = opt;
                }

                int correctIndex = -1;
                try {
                    correctIndex = Integer.parseInt(correctStr);
                } catch (NumberFormatException e) {
                    for (int i = 0; i < options.length; i++) {
                        if (options[i].equalsIgnoreCase(correctStr)) {
                            correctIndex = i;
                            break;
                        }
                    }
                }
                
                if (correctIndex != -1) {
                    Question q = new Question(questionText, options, correctIndex);
                    allQuestions.add(q);
                } else {
                     System.err.println("Could not find correct answer index for: " + correctStr);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing question: " + jsonObj);
        }
    }

    private String extractValue(String json, String key) {
        String keyPattern = "\"" + key + "\":";
        int start = json.indexOf(keyPattern);
        if (start == -1) {
            return null;
        }

        start += keyPattern.length();

        char firstChar = json.charAt(start);
        while (firstChar == ' ') {
            start++;
            firstChar = json.charAt(start);
        }

        if (firstChar == '"') {
            int end = json.indexOf("\"", start + 1);
            return json.substring(start + 1, end);
        } else {
            int end = json.indexOf(",", start);
            if (end == -1) {
                end = json.indexOf("}", start);
            }
            return json.substring(start, end).trim();
        }
    }

    private String extractArray(String json, String key) {
        String keyPattern = "\"" + key + "\":";
        int start = json.indexOf(keyPattern);
        if (start == -1) {
            return null;
        }

        start += keyPattern.length();
        int openBracket = json.indexOf("[", start);
        int closeBracket = json.indexOf("]", openBracket);

        return json.substring(openBracket + 1, closeBracket);
    }

    private void shuffleAndReload() {
        // Convert to array to shuffle
        Question[] qArray = new Question[allQuestions.size()];
        DataStructures.Iterator<Question> it = allQuestions.iterator();
        int i = 0;
        while (it.hasNext()) {
            qArray[i++] = it.next();
        }

        // Shuffle
        Random rand = new Random();
        for (i = qArray.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            Question temp = qArray[index];
            qArray[index] = qArray[i];
            qArray[i] = temp;
        }

        // Enqueue
        for (Question q : qArray) {
            questionQueue.enqueue(q);
        }
    }

    public Question getNextQuestion() {
        if (questionQueue.isEmpty()) {
            shuffleAndReload();
        }
        try {
            return questionQueue.dequeue();
        } catch (Exception e) {
            return null;
        }
    }
}

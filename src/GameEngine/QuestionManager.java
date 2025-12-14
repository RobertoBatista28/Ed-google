package GameEngine;

import DataStructures.ArrayList.ArrayUnorderedList;
import DataStructures.Queue.LinkedQueue;
import Models.Question;
import Models.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * QuestionManager manages the loading and distribution of quiz questions
 * during gameplay. Loads questions from a JSON configuration file, stores
 * them in an internal list, and provides a queue-based distribution mechanism
 * with automatic shuffling to ensure random question ordering throughout
 * the game session.
 */
public class QuestionManager {

    private final LinkedQueue<Question> questionQueue;
    private final ArrayUnorderedList<Question> allQuestions;

    /**
     * Creates a new QuestionManager and loads questions from the specified file.
     * Initializes both the question queue and the master question list, then
     * shuffles the questions and loads them into the queue for distribution.
     *
     * @param filePath the path to the JSON file containing question definitions
     */
    public QuestionManager(String filePath) {
        questionQueue = new LinkedQueue<>();
        allQuestions = new ArrayUnorderedList<>();
        loadQuestions(filePath);
        shuffleAndReload();
    }

    /**
     * Loads questions from a JSON file and parses them into Question objects.
     * Reads the entire file content, removes the outer JSON array brackets,
     * splits the JSON objects by closing braces, and parses each question.
     * Handles IOException gracefully by printing error message.
     *
     * @param filePath the path to the JSON file containing question definitions
     */
    private void loadQuestions(String filePath) {
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

            // Split JSON objects by closing brace and comma delimiter
            String[] objects = json.split("},");

            // Parse each JSON object and add question to list
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

    /**
     * Parses a JSON object string and creates a Question from its properties.
     * Extracts the question text, options array, and correct answer index from JSON.
     * Processes the options array by removing quotes and trimming whitespace.
     * Determines the correct answer index either as an integer or by matching option text.
     * Only adds the question if all required fields are valid.
     *
     * @param jsonObj the JSON object string representing a question definition
     */
    private void parseAndAddQuestion(String jsonObj) {
        try {
            String questionText = extractValue(jsonObj, "question");
            String optionsStr = extractArray(jsonObj, "options");
            String correctStr = extractValue(jsonObj, "correct");

            if (questionText != null && optionsStr != null && correctStr != null) {
                // Split options string by comma delimiter
                String[] rawOptions = optionsStr.split(",");
                String[] options = new String[rawOptions.length];
                
                // Process each option by trimming and removing quotes
                for (int i = 0; i < rawOptions.length; i++) {
                    String opt = rawOptions[i].trim();
                    // Remove surrounding quotes if present
                    if (opt.startsWith("\"") && opt.endsWith("\"")) {
                        opt = opt.substring(1, opt.length() - 1);
                    }
                    options[i] = opt;
                }

                // Determine correct answer index from integer or option text matching
                int correctIndex = -1;
                try {
                    // Try parsing as integer index first
                    correctIndex = Integer.parseInt(correctStr);
                } catch (NumberFormatException e) {
                    // If not integer, search for matching option text (case-insensitive)
                    for (int i = 0; i < options.length; i++) {
                        if (options[i].equalsIgnoreCase(correctStr)) {
                            correctIndex = i;
                            break;
                        }
                    }
                }
                
                // Only create question if correct index was successfully determined
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

    /**
     * Extracts a string value from a JSON object for a given key.
     * Locates the key pattern, advances past the colon, and determines if the
     * value is quoted or unquoted. For quoted values, extracts between quotes;
     * for unquoted values, extracts until a comma or closing brace.
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

        // Advance past the key pattern and skip leading whitespace
        start += keyPattern.length();

        char firstChar = json.charAt(start);
        // Skip any leading whitespace before the value
        while (firstChar == ' ') {
            start++;
            firstChar = json.charAt(start);
        }

        // Check if the value is quoted and extract accordingly
        if (firstChar == '"') {
            // For quoted values, find the closing quote and extract the text between
            int end = json.indexOf("\"", start + 1);
            return json.substring(start + 1, end);
        } else {
            // For unquoted values, find the next delimiter (comma or closing brace)
            int end = json.indexOf(",", start);
            if (end == -1) {
                end = json.indexOf("}", start);
            }
            return json.substring(start, end).trim();
        }
    }

    /**
     * Extracts an array value from a JSON object for a given key.
     * Locates the key pattern, finds the opening bracket, and extracts
     * the array content between brackets without the bracket delimiters.
     *
     * @param json the JSON string to search in
     * @param key the key name to search for
     * @return the array content as a string (without brackets), or null if not found
     */
    private String extractArray(String json, String key) {
        // Build the key pattern to search for in the JSON string
        String keyPattern = "\"" + key + "\":";
        int start = json.indexOf(keyPattern);
        if (start == -1) {
            return null;
        }

        // Find the opening bracket of the array
        start += keyPattern.length();
        int openBracket = json.indexOf("[", start);
        // Find the closing bracket of the array
        int closeBracket = json.indexOf("]", openBracket);

        // Extract content between brackets
        return json.substring(openBracket + 1, closeBracket);
    }

    /**
     * Shuffles all questions and reloads them into the question queue.
     * Converts the question list to an array, applies Fisher-Yates shuffle algorithm,
     * then enqueues all shuffled questions. Called when the queue becomes empty
     * to provide a new randomized round of questions.
     */
    private void shuffleAndReload() {
        // Convert unordered list to array for shuffling
        Question[] qArray = new Question[allQuestions.size()];
        DataStructures.Iterator<Question> it = allQuestions.iterator();
        int i = 0;
        while (it.hasNext()) {
            qArray[i++] = it.next();
        }

        // Apply Fisher-Yates shuffle algorithm for randomization
        Random rand = new Random();
        for (i = qArray.length - 1; i > 0; i--) {
            // Generate random index from 0 to i (inclusive)
            int index = rand.nextInt(i + 1);
            // Swap element at current position with randomly selected element
            Question temp = qArray[index];
            qArray[index] = qArray[i];
            qArray[i] = temp;
        }

        // Enqueue all shuffled questions into the distribution queue
        for (Question q : qArray) {
            questionQueue.enqueue(q);
        }
    }

    /**
     * Retrieves the next question from the queue. If the queue is empty,
     * automatically shuffles and reloads all questions to provide a new
     * randomized round. Handles dequeue exceptions gracefully.
     *
     * @return the next Question in the queue, or null if dequeue fails
     */
    public Question getNextQuestion() {
        // Check if queue is empty and reload with shuffled questions if needed
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

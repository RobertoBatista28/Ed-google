package Models;

/**
 * Question represents a quiz question with multiple choice answers.
 * Contains the question text, array of answer options, and the index
 * of the correct answer. Provides methods to retrieve question data
 * and validate user answers.
 *
 */
public class Question {
    // ----------------------------------------------------------------
    // Fields
    // ----------------------------------------------------------------
    private final String text;
    private final String[] options;
    private final int correctIndex;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    /**
     * Creates a new Question with the specified text, answer options, and correct answer.
     *
     * @param text the question text
     * @param options array of answer option strings
     * @param correctIndex the index of the correct answer in the options array
     */
    public Question(String text, String[] options, int correctIndex) {
        this.text = text;
        this.options = options;
        this.correctIndex = correctIndex;
    }

    // ----------------------------------------------------------------
    // Getters & Methods
    // ----------------------------------------------------------------
    /**
     * Returns the text of this question.
     *
     * @return the question text
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the array of answer options for this question.
     *
     * @return array of answer option strings
     */
    public String[] getOptions() {
        return options;
    }

    /**
     * Returns the index of the correct answer in the options array.
     *
     * @return the index of the correct answer option
     */
    public int getCorrectIndex() {
        return correctIndex;
    }
    
    /**
     * Determines whether the provided answer index is correct.
     * Compares the given index with the correct answer index.
     *
     * @param index the index of the answer provided by the player
     * @return true if the provided index matches the correct answer, false otherwise
     */
    public boolean isCorrect(int index) {
        return index == correctIndex;
    }
}

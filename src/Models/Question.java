package Models;

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
    public Question(String text, String[] options, int correctIndex) {
        this.text = text;
        this.options = options;
        this.correctIndex = correctIndex;
    }

    // ----------------------------------------------------------------
    // Getters & Methods
    // ----------------------------------------------------------------
    public String getText() {
        return text;
    }

    public String[] getOptions() {
        return options;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }
    
    public boolean isCorrect(int index) {
        return index == correctIndex;
    }
}

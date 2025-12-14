import UI.MainWindow;
import javax.swing.SwingUtilities;

/**
 * Main class for the MazeCraft game.
 * Entry point of the application that initializes the main window.
 *
 * @author Roberto Baptista & João Coelho
 * @version 4.2.2
 */
public class main {

    /**
     * Main method that starts the application.
     * Creates and displays the main window using the Event Dispatch Thread.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow());
    }
}

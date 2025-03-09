import javax.swing.*;

/**
 * Main class that runs the application
 */
public class ContactManager {
    public static void main(String[] args) {
        // Set up the database on application start
        DatabaseManager.getInstance().setupDatabase();

        // Use SwingUtilities to ensure GUI is created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
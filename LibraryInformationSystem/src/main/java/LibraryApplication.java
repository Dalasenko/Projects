import ui.MainFrame;
import database.DatabaseConnection;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class LibraryApplication {

    public static void main(String[] args) {
        // Set the look and feel to FlatLaf
        try {
            FlatLightLaf.setup();
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf look and feel: " + e.getMessage());
        }

        // Initialize database
        DatabaseConnection.initializeDatabase();

        // Launch the application UI
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });

        // Add shutdown hook to close database connection
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseConnection.closeConnection();
        }));
    }
}

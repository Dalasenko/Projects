package com.example.library;

import javax.swing.SwingUtilities;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main.java
 *
 * The entry point for the Book Library Information System.
 * It loads library data (or creates a new Library if none exists), adds sample data if needed,
 * launches the UI, and registers a shutdown hook to save the library data on exit.
 */
public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final String PERSISTENCE_FILE = "library.dat";

    public static void main(String[] args) {
        try {
            Library library = loadLibraryData();

            // Add sample data if the library is empty.
            if (library.getBooks().isEmpty() && library.getBorrowers().isEmpty()) {
                addSampleData(library);
            }

            launchUI(library);

            // Save library data on exit.
            registerShutdownHook(library);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred while initializing the library application", e);
        }
    }

    private static Library loadLibraryData() {
        return LibraryPersistence.loadLibrary(PERSISTENCE_FILE);
    }

    private static void addSampleData(Library library) {
        library.addBook("The Great Gatsby", "F. Scott Fitzgerald", "Scribner");
        library.addBook("1984", "George Orwell", "Secker & Warburg");
        library.addBook("To Kill a Mockingbird", "Harper Lee", "J.B. Lippincott & Co.");

        library.addBorrower("Alice Smith", "alice@example.com");
        library.addBorrower("Bob Johnson", "bob@example.com");
    }

    private static void launchUI(Library library) {
        SwingUtilities.invokeLater(() -> {
            LibraryUI ui = new LibraryUI(library);
            ui.setVisible(true);
        });
    }

    private static void registerShutdownHook(Library library) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                LibraryPersistence.saveLibrary(library, PERSISTENCE_FILE);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "An error occurred while saving the library data", e);
            }
        }));
    }
}

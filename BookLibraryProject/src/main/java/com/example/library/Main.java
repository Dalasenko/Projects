package com.example.library;

import javax.swing.SwingUtilities;

/**
 * Main.java
 *
 * The entry point for the Book Library Information System.
 * It loads library data (or creates a new Library if none exists), adds sample data if needed,
 * launches the UI, and registers a shutdown hook to save the library data on exit.
 */
public class Main {
    public static void main(String[] args) {
        String persistenceFile = "library.dat";
        Library library = LibraryPersistence.loadLibrary(persistenceFile);

        // Add sample data if the library is empty.
        if (library.getBooks().isEmpty() && library.getBorrowers().isEmpty()) {
            library.addBook("The Great Gatsby", "F. Scott Fitzgerald", "Scribner");
            library.addBook("1984", "George Orwell", "Secker & Warburg");
            library.addBook("To Kill a Mockingbird", "Harper Lee", "J.B. Lippincott & Co.");

            library.addBorrower("Alice Smith", "alice@example.com");
            library.addBorrower("Bob Johnson", "bob@example.com");
        }

        SwingUtilities.invokeLater(() -> {
            LibraryUI ui = new LibraryUI(library);
            ui.setVisible(true);
        });

        // Save library data on exit.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LibraryPersistence.saveLibrary(library, persistenceFile);
        }));
    }
}

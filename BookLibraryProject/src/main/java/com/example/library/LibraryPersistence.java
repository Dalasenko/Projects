package com.example.library;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The LibraryPersistence class provides static methods to save and load
 * a Library object using Java object serialization.
 *
 * Note: The data is saved in a binary format. To obtain a human-readable file,
 * a custom file format (such as CSV or JSON) must be implemented.
 */
public class LibraryPersistence {

    private static final Logger logger = Logger.getLogger(LibraryPersistence.class.getName());

    /**
     * Saves the given Library object to the specified file.
     *
     * @param library  the Library object to save.
     * @param fileName the name of the file.
     * @throws LibraryPersistenceException if an I/O error occurs.
     */
    public static void saveLibrary(Library library, String fileName) throws LibraryPersistenceException {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(fileName)))) {

            out.writeObject(library);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error saving library data", e);
            throw new LibraryPersistenceException("Error saving library data", e);
        }
    }

    /**
     * Loads a Library object from the specified file.
     *
     * If the file does not exist or an error occurs, a new Library object is returned.
     *
     * @param fileName the name of the file.
     * @return the loaded Library object or a new Library if loading fails.
     * @throws LibraryPersistenceException if an I/O error occurs.
     */
    public static Library loadLibrary(String fileName) throws LibraryPersistenceException {
        File file = new File(fileName);
        if (!file.exists()) {
            return new Library();
        }
        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(fileName)))) {

            Object obj = in.readObject();
            if (obj instanceof Library) {
                return (Library) obj;
            } else {
                logger.log(Level.WARNING, "The file does not contain a valid Library object.");
                return new Library();
            }

        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Error loading library data", e);
            throw new LibraryPersistenceException("Error loading library data", e);
        }
    }
}

/**
 * Custom exception class for library persistence errors.
 */
class LibraryPersistenceException extends Exception {
    public LibraryPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}


























}

package com.example.library;

import java.io.*;

/**
 * The LibraryPersistence class provides static methods to save and load
 * a Library object using Java object serialization.
 *
 * Note: The data is saved in a binary format. To obtain a human-readable file,
 * a custom file format (such as CSV or JSON) must be implemented.
 */
public class LibraryPersistence {

    /**
     * Saves the given Library object to the specified file.
     *
     * @param library  the Library object to save.
     * @param fileName the name of the file.
     */
    public static void saveLibrary(Library library, String fileName) {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(fileName)))) {

            out.writeObject(library);

        } catch (IOException e) {
            System.err.println("Error saving library data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads a Library object from the specified file.
     *
     * If the file does not exist or an error occurs, a new Library object is returned.
     *
     * @param fileName the name of the file.
     * @return the loaded Library object or a new Library if loading fails.
     */
    public static Library loadLibrary(String fileName) {
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
                System.err.println("The file does not contain a valid Library object.");
                return new Library();
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading library data: " + e.getMessage());
            e.printStackTrace();
            return new Library();
        }
    }

































}

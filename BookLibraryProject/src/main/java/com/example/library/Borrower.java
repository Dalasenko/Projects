package com.example.library;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * The Borrower class represents a borrower in the library system.
 * It includes fields for the borrower's ID, name, and email, along with
 * appropriate constructors, getters, setters, and a toString method.
 */
public class Borrower implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String email;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    /**
     * Constructor accepting an id, name, and email.
     *
     * @param id    the borrower's ID
     * @param name  the borrower's name
     * @param email the borrower's email
     */
    public Borrower(int id, String name, String email) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.id = id;
        this.name = name;
        this.email = email;
    }

    /**
     * Gets the borrower's ID.
     *
     * @return the borrower's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the borrower's name.
     *
     * @return the borrower's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the borrower's email.
     *
     * @return the borrower's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the borrower's name.
     *
     * @param name the new name of the borrower
     */
    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
    }

    /**
     * Sets the borrower's email.
     *
     * @param email the new email of the borrower
     */
    public void setEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }

    /**
     * Returns a string representation of the borrower.
     *
     * @return a string representation of the borrower
     */
    @Override
    public String toString() {
        return new StringBuilder()
                .append(id)
                .append(": ")
                .append(name)
                .append(" (")
                .append(email)
                .append(")")
                .toString();
    }
}

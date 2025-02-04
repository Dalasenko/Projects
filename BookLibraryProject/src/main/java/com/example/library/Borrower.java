package com.example.library;

import java.io.Serializable;

public class Borrower implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String email;

    // Constructor accepting an id, name, and email.
    public Borrower(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return id + ": " + name + " (" + email + ")";
    }
}

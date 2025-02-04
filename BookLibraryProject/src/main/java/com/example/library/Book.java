package com.example.library;

import java.io.Serializable;


public class Book implements Serializable {

    // Serial version UID for serialization compatibility.
    private static final long serialVersionUID = 1L;

    // Fields for the book.
    private int id;
    private String title;
    private String author;
    private String publisher;
    private boolean isAvailable;

    // -------------------------------------------------------------------------
    // Constructor: Creates a new Book object with the given id, title, author,
    // and publisher. The book is marked as available by default.
    // -------------------------------------------------------------------------
    public Book(int id, String title, String author, String publisher) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.isAvailable = true;
    }

    // -------------------------------------------------------------------------
    // Getters for the Book fields.
    // -------------------------------------------------------------------------
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    // -------------------------------------------------------------------------
    // Setters for the Book fields.
    // -------------------------------------------------------------------------
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    // -------------------------------------------------------------------------
    // Returns a string representation of the book.
    // -------------------------------------------------------------------------
    @Override
    public String toString() {
        return id + ": " + title + " by " + author + " (" + publisher + ") "
                + (isAvailable ? "[Available]" : "[On Loan]");
    }

    // -------------------------------------------------------------------------
    // Future methods and helper functions for Book can be added here.
    // -------------------------------------------------------------------------




















}

package com.example.library;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * The Loan class represents a loan transaction for a book.
 * It records the loan ID, the associated Book and Borrower,
 * the date of the loan, and the return information.
 *
 * This class implements Serializable for persistence.
 */
public class Loan implements Serializable {

    // Serial version UID.
    private static final long serialVersionUID = 1L;

    // Fields for the loan.
    private int loanId;
    private Book book;
    private Borrower borrower;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private boolean returned;

    /**
     * Constructor: Creates a new Loan object with the specified loanId,
     * associated book, and borrower. Sets the loan date to the current date
     * and marks the loan as not returned.
     *
     * @param loanId the loan ID
     * @param book the book being loaned
     * @param borrower the borrower of the book
     */
    public Loan(int loanId, Book book, Borrower borrower) {
        if (book == null || borrower == null) {
            throw new IllegalArgumentException("Book and Borrower cannot be null");
        }
        this.loanId = loanId;
        this.book = book;
        this.borrower = borrower;
        this.loanDate = LocalDate.now();
        this.returned = false;
    }

    /**
     * Gets the loan ID.
     *
     * @return the loan ID
     */
    public int getLoanId() {
        return loanId;
    }

    /**
     * Gets the book being loaned.
     *
     * @return the book
     */
    public Book getBook() {
        return book;
    }

    /**
     * Gets the borrower of the book.
     *
     * @return the borrower
     */
    public Borrower getBorrower() {
        return borrower;
    }

    /**
     * Gets the loan date.
     *
     * @return the loan date
     */
    public LocalDate getLoanDate() {
        return loanDate;
    }

    /**
     * Gets the return date.
     *
     * @return the return date
     */
    public LocalDate getReturnDate() {
        return returnDate;
    }

    /**
     * Checks if the book has been returned.
     *
     * @return true if the book has been returned, false otherwise
     */
    public boolean isReturned() {
        return returned;
    }

    /**
     * Marks the loan as returned and sets the return date to the current date.
     */
    public void markReturned() {
        this.returned = true;
        this.returnDate = LocalDate.now();
    }

    /**
     * Returns a string representation of the loan.
     *
     * @return a string representation of the loan
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateStr = loanDate.format(formatter);
        String retStr = returned ? "Returned on " + returnDate.format(formatter) : "Not Returned";
        return "Loan " + loanId + ": " + book.getTitle() + " to " +
                borrower.getName() + " on " + dateStr + " (" + retStr + ")";
    }
}


















}

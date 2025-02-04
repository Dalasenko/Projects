package com.example.library;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Loan class represents a loan transaction for a book.
 * It records the loan id, the associated Book and Borrower,
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
    private Date loanDate;
    private Date returnDate;
    private boolean returned;

    // -------------------------------------------------------------------------
    // Constructor: Creates a new Loan object with the specified loanId,
    // associated book, and borrower. Sets the loan date to the current date
    // and marks the loan as not returned.
    // -------------------------------------------------------------------------
    public Loan(int loanId, Book book, Borrower borrower) {
        this.loanId = loanId;
        this.book = book;
        this.borrower = borrower;
        this.loanDate = new Date();
        this.returned = false;
    }

    // -------------------------------------------------------------------------
    // Getters for the Loan fields.
    // -------------------------------------------------------------------------
    public int getLoanId() {
        return loanId;
    }

    public Book getBook() {
        return book;
    }

    public Borrower getBorrower() {
        return borrower;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return returned;
    }

    // -------------------------------------------------------------------------
    // Marks the loan as returned and sets the return date to the current date.
    // -------------------------------------------------------------------------
    public void markReturned() {
        this.returned = true;
        this.returnDate = new Date();
    }

    // -------------------------------------------------------------------------
    // Returns a string representation of the loan.
    // -------------------------------------------------------------------------
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateStr = sdf.format(loanDate);
        String retStr = returned ? "Returned on " + sdf.format(returnDate) : "Not Returned";
        return "Loan " + loanId + ": " + book.getTitle() + " to " +
                borrower.getName() + " on " + dateStr + " (" + retStr + ")";
    }

    // -------------------------------------------------------------------------
    // Additional loan-related methods can be added here.
    // -------------------------------------------------------------------------






















}

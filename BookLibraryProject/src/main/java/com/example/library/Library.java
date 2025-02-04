package com.example.library;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Library class is the central data model for the Book Library Information System.
 * It contains lists of Book, Borrower, and Loan objects and provides methods for adding,
 * updating, removing, and searching these objects.
 *
 * Unique IDs for books, borrowers, and loans are generated using AtomicInteger counters.
 * This class implements Serializable to allow saving and loading the library state.
 */
public class Library implements Serializable {

    private static final long serialVersionUID = 1L;

    // Lists for storing books, borrowers, and loans.
    private List<Book> books;
    private List<Borrower> borrowers;
    private List<Loan> loans;

    // Transient ID generators for auto-generating IDs.
    private transient AtomicInteger bookIdGenerator;
    private transient AtomicInteger borrowerIdGenerator;
    private transient AtomicInteger loanIdGenerator;

    /**
     * Constructor: Initializes the lists and ID generators.
     */
    public Library() {
        books = new ArrayList<>();
        borrowers = new ArrayList<>();
        loans = new ArrayList<>();
        bookIdGenerator = new AtomicInteger(1);
        borrowerIdGenerator = new AtomicInteger(1);
        loanIdGenerator = new AtomicInteger(1);
    }

    /**
     * After deserialization, reinitialize transient fields.
     */
    private void readObject(java.io.ObjectInputStream in) throws Exception {
        in.defaultReadObject();
        bookIdGenerator = new AtomicInteger(1);
        for (Book b : books) {
            if (b.getId() >= bookIdGenerator.get()) {
                bookIdGenerator.set(b.getId() + 1);
            }
        }
        borrowerIdGenerator = new AtomicInteger(1);
        for (Borrower br : borrowers) {
            if (br.getId() >= borrowerIdGenerator.get()) {
                borrowerIdGenerator.set(br.getId() + 1);
            }
        }
        loanIdGenerator = new AtomicInteger(1);
        for (Loan l : loans) {
            if (l.getLoanId() >= loanIdGenerator.get()) {
                loanIdGenerator.set(l.getLoanId() + 1);
            }
        }
    }

    // ---------------------------
    // BOOK METHODS
    // ---------------------------

    /**
     * Adds a new book using auto-generated ID.
     */
    public synchronized Book addBook(String title, String author, String publisher) {
        Book book = new Book(bookIdGenerator.getAndIncrement(), title, author, publisher);
        books.add(book);
        return book;
    }

    /**
     * Overloaded method to add a new book using a manual ID.
     */
    public synchronized Book addBook(int id, String title, String author, String publisher) {
        Book book = new Book(id, title, author, publisher);
        books.add(book);
        // Update the generator if necessary.
        if (id >= bookIdGenerator.get()) {
            bookIdGenerator.set(id + 1);
        }
        return book;
    }

    public synchronized boolean updateBook(int id, String title, String author, String publisher) {
        for (Book b : books) {
            if (b.getId() == id) {
                b.setTitle(title);
                b.setAuthor(author);
                b.setPublisher(publisher);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean removeBook(int id) {
        Iterator<Book> it = books.iterator();
        while (it.hasNext()) {
            Book b = it.next();
            if (b.getId() == id && b.isAvailable()) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public synchronized List<Book> getBooks() {
        return new ArrayList<>(books);
    }

    public synchronized List<Book> searchBooks(String keyword) {
        List<Book> results = new ArrayList<>();
        String lower = keyword.toLowerCase();
        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(lower) ||
                    b.getAuthor().toLowerCase().contains(lower) ||
                    b.getPublisher().toLowerCase().contains(lower)) {
                results.add(b);
            }
        }
        return results;
    }

    // ---------------------------
    // BORROWER METHODS
    // ---------------------------

    /**
     * Adds a new borrower using auto-generated ID.
     */
    public synchronized Borrower addBorrower(String name, String email) {
        Borrower br = new Borrower(borrowerIdGenerator.getAndIncrement(), name, email);
        borrowers.add(br);
        return br;
    }

    /**
     * Overloaded method to add a new borrower using a manual ID.
     */
    public synchronized Borrower addBorrower(int id, String name, String email) {
        Borrower br = new Borrower(id, name, email);
        borrowers.add(br);
        if (id >= borrowerIdGenerator.get()) {
            borrowerIdGenerator.set(id + 1);
        }
        return br;
    }

    public synchronized boolean updateBorrower(int id, String name, String email) {
        for (Borrower br : borrowers) {
            if (br.getId() == id) {
                br.setName(name);
                br.setEmail(email);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean removeBorrower(int id) {
        Iterator<Borrower> it = borrowers.iterator();
        while (it.hasNext()) {
            Borrower br = it.next();
            if (br.getId() == id) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public synchronized List<Borrower> getBorrowers() {
        return new ArrayList<>(borrowers);
    }

    public synchronized List<Borrower> searchBorrowers(String keyword) {
        List<Borrower> result = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (Borrower b : borrowers) {
            if (b.getName().toLowerCase().contains(lowerKeyword) ||
                    b.getEmail().toLowerCase().contains(lowerKeyword)) {
                result.add(b);
            }
        }
        return result;
    }

    // ---------------------------
    // LOAN METHODS
    // ---------------------------

    public synchronized Loan loanBook(int bookId, int borrowerId) {
        Book book = null;
        for (Book b : books) {
            if (b.getId() == bookId && b.isAvailable()) {
                book = b;
                break;
            }
        }
        if (book == null) {
            return null; // book not found or not available
        }
        Borrower borrower = null;
        for (Borrower br : borrowers) {
            if (br.getId() == borrowerId) {
                borrower = br;
                break;
            }
        }
        if (borrower == null) {
            return null; // borrower not found
        }
        book.setAvailable(false);
        Loan loan = new Loan(loanIdGenerator.getAndIncrement(), book, borrower);
        loans.add(loan);
        return loan;
    }

    public synchronized boolean returnBook(int loanId) {
        for (Loan loan : loans) {
            if (loan.getLoanId() == loanId && !loan.isReturned()) {
                loan.markReturned();
                loan.getBook().setAvailable(true);
                return true;
            }
        }
        return false;
    }

    public synchronized List<Loan> getLoans() {
        return new ArrayList<>(loans);
    }

    // Additional library methods can be added here.




















}

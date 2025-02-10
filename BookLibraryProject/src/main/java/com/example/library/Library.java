package com.example.library;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

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
    private final List<Book> books = new ArrayList<>();
    private final List<Borrower> borrowers = new ArrayList<>();
    private final List<Loan> loans = new ArrayList<>();

    // Transient ID generators for auto-generating IDs.
    private transient AtomicInteger bookIdGenerator;
    private transient AtomicInteger borrowerIdGenerator;
    private transient AtomicInteger loanIdGenerator;

    private final transient Lock lock = new ReentrantLock();

    /**
     * Constructor: Initializes the lists and ID generators.
     */
    public Library() {
        bookIdGenerator = new AtomicInteger(1);
        borrowerIdGenerator = new AtomicInteger(1);
        loanIdGenerator = new AtomicInteger(1);
    }

    /**
     * Reinitialize transient fields after deserialization.
     */
    private void readObject(java.io.ObjectInputStream in) throws Exception {
        in.defaultReadObject();
        bookIdGenerator = new AtomicInteger(1);
        borrowers.forEach(br -> borrowerIdGenerator.updateAndGet(x -> Math.max(x, br.getId() + 1)));
        books.forEach(b -> bookIdGenerator.updateAndGet(x -> Math.max(x, b.getId() + 1)));
        loans.forEach(l -> loanIdGenerator.updateAndGet(x -> Math.max(x, l.getLoanId() + 1)));
    }

    // ---------------------------
    // BOOK METHODS
    // ---------------------------

    /**
     * Adds a new book using auto-generated ID.
     */
    public Book addBook(String title, String author, String publisher) {
        validateString(title, "Title");
        validateString(author, "Author");
        validateString(publisher, "Publisher");

        lock.lock();
        try {
            Book book = new Book(bookIdGenerator.getAndIncrement(), title, author, publisher);
            books.add(book);
            return book;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Overloaded method to add a new book using a manual ID.
     */
    public Book addBook(int id, String title, String author, String publisher) {
        validateId(id);
        validateString(title, "Title");
        validateString(author, "Author");
        validateString(publisher, "Publisher");

        lock.lock();
        try {
            Book book = new Book(id, title, author, publisher);
            books.add(book);
            if (id >= bookIdGenerator.get()) {
                bookIdGenerator.set(id + 1);
            }
            return book;
        } finally {
            lock.unlock();
        }
    }

    public boolean updateBook(int id, String title, String author, String publisher) {
        validateId(id);
        validateString(title, "Title");
        validateString(author, "Author");
        validateString(publisher, "Publisher");

        lock.lock();
        try {
            return books.stream()
                    .filter(b -> b.getId() == id)
                    .findFirst()
                    .map(b -> {
                        b.setTitle(title);
                        b.setAuthor(author);
                        b.setPublisher(publisher);
                        return true;
                    })
                    .orElse(false);
        } finally {
            lock.unlock();
        }
    }

    public boolean removeBook(int id) {
        validateId(id);

        lock.lock();
        try {
            return books.removeIf(b -> b.getId() == id && b.isAvailable());
        } finally {
            lock.unlock();
        }
    }

    public List<Book> getBooks() {
        lock.lock();
        try {
            return new ArrayList<>(books);
        } finally {
            lock.unlock();
        }
    }

    public List<Book> searchBooks(String keyword) {
        validateString(keyword, "Keyword");

        lock.lock();
        try {
            String lower = keyword.toLowerCase();
            return books.stream()
                    .filter(b -> b.getTitle().toLowerCase().contains(lower)
                            || b.getAuthor().toLowerCase().contains(lower)
                            || b.getPublisher().toLowerCase().contains(lower))
                    .collect(Collectors.toList());
        } finally {
            lock.unlock();
        }
    }

    // ---------------------------
    // BORROWER METHODS
    // ---------------------------

    public Borrower addBorrower(String name, String email) {
        validateString(name, "Name");
        validateString(email, "Email");

        lock.lock();
        try {
            Borrower br = new Borrower(borrowerIdGenerator.getAndIncrement(), name, email);
            borrowers.add(br);
            return br;
        } finally {
            lock.unlock();
        }
    }

    public Borrower addBorrower(int id, String name, String email) {
        validateId(id);
        validateString(name, "Name");
        validateString(email, "Email");

        lock.lock();
        try {
            Borrower br = new Borrower(id, name, email);
            borrowers.add(br);
            if (id >= borrowerIdGenerator.get()) {
                borrowerIdGenerator.set(id + 1);
            }
            return br;
        } finally {
            lock.unlock();
        }
    }

    public boolean updateBorrower(int id, String name, String email) {
        validateId(id);
        validateString(name, "Name");
        validateString(email, "Email");

        lock.lock();
        try {
            return borrowers.stream()
                    .filter(br -> br.getId() == id)
                    .findFirst()
                    .map(br -> {
                        br.setName(name);
                        br.setEmail(email);
                        return true;
                    })
                    .orElse(false);
        } finally {
            lock.unlock();
        }
    }

    public boolean removeBorrower(int id) {
        validateId(id);

        lock.lock();
        try {
            return borrowers.removeIf(br -> br.getId() == id);
        } finally {
            lock.unlock();
        }
    }

    public List<Borrower> getBorrowers() {
        lock.lock();
        try {
            return new ArrayList<>(borrowers);
        } finally {
            lock.unlock();
        }
    }

    public List<Borrower> searchBorrowers(String keyword) {
        validateString(keyword, "Keyword");

        lock.lock();
        try {
            String lowerKeyword = keyword.toLowerCase();
            return borrowers.stream()
                    .filter(b -> b.getName().toLowerCase().contains(lowerKeyword)
                            || b.getEmail().toLowerCase().contains(lowerKeyword))
                    .collect(Collectors.toList());
        } finally {
            lock.unlock();
        }
    }

    // ---------------------------
    // LOAN METHODS
    // ---------------------------

    public Loan loanBook(int bookId, int borrowerId) {
        validateId(bookId);
        validateId(borrowerId);

        lock.lock();
        try {
            Book book = books.stream().filter(b -> b.getId() == bookId && b.isAvailable()).findFirst().orElse(null);
            if (book == null) {
                return null;
            }
            Borrower borrower = borrowers.stream().filter(br -> br.getId() == borrowerId).findFirst().orElse(null);
            if (borrower == null) {
                return null;
            }
            book.setAvailable(false);
            Loan loan = new Loan(loanIdGenerator.getAndIncrement(), book, borrower);
            loans.add(loan);
            return loan;
        } finally {
            lock.unlock();
        }
    }

    public boolean returnBook(int loanId) {
        validateId(loanId);

        lock.lock();
        try {
            return loans.stream()
                    .filter(loan -> loan.getLoanId() == loanId && !loan.isReturned())
                    .findFirst()
                    .map(loan -> {
                        loan.markReturned();
                        loan.getBook().setAvailable(true);
                        return true;
                    })
                    .orElse(false);
        } finally {
            lock.unlock();
        }
    }

    public List<Loan> getLoans() {
        lock.lock();
        try {
            return new ArrayList<>(loans);
        } finally {
            lock.unlock();
        }
    }

    private void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be positive");
        }
    }

    private void validateString(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }
}



















}

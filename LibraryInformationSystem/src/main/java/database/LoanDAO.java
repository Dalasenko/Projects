package database;

import model.Loan;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {

    public List<Loan> getAllLoans() {
        List<Loan> loans = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT l.*, b.title as book_title, p.name as patron_name " +
                             "FROM loans l " +
                             "JOIN books b ON l.book_id = b.id " +
                             "JOIN patrons p ON l.patron_id = p.id " +
                             "ORDER BY l.loan_date DESC")) {

            while (rs.next()) {
                Loan loan = mapResultSetToLoan(rs);
                loan.setBookTitle(rs.getString("book_title"));
                loan.setPatronName(rs.getString("patron_name"));
                loans.add(loan);
            }

        } catch (SQLException e) {
            System.err.println("Error getting all loans: " + e.getMessage());
        }

        return loans;
    }

    public List<Loan> getActiveLoans() {
        List<Loan> loans = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT l.*, b.title as book_title, p.name as patron_name " +
                             "FROM loans l " +
                             "JOIN books b ON l.book_id = b.id " +
                             "JOIN patrons p ON l.patron_id = p.id " +
                             "WHERE l.returned = FALSE " +
                             "ORDER BY l.due_date ASC")) {

            while (rs.next()) {
                Loan loan = mapResultSetToLoan(rs);
                loan.setBookTitle(rs.getString("book_title"));
                loan.setPatronName(rs.getString("patron_name"));
                loans.add(loan);
            }

        } catch (SQLException e) {
            System.err.println("Error getting active loans: " + e.getMessage());
        }

        return loans;
    }

    public List<Loan> getOverdueLoans() {
        List<Loan> loans = new ArrayList<>();
        LocalDate today = LocalDate.now();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT l.*, b.title as book_title, p.name as patron_name " +
                             "FROM loans l " +
                             "JOIN books b ON l.book_id = b.id " +
                             "JOIN patrons p ON l.patron_id = p.id " +
                             "WHERE l.returned = FALSE AND l.due_date < ? " +
                             "ORDER BY l.due_date ASC")) {

            stmt.setDate(1, Date.valueOf(today));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Loan loan = mapResultSetToLoan(rs);
                loan.setBookTitle(rs.getString("book_title"));
                loan.setPatronName(rs.getString("patron_name"));
                loans.add(loan);
            }

        } catch (SQLException e) {
            System.err.println("Error getting overdue loans: " + e.getMessage());
        }

        return loans;
    }

    public List<Loan> getLoansByPatron(int patronId) {
        List<Loan> loans = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT l.*, b.title as book_title, p.name as patron_name " +
                             "FROM loans l " +
                             "JOIN books b ON l.book_id = b.id " +
                             "JOIN patrons p ON l.patron_id = p.id " +
                             "WHERE l.patron_id = ? " +
                             "ORDER BY l.loan_date DESC")) {

            stmt.setInt(1, patronId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Loan loan = mapResultSetToLoan(rs);
                loan.setBookTitle(rs.getString("book_title"));
                loan.setPatronName(rs.getString("patron_name"));
                loans.add(loan);
            }

        } catch (SQLException e) {
            System.err.println("Error getting loans by patron: " + e.getMessage());
        }

        return loans;
    }

    public boolean checkoutBook(int bookId, int patronId, int loanDays) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO loans (book_id, patron_id, loan_date, due_date, returned) " +
                             "VALUES (?, ?, ?, ?, FALSE)")) {

            LocalDate loanDate = LocalDate.now();
            LocalDate dueDate = loanDate.plusDays(loanDays);

            stmt.setInt(1, bookId);
            stmt.setInt(2, patronId);
            stmt.setDate(3, Date.valueOf(loanDate));
            stmt.setDate(4, Date.valueOf(dueDate));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // Update book availability
                BookDAO bookDAO = new BookDAO();
                return bookDAO.updateBookAvailability(bookId, false);
            }

            return false;

        } catch (SQLException e) {
            System.err.println("Error checking out book: " + e.getMessage());
            return false;
        }
    }

    public boolean returnBook(int loanId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get the book ID from the loan
            int bookId;
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT book_id FROM loans WHERE id = ?")) {
                stmt.setInt(1, loanId);
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    return false;
                }

                bookId = rs.getInt("book_id");
            }

            // Update the loan record
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE loans SET return_date = ?, returned = TRUE WHERE id = ?")) {
                stmt.setDate(1, Date.valueOf(LocalDate.now()));
                stmt.setInt(2, loanId);

                int affectedRows = stmt.executeUpdate();

                if (affectedRows > 0) {
                    // Update book availability
                    BookDAO bookDAO = new BookDAO();
                    return bookDAO.updateBookAvailability(bookId, true);
                }
            }

            return false;

        } catch (SQLException e) {
            System.err.println("Error returning book: " + e.getMessage());
            return false;
        }
    }

    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getInt("id"));
        loan.setBookId(rs.getInt("book_id"));
        loan.setPatronId(rs.getInt("patron_id"));

        Date loanDate = rs.getDate("loan_date");
        if (loanDate != null) {
            loan.setLoanDate(loanDate.toLocalDate());
        }

        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) {
            loan.setDueDate(dueDate.toLocalDate());
        }

        Date returnDate = rs.getDate("return_date");
        if (returnDate != null) {
            loan.setReturnDate(returnDate.toLocalDate());
        }

        loan.setReturned(rs.getBoolean("returned"));

        return loan;
    }
}
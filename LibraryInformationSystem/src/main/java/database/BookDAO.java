package database;

import model.Book;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM books ORDER BY title")) {

            while (rs.next()) {
                Book book = mapResultSetToBook(rs);
                books.add(book);
            }

        } catch (SQLException e) {
            System.err.println("Error getting all books: " + e.getMessage());
        }

        return books;
    }

    public Book getBookById(int id) {
        Book book = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books WHERE id = ?")) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                book = mapResultSetToBook(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting book by ID: " + e.getMessage());
        }

        return book;
    }

    public List<Book> searchBooks(String searchTerm) {
        List<Book> books = new ArrayList<>();
        String searchPattern = "%" + searchTerm + "%";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ? ORDER BY title")) {

            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Book book = mapResultSetToBook(rs);
                books.add(book);
            }

        } catch (SQLException e) {
            System.err.println("Error searching books: " + e.getMessage());
        }

        return books;
    }

    public int addBook(Book book) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO books (isbn, title, author, publisher, publication_date, genre, available) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getPublisher());
            stmt.setDate(5, book.getPublicationDate() != null ?
                    Date.valueOf(book.getPublicationDate()) : null);
            stmt.setString(6, book.getGenre());
            stmt.setBoolean(7, book.isAvailable());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating book failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating book failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
            return -1;
        }
    }

    public boolean updateBook(Book book) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE books SET isbn = ?, title = ?, author = ?, publisher = ?, " +
                             "publication_date = ?, genre = ?, available = ? WHERE id = ?")) {

            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getPublisher());
            stmt.setDate(5, book.getPublicationDate() != null ?
                    Date.valueOf(book.getPublicationDate()) : null);
            stmt.setString(6, book.getGenre());
            stmt.setBoolean(7, book.isAvailable());
            stmt.setInt(8, book.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBook(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM books WHERE id = ?")) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
            return false;
        }
    }

    public boolean updateBookAvailability(int id, boolean available) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE books SET available = ? WHERE id = ?")) {

            stmt.setBoolean(1, available);
            stmt.setInt(2, id);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating book availability: " + e.getMessage());
            return false;
        }
    }

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setIsbn(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setPublisher(rs.getString("publisher"));

        Date publicationDate = rs.getDate("publication_date");
        if (publicationDate != null) {
            book.setPublicationDate(publicationDate.toLocalDate());
        }

        book.setGenre(rs.getString("genre"));
        book.setAvailable(rs.getBoolean("available"));

        return book;
    }
}
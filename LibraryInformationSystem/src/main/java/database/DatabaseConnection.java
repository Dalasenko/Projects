package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:h2:./librarydb";
    private static final String USER = "sa";
    private static final String PASS = "";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create Books table
            stmt.execute("CREATE TABLE IF NOT EXISTS books (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "isbn VARCHAR(20) UNIQUE, " +
                    "title VARCHAR(255) NOT NULL, " +
                    "author VARCHAR(255) NOT NULL, " +
                    "publisher VARCHAR(255), " +
                    "publication_date DATE, " +
                    "genre VARCHAR(100), " +
                    "available BOOLEAN DEFAULT TRUE" +
                    ")");

            // Create Patrons table
            stmt.execute("CREATE TABLE IF NOT EXISTS patrons (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "email VARCHAR(255) UNIQUE, " +
                    "phone VARCHAR(20), " +
                    "address VARCHAR(255), " +
                    "registration_date DATE" +
                    ")");

            // Create Loans table
            stmt.execute("CREATE TABLE IF NOT EXISTS loans (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "book_id INT NOT NULL, " +
                    "patron_id INT NOT NULL, " +
                    "loan_date DATE NOT NULL, " +
                    "due_date DATE NOT NULL, " +
                    "return_date DATE, " +
                    "returned BOOLEAN DEFAULT FALSE, " +
                    "FOREIGN KEY (book_id) REFERENCES books(id), " +
                    "FOREIGN KEY (patron_id) REFERENCES patrons(id)" +
                    ")");

            // Insert sample data if no books exist
            if (conn.createStatement().executeQuery("SELECT COUNT(*) FROM books").next()) {
                if (conn.createStatement().executeQuery("SELECT COUNT(*) FROM books").getInt(1) == 0) {
                    insertSampleData(conn);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    private static void insertSampleData(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        // Insert sample books
        stmt.execute("INSERT INTO books (isbn, title, author, publisher, publication_date, genre, available) VALUES " +
                "('9780061120084', 'To Kill a Mockingbird', 'Harper Lee', 'HarperCollins', '1960-07-11', 'Fiction', TRUE), " +
                "('9780141439518', 'Pride and Prejudice', 'Jane Austen', 'Penguin Classics', '1813-01-28', 'Romance', TRUE), " +
                "('9780451524935', '1984', 'George Orwell', 'Signet Classic', '1949-06-08', 'Dystopian', TRUE), " +
                "('9780743273565', 'The Great Gatsby', 'F. Scott Fitzgerald', 'Scribner', '1925-04-10', 'Classic', TRUE), " +
                "('9780307277671', 'The Road', 'Cormac McCarthy', 'Vintage', '2006-09-26', 'Post-Apocalyptic', TRUE)");

        // Insert sample patrons
        stmt.execute("INSERT INTO patrons (name, email, phone, address, registration_date) VALUES " +
                "('John Smith', 'john.smith@example.com', '555-123-4567', '123 Main St, Anytown', '2023-01-15'), " +
                "('Jane Doe', 'jane.doe@example.com', '555-987-6543', '456 Oak Ave, Somewhere', '2023-02-20'), " +
                "('Bob Johnson', 'bob.johnson@example.com', '555-555-5555', '789 Pine Rd, Nowhere', '2023-03-25')");
    }
}

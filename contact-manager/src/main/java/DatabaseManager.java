import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database operations (Singleton pattern)
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:contacts.db";
    private static DatabaseManager instance;

    // Private constructor for Singleton pattern
    private DatabaseManager() {
        // Load the SQLite JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Database driver not found. The application will not function correctly.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Singleton instance getter
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // Create a database connection
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Set up the database and create tables if they don't exist
    public void setupDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS contacts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "firstName TEXT NOT NULL," +
                "lastName TEXT NOT NULL," +
                "email TEXT," +
                "phone TEXT)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            System.err.println("Error setting up database: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Failed to set up the database: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Add a new contact to the database
    public boolean addContact(Contact contact) {
        String insertSQL = "INSERT INTO contacts (firstName, lastName, email, phone) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, contact.getFirstName());
            pstmt.setString(2, contact.getLastName());
            pstmt.setString(3, contact.getEmail());
            pstmt.setString(4, contact.getPhone());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error adding contact: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Failed to add contact: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Update an existing contact
    public boolean updateContact(Contact contact) {
        String updateSQL = "UPDATE contacts SET firstName = ?, lastName = ?, email = ?, phone = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, contact.getFirstName());
            pstmt.setString(2, contact.getLastName());
            pstmt.setString(3, contact.getEmail());
            pstmt.setString(4, contact.getPhone());
            pstmt.setInt(5, contact.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating contact: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Failed to update contact: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Delete a contact
    public boolean deleteContact(int id) {
        String deleteSQL = "DELETE FROM contacts WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting contact: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Failed to delete contact: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Get all contacts
    public List<Contact> getAllContacts() {
        String selectSQL = "SELECT id, firstName, lastName, email, phone FROM contacts ORDER BY lastName, firstName";
        List<Contact> contacts = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {

            while (rs.next()) {
                Contact contact = new Contact(
                        rs.getInt("id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("phone")
                );
                contacts.add(contact);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching contacts: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Failed to fetch contacts: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return contacts;
    }

    // Search contacts by name
    public List<Contact> searchContacts(String searchTerm) {
        String searchSQL = "SELECT id, firstName, lastName, email, phone FROM contacts " +
                "WHERE firstName LIKE ? OR lastName LIKE ? ORDER BY lastName, firstName";
        List<Contact> contacts = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(searchSQL)) {

            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Contact contact = new Contact(
                            rs.getInt("id"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            rs.getString("email"),
                            rs.getString("phone")
                    );
                    contacts.add(contact);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error searching contacts: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Failed to search contacts: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return contacts;
    }
}
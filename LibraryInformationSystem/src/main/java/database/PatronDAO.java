package database;

import model.Patron;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PatronDAO {

    public List<Patron> getAllPatrons() {
        List<Patron> patrons = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM patrons ORDER BY name")) {

            while (rs.next()) {
                Patron patron = mapResultSetToPatron(rs);
                patrons.add(patron);
            }

        } catch (SQLException e) {
            System.err.println("Error getting all patrons: " + e.getMessage());
        }

        return patrons;
    }

    public Patron getPatronById(int id) {
        Patron patron = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM patrons WHERE id = ?")) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                patron = mapResultSetToPatron(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting patron by ID: " + e.getMessage());
        }

        return patron;
    }

    public List<Patron> searchPatrons(String searchTerm) {
        List<Patron> patrons = new ArrayList<>();
        String searchPattern = "%" + searchTerm + "%";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM patrons WHERE name LIKE ? OR email LIKE ? OR phone LIKE ? ORDER BY name")) {

            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Patron patron = mapResultSetToPatron(rs);
                patrons.add(patron);
            }

        } catch (SQLException e) {
            System.err.println("Error searching patrons: " + e.getMessage());
        }

        return patrons;
    }

    public int addPatron(Patron patron) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO patrons (name, email, phone, address, registration_date) " +
                             "VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, patron.getName());
            stmt.setString(2, patron.getEmail());
            stmt.setString(3, patron.getPhone());
            stmt.setString(4, patron.getAddress());
            stmt.setDate(5, patron.getRegistrationDate() != null ?
                    Date.valueOf(patron.getRegistrationDate()) : Date.valueOf(LocalDate.now()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating patron failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating patron failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error adding patron: " + e.getMessage());
            return -1;
        }
    }

    public boolean updatePatron(Patron patron) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE patrons SET name = ?, email = ?, phone = ?, " +
                             "address = ?, registration_date = ? WHERE id = ?")) {

            stmt.setString(1, patron.getName());
            stmt.setString(2, patron.getEmail());
            stmt.setString(3, patron.getPhone());
            stmt.setString(4, patron.getAddress());
            stmt.setDate(5, patron.getRegistrationDate() != null ?
                    Date.valueOf(patron.getRegistrationDate()) : null);
            stmt.setInt(6, patron.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating patron: " + e.getMessage());
            return false;
        }
    }

    public boolean deletePatron(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM patrons WHERE id = ?")) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting patron: " + e.getMessage());
            return false;
        }
    }

    private Patron mapResultSetToPatron(ResultSet rs) throws SQLException {
        Patron patron = new Patron();
        patron.setId(rs.getInt("id"));
        patron.setName(rs.getString("name"));
        patron.setEmail(rs.getString("email"));
        patron.setPhone(rs.getString("phone"));
        patron.setAddress(rs.getString("address"));

        Date registrationDate = rs.getDate("registration_date");
        if (registrationDate != null) {
            patron.setRegistrationDate(registrationDate.toLocalDate());
        }

        return patron;
    }
}
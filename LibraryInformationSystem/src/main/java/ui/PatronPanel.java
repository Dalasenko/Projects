package ui;

import model.Patron;
import database.PatronDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class PatronPanel extends JPanel {

    private JFrame parentFrame;
    private PatronDAO patronDAO;
    private JTable patronTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    // Constructor for PatronPanel, initializing the UI components and loading data
    public PatronPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.patronDAO = new PatronDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");

        // Adding action listener for the search button
        searchButton.addActionListener(this::searchPatrons);
        // Adding action listener for the clear button
        clearButton.addActionListener(e -> {
            searchField.setText("");
            refreshData();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);

        searchPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(buttonPanel, BorderLayout.EAST);

        // Table
        String[] columnNames = {"ID", "Name", "Email", "Phone", "Address", "Registration Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        patronTable = new JTable(tableModel);
        patronTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patronTable.setAutoCreateRowSorter(true);
        patronTable.getTableHeader().setReorderingAllowed(false);

        // Column widths
        patronTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        patronTable.getColumnModel().getColumn(1).setPreferredWidth(150);  // Name
        patronTable.getColumnModel().getColumn(2).setPreferredWidth(200);  // Email
        patronTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // Phone
        patronTable.getColumnModel().getColumn(4).setPreferredWidth(200);  // Address
        patronTable.getColumnModel().getColumn(5).setPreferredWidth(120);  // Registration Date

        // Set a row sorter to enable filtering
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        patronTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(patronTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Action panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Patron");
        JButton editButton = new JButton("Edit Patron");
        JButton deleteButton = new JButton("Delete Patron");
        JButton viewLoansButton = new JButton("View Loans");

        // Adding action listeners for action buttons
        addButton.addActionListener(e -> addPatron());
        editButton.addActionListener(e -> editPatron());
        deleteButton.addActionListener(e -> deletePatron());
        viewLoansButton.addActionListener(e -> viewPatronLoans());

        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(viewLoansButton);

        // Add components to panel
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        // Load data
        refreshData();
    }

    // Method to refresh the table data
    public void refreshData() {
        tableModel.setRowCount(0); // Clear existing data
        List<Patron> patrons = patronDAO.getAllPatrons(); // Fetch all patrons

        // Add each patron to the table model
        for (Patron patron : patrons) {
            Object[] rowData = {
                    patron.getId(),
                    patron.getName(),
                    patron.getEmail(),
                    patron.getPhone(),
                    patron.getAddress(),
                    patron.getRegistrationDate()
            };
            tableModel.addRow(rowData);
        }
    }

    // Method to search patrons based on the search term
    private void searchPatrons(ActionEvent e) {
        String searchTerm = searchField.getText().trim();
        if (!searchTerm.isEmpty()) {
            tableModel.setRowCount(0); // Clear existing data
            List<Patron> patrons = patronDAO.searchPatrons(searchTerm); // Search patrons

            // Add each found patron to the table model
            for (Patron patron : patrons) {
                Object[] rowData = {
                        patron.getId(),
                        patron.getName(),
                        patron.getEmail(),
                        patron.getPhone(),
                        patron.getAddress(),
                        patron.getRegistrationDate()
                };
                tableModel.addRow(rowData);
            }
        }
    }

    // Method to add a new patron
    private void addPatron() {
        PatronDialog dialog = new PatronDialog(parentFrame, null);
        dialog.setVisible(true);

        if (dialog.getPatron() != null) {
            Patron newPatron = dialog.getPatron();
            int id = patronDAO.addPatron(newPatron); // Add patron to database

            if (id > 0) {
                newPatron.setId(id);
                refreshData(); // Refresh table data
            }
        }
    }

    // Method to edit the selected patron
    private void editPatron() {
        int selectedRow = patronTable.getSelectedRow();
        if (selectedRow >= 0) {
            int patronId = (int) patronTable.getValueAt(patronTable.convertRowIndexToModel(selectedRow), 0);
            Patron patron = patronDAO.getPatronById(patronId);

            if (patron != null) {
                PatronDialog dialog = new PatronDialog(parentFrame, patron);
                dialog.setVisible(true);

                if (dialog.getPatron() != null) {
                    patronDAO.updatePatron(dialog.getPatron()); // Update patron in database
                    refreshData(); // Refresh table data
                }
            }
        } else {
            JOptionPane.showMessageDialog(parentFrame,
                    "Please select a patron to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Method to delete the selected patron
    private void deletePatron() {
        int selectedRow = patronTable.getSelectedRow();
        if (selectedRow >= 0) {
            int patronId = (int) patronTable.getValueAt(patronTable.convertRowIndexToModel(selectedRow), 0);
            String patronName = (String) patronTable.getValueAt(patronTable.convertRowIndexToModel(selectedRow), 1);

            int confirm = JOptionPane.showConfirmDialog(parentFrame,
                    "Are you sure you want to delete patron: " + patronName + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = patronDAO.deletePatron(patronId); // Delete patron from database
                if (success) {
                    refreshData(); // Refresh table data
                } else {
                    JOptionPane.showMessageDialog(parentFrame,
                            "Could not delete patron. They may have active loans.",
                            "Delete Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(parentFrame,
                    "Please select a patron to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Method to view loans of the selected patron
    private void viewPatronLoans() {
        int selectedRow = patronTable.getSelectedRow();
        if (selectedRow >= 0) {
            int patronId = (int) patronTable.getValueAt(patronTable.convertRowIndexToModel(selectedRow), 0);
            Patron patron = patronDAO.getPatronById(patronId);

            if (patron != null) {
                PatronLoansDialog dialog = new PatronLoansDialog(parentFrame, patron);
                dialog.setVisible(true);

                // Refresh the loans panel after viewing/returning loans
                if (parentFrame instanceof MainFrame) {
                    ((MainFrame) parentFrame).refreshPanels();
                }
            }
        } else {
            JOptionPane.showMessageDialog(parentFrame,
                    "Please select a patron to view loans.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
}

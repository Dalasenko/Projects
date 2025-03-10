package ui;

import model.Loan;
import database.LoanDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class LoanPanel extends JPanel {

    private JFrame parentFrame;
    private LoanDAO loanDAO;
    private JTable loanTable;
    private DefaultTableModel tableModel;
    private boolean overdueOnly;

    // Constructor to initialize the LoanPanel
    public LoanPanel(JFrame parentFrame, boolean overdueOnly) {
        this.parentFrame = parentFrame;
        this.loanDAO = new LoanDAO();
        this.overdueOnly = overdueOnly;

        // Set layout and border for the panel
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Status label to indicate whether showing overdue loans or all loans
        JLabel statusLabel = new JLabel(overdueOnly ? "Overdue Loans" : "All Loans");
        statusLabel.setFont(new Font(statusLabel.getFont().getName(), Font.BOLD, 14));

        // Table setup with column names
        String[] columnNames = {"ID", "Book", "Patron", "Loan Date", "Due Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Cells are not editable
            }
        };

        loanTable = new JTable(tableModel);
        loanTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loanTable.getTableHeader().setReorderingAllowed(false);

        // Set preferred column widths
        loanTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        loanTable.getColumnModel().getColumn(1).setPreferredWidth(200);  // Book
        loanTable.getColumnModel().getColumn(2).setPreferredWidth(150);  // Patron
        loanTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // Loan Date
        loanTable.getColumnModel().getColumn(4).setPreferredWidth(100);  // Due Date
        loanTable.getColumnModel().getColumn(5).setPreferredWidth(150);  // Status

        JScrollPane scrollPane = new JScrollPane(loanTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Action panel with buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Refresh");
        JButton returnButton = new JButton("Return Book");

        if (!overdueOnly) {
            JButton showActiveButton = new JButton("Active Loans Only");
            JButton showAllButton = new JButton("All Loans");

            showActiveButton.addActionListener(e -> loadActiveLoans());
            showAllButton.addActionListener(e -> loadAllLoans());

            actionPanel.add(showActiveButton);
            actionPanel.add(showAllButton);
        }

        refreshButton.addActionListener(e -> refreshData());
        returnButton.addActionListener(e -> returnBook());

        actionPanel.add(refreshButton);
        actionPanel.add(returnButton);

        // Add components to panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.add(statusLabel);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        // Load data into the table
        refreshData();
    }

    // Method to refresh the data in the table
    public void refreshData() {
        tableModel.setRowCount(0); // Clear existing rows

        List<Loan> loans;
        if (overdueOnly) {
            loans = loanDAO.getOverdueLoans(); // Get overdue loans if overdueOnly is true
        } else {
            loans = loanDAO.getActiveLoans(); // Default to active loans
        }

        // Populate the table with loan data
        for (Loan loan : loans) {
            String status;
            if (loan.isReturned()) {
                status = "Returned on " + loan.getReturnDate();
            } else if (loan.isOverdue()) {
                status = "Overdue";
            } else {
                status = "Active";
            }

            Object[] rowData = {
                    loan.getId(),
                    loan.getBookTitle(),
                    loan.getPatronName(),
                    loan.getLoanDate(),
                    loan.getDueDate(),
                    status
            };
            tableModel.addRow(rowData);
        }
    }

    // Method to load all loans into the table
    private void loadAllLoans() {
        if (!overdueOnly) {
            tableModel.setRowCount(0); // Clear existing rows
            List<Loan> loans = loanDAO.getAllLoans();

            // Populate the table with all loan data
            for (Loan loan : loans) {
                String status;
                if (loan.isReturned()) {
                    status = "Returned on " + loan.getReturnDate();
                } else if (loan.isOverdue()) {
                    status = "Overdue";
                } else {
                    status = "Active";
                }

                Object[] rowData = {
                        loan.getId(),
                        loan.getBookTitle(),
                        loan.getPatronName(),
                        loan.getLoanDate(),
                        loan.getDueDate(),
                        status
                };
                tableModel.addRow(rowData);
            }
        }
    }

    // Method to load active loans into the table
    private void loadActiveLoans() {
        if (!overdueOnly) {
            tableModel.setRowCount(0); // Clear existing rows
            List<Loan> loans = loanDAO.getActiveLoans();

            // Populate the table with active loan data
            for (Loan loan : loans) {
                String status;
                if (loan.isOverdue()) {
                    status = "Overdue";
                } else {
                    status = "Active";
                }

                Object[] rowData = {
                        loan.getId(),
                        loan.getBookTitle(),
                        loan.getPatronName(),
                        loan.getLoanDate(),
                        loan.getDueDate(),
                        status
                };
                tableModel.addRow(rowData);
            }
        }
    }

    // Method to handle the return of a book
    private void returnBook() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow >= 0) {
            int loanId = (int) loanTable.getValueAt(selectedRow, 0);
            String bookTitle = (String) loanTable.getValueAt(selectedRow, 1);
            String status = (String) loanTable.getValueAt(selectedRow, 5);

            if (status.startsWith("Returned")) {
                JOptionPane.showMessageDialog(parentFrame,
                        "This book has already been returned.",
                        "Already Returned", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(parentFrame,
                    "Confirm return of book: " + bookTitle,
                    "Return Book", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = loanDAO.returnBook(loanId);
                if (success) {
                    refreshData();
                    // Also refresh other panels if needed
                    if (parentFrame instanceof MainFrame) {
                        ((MainFrame) parentFrame).refreshPanels();
                    }
                } else {
                    JOptionPane.showMessageDialog(parentFrame,
                            "Error returning book.",
                            "Return Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(parentFrame,
                    "Please select a loan to process return.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
}

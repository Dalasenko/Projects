package ui;

import model.Loan;
import model.Patron;
import database.LoanDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatronLoansDialog extends JDialog {

    private Patron patron;
    private LoanDAO loanDAO = new LoanDAO();

    private JTable loanTable;
    private DefaultTableModel tableModel;

    public PatronLoansDialog(JFrame parent, Patron patron) {
        super(parent, "Loans for " + patron.getName(), true);
        this.patron = patron;

        // Initialize and configure the dialog
        setSize(700, 500);
        setLocationRelativeTo(parent);

        setupUI();
    }

    private void setupUI() {
        // Patron info
        JLabel patronInfoLabel = new JLabel("Loans for: " + patron.getName() + " (" + patron.getEmail() + ")");
        patronInfoLabel.setFont(new Font(patronInfoLabel.getFont().getName(), Font.BOLD, 14));

        // Table
        String[] columnNames = {"ID", "Book", "Loan Date", "Due Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        loanTable = new JTable(tableModel);
        loanTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loanTable.getTableHeader().setReorderingAllowed(false);

        // Column widths
        loanTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        loanTable.getColumnModel().getColumn(1).setPreferredWidth(250);  // Book
        loanTable.getColumnModel().getColumn(2).setPreferredWidth(100);  // Loan Date
        loanTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // Due Date
        loanTable.getColumnModel().getColumn(4).setPreferredWidth(150);  // Status

        JScrollPane scrollPane = new JScrollPane(loanTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton returnButton = new JButton("Return Book");
        JButton closeButton = new JButton("Close");

        returnButton.addActionListener(e -> returnBook());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(returnButton);
        buttonPanel.add(closeButton);

        // Load the loans
        loadLoans();

        // Add components to dialog
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(patronInfoLabel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(contentPanel);
    }

    private void loadLoans() {
        tableModel.setRowCount(0);
        List<Loan> loans = loanDAO.getLoansByPatron(patron.getId());

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
                    loan.getLoanDate(),
                    loan.getDueDate(),
                    status
            };
            tableModel.addRow(rowData);
        }
    }

    private void returnBook() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow >= 0) {
            int loanId = (int) loanTable.getValueAt(selectedRow, 0);
            String bookTitle = (String) loanTable.getValueAt(selectedRow, 1);
            String status = (String) loanTable.getValueAt(selectedRow, 4);

            if (status.startsWith("Returned")) {
                JOptionPane.showMessageDialog(this,
                        "This book has already been returned.",
                        "Already Returned", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Confirm return of book: " + bookTitle,
                    "Return Book", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = loanDAO.returnBook(loanId);
                if (success) {
                    loadLoans();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error returning book.",
                            "Return Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please select a loan to process return.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
}
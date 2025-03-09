package ui;

import model.Book;
import database.BookDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class BookPanel extends JPanel {

    private JFrame parentFrame;
    private BookDAO bookDAO;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public BookPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.bookDAO = new BookDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");

        searchButton.addActionListener(this::searchBooks);
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
        String[] columnNames = {"ID", "ISBN", "Title", "Author", "Publisher", "Genre", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setAutoCreateRowSorter(true);
        bookTable.getTableHeader().setReorderingAllowed(false);

        // Column widths
        bookTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        bookTable.getColumnModel().getColumn(1).setPreferredWidth(100); // ISBN
        bookTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Title
        bookTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Author
        bookTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Publisher
        bookTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Genre
        bookTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Status

        // Set a row sorter to enable filtering
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        bookTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Action panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Book");
        JButton editButton = new JButton("Edit Book");
        JButton deleteButton = new JButton("Delete Book");
        JButton checkoutButton = new JButton("Checkout Book");

        addButton.addActionListener(e -> addBook());
        editButton.addActionListener(e -> editBook());
        deleteButton.addActionListener(e -> deleteBook());
        checkoutButton.addActionListener(e -> checkoutBook());

        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(checkoutButton);

        // Add components to panel
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        // Load data
        refreshData();
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        List<Book> books = bookDAO.getAllBooks();

        for (Book book : books) {
            Object[] rowData = {
                    book.getId(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getGenre(),
                    book.isAvailable() ? "Available" : "Checked Out"
            };
            tableModel.addRow(rowData);
        }
    }

    private void searchBooks(ActionEvent e) {
        String searchTerm = searchField.getText().trim();
        if (!searchTerm.isEmpty()) {
            tableModel.setRowCount(0);
            List<Book> books = bookDAO.searchBooks(searchTerm);

            for (Book book : books) {
                Object[] rowData = {
                        book.getId(),
                        book.getIsbn(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getPublisher(),
                        book.getGenre(),
                        book.isAvailable() ? "Available" : "Checked Out"
                };
                tableModel.addRow(rowData);
            }
        }
    }

    private void addBook() {
        BookDialog dialog = new BookDialog(parentFrame, null);
        dialog.setVisible(true);

        if (dialog.getBook() != null) {
            Book newBook = dialog.getBook();
            int id = bookDAO.addBook(newBook);

            if (id > 0) {
                newBook.setId(id);
                refreshData();
            }
        }
    }

    private void editBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow >= 0) {
            int bookId = (int) bookTable.getValueAt(bookTable.convertRowIndexToModel(selectedRow), 0);
            Book book = bookDAO.getBookById(bookId);

            if (book != null) {
                BookDialog dialog = new BookDialog(parentFrame, book);
                dialog.setVisible(true);

                if (dialog.getBook() != null) {
                    bookDAO.updateBook(dialog.getBook());
                    refreshData();
                }
            }
        } else {
            JOptionPane.showMessageDialog(parentFrame,
                    "Please select a book to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow >= 0) {
            int bookId = (int) bookTable.getValueAt(bookTable.convertRowIndexToModel(selectedRow), 0);
            String bookTitle = (String) bookTable.getValueAt(bookTable.convertRowIndexToModel(selectedRow), 2);

            int confirm = JOptionPane.showConfirmDialog(parentFrame,
                    "Are you sure you want to delete book: " + bookTitle + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = bookDAO.deleteBook(bookId);
                if (success) {
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(parentFrame,
                            "Could not delete book. It may be checked out.",
                            "Delete Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(parentFrame,
                    "Please select a book to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void checkoutBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow >= 0) {
            int bookId = (int) bookTable.getValueAt(bookTable.convertRowIndexToModel(selectedRow), 0);
            Book book = bookDAO.getBookById(bookId);

            if (book != null) {
                if (!book.isAvailable()) {
                    JOptionPane.showMessageDialog(parentFrame,
                            "This book is already checked out.",
                            "Book Unavailable", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                CheckoutDialog dialog = new CheckoutDialog(parentFrame, bookId);
                dialog.setVisible(true);

                if (dialog.isConfirmed()) {
                    refreshData();
                    // Also refresh the loans panel if needed
                    if (parentFrame instanceof MainFrame) {
                        ((MainFrame) parentFrame).refreshPanels();
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(parentFrame,
                    "Please select a book to checkout.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
}

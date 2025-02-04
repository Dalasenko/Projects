package com.example.library;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * The LibraryUI class creates the graphical user interface for managing the library.
 * It includes tabs for managing books and borrowers.
 *
 * This updated version makes the ID fields editable. In the Add operations, if a user
 * enters a value in the ID field, that value is used (via overloaded methods in Library);
 * otherwise, the auto-generated ID is used.
 */
public class LibraryUI extends JFrame {

    // Fields for the Books tab.
    private JTextField txtBookId, txtBookTitle, txtBookAuthor, txtBookPublisher, txtBookSearch;
    private JTable bookTable;
    private DefaultTableModel bookTableModel;

    // Fields for the Borrowers tab.
    private JTextField txtBorrowerId, txtBorrowerName, txtBorrowerEmail, txtBorrowerSearch;
    private JTable borrowerTable;
    private DefaultTableModel borrowerTableModel;

    // Reference to the Library model.
    private Library library;

    /**
     * Constructor.
     * @param library the Library model instance.
     */
    public LibraryUI(Library library) {
        this.library = library;
        FlatLightLaf.setup();
        customizeDefaults();
        initComponents();
    }

    /**
     * Sets UIManager properties to configure fonts and colors.
     */
    private void customizeDefaults() {
        Font uiFontPlain = new Font("Segoe UI", Font.PLAIN, 16);
        Font uiFontBold = new Font("Segoe UI", Font.BOLD, 18);

        UIManager.put("Label.font", uiFontPlain);
        UIManager.put("Button.font", uiFontBold);
        UIManager.put("TextField.font", uiFontPlain);
        UIManager.put("Table.font", uiFontPlain);
        UIManager.put("TableHeader.font", uiFontBold);
        UIManager.put("OptionPane.messageFont", uiFontPlain);

        UIManager.put("Panel.background", new Color(245, 245, 245));
        UIManager.put("TextField.background", new Color(255, 255, 255));
        UIManager.put("TextField.foreground", Color.BLACK);
        UIManager.put("Table.background", new Color(255, 255, 255));
        UIManager.put("Table.foreground", Color.BLACK);
        UIManager.put("Table.gridColor", new Color(220, 220, 220));
        UIManager.put("TableHeader.background", new Color(240, 240, 240));
        UIManager.put("TableHeader.foreground", Color.BLACK);
    }

    /**
     * Initializes and arranges UI components.
     */
    private void initComponents() {
        setTitle("Library Information System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        ((JComponent)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(245, 245, 245));
        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(Color.DARK_GRAY);
        JMenuItem miSave = new JMenuItem("Save");
        JMenuItem miLoad = new JMenuItem("Load");
        fileMenu.add(miSave);
        fileMenu.add(miLoad);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        miSave.addActionListener(e -> {
            LibraryPersistence.saveLibrary(library, "library.dat");
            JOptionPane.showMessageDialog(this, "Library saved successfully.", "Save", JOptionPane.INFORMATION_MESSAGE);
        });
        miLoad.addActionListener(e -> {
            library = LibraryPersistence.loadLibrary("library.dat");
            refreshBooksTable(library.getBooks());
            refreshBorrowersTable(library.getBorrowers());
            JOptionPane.showMessageDialog(this, "Library loaded successfully.", "Load", JOptionPane.INFORMATION_MESSAGE);
        });

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Books", createBooksPanel());
        tabbedPane.addTab("Borrowers", createBorrowersPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Constructs the Books tab panel.
     * @return the Books panel.
     */
    private JPanel createBooksPanel() {
        JPanel booksPanel = new JPanel(new BorderLayout(10, 10));
        booksPanel.setBackground(new Color(245, 245, 245));
        TitledBorder border = BorderFactory.createTitledBorder(
                new LineBorder(new Color(220, 220, 220), 2), "Books Management",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 18), Color.DARK_GRAY);
        booksPanel.setBorder(border);

        // Form panel.
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 245));
        TitledBorder formBorder = BorderFactory.createTitledBorder("Book Details");
        formBorder.setTitleColor(Color.DARK_GRAY);
        formPanel.setBorder(formBorder);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(16, 16, 16, 16);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: ID (now editable) and Title.
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        txtBookId = new JTextField(12);
        // Make the ID field editable.
        txtBookId.setEditable(true);
        formPanel.add(txtBookId, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 3;
        txtBookTitle = new JTextField(25);
        formPanel.add(txtBookTitle, gbc);

        // Row 2: Author and Publisher.
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1;
        txtBookAuthor = new JTextField(20);
        formPanel.add(txtBookAuthor, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Publisher:"), gbc);
        gbc.gridx = 3;
        txtBookPublisher = new JTextField(20);
        formPanel.add(txtBookPublisher, gbc);

        // Row 3: Search field and button.
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Search:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        txtBookSearch = new JTextField(30);
        formPanel.add(txtBookSearch, gbc);
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        JButton btnSearch = new JButton("Search");
        customizeButton(btnSearch);
        formPanel.add(btnSearch, gbc);

        // Buttons panel.
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 16));
        buttonsPanel.setBackground(new Color(245, 245, 245));
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        customizeButton(btnAdd);
        customizeButton(btnUpdate);
        customizeButton(btnDelete);
        buttonsPanel.add(btnAdd);
        buttonsPanel.add(btnUpdate);
        buttonsPanel.add(btnDelete);

        // Table panel.
        bookTableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Author", "Publisher"}, 0);
        bookTable = new JTable(bookTableModel);
        styleTable(bookTable);
        JScrollPane tableScrollPane = new JScrollPane(bookTable);
        tableScrollPane.setPreferredSize(new Dimension(1000, 350));
        tableScrollPane.getViewport().setBackground(new Color(255, 255, 255));
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        // Assemble Books panel.
        booksPanel.add(formPanel, BorderLayout.NORTH);
        booksPanel.add(buttonsPanel, BorderLayout.CENTER);
        booksPanel.add(tableScrollPane, BorderLayout.SOUTH);

        // Listeners.
        btnAdd.addActionListener(e -> {
            String idText = txtBookId.getText().trim();
            String title = txtBookTitle.getText().trim();
            String author = txtBookAuthor.getText().trim();
            String publisher = txtBookPublisher.getText().trim();
            if (title.isEmpty() || author.isEmpty() || publisher.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all book fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Book newBook;
            if (!idText.isEmpty()) {
                try {
                    int id = Integer.parseInt(idText);
                    newBook = library.addBook(id, title, author, publisher);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a valid integer.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else {
                newBook = library.addBook(title, author, publisher);
            }
            refreshBooksTable(library.getBooks());
            clearBookFields();
            JOptionPane.showMessageDialog(this, "Book added with ID: " + newBook.getId(), "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        btnUpdate.addActionListener(e -> {
            String idText = txtBookId.getText().trim();
            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select a book to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                int id = Integer.parseInt(idText);
                String title = txtBookTitle.getText().trim();
                String author = txtBookAuthor.getText().trim();
                String publisher = txtBookPublisher.getText().trim();
                if (library.updateBook(id, title, author, publisher)) {
                    refreshBooksTable(library.getBooks());
                    clearBookFields();
                    JOptionPane.showMessageDialog(this, "Book updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID format.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnDelete.addActionListener(e -> {
            String idText = txtBookId.getText().trim();
            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select a book to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                int id = Integer.parseInt(idText);
                if (library.removeBook(id)) {
                    refreshBooksTable(library.getBooks());
                    clearBookFields();
                    JOptionPane.showMessageDialog(this, "Book deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Cannot delete a book that is on loan.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID format.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnSearch.addActionListener(e -> {
            String keyword = txtBookSearch.getText().trim();
            if (keyword.isEmpty()) {
                refreshBooksTable(library.getBooks());
            } else {
                refreshBooksTable(library.searchBooks(keyword));
            }
        });

        bookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = bookTable.getSelectedRow();
                if (row != -1) {
                    txtBookId.setText(bookTableModel.getValueAt(row, 0).toString());
                    txtBookTitle.setText(bookTableModel.getValueAt(row, 1).toString());
                    txtBookAuthor.setText(bookTableModel.getValueAt(row, 2).toString());
                    txtBookPublisher.setText(bookTableModel.getValueAt(row, 3).toString());
                }
            }
        });

        return booksPanel;
    }

    /**
     * Constructs the Borrowers tab panel.
     * @return the Borrowers panel.
     */
    private JPanel createBorrowersPanel() {
        JPanel borrowersPanel = new JPanel(new BorderLayout(10, 10));
        borrowersPanel.setBackground(new Color(245, 245, 245));
        TitledBorder border = BorderFactory.createTitledBorder(
                new LineBorder(new Color(220, 220, 220), 2), "Borrowers Management",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 18), Color.DARK_GRAY);
        borrowersPanel.setBorder(border);

        // Form panel.
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 245));
        TitledBorder formBorder = BorderFactory.createTitledBorder("Borrower Details");
        formBorder.setTitleColor(Color.DARK_GRAY);
        formPanel.setBorder(formBorder);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(16, 16, 16, 16);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: ID (editable) and Name.
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        txtBorrowerId = new JTextField(12);
        txtBorrowerId.setEditable(true);
        formPanel.add(txtBorrowerId, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 3;
        txtBorrowerName = new JTextField(25);
        formPanel.add(txtBorrowerName, gbc);

        // Row 2: Email.
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtBorrowerEmail = new JTextField(30);
        formPanel.add(txtBorrowerEmail, gbc);

        // Row 3: Search field and button.
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Search:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        txtBorrowerSearch = new JTextField(30);
        formPanel.add(txtBorrowerSearch, gbc);
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        JButton btnSearchBorrower = new JButton("Search");
        customizeButton(btnSearchBorrower);
        formPanel.add(btnSearchBorrower, gbc);

        // Buttons panel.
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 16));
        btnPanel.setBackground(new Color(245, 245, 245));
        JButton btnAddBorrower = new JButton("Add");
        JButton btnUpdateBorrower = new JButton("Update");
        JButton btnDeleteBorrower = new JButton("Delete");
        customizeButton(btnAddBorrower);
        customizeButton(btnUpdateBorrower);
        customizeButton(btnDeleteBorrower);
        btnPanel.add(btnAddBorrower);
        btnPanel.add(btnUpdateBorrower);
        btnPanel.add(btnDeleteBorrower);

        // Table panel.
        borrowerTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Email"}, 0);
        borrowerTable = new JTable(borrowerTableModel);
        styleTable(borrowerTable);
        JScrollPane tableScrollPane = new JScrollPane(borrowerTable);
        tableScrollPane.setPreferredSize(new Dimension(1000, 300));
        tableScrollPane.getViewport().setBackground(new Color(255, 255, 255));
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        // Assemble Borrowers panel.
        borrowersPanel.add(formPanel, BorderLayout.NORTH);
        borrowersPanel.add(btnPanel, BorderLayout.CENTER);
        borrowersPanel.add(tableScrollPane, BorderLayout.SOUTH);

        // Listeners.
        btnAddBorrower.addActionListener(e -> {
            String idText = txtBorrowerId.getText().trim();
            String name = txtBorrowerName.getText().trim();
            String email = txtBorrowerEmail.getText().trim();
            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all borrower fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Borrower newBorrower;
            if (!idText.isEmpty()) {
                try {
                    int id = Integer.parseInt(idText);
                    newBorrower = library.addBorrower(id, name, email);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a valid integer.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else {
                newBorrower = library.addBorrower(name, email);
            }
            refreshBorrowersTable(library.getBorrowers());
            clearBorrowerFields();
            JOptionPane.showMessageDialog(this, "Borrower added with ID: " + newBorrower.getId(), "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        btnUpdateBorrower.addActionListener(e -> {
            String idText = txtBorrowerId.getText().trim();
            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select a borrower to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                int id = Integer.parseInt(idText);
                String name = txtBorrowerName.getText().trim();
                String email = txtBorrowerEmail.getText().trim();
                if (library.updateBorrower(id, name, email)) {
                    refreshBorrowersTable(library.getBorrowers());
                    clearBorrowerFields();
                    JOptionPane.showMessageDialog(this, "Borrower updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID format.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnDeleteBorrower.addActionListener(e -> {
            String idText = txtBorrowerId.getText().trim();
            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select a borrower to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                int id = Integer.parseInt(idText);
                if (library.removeBorrower(id)) {
                    refreshBorrowersTable(library.getBorrowers());
                    clearBorrowerFields();
                    JOptionPane.showMessageDialog(this, "Borrower deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID format.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnSearchBorrower.addActionListener(e -> {
            String keyword = txtBorrowerSearch.getText().trim();
            if (keyword.isEmpty()) {
                refreshBorrowersTable(library.getBorrowers());
            } else {
                refreshBorrowersTable(library.searchBorrowers(keyword));
            }
        });

        borrowerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = borrowerTable.getSelectedRow();
                if (row != -1) {
                    txtBorrowerId.setText(borrowerTableModel.getValueAt(row, 0).toString());
                    txtBorrowerName.setText(borrowerTableModel.getValueAt(row, 1).toString());
                    txtBorrowerEmail.setText(borrowerTableModel.getValueAt(row, 2).toString());
                }
            }
        });

        return borrowersPanel;
    }

    // ---------------------------
    // Utility methods to clear and refresh fields.
    // ---------------------------
    private void clearBookFields() {
        txtBookId.setText("");
        txtBookTitle.setText("");
        txtBookAuthor.setText("");
        txtBookPublisher.setText("");
        txtBookSearch.setText("");
    }

    private void clearBorrowerFields() {
        txtBorrowerId.setText("");
        txtBorrowerName.setText("");
        txtBorrowerEmail.setText("");
        txtBorrowerSearch.setText("");
    }

    private void refreshBooksTable(List<Book> books) {
        bookTableModel.setRowCount(0);
        for (Book b : books) {
            bookTableModel.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getPublisher()});
        }
    }

    private void refreshBorrowersTable(List<Borrower> borrowers) {
        borrowerTableModel.setRowCount(0);
        for (Borrower br : borrowers) {
            borrowerTableModel.addRow(new Object[]{br.getId(), br.getName(), br.getEmail()});
        }
    }

    // ---------------------------
    // customizeButton: Applies a consistent style to buttons.
    // ---------------------------
    private void customizeButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ---------------------------
    // styleTable: Applies a consistent style to JTable components.
    // ---------------------------
    private void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setBackground(new Color(255, 255, 255));
        table.setForeground(Color.BLACK);
        table.setRowHeight(30);
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }





    // Additional UI helper methods can be added here.




















}
















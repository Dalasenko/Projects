package com.example.library;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
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

    // Constants for UI customization
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color FOREGROUND_COLOR = Color.BLACK;
    private static final Color BUTTON_COLOR = new Color(100, 149, 237);
    private static final Color BORDER_COLOR = new Color(220, 220, 220);
    private static final Font FONT_PLAIN = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 18);

    // Fields for the Books tab
    private JTextField txtBookId, txtBookTitle, txtBookAuthor, txtBookPublisher, txtBookSearch;
    private JTable bookTable;
    private DefaultTableModel bookTableModel;
    private TableRowSorter<DefaultTableModel> bookSorter;

    // Fields for the Borrowers tab
    private JTextField txtBorrowerId, txtBorrowerName, txtBorrowerEmail, txtBorrowerSearch;
    private JTable borrowerTable;
    private DefaultTableModel borrowerTableModel;
    private TableRowSorter<DefaultTableModel> borrowerSorter;

    // Reference to the Library model
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
        UIManager.put("Label.font", FONT_PLAIN);
        UIManager.put("Button.font", FONT_BOLD);
        UIManager.put("TextField.font", FONT_PLAIN);
        UIManager.put("Table.font", FONT_PLAIN);
        UIManager.put("TableHeader.font", FONT_BOLD);
        UIManager.put("OptionPane.messageFont", FONT_PLAIN);

        UIManager.put("Panel.background", BACKGROUND_COLOR);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", FOREGROUND_COLOR);
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.foreground", FOREGROUND_COLOR);
        UIManager.put("Table.gridColor", BORDER_COLOR);
        UIManager.put("TableHeader.background", new Color(240, 240, 240));
        UIManager.put("TableHeader.foreground", FOREGROUND_COLOR);
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

        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Books", createBooksPanel());
        tabbedPane.addTab("Borrowers", createBorrowersPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(BACKGROUND_COLOR);
        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(Color.DARK_GRAY);
        JMenuItem miSave = new JMenuItem("Save");
        JMenuItem miLoad = new JMenuItem("Load");
        fileMenu.add(miSave);
        fileMenu.add(miLoad);
        menuBar.add(fileMenu);

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
        return menuBar;
    }

    /**
     * Constructs the Books tab panel.
     * @return the Books panel.
     */
    private JPanel createBooksPanel() {
        JPanel booksPanel = new JPanel(new BorderLayout(10, 10));
        booksPanel.setBackground(BACKGROUND_COLOR);
        booksPanel.setBorder(createTitledBorder("Books Management"));

        JPanel formPanel = createBooksFormPanel();
        JPanel buttonsPanel = createBooksButtonsPanel();
        JScrollPane tableScrollPane = createBooksTableScrollPane();

        booksPanel.add(formPanel, BorderLayout.NORTH);
        booksPanel.add(buttonsPanel, BorderLayout.CENTER);
        booksPanel.add(tableScrollPane, BorderLayout.SOUTH);

        return booksPanel;
    }

    private JPanel createBooksFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(createTitledBorder("Book Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(16, 16, 16, 16);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: ID (now editable) and Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        txtBookId = new JTextField(12);
        txtBookId.setEditable(true);
        txtBookId.setToolTipText("Enter the book ID (leave blank for auto-generated ID)");
        formPanel.add(txtBookId, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 3;
        txtBookTitle = new JTextField(25);
        txtBookTitle.setToolTipText("Enter the book title");
        formPanel.add(txtBookTitle, gbc);

        // Row 2: Author and Publisher
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1;
        txtBookAuthor = new JTextField(20);
        txtBookAuthor.setToolTipText("Enter the author's name");
        formPanel.add(txtBookAuthor, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Publisher:"), gbc);
        gbc.gridx = 3;
        txtBookPublisher = new JTextField(20);
        txtBookPublisher.setToolTipText("Enter the publisher's name");
        formPanel.add(txtBookPublisher, gbc);

        // Row 3: Search field and button
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Search:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        txtBookSearch = new JTextField(30);
        txtBookSearch.setToolTipText("Enter a keyword to search books");
        formPanel.add(txtBookSearch, gbc);
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        JButton btnSearch = new JButton("Search");
        customizeButton(btnSearch);
        formPanel.add(btnSearch, gbc);

        btnSearch.addActionListener(e -> searchBooks());
        return formPanel;
    }

    private JPanel createBooksButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 16));
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        customizeButton(btnAdd);
        customizeButton(btnUpdate);
        customizeButton(btnDelete);
        buttonsPanel.add(btnAdd);
        buttonsPanel.add(btnUpdate);
        buttonsPanel.add(btnDelete);

        btnAdd.addActionListener(e -> addBook());
        btnUpdate.addActionListener(e -> updateBook());
        btnDelete.addActionListener(e -> deleteBook());
        return buttonsPanel;
    }

    private JScrollPane createBooksTableScrollPane() {
        bookTableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Author", "Publisher"}, 0);
        bookTable = new JTable(bookTableModel);
        styleTable(bookTable);
        bookSorter = new TableRowSorter<>(bookTableModel);
        bookTable.setRowSorter(bookSorter);
        JScrollPane tableScrollPane = new JScrollPane(bookTable);
        tableScrollPane.setPreferredSize(new Dimension(1000, 350));
        tableScrollPane.getViewport().setBackground(Color.WHITE);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

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
        return tableScrollPane;
    }

    private JPanel createBorrowersPanel() {
        JPanel borrowersPanel = new JPanel(new BorderLayout(10, 10));
        borrowersPanel.setBackground(BACKGROUND_COLOR);
        borrowersPanel.setBorder(createTitledBorder("Borrowers Management"));

        JPanel formPanel = createBorrowersFormPanel();
        JPanel buttonsPanel = createBorrowersButtonsPanel();
        JScrollPane tableScrollPane = createBorrowersTableScrollPane();

        borrowersPanel.add(formPanel, BorderLayout.NORTH);
        borrowersPanel.add(buttonsPanel, BorderLayout.CENTER);
        borrowersPanel.add(tableScrollPane, BorderLayout.SOUTH);

        return borrowersPanel;
    }

    private JPanel createBorrowersFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(createTitledBorder("Borrower Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(16, 16, 16, 16);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: ID (editable) and Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        txtBorrowerId = new JTextField(12);
        txtBorrowerId.setEditable(true);
        txtBorrowerId.setToolTipText("Enter the borrower ID (leave blank for auto-generated ID)");
        formPanel.add(txtBorrowerId, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 3;
        txtBorrowerName = new JTextField(25);
        txtBorrowerName.setToolTipText("Enter the borrower name");
        formPanel.add(txtBorrowerName, gbc);

        // Row 2: Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtBorrowerEmail = new JTextField(30);
        txtBorrowerEmail.setToolTipText("Enter the borrower email");
        formPanel.add(txtBorrowerEmail, gbc);

        // Row 3: Search field and button
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Search:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        txtBorrowerSearch = new JTextField(30);
        txtBorrowerSearch.setToolTipText("Enter a keyword to search borrowers");
        formPanel.add(txtBorrowerSearch, gbc);
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        JButton btnSearchBorrower = new JButton("Search");
        customizeButton(btnSearchBorrower);
        formPanel.add(btnSearchBorrower, gbc);

        btnSearchBorrower.addActionListener(e -> searchBorrowers());
        return formPanel;
    }

    private JPanel createBorrowersButtonsPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 16));
        btnPanel.setBackground(BACKGROUND_COLOR);
        JButton btnAddBorrower = new JButton("Add");
        JButton btnUpdateBorrower = new JButton("Update");
        JButton btnDeleteBorrower = new JButton("Delete");
        customizeButton(btnAddBorrower);
        customizeButton(btnUpdateBorrower);
        customizeButton(btnDeleteBorrower);
        btnPanel.add(btnAddBorrower);
        btnPanel.add(btnUpdateBorrower);
        btnPanel.add(btnDeleteBorrower);

        btnAddBorrower.addActionListener(e -> addBorrower());
        btnUpdateBorrower.addActionListener(e -> updateBorrower());
        btnDeleteBorrower.addActionListener(e -> deleteBorrower());
        return btnPanel;
    }

    private JScrollPane createBorrowersTableScrollPane() {
        borrowerTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Email"}, 0);
        borrowerTable = new JTable(borrowerTableModel);
        styleTable(borrowerTable);
        borrowerSorter = new TableRowSorter<>(borrowerTableModel);
        borrowerTable.setRowSorter(borrowerSorter);
        JScrollPane tableScrollPane = new JScrollPane(borrowerTable);
        tableScrollPane.setPreferredSize(new Dimension(1000, 300));
        tableScrollPane.getViewport().setBackground(Color.WHITE);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

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
        return tableScrollPane;
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
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(FONT_BOLD);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ---------------------------
    // styleTable: Applies a consistent style to JTable components.
    // ---------------------------
    private void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);
        table.setForeground(FOREGROUND_COLOR);
        table.setRowHeight(30);
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setForeground(FOREGROUND_COLOR);
        table.getTableHeader().setFont(FONT_BOLD);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
                new LineBorder(BORDER_COLOR, 2), title,
                TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD, Color.DARK_GRAY);
    }

    // ---------------------------
    // ActionListener methods
    // ---------------------------
    private void addBook() {
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






















}
















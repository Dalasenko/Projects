package ui;

import model.Book;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class BookDialog extends JDialog {

    private Book book;
    private Book result = null;

    private JTextField isbnField;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField publisherField;
    private JTextField publicationDateField;
    private JTextField genreField;
    private JCheckBox availableCheckbox;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public BookDialog(JFrame parent, Book book) {
        super(parent, book != null ? "Edit Book" : "Add New Book", true);
        this.book = book != null ? book : new Book();

        // Initialize and configure the dialog
        setSize(400, 450);
        setLocationRelativeTo(parent);
        setResizable(false);

        setupUI();
    }

    private void setupUI() {
        // Main panel using GridBagLayout for form layout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ISBN
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("ISBN:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        isbnField = new JTextField(20);
        mainPanel.add(isbnField, gbc);

        // Title
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Title:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        titleField = new JTextField(20);
        mainPanel.add(titleField, gbc);

        // Author
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Author:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        authorField = new JTextField(20);
        mainPanel.add(authorField, gbc);

        // Publisher
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Publisher:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        publisherField = new JTextField(20);
        mainPanel.add(publisherField, gbc);

        // Publication Date
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Publication Date (yyyy-MM-dd):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        publicationDateField = new JTextField(10);
        mainPanel.add(publicationDateField, gbc);

        // Genre
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Genre:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        genreField = new JTextField(20);
        mainPanel.add(genreField, gbc);

        // Available
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Available:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        availableCheckbox = new JCheckBox();
        mainPanel.add(availableCheckbox, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            if (validateInput()) {
                saveBook();
                result = book;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Set existing values if editing
        if (book.getId() != 0) {
            isbnField.setText(book.getIsbn());
            titleField.setText(book.getTitle());
            authorField.setText(book.getAuthor());
            publisherField.setText(book.getPublisher());
            if (book.getPublicationDate() != null) {
                publicationDateField.setText(book.getPublicationDate().format(DATE_FORMATTER));
            }
            genreField.setText(book.getGenre());
            availableCheckbox.setSelected(book.isAvailable());
        } else {
            availableCheckbox.setSelected(true);
        }

        // Add panels to dialog
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(contentPanel);
    }

    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        if (titleField.getText().trim().isEmpty()) {
            errorMessage.append("Title is required.\n");
        }

        if (authorField.getText().trim().isEmpty()) {
            errorMessage.append("Author is required.\n");
        }

        if (!publicationDateField.getText().trim().isEmpty()) {
            try {
                LocalDate.parse(publicationDateField.getText().trim(), DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                errorMessage.append("Invalid date format. Use yyyy-MM-dd.\n");
            }
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(this,
                    errorMessage.toString(),
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void saveBook() {
        book.setIsbn(isbnField.getText().trim());
        book.setTitle(titleField.getText().trim());
        book.setAuthor(authorField.getText().trim());
        book.setPublisher(publisherField.getText().trim());

        String dateText = publicationDateField.getText().trim();
        if (!dateText.isEmpty()) {
            try {
                book.setPublicationDate(LocalDate.parse(dateText, DATE_FORMATTER));
            } catch (DateTimeParseException e) {
                // This should not happen due to validation
                book.setPublicationDate(null);
            }
        } else {
            book.setPublicationDate(null);
        }

        book.setGenre(genreField.getText().trim());
        book.setAvailable(availableCheckbox.isSelected());
    }

    public Book getBook() {
        return result;
    }
}

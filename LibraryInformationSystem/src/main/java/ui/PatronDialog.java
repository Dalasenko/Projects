package ui;

import model.Patron;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PatronDialog extends JDialog {

    private Patron patron;
    private Patron result = null;

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JTextField registrationDateField;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public PatronDialog(JFrame parent, Patron patron) {
        super(parent, patron != null ? "Edit Patron" : "Add New Patron", true);
        this.patron = patron != null ? patron : new Patron();

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

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        mainPanel.add(nameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        emailField = new JTextField(20);
        mainPanel.add(emailField, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Phone:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        phoneField = new JTextField(20);
        mainPanel.add(phoneField, gbc);

        // Address
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(new JLabel("Address:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        addressArea = new JTextArea(4, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        mainPanel.add(addressScroll, gbc);

        // Registration Date
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(new JLabel("Registration Date (yyyy-MM-dd):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        registrationDateField = new JTextField(10);
        mainPanel.add(registrationDateField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            if (validateInput()) {
                savePatron();
                result = patron;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Set existing values if editing
        if (patron.getId() != 0) {
            nameField.setText(patron.getName());
            emailField.setText(patron.getEmail());
            phoneField.setText(patron.getPhone());
            addressArea.setText(patron.getAddress());
            if (patron.getRegistrationDate() != null) {
                registrationDateField.setText(patron.getRegistrationDate().format(DATE_FORMATTER));
            }
        } else {
            registrationDateField.setText(LocalDate.now().format(DATE_FORMATTER));
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

        if (nameField.getText().trim().isEmpty()) {
            errorMessage.append("Name is required.\n");
        }

        if (emailField.getText().trim().isEmpty()) {
            errorMessage.append("Email is required.\n");
        }

        if (!registrationDateField.getText().trim().isEmpty()) {
            try {
                LocalDate.parse(registrationDateField.getText().trim(), DATE_FORMATTER);
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

    private void savePatron() {
        patron.setName(nameField.getText().trim());
        patron.setEmail(emailField.getText().trim());
        patron.setPhone(phoneField.getText().trim());
        patron.setAddress(addressArea.getText().trim());

        String dateText = registrationDateField.getText().trim();
        if (!dateText.isEmpty()) {
            try {
                patron.setRegistrationDate(LocalDate.parse(dateText, DATE_FORMATTER));
            } catch (DateTimeParseException e) {
                // This should not happen due to validation
                patron.setRegistrationDate(LocalDate.now());
            }
        } else {
            patron.setRegistrationDate(LocalDate.now());
        }
    }

    public Patron getPatron() {
        return result;
    }
}

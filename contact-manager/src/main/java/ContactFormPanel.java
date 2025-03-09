import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Form panel for entering and editing contact details
 */
public class ContactFormPanel extends JPanel {
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;

    private JButton saveButton;
    private JButton deleteButton;
    private JButton clearButton;

    private FormListener formListener;
    private Contact currentContact;

    public ContactFormPanel() {
        // Set border and size
        setBorder(BorderFactory.createTitledBorder("Contact Details"));
        setPreferredSize(new Dimension(300, 0));

        // Set layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Create form fields
        JLabel firstNameLabel = new JLabel("First Name:");
        JLabel lastNameLabel = new JLabel("Last Name:");
        JLabel emailLabel = new JLabel("Email:");
        JLabel phoneLabel = new JLabel("Phone:");

        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);

        // Add field components to panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(firstNameLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(firstNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(lastNameLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(lastNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(emailLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        add(phoneLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        add(phoneField, gbc);

        // Create buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        saveButton = new JButton("Save");
        deleteButton = new JButton("Delete");
        clearButton = new JButton("Clear");

        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.insets = new Insets(15, 5, 5, 5);
        add(buttonPanel, gbc);

        // Add action listeners
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateForm()) {
                    submitForm("save");
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentContact != null && currentContact.getId() > 0) {
                    submitForm("delete");
                } else {
                    JOptionPane.showMessageDialog(ContactFormPanel.this,
                            "No contact selected to delete.",
                            "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
                if (formListener != null) {
                    formListener.formEventOccurred(new FormEvent("clear", null));
                }
            }
        });
    }

    public void setFormListener(FormListener listener) {
        this.formListener = listener;
    }

    public void setContact(Contact contact) {
        this.currentContact = contact;

        // Populate form fields
        firstNameField.setText(contact.getFirstName());
        lastNameField.setText(contact.getLastName());
        emailField.setText(contact.getEmail());
        phoneField.setText(contact.getPhone());
    }

    public void clearForm() {
        currentContact = null;
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
    }

    private boolean validateForm() {
        // Check if required fields are filled
        if (firstNameField.getText().trim().isEmpty() || lastNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "First Name and Last Name are required.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void submitForm(String actionCommand) {
        // Create a contact from form data
        Contact contact;

        if (currentContact != null) {
            // Update existing contact
            contact = new Contact(
                    currentContact.getId(),
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim()
            );
        } else {
            // Create new contact
            contact = new Contact(
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim()
            );
        }

        // Notify listener
        if (formListener != null) {
            formListener.formEventOccurred(new FormEvent(actionCommand, contact));
        }
    }
}
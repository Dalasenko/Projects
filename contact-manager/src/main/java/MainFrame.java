import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Main application window
 */
public class MainFrame extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private JTable contactTable;
    private DefaultTableModel tableModel;
    private ContactFormPanel formPanel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton clearSearchButton;
    private Contact selectedContact = null;

    public MainFrame() {
        // Set up the frame
        setTitle("Contact Manager");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set up the layout
        setLayout(new BorderLayout());

        // Create components
        createToolbar();
        createContactTable();
        createFormPanel();

        // Load contacts
        refreshContacts();
    }

    private void createToolbar() {
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Search components
        JLabel searchLabel = new JLabel("Search: ");
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        clearSearchButton = new JButton("Clear");

        toolbarPanel.add(searchLabel);
        toolbarPanel.add(searchField);
        toolbarPanel.add(searchButton);
        toolbarPanel.add(clearSearchButton);

        // Add action listeners
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                List<Contact> results = DatabaseManager.getInstance().searchContacts(searchTerm);
                updateTableWithContacts(results);
            }
        });

        clearSearchButton.addActionListener(e -> {
            searchField.setText("");
            refreshContacts();
        });

        // Add toolbar to the frame
        add(toolbarPanel, BorderLayout.NORTH);
    }

    private void createContactTable() {
        // Create table model with column names
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make the table non-editable
            }
        };

        tableModel.addColumn("ID");
        tableModel.addColumn("First Name");
        tableModel.addColumn("Last Name");
        tableModel.addColumn("Email");
        tableModel.addColumn("Phone");

        // Create the table with the model
        contactTable = new JTable(tableModel);

        // Hide the ID column (we still need it for reference)
        contactTable.getColumnModel().getColumn(0).setMinWidth(0);
        contactTable.getColumnModel().getColumn(0).setMaxWidth(0);
        contactTable.getColumnModel().getColumn(0).setWidth(0);

        // Set column widths
        contactTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        contactTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        contactTable.getColumnModel().getColumn(3).setPreferredWidth(250);
        contactTable.getColumnModel().getColumn(4).setPreferredWidth(150);

        // Add mouse listener to handle row selection
        contactTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = contactTable.getSelectedRow();
                if (row >= 0) {
                    // Get data from the selected row
                    int id = (int) tableModel.getValueAt(row, 0);
                    String firstName = (String) tableModel.getValueAt(row, 1);
                    String lastName = (String) tableModel.getValueAt(row, 2);
                    String email = (String) tableModel.getValueAt(row, 3);
                    String phone = (String) tableModel.getValueAt(row, 4);

                    // Create a contact object from the selected row
                    selectedContact = new Contact(id, firstName, lastName, email, phone);

                    // Update the form with the selected contact's data
                    formPanel.setContact(selectedContact);
                }
            }
        });

        // Put the table in a scroll pane
        JScrollPane scrollPane = new JScrollPane(contactTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void createFormPanel() {
        formPanel = new ContactFormPanel();

        // Add form submission listener
        formPanel.setFormListener(e -> {
            if (e.getActionCommand().equals("save")) {
                Contact contact = e.getContact();

                if (contact.getId() > 0) {
                    // Update existing contact
                    if (DatabaseManager.getInstance().updateContact(contact)) {
                        JOptionPane.showMessageDialog(this,
                                "Contact updated successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    // Add new contact
                    if (DatabaseManager.getInstance().addContact(contact)) {
                        JOptionPane.showMessageDialog(this,
                                "Contact added successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                }

                // Clear form and refresh contacts
                formPanel.clearForm();
                selectedContact = null;
                refreshContacts();

            } else if (e.getActionCommand().equals("delete")) {
                Contact contact = e.getContact();

                if (contact.getId() > 0) {
                    // Confirm deletion
                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Are you sure you want to delete this contact?",
                            "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        // Delete the contact
                        if (DatabaseManager.getInstance().deleteContact(contact.getId())) {
                            JOptionPane.showMessageDialog(this,
                                    "Contact deleted successfully!",
                                    "Success", JOptionPane.INFORMATION_MESSAGE);

                            // Clear form and refresh contacts
                            formPanel.clearForm();
                            selectedContact = null;
                            refreshContacts();
                        }
                    }
                }

            } else if (e.getActionCommand().equals("clear")) {
                // Clear form and selection
                formPanel.clearForm();
                selectedContact = null;
                contactTable.clearSelection();
            }
        });

        add(formPanel, BorderLayout.EAST);
    }

    private void refreshContacts() {
        List<Contact> contacts = DatabaseManager.getInstance().getAllContacts();
        updateTableWithContacts(contacts);
    }

    private void updateTableWithContacts(List<Contact> contacts) {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Add contacts to the table
        for (Contact contact : contacts) {
            Object[] row = {
                    contact.getId(),
                    contact.getFirstName(),
                    contact.getLastName(),
                    contact.getEmail(),
                    contact.getPhone()
            };
            tableModel.addRow(row);
        }
    }
}
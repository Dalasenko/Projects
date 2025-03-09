package ui;

import model.Book;
import model.Patron;
import database.BookDAO;
import database.LoanDAO;
import database.PatronDAO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CheckoutDialog extends JDialog {

    private int bookId;
    private boolean confirmed = false;

    private JComboBox<Patron> patronComboBox;
    private JSpinner loanDaysSpinner;

    private BookDAO bookDAO = new BookDAO();
    private PatronDAO patronDAO = new PatronDAO();
    private LoanDAO loanDAO = new LoanDAO();

    public CheckoutDialog(JFrame parent, int bookId) {
        super(parent, "Checkout Book", true);
        this.bookId = bookId;

        // Initialize and configure the dialog
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setResizable(false);

        setupUI();
    }

    private void setupUI() {
        Book book = bookDAO.getBookById(bookId);

        // Main panel using GridBagLayout for form layout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Book info
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Book:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JLabel bookInfoLabel = new JLabel(book.getTitle() + " by " + book.getAuthor());
        mainPanel.add(bookInfoLabel, gbc);

        // Patron selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Patron:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;

        List<Patron> patrons = patronDAO.getAllPatrons();
        patronComboBox = new JComboBox<>();
        DefaultComboBoxModel<Patron> patronModel = new DefaultComboBoxModel<>();

        for (Patron patron : patrons) {
            patronModel.addElement(patron);
        }

        patronComboBox.setModel(patronModel);
        patronComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Patron) {
                    Patron patron = (Patron) value;
                    setText(patron.getName() + " (" + patron.getEmail() + ")");
                }
                return this;
            }
        });

        mainPanel.add(patronComboBox, gbc);

        // Loan duration
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        mainPanel.add(new JLabel("Loan Days:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(14, 1, 30, 1);
        loanDaysSpinner = new JSpinner(spinnerModel);
        mainPanel.add(loanDaysSpinner, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton checkoutButton = new JButton("Checkout");
        JButton cancelButton = new JButton("Cancel");

        checkoutButton.addActionListener(e -> {
            if (validateInput()) {
                processCheckout();
                confirmed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(checkoutButton);
        buttonPanel.add(cancelButton);

        // Add panels to dialog
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(contentPanel);
    }

    private boolean validateInput() {
        if (patronComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a patron.",
                    "Invalid Selection", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private void processCheckout() {
        Patron selectedPatron = (Patron) patronComboBox.getSelectedItem();
        int loanDays = (int) loanDaysSpinner.getValue();

        if (selectedPatron != null) {
            loanDAO.checkoutBook(bookId, selectedPatron.getId(), loanDays);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
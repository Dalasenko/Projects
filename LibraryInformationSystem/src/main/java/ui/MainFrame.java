package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import database.DatabaseConnection;

public class MainFrame extends JFrame {

    private JTabbedPane tabbedPane;
    private BookPanel bookPanel;
    private PatronPanel patronPanel;
    private LoanPanel loanPanel;
    private LoanPanel overduePanel;

    public MainFrame() {
        // Set up the frame
        setTitle("Library Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the tabbed pane
        tabbedPane = new JTabbedPane();

        // Create and add panels
        bookPanel = new BookPanel(this);
        patronPanel = new PatronPanel(this);
        loanPanel = new LoanPanel(this, false); // Regular loans
        overduePanel = new LoanPanel(this, true); // Overdue loans

        tabbedPane.addTab("Books", bookPanel);
        tabbedPane.addTab("Patrons", patronPanel);
        tabbedPane.addTab("Loans", loanPanel);
        tabbedPane.addTab("Overdue", overduePanel);

        // Create menu bar
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        // Add to frame
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // Add window listener to close database connection on exit
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DatabaseConnection.closeConnection();
            }
        });
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> {
            DatabaseConnection.closeConnection();
            System.exit(0);
        });
        fileMenu.add(exitItem);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "Library Management System\nVersion 1.0\n\nA simple library management system.",
                "About", JOptionPane.INFORMATION_MESSAGE);
    }

    public void refreshPanels() {
        bookPanel.refreshData();
        patronPanel.refreshData();
        loanPanel.refreshData();
        overduePanel.refreshData();
    }
}
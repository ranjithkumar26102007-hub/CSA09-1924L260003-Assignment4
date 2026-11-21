import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Task 06 – Campus Event Registration GUI
 * Course: Programming in Java (CSA09)
 * Student: Ranjith Kumar.R | Register Number: 1924L260003
 * Java Version: 27 | IDE: Antigravity
 *
 * Implements a layered Swing + MySQL architecture.
 * All database operations are executed asynchronously via SwingWorker to guarantee
 * that long-running network or database calls never freeze the Event Dispatch Thread (EDT).
 */
public class EventRegistrationApp extends JFrame {

    // --- Services and Models ---
    private final EventRegistrationService registrationService = new EventRegistrationService();
    private final List<Event> availableEvents = new ArrayList<>();
    private final List<Registration> currentDisplayedRegistrations = new ArrayList<>();

    // --- Form Input Components (Student Registration) ---
    private JTextField studentIdField;
    private JTextField studentNameField;
    private JTextField studentEmailField;
    private JComboBox<String> departmentCombo;
    private JComboBox<Event> eventCombo;
    private JButton registerButton;
    private JButton clearButton;

    // --- Dashboard & Filter Components (Committee Member View) ---
    private JComboBox<Object> tableFilterCombo;
    private JButton refreshButton;
    private JButton exportButton;
    private JTable registrationTable;
    private DefaultTableModel tableModel;

    // --- Status and Feedback Components ---
    private JLabel statusLabel;
    private JLabel dbStatusLabel;
    private JProgressBar progressBar;

    public EventRegistrationApp() {
        setTitle("Campus Event Registration System (Task 06 – Swing + MySQL)");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(980, 680);
        setMinimumSize(new Dimension(850, 550));
        setLocationRelativeTo(null);

        // Window closing handled via WindowAdapter
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmAndExit();
            }
        });

        // Initialize Menu Bar
        setupMenuBar();

        // Build User Interface Layout
        buildGUI();

        // Asynchronously load events and initial registration records
        loadEventsBackground();
        loadRegistrationsBackground(null);
    }

    /**
     * Constructs the desktop menu bar for application management.
     */
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // 1. File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exportItem = new JMenuItem("Export Registrations to CSV...");
        exportItem.addActionListener(e -> handleExportCsv());
        JMenuItem refreshItem = new JMenuItem("Refresh Data");
        refreshItem.addActionListener(e -> loadRegistrationsBackground(getSelectedFilterEventId()));
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> confirmAndExit());

        fileMenu.add(exportItem);
        fileMenu.add(refreshItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // 2. Database Menu
        JMenu dbMenu = new JMenu("Database");
        JMenuItem checkConnItem = new JMenuItem("Probe MySQL Connection");
        checkConnItem.addActionListener(e -> probeDatabaseConnection());
        JMenuItem configItem = new JMenuItem("Connection Settings...");
        configItem.addActionListener(e -> showConnectionSettingsDialog());

        dbMenu.add(checkConnItem);
        dbMenu.add(configItem);

        // 3. Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About Task 06");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(dbMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    /**
     * Builds the complete split-view GUI hierarchy using nested panels.
     */
    private void buildGUI() {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(0, 8));

        // --------------------------------------------------------------------
        // TOP BANNER
        // --------------------------------------------------------------------
        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setBackground(new Color(24, 52, 94));
        bannerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel titleLabel = new JLabel("Campus Event Registration & Committee Dashboard");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Student Registration Form & Committee Member Real-Time Table");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(210, 225, 245));

        bannerPanel.add(titleLabel, BorderLayout.NORTH);
        bannerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        contentPane.add(bannerPanel, BorderLayout.NORTH);

        // --------------------------------------------------------------------
        // CENTER: JSplitPane (Left: Registration Form, Right: Dashboard Table)
        // --------------------------------------------------------------------
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(360);
        splitPane.setContinuousLayout(true);

        // Left Panel: Student Registration Form
        JPanel formContainer = buildRegistrationFormPanel();
        splitPane.setLeftComponent(formContainer);

        // Right Panel: Committee Dashboard Table
        JPanel dashboardContainer = buildDashboardTablePanel();
        splitPane.setRightComponent(dashboardContainer);

        contentPane.add(splitPane, BorderLayout.CENTER);

        // --------------------------------------------------------------------
        // BOTTOM: Status Bar + Progress Indicator
        // --------------------------------------------------------------------
        JPanel bottomBar = new JPanel(new BorderLayout(10, 0));
        bottomBar.setBorder(new CompoundBorder(
                BorderFactory.createBevelBorder(BevelBorder.LOWERED),
                new EmptyBorder(4, 10, 4, 10)
        ));

        statusLabel = new JLabel("Ready. Initializing system...");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel rightStatusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(100, 16));
        progressBar.setVisible(false);

        dbStatusLabel = new JLabel("Checking DB...");
        dbStatusLabel.setFont(new Font("SansSerif", Font.BOLD, 11));

        rightStatusPanel.add(progressBar);
        rightStatusPanel.add(dbStatusLabel);

        bottomBar.add(statusLabel, BorderLayout.CENTER);
        bottomBar.add(rightStatusPanel, BorderLayout.EAST);

        contentPane.add(bottomBar, BorderLayout.SOUTH);

        // Update initial DB status badge
        updateDbBadge();
    }

    /**
     * Builds the student registration form panel.
     */
    private JPanel buildRegistrationFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(new CompoundBorder(
                new TitledBorder("Student Event Registration"),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JPanel gridPanel = new JPanel(new GridLayout(5, 2, 8, 12));

        gridPanel.add(new JLabel("Student ID *:"));
        studentIdField = new JTextField();
        gridPanel.add(studentIdField);

        gridPanel.add(new JLabel("Full Name *:"));
        studentNameField = new JTextField();
        gridPanel.add(studentNameField);

        gridPanel.add(new JLabel("Email Address *:"));
        studentEmailField = new JTextField();
        gridPanel.add(studentEmailField);

        gridPanel.add(new JLabel("Department *:"));
        String[] depts = {
                "-- Select Department --",
                "Computer Science",
                "Information Technology",
                "Electronics & Comm",
                "Electrical Engineering",
                "Mechanical Engineering",
                "Civil Engineering",
                "Mathematics & Computing"
        };
        departmentCombo = new JComboBox<>(depts);
        gridPanel.add(departmentCombo);

        gridPanel.add(new JLabel("Select Event *:"));
        eventCombo = new JComboBox<>();
        gridPanel.add(eventCombo);

        panel.add(gridPanel, BorderLayout.NORTH);

        // Action Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        registerButton = new JButton("Register for Event");
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        registerButton.setBackground(new Color(30, 120, 45));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(e -> handleRegisterAction());

        clearButton = new JButton("Clear Form");
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(registerButton);
        buttonPanel.add(clearButton);
        panel.add(buttonPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Builds the committee registrations table panel with filtering and export controls.
     */
    private JPanel buildDashboardTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(new CompoundBorder(
                new TitledBorder("Committee Registration Records"),
                new EmptyBorder(8, 12, 8, 12)
        ));

        // Top Filter Toolbar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        filterBar.add(new JLabel("Filter by Event:"));

        tableFilterCombo = new JComboBox<>();
        tableFilterCombo.addItem("All Campus Events");
        tableFilterCombo.addActionListener(e -> {
            Integer eventId = getSelectedFilterEventId();
            loadRegistrationsBackground(eventId);
        });
        filterBar.add(tableFilterCombo);

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadRegistrationsBackground(getSelectedFilterEventId()));
        filterBar.add(refreshButton);

        exportButton = new JButton("Export to CSV");
        exportButton.setFont(new Font("SansSerif", Font.BOLD, 11));
        exportButton.addActionListener(e -> handleExportCsv());
        filterBar.add(exportButton);

        panel.add(filterBar, BorderLayout.NORTH);

        // Table Model Setup
        String[] columns = {"Reg ID", "Student ID", "Student Name", "Email", "Department", "Event Name", "Timestamp"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only view
            }
        };

        registrationTable = new JTable(tableModel);
        registrationTable.setRowHeight(24);
        registrationTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        registrationTable.getTableHeader().setReorderingAllowed(false);
        registrationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Center align ID columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        registrationTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        registrationTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(registrationTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ========================================================================
    // ASYNCHRONOUS DATABASE OPERATIONS (USING SWINGWORKER)
    // ========================================================================

    /**
     * Loads available campus events from the database off the EDT.
     */
    private void loadEventsBackground() {
        SwingWorker<List<Event>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Event> doInBackground() throws Exception {
                return registrationService.fetchAllEvents();
            }

            @Override
            protected void done() {
                try {
                    List<Event> events = get();
                    availableEvents.clear();
                    availableEvents.addAll(events);

                    eventCombo.removeAllItems();
                    tableFilterCombo.removeAllItems();
                    tableFilterCombo.addItem("All Campus Events");

                    for (Event ev : events) {
                        eventCombo.addItem(ev);
                        tableFilterCombo.addItem(ev);
                    }
                    updateDbBadge();
                } catch (Exception ex) {
                    statusLabel.setText("Failed to load events: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    /**
     * Asynchronously loads registration records and updates the JTable.
     */
    private void loadRegistrationsBackground(Integer eventIdFilter) {
        setBusy(true, "Querying registrations database...");

        SwingWorker<List<Registration>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Registration> doInBackground() throws Exception {
                return registrationService.fetchRegistrations(eventIdFilter);
            }

            @Override
            protected void done() {
                setBusy(false, "");
                try {
                    List<Registration> records = get();
                    currentDisplayedRegistrations.clear();
                    currentDisplayedRegistrations.addAll(records);

                    tableModel.setRowCount(0);
                    for (Registration r : records) {
                        tableModel.addRow(new Object[]{
                                r.getRegistrationId(),
                                r.getStudentId(),
                                r.getStudentName(),
                                r.getStudentEmail(),
                                r.getDepartment(),
                                r.getEventName(),
                                r.getTimestamp()
                        });
                    }

                    statusLabel.setText("Loaded " + records.size() + " registration record(s).");
                    updateDbBadge();
                } catch (Exception ex) {
                    statusLabel.setText("Error querying records: " + ex.getMessage());
                    JOptionPane.showMessageDialog(EventRegistrationApp.this,
                            "Failed to load records: " + ex.getMessage(),
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    /**
     * Handles student registration in the background using SwingWorker.
     */
    private void handleRegisterAction() {
        String studentId = studentIdField.getText();
        String studentName = studentNameField.getText();
        String studentEmail = studentEmailField.getText();
        String department = (String) departmentCombo.getSelectedItem();
        Event selectedEvent = (Event) eventCombo.getSelectedItem();

        setBusy(true, "Validating and submitting registration...");

        SwingWorker<Registration, Void> worker = new SwingWorker<>() {
            @Override
            protected Registration doInBackground() throws Exception {
                // Performs business validation and database insertion off EDT
                return registrationService.registerStudent(
                        studentId, studentName, studentEmail, department, selectedEvent
                );
            }

            @Override
            protected void done() {
                setBusy(false, "");
                try {
                    Registration reg = get();
                    statusLabel.setText("Registration successful! Reg ID: #" + reg.getRegistrationId());

                    JOptionPane.showMessageDialog(EventRegistrationApp.this,
                            "EVENT REGISTRATION SUCCESSFUL!\n\n"
                                    + "Registration ID: #" + reg.getRegistrationId() + "\n"
                                    + "Student: " + reg.getStudentName() + " (" + reg.getStudentId() + ")\n"
                                    + "Department: " + reg.getDepartment() + "\n"
                                    + "Event: " + reg.getEventName() + "\n"
                                    + "Registered At: " + reg.getTimestamp(),
                            "Registration Confirmed", JOptionPane.INFORMATION_MESSAGE);

                    // Clear inputs and reload table to reflect new registration
                    clearForm();
                    loadRegistrationsBackground(getSelectedFilterEventId());

                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    String message = cause.getMessage();

                    if (message != null && message.contains("DUPLICATE_REGISTRATION")) {
                        statusLabel.setText("Duplicate rejected: " + studentId + " already registered.");
                        JOptionPane.showMessageDialog(EventRegistrationApp.this,
                                "DUPLICATE REGISTRATION ERROR\n\n"
                                        + "Student ID '" + studentId + "' is ALREADY registered for this event.\n"
                                        + "Each student may only register once per campus event.",
                                "Duplicate Registration Prevented", JOptionPane.WARNING_MESSAGE);
                    } else {
                        statusLabel.setText("Validation error: " + message);
                        JOptionPane.showMessageDialog(EventRegistrationApp.this,
                                message, "Input Validation Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
        worker.execute();
    }

    /**
     * Exports current table view records to a CSV file.
     */
    private void handleExportCsv() {
        if (currentDisplayedRegistrations.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "There are no registration records in the table to export.",
                    "Export Empty", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Registration Records to CSV");
        fileChooser.setSelectedFile(new File("campus_event_registrations.csv"));

        int choice = fileChooser.showSaveDialog(this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                int count = registrationService.exportToCsv(currentDisplayedRegistrations, selectedFile);
                statusLabel.setText("Exported " + count + " records to " + selectedFile.getName());
                JOptionPane.showMessageDialog(this,
                        "Successfully exported " + count + " records to:\n" + selectedFile.getAbsolutePath(),
                        "Export Completed", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Failed to export CSV: " + ex.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ========================================================================
    // HELPER & UTILITY METHODS
    // ========================================================================

    private Integer getSelectedFilterEventId() {
        Object item = tableFilterCombo.getSelectedItem();
        if (item instanceof Event) {
            return ((Event) item).getEventId();
        }
        return null; // "All Campus Events"
    }

    private void clearForm() {
        studentIdField.setText("");
        studentNameField.setText("");
        studentEmailField.setText("");
        departmentCombo.setSelectedIndex(0);
        if (eventCombo.getItemCount() > 0) {
            eventCombo.setSelectedIndex(0);
        }
    }

    private void setBusy(boolean busy, String message) {
        progressBar.setIndeterminate(busy);
        progressBar.setVisible(busy);
        registerButton.setEnabled(!busy);
        refreshButton.setEnabled(!busy);
        exportButton.setEnabled(!busy);
        if (busy && message != null) {
            statusLabel.setText(message);
        }
    }

    private void updateDbBadge() {
        if (RegistrationDAO.isLiveDbAvailable()) {
            dbStatusLabel.setText("● Live MySQL Connected");
            dbStatusLabel.setForeground(new Color(25, 125, 35));
            dbStatusLabel.setToolTipText("Connected to MySQL server: " + DatabaseConnection.getDbUrl());
        } else {
            dbStatusLabel.setText("○ Mode: Offline / Fallback Store");
            dbStatusLabel.setForeground(new Color(190, 80, 20));
            dbStatusLabel.setToolTipText("MySQL Server is unreachable. Running on synchronized fallback memory store.");
        }
    }

    private void probeDatabaseConnection() {
        setBusy(true, "Probing MySQL database server...");
        SwingWorker<Boolean, Void> probeWorker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                RegistrationDAO.refreshDbStatus();
                return RegistrationDAO.isLiveDbAvailable();
            }

            @Override
            protected void done() {
                setBusy(false, "");
                try {
                    boolean online = get();
                    updateDbBadge();
                    if (online) {
                        JOptionPane.showMessageDialog(EventRegistrationApp.this,
                                "SUCCESS: Live MySQL database connection established!\n\n"
                                        + "Database URL: " + DatabaseConnection.getDbUrl() + "\n"
                                        + "User: " + DatabaseConnection.getDbUser(),
                                "MySQL Connection Active", JOptionPane.INFORMATION_MESSAGE);
                        loadEventsBackground();
                        loadRegistrationsBackground(null);
                    } else {
                        JOptionPane.showMessageDialog(EventRegistrationApp.this,
                                "NOTICE: MySQL server is not reachable on localhost:3306.\n\n"
                                        + "The application is running in 'Offline / Fallback Demo Mode'.\n"
                                        + "All registration, unique constraints, and CSV export functions\n"
                                        + "continue to operate using the synchronized fallback store.\n\n"
                                        + "To enable live MySQL:\n"
                                        + "1. Start MySQL Server (port 3306).\n"
                                        + "2. Run 'sql/schema.sql'.\n"
                                        + "3. Add mysql-connector-j.jar to the classpath.",
                                "Database Offline", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ignored) {}
            }
        };
        probeWorker.execute();
    }

    private void showConnectionSettingsDialog() {
        JTextField urlField = new JTextField(DatabaseConnection.getDbUrl(), 30);
        JTextField userField = new JTextField(DatabaseConnection.getDbUser(), 15);
        JPasswordField passField = new JPasswordField(15);

        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));
        panel.add(new JLabel("JDBC URL:"));
        panel.add(urlField);
        panel.add(new JLabel("Username:"));
        panel.add(userField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "MySQL Connection Settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            DatabaseConnection.configure(urlField.getText(), userField.getText(), new String(passField.getPassword()));
            probeDatabaseConnection();
        }
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "Campus Event Registration System (Task 06)\n"
                        + "Course: Programming in Java (CSA09)\n"
                        + "Student: Ranjith Kumar.R | Reg No: 1924L260003\n"
                        + "Java Version: 27 | Architecture: Swing + JDBC + MySQL\n\n"
                        + "Features:\n"
                        + "- Multi-tier architecture: UI, Service, DAO\n"
                        + "- Non-blocking SwingWorker background execution\n"
                        + "- Unique duplicate registration prevention\n"
                        + "- Real-time event filtering and CSV export\n"
                        + "- Parameterized PreparedStatements with try-with-resources",
                "About Task 06", JOptionPane.INFORMATION_MESSAGE);
    }

    private void confirmAndExit() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit the Campus Event Registration application?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EventRegistrationApp app = new EventRegistrationApp();
            app.setVisible(true);
        });
    }
}

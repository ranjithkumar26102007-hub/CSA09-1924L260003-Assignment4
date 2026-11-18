import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Task 03 – Campus Library Search GUI
 * Course: Programming in Java (CSA09)
 * Student: Ranjith Kumar.R | Register Number: 1924L260003
 * Java Version: 27 | IDE: Antigravity
 *
 * This application implements a nested layout design using BorderLayout
 * and FlowLayout. The search results area in the CENTER expands dynamically
 * during window resizing, while top search controls and bottom action buttons
 * maintain their preferred dimensions.
 */
public class LibrarySearchApp extends JFrame {

    // --- Data Model for In-Memory Books ---
    public static class Book {
        private final String id;
        private final String title;
        private final String author;
        private final String category;
        private String status; // "Available" or "Reserved"

        public Book(String id, String title, String author, String category, String status) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.category = category;
            this.status = status;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public String getCategory() { return category; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    // --- GUI Components ---
    private JTextField searchField;
    private JComboBox<String> categoryCombo;
    private JButton searchButton;
    private JButton showAllButton;

    private JTable resultsTable;
    private DefaultTableModel tableModel;

    private JButton prevButton;
    private JButton nextButton;
    private JButton reserveButton;
    private JLabel statusLabel;

    // --- In-Memory Repository ---
    private final List<Book> allBooks = new ArrayList<>();
    private final List<Book> displayedBooks = new ArrayList<>();

    public LibrarySearchApp() {
        setTitle("Campus Library - Book Search System (Task 03)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500); // Default initial dimension
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);

        // Load sample library catalog into memory
        initializeBookData();

        // Build the nested GUI layout
        buildGUI();

        // Perform initial display showing all catalog books
        performSearch();
    }

    /**
     * Initializes sample books across diverse campus academic disciplines.
     */
    private void initializeBookData() {
        allBooks.add(new Book("CS-101", "Introduction to Algorithms", "Thomas H. Cormen", "Computer Science", "Available"));
        allBooks.add(new Book("CS-102", "Clean Code: Agile Software Craft", "Robert C. Martin", "Computer Science", "Available"));
        allBooks.add(new Book("CS-103", "Operating System Concepts", "Abraham Silberschatz", "Computer Science", "Reserved"));
        allBooks.add(new Book("CS-104", "Artificial Intelligence: A Modern Approach", "Stuart Russell", "Computer Science", "Available"));
        allBooks.add(new Book("IT-201", "Computer Networking: A Top-Down Approach", "James F. Kurose", "Information Technology", "Available"));
        allBooks.add(new Book("IT-202", "Database System Concepts", "Abraham Silberschatz", "Information Technology", "Available"));
        allBooks.add(new Book("IT-203", "Cloud Computing: Concepts & Architecture", "Thomas Erl", "Information Technology", "Reserved"));
        allBooks.add(new Book("EC-301", "Digital Logic and Computer Design", "M. Morris Mano", "Electronics", "Available"));
        allBooks.add(new Book("EC-302", "Electronic Devices and Circuit Theory", "Robert Boylestad", "Electronics", "Available"));
        allBooks.add(new Book("EC-303", "Signals and Systems", "Alan V. Oppenheim", "Electronics", "Available"));
        allBooks.add(new Book("MA-401", "Higher Engineering Mathematics", "B. S. Grewal", "Mathematics", "Available"));
        allBooks.add(new Book("MA-402", "Discrete Mathematics and Applications", "Kenneth H. Rosen", "Mathematics", "Available"));
        allBooks.add(new Book("MA-403", "Introduction to Linear Algebra", "Gilbert Strang", "Mathematics", "Reserved"));
        allBooks.add(new Book("ME-501", "Engineering Mechanics: Dynamics", "J. L. Meriam", "Mechanical", "Available"));
        allBooks.add(new Book("ME-502", "Fundamentals of Thermodynamics", "Claus Borgnakke", "Mechanical", "Available"));
    }

    /**
     * Constructs the nested GUI hierarchy using standard Swing layout managers.
     */
    private void buildGUI() {
        // Main Window Content Pane uses BorderLayout
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(0, 8));

        // Setup a helper menu to facilitate easy window resizing during testing
        setupMenuBar();

        // ====================================================================
        // 1. NORTH CONTAINER: Title Banner + Search Controls Panel
        // ====================================================================
        JPanel topContainer = new JPanel(new BorderLayout(0, 4));
        topContainer.setBorder(new EmptyBorder(10, 15, 6, 15));

        // Top Heading (FlowLayout: Center)
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JLabel titleLabel = new JLabel("Campus Library Book Search System");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 19));
        titleLabel.setForeground(new Color(25, 55, 110));
        titlePanel.add(titleLabel);

        // Search Controls Bar (FlowLayout: Center)
        JPanel searchControlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));

        searchControlsPanel.add(new JLabel("Search Keyword:"));
        searchField = new JTextField(16);
        searchField.setToolTipText("Enter title, author, or book ID (leave empty to show all)");
        searchControlsPanel.add(searchField);

        searchControlsPanel.add(new JLabel("Category:"));
        String[] categories = {
                "All Categories",
                "Computer Science",
                "Information Technology",
                "Electronics",
                "Mathematics",
                "Mechanical"
        };
        categoryCombo = new JComboBox<>(categories);
        searchControlsPanel.add(categoryCombo);

        searchButton = new JButton("Search");
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        searchControlsPanel.add(searchButton);

        showAllButton = new JButton("Show All");
        searchControlsPanel.add(showAllButton);

        topContainer.add(titlePanel, BorderLayout.NORTH);
        topContainer.add(searchControlsPanel, BorderLayout.SOUTH);
        contentPane.add(topContainer, BorderLayout.NORTH);

        // ====================================================================
        // 2. CENTER CONTAINER: Scrollable JTable Results (Expands on Resize)
        // ====================================================================
        String[] columnNames = {"Book ID", "Title", "Author", "Category", "Availability"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            // Make table cells read-only
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resultsTable = new JTable(tableModel);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsTable.setRowHeight(24);
        resultsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        resultsTable.getTableHeader().setReorderingAllowed(false);

        // Set column preferred widths for clean readability
        resultsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        resultsTable.getColumnModel().getColumn(1).setPreferredWidth(260);
        resultsTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        resultsTable.getColumnModel().getColumn(3).setPreferredWidth(140);
        resultsTable.getColumnModel().getColumn(4).setPreferredWidth(90);

        // Center-align the Book ID and Availability columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        resultsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        resultsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        JScrollPane tableScrollPane = new JScrollPane(resultsTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Matching Catalog Books (Table Expands with Window)"));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(0, 15, 0, 15));
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        contentPane.add(centerPanel, BorderLayout.CENTER);

        // ====================================================================
        // 3. SOUTH CONTAINER: Action Buttons + Status Bar
        // ====================================================================
        JPanel bottomContainer = new JPanel(new BorderLayout(0, 4));
        bottomContainer.setBorder(new EmptyBorder(6, 15, 10, 15));

        // Action Button Panel (FlowLayout: Center)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 6));

        prevButton = new JButton("◀ Previous");
        nextButton = new JButton("Next ▶");
        reserveButton = new JButton("Reserve Selected Book");
        reserveButton.setFont(new Font("SansSerif", Font.BOLD, 12));

        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(reserveButton);

        // Status Bar Panel (BorderLayout with lowered bevel border)
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createBevelBorder(BevelBorder.LOWERED),
                new EmptyBorder(4, 8, 4, 8)
        ));

        statusLabel = new JLabel("Ready. Welcome to Campus Library Search.");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusPanel.add(statusLabel, BorderLayout.CENTER);

        bottomContainer.add(buttonPanel, BorderLayout.NORTH);
        bottomContainer.add(statusPanel, BorderLayout.SOUTH);
        contentPane.add(bottomContainer, BorderLayout.SOUTH);

        // ====================================================================
        // 4. ATTACH EVENT LISTENERS
        // ====================================================================

        // Search button triggers filtered query
        searchButton.addActionListener(e -> performSearch());

        // Pressing ENTER inside the searchField also triggers search
        searchField.addActionListener(e -> performSearch());

        // Category selection triggers quick search
        categoryCombo.addActionListener(e -> performSearch());

        // Show All button resets filter and search text
        showAllButton.addActionListener(e -> {
            searchField.setText("");
            categoryCombo.setSelectedIndex(0);
            performSearch();
        });

        // Navigation listeners
        prevButton.addActionListener(e -> navigatePrevious());
        nextButton.addActionListener(e -> navigateNext());

        // Reserve action listener
        reserveButton.addActionListener(e -> reserveSelectedBook());

        // Table selection listener updates the status bar
        resultsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectionStatus();
            }
        });
    }

    /**
     * Creates a helper menu bar allowing the user/grader to switch between
     * the 3 required test window dimensions with a single click.
     */
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu viewMenu = new JMenu("Window Size (Resize Tests)");

        JMenuItem size1 = new JMenuItem("Compact (700 x 500)");
        size1.addActionListener(e -> setSize(700, 500));

        JMenuItem size2 = new JMenuItem("Standard (900 x 600)");
        size2.addActionListener(e -> setSize(900, 600));

        JMenuItem size3 = new JMenuItem("Expanded (1200 x 800)");
        size3.addActionListener(e -> setSize(1200, 800));

        viewMenu.add(size1);
        viewMenu.add(size2);
        viewMenu.add(size3);

        menuBar.add(viewMenu);
        setJMenuBar(menuBar);
    }

    // ========================================================================
    // BUSINESS LOGIC & EVENT HANDLER METHODS
    // ========================================================================

    /**
     * Filters books based on the search keyword and category.
     */
    private void performSearch() {
        String keyword = searchField.getText().trim().toLowerCase();
        String selectedCategory = (String) categoryCombo.getSelectedItem();

        displayedBooks.clear();
        tableModel.setRowCount(0); // Clear current table rows

        for (Book book : allBooks) {
            boolean matchesCategory = selectedCategory == null
                    || selectedCategory.equals("All Categories")
                    || book.getCategory().equalsIgnoreCase(selectedCategory);

            boolean matchesKeyword = keyword.isEmpty()
                    || book.getTitle().toLowerCase().contains(keyword)
                    || book.getAuthor().toLowerCase().contains(keyword)
                    || book.getId().toLowerCase().contains(keyword);

            if (matchesCategory && matchesKeyword) {
                displayedBooks.add(book);
                tableModel.addRow(new Object[]{
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getCategory(),
                        book.getStatus()
                });
            }
        }

        // Update UI controls state based on search results
        if (displayedBooks.isEmpty()) {
            prevButton.setEnabled(false);
            nextButton.setEnabled(false);
            reserveButton.setEnabled(false);

            String msg = "No matching books found";
            if (!keyword.isEmpty()) {
                msg += " for keyword '" + keyword + "'";
            }
            if (!"All Categories".equals(selectedCategory)) {
                msg += " in category '" + selectedCategory + "'";
            }
            statusLabel.setText(msg + ".");
        } else {
            prevButton.setEnabled(true);
            nextButton.setEnabled(true);
            reserveButton.setEnabled(true);

            // Automatically select the first matching row
            resultsTable.setRowSelectionInterval(0, 0);
            updateSelectionStatus();
        }
    }

    /**
     * Selects the previous book in the results table.
     */
    private void navigatePrevious() {
        if (displayedBooks.isEmpty()) return;

        int currentRow = resultsTable.getSelectedRow();
        if (currentRow > 0) {
            int newRow = currentRow - 1;
            resultsTable.setRowSelectionInterval(newRow, newRow);
            resultsTable.scrollRectToVisible(resultsTable.getCellRect(newRow, 0, true));
        } else {
            // Already at top: wrap to last row
            int lastRow = displayedBooks.size() - 1;
            resultsTable.setRowSelectionInterval(lastRow, lastRow);
            resultsTable.scrollRectToVisible(resultsTable.getCellRect(lastRow, 0, true));
        }
        updateSelectionStatus();
    }

    /**
     * Selects the next book in the results table.
     */
    private void navigateNext() {
        if (displayedBooks.isEmpty()) return;

        int currentRow = resultsTable.getSelectedRow();
        if (currentRow >= 0 && currentRow < displayedBooks.size() - 1) {
            int newRow = currentRow + 1;
            resultsTable.setRowSelectionInterval(newRow, newRow);
            resultsTable.scrollRectToVisible(resultsTable.getCellRect(newRow, 0, true));
        } else {
            // Already at bottom: wrap to first row
            resultsTable.setRowSelectionInterval(0, 0);
            resultsTable.scrollRectToVisible(resultsTable.getCellRect(0, 0, true));
        }
        updateSelectionStatus();
    }

    /**
     * Handles reserving the currently selected book.
     */
    private void reserveSelectedBook() {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a book from the table to reserve.",
                    "No Book Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Book selectedBook = displayedBooks.get(selectedRow);

        if ("Reserved".equalsIgnoreCase(selectedBook.getStatus())) {
            JOptionPane.showMessageDialog(this,
                    "The book \"" + selectedBook.getTitle() + "\" is already reserved.\nPlease select an available book.",
                    "Reservation Unavailable",
                    JOptionPane.WARNING_MESSAGE);
            statusLabel.setText("Reservation failed: " + selectedBook.getTitle() + " is already reserved.");
            return;
        }

        // Update book availability in memory and in the JTable model
        selectedBook.setStatus("Reserved");
        tableModel.setValueAt("Reserved", selectedRow, 4);

        statusLabel.setText("Successfully reserved: \"" + selectedBook.getTitle() + "\" (ID: " + selectedBook.getId() + ")");

        JOptionPane.showMessageDialog(this,
                "RESERVATION CONFIRMED!\n\n"
                        + "Book ID: " + selectedBook.getId() + "\n"
                        + "Title: " + selectedBook.getTitle() + "\n"
                        + "Author: " + selectedBook.getAuthor() + "\n"
                        + "Category: " + selectedBook.getCategory() + "\n"
                        + "Status: Reserved\n\n"
                        + "Please collect the book from the library circulation counter within 24 hours.",
                "Reservation Successful",
                JOptionPane.INFORMATION_MESSAGE);

        updateSelectionStatus();
    }

    /**
     * Updates the status bar text reflecting current row selection.
     */
    private void updateSelectionStatus() {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < displayedBooks.size()) {
            Book book = displayedBooks.get(selectedRow);
            statusLabel.setText("Book " + (selectedRow + 1) + " of " + displayedBooks.size() + ": \""
                    + book.getTitle() + "\" by " + book.getAuthor()
                    + " | Category: " + book.getCategory()
                    + " | Status: " + book.getStatus());
        }
    }

    /**
     * Application entry point starting on the Swing Event Dispatch Thread (EDT).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibrarySearchApp app = new LibrarySearchApp();
            app.setVisible(true);
        });
    }
}

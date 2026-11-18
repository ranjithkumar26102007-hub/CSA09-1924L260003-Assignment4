import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Task 04 – Event-Handling Forensics (Drawing Pad)
 * Course: Programming in Java (CSA09)
 * Student: Ranjith Kumar.R | Register Number: 1924L260003
 * Java Version: 27 | IDE: Antigravity
 *
 * FORENSIC DIAGNOSIS:
 * 1. Bug 1 (Silent Handler Failure): The original code defined a mouse listener
 *    method named 'mouseClick(MouseEvent e)' instead of 'mouseClicked(MouseEvent e)'
 *    without the '@Override' annotation. Since MouseAdapter contains an empty
 *    'mouseClicked' implementation, the compiler treated 'mouseClick' as an
 *    arbitrary uncalled method, resulting in zero execution and a frozen counter.
 *
 * 2. Bug 2 (Disappearing Dots on Minimize / Restore): The original code attempted
 *    to draw dots directly onto 'getGraphics()' inside the listener without persisting
 *    coordinates. When the OS minimizes/restores or resizes the window, the native
 *    surface is invalidated and cleared. Because no points were retained in a data
 *    model, the repainting operation wiped all previous drawings.
 *
 * REPAIR IMPLEMENTATION:
 * - TODO 1: Registered a corrected MouseAdapter with '@Override public void mouseClicked(...)',
 *           persisting clicked coordinates into a List<Point>, updating the counter,
 *           and requesting repaint via 'repaint()'.
 * - TODO 2: Overrode 'paintComponent(Graphics g)' with '@Override', calling
 *           'super.paintComponent(g)' and re-rendering all persisted points.
 */
public class DrawingPadApp extends JFrame {

    private final JLabel clickCounterLabel = new JLabel("Total Clicks: 0 | Points Stored: 0");
    private final DrawingCanvas drawingCanvas;
    private int clickCount = 0;

    public DrawingPadApp() {
        setTitle("Drawing Pad - Event-Handling Forensics (Task 04)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 520);
        setMinimumSize(new Dimension(500, 380));
        setLocationRelativeTo(null);

        // Main layout container using BorderLayout
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(0, 5));

        // Top Header & Info Panel
        JPanel topPanel = new JPanel(new BorderLayout(0, 5));
        topPanel.setBorder(new EmptyBorder(10, 15, 8, 15));

        JLabel titleLabel = new JLabel("Interactive Drawing Pad (Click to Plot Dots)");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(new Color(20, 50, 110));

        JLabel instructionLabel = new JLabel("Click anywhere on the white canvas below. Test minimizing and restoring the window.");
        instructionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        instructionLabel.setForeground(Color.DARK_GRAY);

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(instructionLabel, BorderLayout.SOUTH);
        contentPane.add(topPanel, BorderLayout.NORTH);

        // Center Drawing Canvas
        drawingCanvas = new DrawingCanvas();
        contentPane.add(drawingCanvas, BorderLayout.CENTER);

        // Bottom Action Bar & Status Bar
        JPanel bottomContainer = new JPanel(new BorderLayout(0, 4));
        bottomContainer.setBorder(new EmptyBorder(6, 15, 10, 15));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        JButton clearButton = new JButton("Clear Canvas");
        clearButton.addActionListener(e -> clearCanvas());
        buttonPanel.add(clearButton);

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createBevelBorder(BevelBorder.LOWERED),
                new EmptyBorder(4, 8, 4, 8)
        ));
        clickCounterLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        clickCounterLabel.setForeground(new Color(30, 90, 30));
        statusPanel.add(clickCounterLabel, BorderLayout.CENTER);

        bottomContainer.add(buttonPanel, BorderLayout.NORTH);
        bottomContainer.add(statusPanel, BorderLayout.SOUTH);
        contentPane.add(bottomContainer, BorderLayout.SOUTH);

        // --------------------------------------------------------------------
        // TODO 1: ATTACH REPAIRED MOUSE LISTENER
        // --------------------------------------------------------------------
        // The original defect had an incorrectly named method 'mouseClick(MouseEvent e)'
        // without @Override, preventing the AWT Event Dispatcher from triggering it.
        // We repair it here using @Override on the correct 'mouseClicked(MouseEvent e)' method.
        drawingCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 1. Store clicked position as a Point object in the collection
                Point clickedPoint = e.getPoint();
                drawingCanvas.addPoint(clickedPoint);

                // 2. Increment click count and update the click counter label
                clickCount++;
                updateCounterLabel();

                // 3. Request Swing to repaint the canvas through the official paint pipeline
                drawingCanvas.repaint();
            }
        });
    }

    /**
     * Updates the status bar with current click totals and stored point counts.
     */
    private void updateCounterLabel() {
        clickCounterLabel.setText("Total Clicks: " + clickCount
                + " | Points Stored: " + drawingCanvas.getPointCount()
                + " | Window Status: Active");
    }

    /**
     * Clears all stored points and resets the click counter.
     */
    private void clearCanvas() {
        drawingCanvas.clearPoints();
        clickCount = 0;
        updateCounterLabel();
    }

    // ========================================================================
    // INNER CLASS: Custom Drawing Canvas
    // ========================================================================
    public static class DrawingCanvas extends JPanel {

        // Persistent data structure holding all clicked coordinates
        private final List<Point> points = new ArrayList<>();
        private static final int DOT_RADIUS = 5;
        private static final int DOT_DIAMETER = DOT_RADIUS * 2;

        public DrawingCanvas() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }

        public void addPoint(Point p) {
            points.add(p);
        }

        public int getPointCount() {
            return points.size();
        }

        public List<Point> getPoints() {
            return Collections.unmodifiableList(points);
        }

        public void clearPoints() {
            points.clear();
            repaint();
        }

        // --------------------------------------------------------------------
        // TODO 2: REPAIRED REPAINTING PIPELINE
        // --------------------------------------------------------------------
        // Overriding paintComponent ensures that whenever the OS or Swing requests
        // a redraw (e.g., window restore, un-minimize, resize, or exposure event),
        // the background is cleanly wiped and every stored point is accurately redrawn.
        @Override
        protected void paintComponent(Graphics g) {
            // Must invoke super.paintComponent(g) to clear the background properly
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;
            // Enable anti-aliasing for smooth, circular dots
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw all stored points in the collection
            g2d.setColor(new Color(220, 40, 40)); // Distinct Red Dot Color
            for (Point p : points) {
                g2d.fillOval(p.x - DOT_RADIUS, p.y - DOT_RADIUS, DOT_DIAMETER, DOT_DIAMETER);
            }

            // Draw visual prompt if canvas is empty
            if (points.isEmpty()) {
                g2d.setColor(new Color(160, 160, 160));
                g2d.setFont(new Font("SansSerif", Font.ITALIC, 14));
                String hint = "Drawing canvas is empty. Click anywhere with your mouse to draw dots.";
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(hint)) / 2;
                int textY = getHeight() / 2;
                g2d.drawString(hint, Math.max(textX, 20), textY);
            }
        }
    }

    /**
     * Application entry point scheduled on the Event Dispatch Thread (EDT).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DrawingPadApp app = new DrawingPadApp();
            app.setVisible(true);
        });
    }
}

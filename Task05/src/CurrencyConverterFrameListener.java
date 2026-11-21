import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Task 05 – Style 1: Frame Implements ActionListener
 * Course: Programming in Java (CSA09)
 * Student: Ranjith Kumar.R | Register Number: 1924L260003
 * Java Version: 27 | IDE: Antigravity
 *
 * In this style, the outer JFrame class directly implements the ActionListener interface.
 * All event-generating components register the frame instance (this) as their listener.
 * The single actionPerformed() method uses conditional checks (e.getSource()) to differentiate events.
 */
public class CurrencyConverterFrameListener extends JFrame implements ActionListener {

    private final JTextField amountField;
    private final JComboBox<String> fromCombo;
    private final JComboBox<String> toCombo;
    private final JButton convertButton;
    private final JLabel resultLabel;

    private final CurrencyService currencyService = new CurrencyService();

    public CurrencyConverterFrameListener() {
        setTitle("Currency Converter - Style 1 (Frame Implements ActionListener)");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(520, 380);
        setMinimumSize(new Dimension(480, 340));
        setLocationRelativeTo(null);

        // Window closing handled via WindowAdapter
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmAndExit();
            }
        });

        // Setup Main Layout (BorderLayout)
        JPanel contentPane = new JPanel(new BorderLayout(0, 10));
        contentPane.setBorder(new EmptyBorder(15, 20, 15, 20));
        setContentPane(contentPane);

        // 1. Header Banner
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        JLabel titleLabel = new JLabel("Currency Converter (Style 1)", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(new Color(25, 55, 120));

        JLabel styleSubtitle = new JLabel("Event Style: Outer JFrame implements ActionListener", SwingConstants.CENTER);
        styleSubtitle.setFont(new Font("SansSerif", Font.ITALIC, 12));
        styleSubtitle.setForeground(Color.DARK_GRAY);

        headerPanel.add(titleLabel);
        headerPanel.add(styleSubtitle);
        contentPane.add(headerPanel, BorderLayout.NORTH);

        // 2. Form Grid (GridLayout)
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 12, 12));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder("Conversion Details"),
                new EmptyBorder(10, 15, 10, 15)
        ));

        formPanel.add(new JLabel("Amount to Convert:"));
        amountField = new JTextField();
        amountField.setToolTipText("Enter numeric amount and press ENTER to convert");
        formPanel.add(amountField);

        String[] currencies = CurrencyService.getSupportedCurrenciesArray();
        formPanel.add(new JLabel("Source Currency (From):"));
        fromCombo = new JComboBox<>(currencies);
        fromCombo.setSelectedItem("USD");
        formPanel.add(fromCombo);

        formPanel.add(new JLabel("Target Currency (To):"));
        toCombo = new JComboBox<>(currencies);
        toCombo.setSelectedItem("INR");
        formPanel.add(toCombo);

        formPanel.add(new JLabel("Action:"));
        convertButton = new JButton("Convert Currency");
        convertButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        formPanel.add(convertButton);

        contentPane.add(formPanel, BorderLayout.CENTER);

        // 3. Bottom Result & Status Area
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 6));
        resultLabel = new JLabel("Result: Ready. Enter an amount and click Convert.", SwingConstants.CENTER);
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        resultLabel.setForeground(new Color(20, 90, 30));
        resultLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel hintLabel = new JLabel("Tip: Press ENTER in the amount field to convert with dialog popup.", SwingConstants.CENTER);
        hintLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        hintLabel.setForeground(Color.GRAY);

        bottomPanel.add(resultLabel, BorderLayout.CENTER);
        bottomPanel.add(hintLabel, BorderLayout.SOUTH);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);

        // --------------------------------------------------------------------
        // EVENT REGISTRATION: STYLE 1 (Registering 'this' as the ActionListener)
        // --------------------------------------------------------------------
        convertButton.addActionListener(this);
        amountField.addActionListener(this);
    }

    // ------------------------------------------------------------------------
    // EVENT HANDLING: STYLE 1 (Frame Implements ActionListener)
    // ------------------------------------------------------------------------
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == convertButton) {
            // Convert button click updates on-screen result label
            performConversion(false);
        } else if (e.getSource() == amountField) {
            // ENTER in amount field shows result in JOptionPane dialog
            performConversion(true);
        }
    }

    /**
     * Executes the currency conversion and handles visual output and errors.
     *
     * @param showDialog true if the result should also appear in a JOptionPane dialog
     */
    private void performConversion(boolean showDialog) {
        String amountText = amountField.getText();
        String from = (String) fromCombo.getSelectedItem();
        String to = (String) toCombo.getSelectedItem();

        try {
            CurrencyService.ConversionResult result = currencyService.convert(amountText, from, to);
            String summary = result.getFormattedSummary();

            resultLabel.setText("Result: " + summary);
            resultLabel.setForeground(new Color(20, 90, 30));

            if (showDialog) {
                JOptionPane.showMessageDialog(
                        this,
                        summary,
                        "Conversion Result (Style 1)",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (IllegalArgumentException ex) {
            resultLabel.setText("Error: " + ex.getMessage());
            resultLabel.setForeground(new Color(180, 30, 30));

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Confirmation dialog when user attempts to close the window.
     */
    private void confirmAndExit() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit the Currency Converter?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CurrencyConverterFrameListener app = new CurrencyConverterFrameListener();
            app.setVisible(true);
        });
    }
}

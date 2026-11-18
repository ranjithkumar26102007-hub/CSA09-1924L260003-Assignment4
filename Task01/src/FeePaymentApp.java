import javax.swing.*;
import java.awt.*;

public class FeePaymentApp extends JFrame {

    private final JTextField idField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField amountField = new JTextField();
    private final JComboBox<String> methodBox =
            new JComboBox<>(new String[]{"UPI", "Debit Card", "Net Banking"});
    private final JTextArea resultArea = new JTextArea(6, 25);

    private final JButton payButton = new JButton("Pay Fee");
    private final JButton receiptButton = new JButton("View Receipt");
    private final JButton clearButton = new JButton("Clear");

    private final FeeService feeService = new FeeService();
    private String lastReceipt = "";

    public FeePaymentApp() {
        setTitle("Student Fee Payment");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 430);
        setLocationRelativeTo(null);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 12, 18));

        formPanel.add(new JLabel("Student ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Student Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Fee Amount (Rs.):"));
        formPanel.add(amountField);
        formPanel.add(new JLabel("Payment Method:"));
        formPanel.add(methodBox);
        formPanel.add(payButton);
        formPanel.add(receiptButton);
        formPanel.add(clearButton);
        formPanel.add(new JLabel(""));

        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setBorder(BorderFactory.createTitledBorder("Payment Status"));

        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // Each button has a focused event handler.
        payButton.addActionListener(e -> handlePayment());
        receiptButton.addActionListener(e -> showReceipt());
        clearButton.addActionListener(e -> clearForm());

        receiptButton.setEnabled(false);
        setVisible(true);
    }

    private void handlePayment() {
        try {
            lastReceipt = feeService.processPayment(
                    idField.getText(),
                    nameField.getText(),
                    amountField.getText(),
                    (String) methodBox.getSelectedItem()
            );

            resultArea.setText(lastReceipt);
            receiptButton.setEnabled(true);

            JOptionPane.showMessageDialog(
                    this, "Payment completed successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IllegalArgumentException ex) {
            lastReceipt = "";
            receiptButton.setEnabled(false);
            resultArea.setText("Payment failed: " + ex.getMessage());

            JOptionPane.showMessageDialog(
                    this, ex.getMessage(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showReceipt() {
        if (lastReceipt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No successful payment is available.");
            return;
        }

        JOptionPane.showMessageDialog(
                this, lastReceipt, "Payment Receipt",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        amountField.setText("");
        methodBox.setSelectedIndex(0);
        resultArea.setText("");
        lastReceipt = "";
        receiptButton.setEnabled(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FeePaymentApp::new);
    }
}

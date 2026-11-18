public class FeeService {

    public String processPayment(String studentId,
                                 String studentName,
                                 String amountText,
                                 String paymentMethod) {

        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID is required.");
        }

        if (studentName == null || studentName.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name is required.");
        }

        if (amountText == null || amountText.trim().isEmpty()) {
            throw new IllegalArgumentException("Fee amount is required.");
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Enter a valid numeric amount.");
        }

        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("Select a payment method.");
        }

        return "PAYMENT SUCCESSFUL\n"
                + "Student ID: " + studentId.trim() + "\n"
                + "Student Name: " + studentName.trim() + "\n"
                + "Amount: Rs. " + String.format("%.2f", amount) + "\n"
                + "Payment Method: " + paymentMethod;
    }
}

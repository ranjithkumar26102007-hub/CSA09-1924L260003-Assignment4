import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Task 05 – Currency Conversion Service
 * Course: Programming in Java (CSA09)
 * Student: Ranjith Kumar.R | Register Number: 1924L260003
 * Java Version: 27 | IDE: Antigravity
 *
 * Independent business logic component for validating and executing currency conversions.
 * Uses BigDecimal for precision monetary arithmetic, completely separated from Swing GUI code.
 */
public class CurrencyService {

    // Base currency for exchange rates is USD (1 USD = rate units of target currency)
    private static final Map<String, BigDecimal> EXCHANGE_RATES;

    static {
        Map<String, BigDecimal> rates = new LinkedHashMap<>();
        rates.put("USD", new BigDecimal("1.00"));
        rates.put("INR", new BigDecimal("86.50"));
        rates.put("EUR", new BigDecimal("0.92"));
        rates.put("GBP", new BigDecimal("0.79"));
        rates.put("JPY", new BigDecimal("152.00"));
        rates.put("AUD", new BigDecimal("1.54"));
        rates.put("CAD", new BigDecimal("1.40"));
        EXCHANGE_RATES = Collections.unmodifiableMap(rates);
    }

    public static Set<String> getSupportedCurrencies() {
        return EXCHANGE_RATES.keySet();
    }

    public static String[] getSupportedCurrenciesArray() {
        return EXCHANGE_RATES.keySet().toArray(new String[0]);
    }

    /**
     * Value object representing the outcome of a currency conversion.
     */
    public record ConversionResult(
            BigDecimal fromAmount,
            String fromCurrency,
            BigDecimal toAmount,
            String toCurrency,
            BigDecimal effectiveRate
    ) {
        public String getFormattedSummary() {
            return String.format("%.2f %s = %.2f %s (Rate: 1 %s = %.4f %s)",
                    fromAmount, fromCurrency, toAmount, toCurrency, fromCurrency, effectiveRate, toCurrency);
        }
    }

    /**
     * Validates input amount text and converts between the selected currencies.
     *
     * @param amountText   raw user input string
     * @param fromCurrency source currency code
     * @param toCurrency   target currency code
     * @return ConversionResult with precise monetary values
     * @throws IllegalArgumentException if amount is missing, invalid, or non-positive
     */
    public ConversionResult convert(String amountText, String fromCurrency, String toCurrency) {
        if (amountText == null || amountText.trim().isEmpty()) {
            throw new IllegalArgumentException("Amount is required. Please enter an amount.");
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format. Please enter a valid numeric value.");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        if (!EXCHANGE_RATES.containsKey(fromCurrency)) {
            throw new IllegalArgumentException("Unsupported source currency: " + fromCurrency);
        }

        if (!EXCHANGE_RATES.containsKey(toCurrency)) {
            throw new IllegalArgumentException("Unsupported target currency: " + toCurrency);
        }

        // Conversion logic using USD as base:
        // 1. Amount in USD = Amount / Rate(From)
        // 2. Amount in Target = Amount in USD * Rate(To)
        BigDecimal fromRate = EXCHANGE_RATES.get(fromCurrency);
        BigDecimal toRate = EXCHANGE_RATES.get(toCurrency);

        // Compute effective exchange rate to 4 decimal places (standard Forex rate precision)
        BigDecimal effectiveRate = toRate.divide(fromRate, 4, RoundingMode.HALF_UP);

        // Compute converted amount directly from the displayed effective rate to ensure 100% mathematical consistency
        BigDecimal convertedAmount = amount.multiply(effectiveRate).setScale(2, RoundingMode.HALF_UP);

        return new ConversionResult(
                amount.setScale(2, RoundingMode.HALF_UP),
                fromCurrency,
                convertedAmount,
                toCurrency,
                effectiveRate
        );
    }
}

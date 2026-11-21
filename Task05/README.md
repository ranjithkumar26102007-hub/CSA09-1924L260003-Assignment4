# Task 05 – Currency Converter: Same Screen, Four Event Styles

**Course:** Programming in Java (CSA09)  
**Student:** Ranjith Kumar.R  
**Register Number:** 1924L260003  
**IDE:** Antigravity  
**Java Version:** Java 27  

---

## 1. Overview
Task 05 investigates four distinct event-handling paradigms in Java AWT/Swing by implementing the exact same **Campus Currency Converter** GUI across four standalone executable programs:
1. **Style 1 (`CurrencyConverterFrameListener.java`):** Outer `JFrame` implements `ActionListener`.
2. **Style 2 (`CurrencyConverterNamedInner.java`):** Dedicated named member inner class (`ConvertActionHandler`) implements `ActionListener`.
3. **Style 3 (`CurrencyConverterAnonymous.java`):** Inline anonymous inner classes (`new ActionListener() { ... }`).
4. **Style 4 (`CurrencyConverterApp.java`):** Primary application using Java 8+ Lambda expressions (`e -> performConversion(...)`).

### Core Features Across All Four Implementations:
- **Separation of Concerns:** Pure financial conversion and validation logic encapsulated in [`CurrencyService.java`](src/CurrencyService.java) using high-precision `BigDecimal`.
- **Supported Currencies:** USD, INR, EUR, GBP, JPY, AUD, and CAD.
- **Dual-Trigger Event Handling:**
  - Clicking **Convert Currency** updates the on-screen `resultLabel`.
  - Pressing **ENTER** inside the amount `JTextField` performs the conversion and displays a summary dialog (`JOptionPane.showMessageDialog`).
- **Defensive Window Management:** Utilizes `WindowAdapter` on `DO_NOTHING_ON_CLOSE` to display an exit confirmation dialog before releasing resources via `dispose()`.

---

## 2. Compilation and Execution Commands

Open PowerShell or Command Prompt and navigate to `Task05/src`:

```powershell
cd 'c:\Users\User\Downloads\CSA09-1924L260003-Assignment4-Task01-Starter\CSA09-1924L260003-Assignment4\Task05\src'
```

### Compile All Applications Simultaneously:
```powershell
javac CurrencyService.java CurrencyConverterFrameListener.java CurrencyConverterNamedInner.java CurrencyConverterAnonymous.java CurrencyConverterApp.java
```

### Run Any of the Four Implementations:

#### 1. Run Primary Lambda Version (Style 4):
```powershell
java CurrencyConverterApp
```

#### 2. Run Outer Frame Listener Version (Style 1):
```powershell
java CurrencyConverterFrameListener
```

#### 3. Run Named Member Inner Class Version (Style 2):
```powershell
java CurrencyConverterNamedInner
```

#### 4. Run Anonymous Inner Class Version (Style 3):
```powershell
java CurrencyConverterAnonymous
```

---

## 3. Test Cases & Verification Status

### Captured Genuine Screenshots:
- [`screenshots/01-valid-conversion.png`](screenshots/01-valid-conversion.png) – Demonstrates valid conversion of 100 USD to 8650.00 INR.
- [`screenshots/02-invalid-amount.png`](screenshots/02-invalid-amount.png) – Demonstrates error dialog on invalid/non-numeric amount entry.

### Verification Status by Style:
- **Style 4 (Lambda - Primary):** GUI, Convert button, ENTER-key popup, and invalid amount dialog fully tested and verified.
- **Style 3 (Anonymous Inner):** GUI, Convert button, ENTER-key popup, and invalid amount dialog fully tested and verified.
- **Style 1 (Frame Listener):** GUI, Convert button, and Window closing dialog tested and verified. (*ENTER-key popup test pending manual verification*).
- **Style 2 (Named Inner Class):** GUI, Convert button, and Window closing dialog tested and verified. (*ENTER-key popup test pending manual verification*).

### Functional Test Protocol:
1. **Valid Conversion:** Enter `100` USD to `INR`, click **Convert Currency**; verify result label displays `100.00 USD = 8650.00 INR (Rate: 1 USD = 86.5000 INR)`.
2. **Dialog Conversion (ENTER Key):** Enter `250.50` EUR to `USD`, press **ENTER**; verify information popup dialog displays `250.50 EUR = 272.29 USD (Rate: 1 EUR = 1.0870 USD)`.
3. **Invalid Amount:** Enter `-25` or `abc`, click **Convert**; verify modal error dialog displays appropriate validation message.
4. **Exit Confirmation:** Click window close box (`X`); verify confirmation dialog prompts before closing.

---

## 4. File Structure

```
Task05/
├── README.md                              # This documentation and test guide
├── src/
│   ├── CurrencyService.java               # Business logic, exchange rates, and BigDecimal arithmetic
│   ├── CurrencyConverterFrameListener.java# Style 1: Outer JFrame implements ActionListener
│   ├── CurrencyConverterNamedInner.java   # Style 2: Named Member Inner Class
│   ├── CurrencyConverterAnonymous.java    # Style 3: Anonymous Inner Class
│   └── CurrencyConverterApp.java          # Style 4: Lambda Expression (Primary)
├── report/
│   └── Task05-Event-Styles-Report.md      # Detailed metrics, code comparisons, and architectural report
└── screenshots/                           # Dedicated directory for genuine execution screenshots
```

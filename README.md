# CSA09-1924L260003-Assignment4

**Student:** Ranjith Kumar.R  
**Register Number:** 1924L260003  
**IDE:** Antigravity  
**Java version:** Java 27 (reported by the student)

## Assignment 4
This repository contains six task folders for the CSA09 Event-Driven GUI Design assignment.

- `Task01`: Fee Payment GUI design review and Swing implementation.
- `Task02`: Hospital outpatient appointment event-driven design.
- `Task03`: Campus library search screen and resize tests.
- `Task04`: Drawing pad mouse-event handling and repainting.
- `Task05`: Currency converter implemented using four event-listener styles.
- `Task06`: Campus event registration design and Swing/MySQL/JDBC implementation.

## Compile and run Task 01
Open a terminal in `Task01/src` and run:

```bash
javac FeeService.java FeePaymentApp.java
java FeePaymentApp
```

## Task 01 test cases
1. Enter valid student details and a positive amount; choose a payment method and click **Pay Fee**.
2. Leave Student ID empty; the application should show a validation error.
3. Enter non-numeric amount text; the application should reject it.
4. Enter zero or a negative amount; the application should reject it.
5. Complete a valid payment and click **View Receipt**.
6. Click **Clear** and verify that all fields and the status area are reset.

**Important:** Task 01 is a classroom demonstration. It does not process real payments or connect to a bank/payment gateway or database.

## Evidence
Save actual screenshots of your own program execution in each task's `screenshots` folder. Add a short note explaining which test each screenshot demonstrates. Do not use sample or fabricated screenshots.

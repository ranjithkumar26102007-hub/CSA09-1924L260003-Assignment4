# Task 01 – GUI Design Review

## Scenario

A student fee-payment window uses one listener for every button and places all application logic inside the event handler.

## Design problems and impact

1. **Mixing UI and business logic:** makes the event handler difficult to test and maintain.
2. **One shared listener for unrelated actions:** requires conditional branching and makes the code harder to extend.
3. **Insufficient input validation:** blank IDs, invalid numbers, zero, or negative amounts can be accepted or cause runtime errors.
4. **Blocking work in an event handler:** using `Thread.sleep()` or slow database work on the Swing Event Dispatch Thread (EDT) freezes the interface.
5. **Poor user feedback:** users may not know whether payment succeeded or why it failed.
6. **Receipt action not separated:** receipt generation/display should be a distinct action and should not run before a successful payment.
7. **Unsafe SQL construction:** concatenating user input into SQL can permit SQL injection; use `PreparedStatement` if a database is added.
8. **Missing resource management and exception handling:** JDBC resources should be closed with try-with-resources and failures should be handled with clear messages.
9. **Incorrect String comparison:** use `.equals()` for String content, not `==`.

## Corrected class structure

FeePaymentApp (JFrame)

&#x20;   |

&#x20;   |-- Collects student input

&#x20;   |-- Registers button listeners

&#x20;   |-- Displays status and receipt

&#x20;   |

&#x20;   v

FeeService

&#x20;   |

&#x20;   |-- Validates input

&#x20;   |-- Processes payment request

&#x20;   |-- Returns receipt or validation error

Event handling summary

* Pay Fee button -> `handlePayment()` -> `FeeService.processPayment(...)` -> display success or validation error.
* View Receipt button -> `showReceipt()` -> display the last successful receipt.
* Clear button -> `clearForm()` -> reset fields and status.

## Design justification (under 100 words)

The redesigned application separates presentation from business rules. `FeePaymentApp` collects input, handles button events, and displays messages, while `FeeService` validates the data and processes the payment request. This separation improves readability, testing, reuse, and maintenance. Dedicated listeners keep each button action focused. Validation prevents invalid amounts and missing details. The Swing UI remains responsive because long-running work is not performed in the event handler. A real database implementation should use prepared statements and close JDBC resources safely.

## Shared listener vs separate listeners / Swing Action

A shared listener reduces the number of listener objects but usually needs `getSource()` checks or command branching. Separate listeners or focused handler methods make each action easier to understand and test. A Swing `Action` is useful when the same action must be shared by multiple components, such as a menu item and toolbar button.

## Complexity analysis

* Input validation: O(1) time and O(1) auxiliary space for the fixed set of fields.
* Formatting the receipt: O(n) time and O(n) output space, where n is the length of the generated receipt text.
* GUI event dispatch: each button event invokes a bounded handler; the local demonstration has O(1) application work excluding UI rendering.

## Screenshot evidence captured after running



01-Valid-Payment.png – Successful payment with all valid fields.

02-View-Receipt.png – Receipt displayed after successful payment.

03-Clear-Form.png – Clear button resets all fields and status.

04-Empty-Student-ID.png – Required-field validation.

05-Invalid-Amount.png – Non-numeric amount validation.

06-Negative-Amount.png – Positive-amount validation.



All screenshots are saved in Task01/screenshots/.


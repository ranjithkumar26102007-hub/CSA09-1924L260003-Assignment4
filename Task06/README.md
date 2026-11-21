# Task 06 – Campus Event Registration (Swing + JDBC + MySQL)

## Student Details

| Field            | Value                          |
|------------------|-------------------------------|
| **Name**         | Ranjith Kumar.R               |
| **Register No.** | 1924L260003                   |
| **Course**       | CSA09 – Programming in Java   |
| **Assignment**   | Assignment 4 – Task 06        |
| **IDE**          | Antigravity                   |
| **Java Version** | 27                            |

---

## Overview

**Campus Event Registration** is a full-featured Java Swing desktop application that lets students register for campus events and allows the events committee to view and manage registrations in real time.

The application implements a **dual-mode architecture**:

| Mode | When active | Indicator |
|------|-------------|-----------|
| **Live MySQL** | MySQL Server is running and JDBC driver is on classpath | Green badge: `● Live MySQL Connected` |
| **Offline / Fallback Store** | MySQL unavailable or driver absent | Orange badge: `○ Mode: Offline / Fallback Store` |

Both modes expose the same GUI and all features (registration, duplicate prevention, event filtering, CSV export). The fallback store uses a thread-safe in-memory `HashMap`/`List` and is engaged automatically — no code changes are needed.

---

## Folder Structure

```
Task06/
├── README.md                        ← this file
├── sql/
│   └── schema.sql                   ← MySQL DDL (database, tables, seed data)
├── src/
│   ├── Event.java                   ← Event entity model (POJO)
│   ├── Registration.java            ← Registration entity model + toCsvLine()
│   ├── DatabaseConnection.java      ← JDBC connection manager + isAvailable() probe
│   ├── RegistrationDAO.java         ← Full JDBC DAO with in-memory fallback
│   ├── EventRegistrationService.java← Business validation layer + exportToCsv()
│   └── EventRegistrationApp.java    ← Main Swing GUI entry point (SwingWorker)
├── report/
│   └── Task06-Design-Challenge.md   ← Full design report
└── screenshots/
    ├── 01-registration-success.png  ← (capture after running)
    ├── 02-duplicate-rejected.png    ← (capture after running)
    └── 03-committee-dashboard.png   ← (capture after running)
```

---

## Features

### Student Registration Panel (left pane)
- **Student ID** – numeric only, required
- **Name** – alphabetic + spaces, required
- **Email** – validated with regex `^[\w.+-]+@[\w-]+\.[a-zA-Z]{2,}$`
- **Department** – selected from JComboBox (CS, IT, ECE, EEE, MECH, CIVIL, MBA)
- **Event** – selected from JComboBox populated from DB / fallback store
- **Register** button – runs `RegistrationService.register()` via `SwingWorker`
- **Clear Form** button – resets all fields

### Committee Dashboard (right pane)
- `JTable` displaying all registrations with columns: Reg. ID, Student ID, Name, Email, Department, Event, Registered At
- **Filter by Event** – JComboBox + Filter button; `All Events` shows the full table
- **Refresh** button – reloads from DB / fallback store
- **Export CSV** – saves current table view to `registrations_export.csv` with timestamp header

### Menu Bar
- **File → Export CSV** – same as Export button
- **File → Exit** – triggers window-close confirmation

### Window Close Confirmation
`WindowAdapter.windowClosing()` shows a `JOptionPane.showConfirmDialog`; the application exits only on **Yes**.

---

## Database Schema Summary

```sql
-- database
CREATE DATABASE IF NOT EXISTS campus_events;

-- events table
CREATE TABLE events (
    event_id   INT PRIMARY KEY AUTO_INCREMENT,
    event_name VARCHAR(150) NOT NULL UNIQUE,
    venue      VARCHAR(100),
    event_date DATE,
    capacity   INT DEFAULT 50
);

-- registrations table
CREATE TABLE registrations (
    reg_id      INT PRIMARY KEY AUTO_INCREMENT,
    student_id  VARCHAR(20)  NOT NULL,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL,
    department  VARCHAR(50),
    event_id    INT NOT NULL,
    reg_time    DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES events(event_id),
    UNIQUE (student_id, event_id)          -- prevents duplicate registration
);
```

Five sample events and five seed registrations are inserted by `schema.sql`.

---

## Compilation

### Without MySQL (Fallback / Demo Mode — no extra jar needed)

```powershell
cd 'c:\Users\User\Downloads\CSA09-1924L260003-Assignment4-Task01-Starter\CSA09-1924L260003-Assignment4\Task06\src'

javac Event.java Registration.java DatabaseConnection.java RegistrationDAO.java EventRegistrationService.java EventRegistrationApp.java
```

Expected output: **no output** (zero errors, zero warnings).

### With Live MySQL (add connector jar to classpath)

```powershell
# Replace X.Y.Z with the actual version you downloaded
javac -cp ".;mysql-connector-j-X.Y.Z.jar" Event.java Registration.java DatabaseConnection.java RegistrationDAO.java EventRegistrationService.java EventRegistrationApp.java
```

---

## Compilation Result (verified)

| File | Lines | .class produced | Inner classes |
|------|-------|-----------------|---------------|
| `Event.java` | 36 | `Event.class` (945 B) | — |
| `Registration.java` | 70 | `Registration.class` (1 811 B) | — |
| `DatabaseConnection.java` | 75 | `DatabaseConnection.class` (1 826 B) | — |
| `RegistrationDAO.java` | 310 | `RegistrationDAO.class` (9 294 B) | — |
| `EventRegistrationService.java` | 125 | `EventRegistrationService.class` (3 133 B) | — |
| `EventRegistrationApp.java` | 760+ | `EventRegistrationApp.class` (15 771 B) | `$1`–`$6` |
| **Total** | **~1 376** | **12 .class files** | |

**Compiler:** `javac 27` — **0 errors, 0 warnings**

---

## Running the Application

### Fallback mode (no MySQL required)

```powershell
cd 'c:\Users\User\Downloads\CSA09-1924L260003-Assignment4-Task01-Starter\CSA09-1924L260003-Assignment4\Task06\src'
java EventRegistrationApp
```

### Live MySQL mode

```powershell
# 1. Start MySQL Server and import schema
mysql -u root -p < ..\sql\schema.sql

# 2. Run with JDBC driver on classpath
java -cp ".;mysql-connector-j-X.Y.Z.jar" EventRegistrationApp
```

> **Connection defaults:** `localhost:3306`, database `campus_events`, user `root`, password `root`.
> Edit `DatabaseConnection.java` constants to match your MySQL credentials.

---

## MySQL Setup (for live mode)

1. Install **MySQL Server 8.0+** – https://dev.mysql.com/downloads/installer/
2. Download **MySQL Connector/J** – https://dev.mysql.com/downloads/connector/j/ (Platform Independent, .zip)
3. Extract and note the path to `mysql-connector-j-X.Y.Z.jar`
4. Run the schema:
   ```bash
   mysql -u root -p < Task06/sql/schema.sql
   ```
5. Compile and run with the jar on the classpath (see commands above)

---

## Manual Test Checklist

| # | Action | Expected Result |
|---|--------|-----------------|
| 1 | Launch app | Fallback or Live badge visible; table loads 4–5 seed registrations |
| 2 | Submit empty form | Validation error: `Student ID is required.` |
| 3 | Enter invalid email | Error: `Invalid email address format.` |
| 4 | Register a valid new student | Success dialog; new row appears in table |
| 5 | Register same student + same event again | Error: `DUPLICATE_REGISTRATION: Student … is already registered for …` |
| 6 | Filter by a specific event | Table shows only registrations for that event |
| 7 | Filter → `All Events` | Full table restored |
| 8 | Click **Export CSV** | `registrations_export.csv` saved; success dialog shown |
| 9 | File → Exit (or close window) | Confirmation dialog appears; app exits on Yes, stays on No |

---

## Key Design Decisions

| Decision | Rationale |
|----------|-----------|
| `SwingWorker<T, Void>` for all DB operations | Keeps EDT responsive; never blocks the GUI thread during JDBC calls |
| `PreparedStatement` throughout DAO | Prevents SQL injection; allows parameter binding |
| `try-with-resources` on every `Connection` / `Statement` | Guarantees resource release even on exception |
| `synchronized` in-memory fallback | Thread-safe access from multiple `SwingWorker` background threads |
| `UNIQUE(student_id, event_id)` constraint | Duplicate enforcement at database level (backed up by Java-level check in fallback mode) |
| `toCsvLine()` on `Registration` | Keeps CSV formatting logic inside the entity class, away from the GUI |
| `WindowAdapter` for close confirmation | Only method required for `windowClosing`; avoids implementing 6 empty methods of `WindowListener` |

---

## Screenshots

Capture the following genuine screenshots during manual testing and save them in `Task06/screenshots/`:

| Filename | What to capture |
|----------|-----------------|
| `01-registration-success.png` | App after a successful student registration (table updated) |
| `02-duplicate-rejected.png` | Error dialog when same student registers for the same event twice |
| `03-committee-dashboard.png` | Committee table with event filter active |

---

## Report

See [`report/Task06-Design-Challenge.md`](report/Task06-Design-Challenge.md) for:
- Architecture diagram (Presentation / Service / DAO / Model / DB layers)
- Event-flow table (all user actions → listeners → DAO → UI update)
- SwingWorker thread lifecycle explanation
- PreparedStatement vs. Statement security analysis
- Duplicate-prevention strategy (DB UNIQUE + fallback HashMap check)
- CSV export design
- Complexity and scalability analysis

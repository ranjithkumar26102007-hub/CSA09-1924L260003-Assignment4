# Task 03 – Campus Library Search GUI: From Sketch to Layout

**Course:** Programming in Java (CSA09)  
**Student:** Ranjith Kumar.R  
**Register Number:** 1924L260003  
**IDE:** Antigravity  
**Java Version:** Java 27  

---

## 1. Overview
Task 03 implements a responsive desktop Campus Library Book Search and Reservation application using Java Swing. The architecture uses composite nested layout managers (`BorderLayout` and `FlowLayout`) to guarantee that:
- The top header and search controls remain ergonomically centered and fixed in height.
- The central results area (`JTable` embedded in a `JScrollPane`) expands dynamically to utilize all available screen space when the window is resized.
- The bottom navigation buttons and status bar remain pinned to the bottom.

---

## 2. Compile and Run Instructions

Open PowerShell or Command Prompt, navigate to the `Task03/src` directory, and execute:

```powershell
cd 'c:\Users\User\Downloads\CSA09-1924L260003-Assignment4-Task01-Starter\CSA09-1924L260003-Assignment4\Task03\src'
javac LibrarySearchApp.java
java LibrarySearchApp
```

---

## 3. Test Cases and Functionality Summary

1. **Catalog Search & Filtering:**
   - Type a keyword (e.g. `Algorithms`, `Data`, `Clean`) and click **Search** (or press `ENTER`).
   - Filter by academic disciplines using the **Category** dropdown (e.g. *Computer Science*, *Electronics*, *Mathematics*).
   - Click **Show All** to reset filters and view the full in-memory catalog.
2. **Sequential Navigation:**
   - Click **Next ▶** to advance row selection downwards; wraps around to row 1 when reaching the bottom.
   - Click **◀ Previous** to navigate upwards; wraps around to the last row when at the top.
   - The status bar dynamically reflects the active book title, author, and status.
3. **Book Reservation:**
   - Select an available book and click **Reserve Selected Book**.
   - Confirmation dialog displays reservation details and counter collection guidance.
   - The book's status in the table and in-memory model updates to `Reserved`.
   - Attempting to reserve an already reserved book shows a polite warning dialog.
4. **Resizing Tests (700x500, 900x600, 1200x800):**
   - Use the built-in **Window Size (Resize Tests)** menu bar to instantly test all 3 dimensions.
   - Observe how the table viewport expands while top/bottom panels remain fixed.

---

## 4. Deliverables and Structure

```
Task03/
├── README.md                      # This overview and execution guide
├── src/
│   ├── LibrarySearchApp.java      # Working Java Swing implementation
│   ├── LibrarySearchApp.class     # Compiled bytecode
│   ├── LibrarySearchApp$Book.class
│   └── LibrarySearchApp$1.class
├── report/
│   └── Task03-Layout-Design-Report.md  # Detailed layout architecture report
└── screenshots/                   # Folder for saving genuine test execution screenshots
```

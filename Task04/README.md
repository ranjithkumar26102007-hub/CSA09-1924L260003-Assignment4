# Task 04 – Event-Handling Forensics (Drawing Pad)

**Course:** Programming in Java (CSA09)  
**Student:** Ranjith Kumar.R  
**Register Number:** 1924L260003  
**IDE:** Antigravity  
**Java Version:** Java 27  

---

## 1. Overview
Task 04 diagnoses and repairs two classic event-handling and repainting defects in a Java Swing Drawing Pad application:
1. **Silent Handler Failure:** An incorrectly named method (`mouseClick` instead of `mouseClicked`) without `@Override` in a `MouseAdapter` subclass prevented mouse click events from being recognized by Swing's Event Dispatch Thread.
2. **Repainting Amnesia:** Direct painting via `getGraphics()` caused previously drawn dots to permanently disappear when the window was minimized, restored, or resized.

The repaired application stores clicked mouse positions as `Point` objects in an in-memory collection, updates the click counter synchronously, and delegates all rendering to `paintComponent(Graphics g)` through the Swing repainting pipeline.

---

## 2. Compile and Run Instructions

Open PowerShell or Command Prompt, navigate to `Task04/src`, and run:

```powershell
cd 'c:\Users\User\Downloads\CSA09-1924L260003-Assignment4-Task01-Starter\CSA09-1924L260003-Assignment4\Task04\src'
javac DrawingPadApp.java
java DrawingPadApp
```

---

## 3. Testing and Verification Summary

1. **Mouse Click & Dot Rendering:** Click anywhere on the canvas; a crisp red dot appears immediately at the cursor position.
2. **Counter Synchronization:** The status bar updates in real-time (`Total Clicks: X | Points Stored: X`).
3. **Minimization & Restoration Test:**
   - Plot several dots on the canvas.
   - Minimize the window to the taskbar.
   - Restore the window; verify that **all plotted dots remain visible** and the counter remains accurate.
4. **Resizing Test:** Resize the window; observe that the canvas scales smoothly and dots remain anchored to their coordinates.
5. **Clear Canvas:** Click `Clear Canvas` to wipe all dots and reset the counter.

---

## 4. Deliverables and Structure

```
Task04/
├── README.md                          # Task overview, compile/run commands, and test summary
├── src/
│   ├── DrawingPadApp.java             # Repaired Java Swing program with completed TODOs
│   ├── DrawingPadApp.class            # Compiled bytecode
│   ├── DrawingPadApp$DrawingCanvas.class
│   └── DrawingPadApp$1.class
├── report/
│   └── Task04-Event-Handling-Forensics.md  # Detailed forensic investigation report
└── screenshots/                       # Folder for saving genuine test execution screenshots
```

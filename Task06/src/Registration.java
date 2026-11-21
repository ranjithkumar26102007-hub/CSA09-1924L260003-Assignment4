/**
 * Task 06 – Registration Entity Model
 * Course: Programming in Java (CSA09)
 * Student: Ranjith Kumar.R | Register Number: 1924L260003
 * Java Version: 27 | IDE: Antigravity
 */
public class Registration {
    private final int registrationId;
    private final String studentId;
    private final String studentName;
    private final String studentEmail;
    private final String department;
    private final int eventId;
    private final String eventName;
    private final String timestamp;

    public Registration(int registrationId, String studentId, String studentName,
                        String studentEmail, String department, int eventId,
                        String eventName, String timestamp) {
        this.registrationId = registrationId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.department = department;
        this.eventId = eventId;
        this.eventName = eventName;
        this.timestamp = timestamp;
    }

    public int getRegistrationId() { return registrationId; }
    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getStudentEmail() { return studentEmail; }
    public String getDepartment() { return department; }
    public int getEventId() { return eventId; }
    public String getEventName() { return eventName; }
    public String getTimestamp() { return timestamp; }

    /**
     * Formats the registration record as a comma-separated CSV line.
     */
    public String toCsvLine() {
        return String.format("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                registrationId,
                escapeQuotes(studentId),
                escapeQuotes(studentName),
                escapeQuotes(studentEmail),
                escapeQuotes(department),
                escapeQuotes(eventName),
                escapeQuotes(timestamp));
    }

    private String escapeQuotes(String val) {
        return val == null ? "" : val.replace("\"", "\"\"");
    }
}

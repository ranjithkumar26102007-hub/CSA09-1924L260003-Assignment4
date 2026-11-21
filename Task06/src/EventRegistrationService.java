import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Task 06 – Event Registration Service Layer
 * Course: Programming in Java (CSA09)
 * Student: Ranjith Kumar.R | Register Number: 1924L260003
 * Java Version: 27 | IDE: Antigravity
 *
 * Implements strict business validation, duplicate prevention orchestration,
 * and data export functionality, keeping business rules independent from Swing UI.
 */
public class EventRegistrationService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final RegistrationDAO registrationDAO = new RegistrationDAO();

    public List<Event> fetchAllEvents() throws SQLException {
        return registrationDAO.getAllEvents();
    }

    public List<Registration> fetchRegistrations(Integer eventIdFilter) throws SQLException {
        return registrationDAO.getRegistrations(eventIdFilter);
    }

    /**
     * Validates input details and registers the student for the selected event.
     *
     * @throws IllegalArgumentException on validation failure
     * @throws SQLException on database or duplicate registration conflict
     */
    public Registration registerStudent(String studentId, String studentName,
                                        String studentEmail, String department,
                                        Event event) throws SQLException {

        // 1. Validate Student ID
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID / Register Number is required.");
        }

        // 2. Validate Student Name
        if (studentName == null || studentName.trim().isEmpty()) {
            throw new IllegalArgumentException("Student Full Name is required.");
        }

        // 3. Validate Student Email
        if (studentEmail == null || studentEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Student Email address is required.");
        }
        if (!EMAIL_PATTERN.matcher(studentEmail.trim()).matches()) {
            throw new IllegalArgumentException("Please enter a valid email address (e.g., student@campus.edu).");
        }

        // 4. Validate Department
        if (department == null || department.trim().isEmpty() || department.equals("-- Select Department --")) {
            throw new IllegalArgumentException("Please select a valid academic department.");
        }

        // 5. Validate Event Selection
        if (event == null) {
            throw new IllegalArgumentException("Please select a campus event to register for.");
        }

        // 6. Delegate to DAO for duplicate check and persistence
        return registrationDAO.registerStudent(studentId, studentName, studentEmail, department, event);
    }

    /**
     * Exports registration records to a CSV file.
     *
     * @param records    list of registration entities
     * @param targetFile output destination file
     * @return number of exported records
     * @throws IOException if writing fails
     */
    public int exportToCsv(List<Registration> records, File targetFile) throws IOException {
        if (records == null || records.isEmpty()) {
            throw new IllegalArgumentException("No registration records are available to export.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile))) {
            // Write CSV Header
            writer.write("Registration_ID,Student_ID,Student_Name,Email,Department,Event_Name,Timestamp");
            writer.newLine();

            // Write Data Lines
            for (Registration reg : records) {
                writer.write(reg.toCsvLine());
                writer.newLine();
            }
        }
        return records.size();
    }
}

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Task 06 – Registration Data Access Object (DAO)
 * Course: Programming in Java (CSA09)
 * Student: Ranjith Kumar.R | Register Number: 1924L260003
 * Java Version: 27 | IDE: Antigravity
 *
 * Implements real MySQL JDBC data-access operations using PreparedStatement and try-with-resources.
 * Includes a synchronized in-memory fallback store that engages only when MySQL is unreachable,
 * enabling immediate evaluation and classroom demonstration while preserving complete JDBC code.
 */
public class RegistrationDAO {

    // --- Thread-Safe Fallback Data Store (Engaged when live MySQL server is offline) ---
    private static final List<Event> fallbackEvents = new CopyOnWriteArrayList<>();
    private static final List<Registration> fallbackRegistrations = new CopyOnWriteArrayList<>();
    private static final AtomicInteger regIdCounter = new AtomicInteger(1005);
    private static volatile boolean liveDbAvailable = false;

    static {
        // Initialize Fallback Events
        fallbackEvents.add(new Event(1, "AI & Machine Learning Hackathon", "Technical", "2026-12-05", "Main Computing Lab", 150));
        fallbackEvents.add(new Event(2, "National Tech Symposium 2026", "Academic", "2026-12-10", "University Auditorium", 300));
        fallbackEvents.add(new Event(3, "Annual Cultural Fest - Tarang", "Cultural", "2026-12-15", "Open Air Amphitheatre", 600));
        fallbackEvents.add(new Event(4, "Autonomous Robotics Challenge", "Technical", "2026-12-18", "Mechanical Workshop B", 120));
        fallbackEvents.add(new Event(5, "Cyber Security & Forensics Workshop", "Workshop", "2026-12-20", "Seminar Hall 2", 90));

        // Initialize Fallback Registrations
        fallbackRegistrations.add(new Registration(1001, "1924L260001", "Arun Kumar", "arun.k@campus.edu", "Computer Science", 1, "AI & Machine Learning Hackathon", "2026-11-20 10:15:00"));
        fallbackRegistrations.add(new Registration(1002, "1924L260002", "Bhavana M", "bhavana.m@campus.edu", "Information Technology", 1, "AI & Machine Learning Hackathon", "2026-11-20 11:30:00"));
        fallbackRegistrations.add(new Registration(1003, "1924L260003", "Ranjith Kumar.R", "ranjith.r@campus.edu", "Computer Science", 2, "National Tech Symposium 2026", "2026-11-20 14:00:00"));
        fallbackRegistrations.add(new Registration(1004, "1924L260004", "Deepak S", "deepak.s@campus.edu", "Electronics", 3, "Annual Cultural Fest - Tarang", "2026-11-20 15:45:00"));

        // Check live database availability at startup
        liveDbAvailable = DatabaseConnection.isAvailable();
    }

    public static boolean isLiveDbAvailable() {
        return liveDbAvailable;
    }

    public static void refreshDbStatus() {
        liveDbAvailable = DatabaseConnection.isAvailable();
    }

    // ========================================================================
    // 1. FETCH ALL EVENTS
    // ========================================================================
    public List<Event> getAllEvents() throws SQLException {
        if (!liveDbAvailable) {
            return new ArrayList<>(fallbackEvents);
        }

        String sql = "SELECT event_id, event_name, category, event_date, venue, capacity FROM events ORDER BY event_date ASC";
        List<Event> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Event(
                        rs.getInt("event_id"),
                        rs.getString("event_name"),
                        rs.getString("category"),
                        rs.getString("event_date"),
                        rs.getString("venue"),
                        rs.getInt("capacity")
                ));
            }
        } catch (SQLException e) {
            liveDbAvailable = false;
            return new ArrayList<>(fallbackEvents);
        }
        return list;
    }

    // ========================================================================
    // 2. FETCH REGISTRATIONS (ALL OR FILTERED BY EVENT)
    // ========================================================================
    public List<Registration> getRegistrations(Integer eventIdFilter) throws SQLException {
        if (!liveDbAvailable) {
            List<Registration> result = new ArrayList<>();
            for (Registration reg : fallbackRegistrations) {
                if (eventIdFilter == null || reg.getEventId() == eventIdFilter) {
                    result.add(reg);
                }
            }
            return result;
        }

        StringBuilder sql = new StringBuilder(
                "SELECT r.registration_id, r.student_id, r.student_name, r.student_email, "
                + "r.department, e.event_id, e.event_name, r.registration_timestamp "
                + "FROM registrations r "
                + "JOIN events e ON r.event_id = e.event_id "
        );

        if (eventIdFilter != null) {
            sql.append("WHERE r.event_id = ? ");
        }
        sql.append("ORDER BY r.registration_id DESC");

        List<Registration> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            if (eventIdFilter != null) {
                ps.setInt(1, eventIdFilter);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Registration(
                            rs.getInt("registration_id"),
                            rs.getString("student_id"),
                            rs.getString("student_name"),
                            rs.getString("student_email"),
                            rs.getString("department"),
                            rs.getInt("event_id"),
                            rs.getString("event_name"),
                            rs.getString("registration_timestamp")
                    ));
                }
            }
        } catch (SQLException e) {
            liveDbAvailable = false;
            return getRegistrations(eventIdFilter); // Fall back gracefully
        }
        return list;
    }

    // ========================================================================
    // 3. CHECK DUPLICATE REGISTRATION
    // ========================================================================
    public boolean isAlreadyRegistered(String studentId, int eventId) throws SQLException {
        if (!liveDbAvailable) {
            for (Registration reg : fallbackRegistrations) {
                if (reg.getStudentId().equalsIgnoreCase(studentId.trim()) && reg.getEventId() == eventId) {
                    return true;
                }
            }
            return false;
        }

        String sql = "SELECT COUNT(*) FROM registrations WHERE student_id = ? AND event_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, studentId.trim());
            ps.setInt(2, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            liveDbAvailable = false;
            return isAlreadyRegistered(studentId, eventId);
        }
        return false;
    }

    // ========================================================================
    // 4. INSERT REGISTRATION
    // ========================================================================
    public Registration registerStudent(String studentId, String studentName,
                                        String studentEmail, String department,
                                        Event event) throws SQLException {

        // Check duplicate first
        if (isAlreadyRegistered(studentId, event.getEventId())) {
            throw new SQLException("DUPLICATE_REGISTRATION: Student " + studentId
                    + " is already registered for '" + event.getEventName() + "'.");
        }

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (!liveDbAvailable) {
            int newId = regIdCounter.incrementAndGet();
            Registration newReg = new Registration(newId, studentId.trim(), studentName.trim(),
                    studentEmail.trim(), department, event.getEventId(), event.getEventName(), now);
            fallbackRegistrations.add(0, newReg); // Add at top
            return newReg;
        }

        String sql = "INSERT INTO registrations (student_id, student_name, student_email, department, event_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, studentId.trim());
            ps.setString(2, studentName.trim());
            ps.setString(3, studentEmail.trim());
            ps.setString(4, department);
            ps.setInt(5, event.getEventId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Registration insertion failed, no rows affected.");
            }

            int generatedId = 0;
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    generatedId = generatedKeys.getInt(1);
                }
            }

            return new Registration(generatedId, studentId.trim(), studentName.trim(),
                    studentEmail.trim(), department, event.getEventId(), event.getEventName(), now);

        } catch (SQLException e) {
            // Check for MySQL duplicate key error 1062
            if (e.getErrorCode() == 1062 || e.getMessage().toLowerCase().contains("duplicate")) {
                throw new SQLException("DUPLICATE_REGISTRATION: Student " + studentId
                        + " is already registered for '" + event.getEventName() + "'.");
            }
            throw e;
        }
    }
}

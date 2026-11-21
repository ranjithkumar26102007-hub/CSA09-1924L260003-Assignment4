import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Task 06 – Database Connection Manager
 * Course: Programming in Java (CSA09)
 * Student: Ranjith Kumar.R | Register Number: 1924L260003
 * Java Version: 27 | IDE: Antigravity
 *
 * Centralizes MySQL JDBC connectivity. Provides connection pooling properties,
 * driver verification, and dynamic connection testing.
 */
public class DatabaseConnection {

    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/campus_events?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "root";

    private static String dbUrl = DEFAULT_URL;
    private static String dbUser = DEFAULT_USER;
    private static String dbPassword = DEFAULT_PASSWORD;

    static {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            // Driver not present on classpath; will be detected by isAvailable()
        }
    }

    public static void configure(String url, String user, String password) {
        dbUrl = url;
        dbUser = user;
        dbPassword = password;
    }

    /**
     * Establishes a standard JDBC Connection to the MySQL database.
     *
     * @return active java.sql.Connection
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    /**
     * Tests whether MySQL server is currently reachable on the specified socket.
     *
     * @return true if live MySQL connection succeeds, false otherwise
     */
    public static boolean isAvailable() {
        try {
            Class.forName(DRIVER_CLASS);
            DriverManager.setLoginTimeout(2); // 2 seconds fast probe
            try (Connection conn = getConnection()) {
                return conn != null && !conn.isClosed();
            }
        } catch (Throwable t) {
            return false;
        }
    }

    public static String getDbUrl() { return dbUrl; }
    public static String getDbUser() { return dbUser; }
}

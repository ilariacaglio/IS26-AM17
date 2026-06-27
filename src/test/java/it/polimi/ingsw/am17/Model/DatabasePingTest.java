package it.polimi.ingsw.am17.Model;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabasePingTest {

    static void main() {
        System.out.println("Starting Database connection test...");

        try {
            // Load environment variables
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            String url = dotenv.get("DB_URL");
            String username = dotenv.get("DB_USER");
            String password = dotenv.get("DB_PASSWORD");

            System.out.println("URL located: " + (url != null ? "YES (" + url + ")" : "NO"));
            System.out.println("USER located: " + (username != null ? "YES" : "NO"));
            System.out.println("PASSWORD located: " + (password != null ? "YES" : "NO"));

            if (url == null || username == null) {
                System.err.println("ERROR: Missing environment variables. Please verify the .env file configuration.");
                return;
            }

            // Attempt the real connection
            System.out.println("\nAttempting to establish connection...");
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                if (connection.isValid(5)) {
                    System.out.println("SUCCESS: Database connection established successfully.");
                } else {
                    System.err.println("WARNING: Connection established, but validation check failed.");
                }
            }

        } catch (SQLException e) {
            System.err.println("FAILURE: Unable to connect to the database.");
            System.err.println("Reason: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("UNEXPECTED ERROR: " + e.getMessage());
        }
    }
}
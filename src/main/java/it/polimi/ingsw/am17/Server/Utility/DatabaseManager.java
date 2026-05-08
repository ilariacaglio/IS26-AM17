package it.polimi.ingsw.am17.Server.Utility;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;
import java.time.LocalDate;
import java.util.UUID;

public class DatabaseManager {

    // Loading file .env and connection data
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = dotenv.get("DB_URL");
    private static final String USERNAME = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    /**
     * Inserts a new game into the game table
     */
    public void insertGame (UUID gameId, int numPlayers) {
        // structure of the query
        String gameInsertQuery = "INSERT INTO Game(gameid, date, numplayers) VALUES(?, ?, ?)";

        try (   // establish connection to database
                Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

                // pre-compile the query
                PreparedStatement statement = connection.prepareStatement(gameInsertQuery);) {

            // bind parameters to placeholders
            statement.setObject(1, gameId);
            statement.setDate(2, Date.valueOf(LocalDate.now()));
            statement.setInt(3, numPlayers);

            // send the query to db
            statement.executeUpdate();

            // TODO: print logging
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertPlayer (String nickname, UUID gameId, int finalPoints) {

    }
}
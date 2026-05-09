package it.polimi.ingsw.am17.Server.Utility;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;
import java.time.LocalDate;
import java.util.UUID;
import java.util.logging.Logger;

public class DatabaseManager {

    // Loading file .env and connection data
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = dotenv.get("DB_URL");
    private static final String USERNAME = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    private final Logger logger = Logger.getLogger(DatabaseManager.class.getName());

    /**
     * Inserts a new game into the game table
     */
    public void insertGame (UUID gameId, int numPlayers) {
        logger.info("Inserting game " + gameId + " with " + numPlayers + " players");
        // structure of the query
        String gameInsertQuery = "INSERT INTO Game(gameid, date, numplayers) VALUES(?, ?, ?)";

        try (   // establish connection to database
                Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

                // pre-compile the query
                PreparedStatement statement = connection.prepareStatement(gameInsertQuery)) {

            // bind parameters to placeholders
            statement.setObject(1, gameId);
            statement.setDate(2, Date.valueOf(LocalDate.now()));
            statement.setInt(3, numPlayers);

            // send the query to db
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Inserts a player into the player table
     * @param nickname      the nickname to be inserted
     * @param gameId        the gameId to be inserted
     * @param finalPoints   the finalPoints to be inserted
     */
    public void insertPlayer (String nickname, UUID gameId, int finalPoints) {
        logger.info("Inserting player " + nickname + " with " + finalPoints + " points into game " + gameId);
        // query structure
        String playerInsertQuery = "INSERT INTO Player(nickname, gameid, finalpoints) VALUES(?, ?)";
        try (   // establish connection to database
                Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

                // pre-compile the query
                PreparedStatement statement = connection.prepareStatement(playerInsertQuery)){

            // bind parameters to placeholders
            statement.setString(1, nickname);
            statement.setObject(2, gameId);
            statement.setInt(3, finalPoints);

            // send the query to db
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
package it.polimi.ingsw.am17.Server.Utility;

import io.github.cdimascio.dotenv.Dotenv;
import it.polimi.ingsw.am17.Server.Model.Game;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

/**
 * Utility class that sends query to hosted (remote) postgreSQL database
 */
public class DatabaseManager {

    // Loading file .env and connection data
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private static final String URL = dotenv.get("DB_URL");
    private static final String USERNAME = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    private static final Logger logger = Logger.getLogger(DatabaseManager.class.getName());

    /**
     * Inserts a game into the game table
     *
     * @param gameId        the gameId to be inserted
     * @param numPlayers    the numPlayers to be inserted
     * @param connection     the open connection to db
     */
    private static void insertGame (UUID gameId, int numPlayers, Connection connection) throws SQLException {
        logger.info("Inserting game " + gameId + " with " + numPlayers + " players");
        // structure of the query
        String gameInsertQuery = "INSERT INTO Game(gameid, date, numplayers) VALUES(?, ?, ?)";

        try (   // pre-compile the query
                PreparedStatement statement = connection.prepareStatement(gameInsertQuery)) {

            // bind parameters to placeholders
            statement.setObject(1, gameId);
            statement.setDate(2, Date.valueOf(LocalDate.now()));
            statement.setInt(3, numPlayers);

            // send the query to db
            statement.executeUpdate();
        }
    }

    /**
     * Inserts a player into the player table
     * NB: a player is linked to a specific game
     *
     * @param nickname      the nickname to be inserted
     * @param gameId        the gameId to be inserted
     * @param finalPoints   the finalPoints to be inserted
     * @param connection    the open connection to db
     */
    private static void insertPlayer (String nickname, UUID gameId, int finalPoints, Connection connection) throws SQLException {
        logger.info("Inserting player " + nickname + " with " + finalPoints + " points into game " + gameId);
        // query structure
        String playerInsertQuery = "INSERT INTO Player(nickname, gameid, finalpoints) VALUES(?, ?, ?)";
        try (   // pre-compile the query
                PreparedStatement statement = connection.prepareStatement(playerInsertQuery)) {

            // bind parameters to placeholders
            statement.setString(1, nickname);
            statement.setObject(2, gameId);
            statement.setInt(3, finalPoints);

            // send the query to db
            statement.executeUpdate();
        }
    }

    /**
     * Inserts game data into db
     *
     * @param game  the game to save
     */
    public static void insertGameData (Game game) {
        logger.info("Inserting game data into game " + game.getId());
        try (   // establish connection to database
                Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)){

            // begin transaction
            connection.setAutoCommit(false);

            try {
                // get players
                var players = game.getPlayersList();

                // insert game data
                insertGame(game.getId(), players.size(), connection);

                // insert player data
                for (Player player : players) {
                    insertPlayer(player.getNickname(), game.getId(), player.getPp(), connection);
                }

                // commit changes
                connection.commit();
                logger.info("Successfully inserted game data into game " + game.getId());
            } catch (SQLException ex) {
                // rollback changes
                connection.rollback();
                logger.severe("Transaction failed " + ex.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }
    }

    /**
     * gets the ranking of all players of games with the specified number of players
     * @param numPlayers    the specified number of players
     * @return              the raking as a list
     */
    public static List<RankingEntry> getRanking (int numPlayers) {
        logger.info("Getting ranking of games with " + numPlayers + " players");
        List<RankingEntry> ranking = new ArrayList<>();
        String query = """
            SELECT g.gameid, g.date, p.nickname, p.finalpoints
            FROM Game g
            JOIN Player p ON p.gameid = g.gameid
            WHERE g.numplayers = ?
            ORDER BY p.finalpoints DESC
            """;
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            // set numPlayers parameter
            statement.setInt(1, numPlayers);

            // get query results
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                // get values
                UUID gameId = resultSet.getObject("gameid", UUID.class);
                LocalDate date = resultSet.getDate("date").toLocalDate();
                String nickname = resultSet.getString("nickname");
                int finalPoints = resultSet.getInt("finalpoints");

                // insert entry into list
                ranking.add(new RankingEntry(gameId, date, nickname, finalPoints));
            }

        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }
        return ranking;
    }
}
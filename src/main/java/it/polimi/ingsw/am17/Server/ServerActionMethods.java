package it.polimi.ingsw.am17.Server;

import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import it.polimi.ingsw.am17.Server.Controller.GamesController;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Used by RMI/Socket servers to handle client requests.
 * A request is handled in a new thread to avoid blocking the server.
 */
public class ServerActionMethods {
    private final static Logger logger = Logger.getLogger(ServerActionMethods.class.getName());

    /**
     * Sends an update to the client with the list of open games.
     * @param controller to get the game list.
     * @param client to send the update.
     */
    public static void getGamesList(GamesController controller, VirtualView client) {
        new Thread(() -> {
            logger.info("Client" + client.getClass().getSimpleName() + " requested the games list.");
            try {

                // send the list of open games to the client
                client.updateGamesIdList(controller.getGamesList());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }


    /**
     * Creates a game with the specified number of players and adds the player creating it to the game.
     * Signs up the client as an observer for the game.
     * @param controller controller to forward the request to.
     * @param client client to send the gameId.
     * @param player player creating the game.
     * @param numPlayers number of players for the game.
     */
    public static void createGame(GamesController controller, VirtualView client, Player player, int numPlayers) {
        new Thread(() -> {
            logger.info("Client" + client.getClass().getSimpleName() + " wants to create a new game with " + numPlayers + " players.");

            // create a game and add player to it
            UUID id = controller.createGame(player, numPlayers);

            // sign up client as an observer
            controller.signUpAsObserver(client, id);

            // send gameId to client
            try {
                client.updateGameId(id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /**
     * Closes a game.
     * TODO: add check on "creator" player, i.e. only the creator can close the game.
     * TODO: use player parameter (changes in closeGame)
     * @param controller controller to forward the request to.
     * @param client client generating the request.
     * @param player associated with the client generating the request.
     * @param gameId of the game to close.
     */
    public static void closeGame(GamesController controller, VirtualView client, Player player, UUID gameId) {
        new Thread(() -> {
            logger.info("Client" + client.getClass().getSimpleName() + " wants to close game with id" + gameId);

            controller.closeGame(client);
        }).start();
    }


    /**
     * Adds a player to the game with the specified gameId and register the client as an observer.
     * @param controller to forward the request to.
     * @param client to send the gameId.
     * @param gameId of the game to join.
     * @param player to add to the game.
     */
    public static void joinGame(GamesController controller, VirtualView client, UUID gameId, Player player) {
        new Thread(() -> {
            try {
                logger.info("Client" + client.getClass().getSimpleName() + " wants to join game with id " + gameId + " as player " + player.getNickname());

                // Sign up client as an observer (see N.B. hereunder)
                controller.signUpAsObserver(client, gameId);

                // Notify gameId to the client
                client.updateGameId(gameId);

                // Add player to the game
                controller.joinGame(gameId, player);

            } catch (Exception e) {
                logger.warning("Error joining game: " + e.getMessage());
                try { // TODO: why the try catch?
                    // N.B. we need to sign up the client before joining the player
                    // so that it's notified from the addPlayer, if something goes wrong,
                    // we remove it here.

                    // Remove the client from the observers.
                    controller.removeClientAsObserver(client, gameId);
                } catch (Exception ignored) {}
            }
        }).start();
    }


    /**
     * Forward a pickOfferingCard request to the controller.
     * @param controller to forward the request to.
     * @param gameId to forward to the controller.
     * @param player to forward to the controller.
     * @param offeringCard to forward to the controller.
     */
    public static void pickOfferingCard(GamesController controller, UUID gameId, Player player, OfferingCard offeringCard){
        new Thread(() -> {
            // N.B.: here a client is not passed, the "pick" methods are conceptually different
            // TODO: we still should check that the request comes from the right client tho.
            logger.info("pickOfferingCard request received"); // TODO: improve
            controller.pickOfferingCard(gameId, player, offeringCard);
        }).start();
    }

    /**
     * Forward a pickTribeCards request to the controller.
     * @param controller to forward the request to.
     * @param gameId to forward to the controller.
     * @param player to forward to the controller.
     * @param characterCards to forward to the controller.
     * @param buildingCards to forward to the controller.
     */
    public static void pickTribeCards(GamesController controller, UUID gameId, Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) {
        new Thread(() -> {
            logger.info("pickTribeCards request received"); // TODO: improve
            controller.pickTribeCards(gameId, player, characterCards, buildingCards);
        }).start();
    }
}

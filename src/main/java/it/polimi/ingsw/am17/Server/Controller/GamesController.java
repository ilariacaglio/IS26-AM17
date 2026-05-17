package it.polimi.ingsw.am17.Server.Controller;

import it.polimi.ingsw.am17.Server.Model.Game;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;

import java.util.*;
import java.util.logging.Logger;

/**
 * Manages multiple games with an eye to concurrency.
 * Receives requests from the client via a ServerSocket/RMI and forwards it to the model.
 */
public class GamesController {
    private static final List<Game> gamesList = new ArrayList<>();
    private static final Map<VirtualView, UUID> gameMapping = new HashMap<>();
    private static final Map<VirtualView, String> playerMapping = new HashMap<>(); // string field is for nickname
    private final static Logger logger = Logger.getLogger(GamesController.class.getName());

    public GamesController(){}

    /**
     * Adds a game to the games list.
     * @param game the game to be added
     */
    private void addGame(Game game) {
        synchronized (gamesList){
            gamesList.add(game);
        }
    }

    /**
     * @return the ids list of the games that are not started yet
     */
    private List<UUID> getGamesList() {
        synchronized (gamesList){
            return gamesList.stream()
                    .filter(g -> !g.isStarted())
                    .map(Game::getId)
                    .toList(); // returns an unmodifiable list!
        }
    }

    /**
     * @param id game id
     * @return the game in the list with the given id
     */
    private Game getGameFromId(UUID id) throws NoSuchElementException {
        synchronized (gamesList){
            return gamesList
                    .stream()
                    .filter(game -> game.getId().equals(id))
                    .findFirst().orElseThrow();
        }
    }

    /**
     * remove a game from the games list.
     * @param id game id
     */
    private void removeGameFromId(UUID id) throws NoSuchElementException {
        synchronized (gamesList){
            gamesList.remove(getGameFromId(id));
        }
    }

    /**
     * Signs up a client as an observer of a game (model).
     * @param client to be registered
     * @param gameId of the game
     */
    private void signUpAsObserver(VirtualView client, UUID gameId) throws NoSuchElementException {
        Game game = getGameFromId(gameId);

        // add client to game mapping
        gameMapping.put(client, gameId);

        synchronized (game) {
            game.attach(client);
        }
    }

    /**
     * Removes a client from the game's (model) observer list.
     * @param client to be removed
     * @param gameId of the game
     */
    private void removeClientAsObserver(VirtualView client, UUID gameId) throws NoSuchElementException {
        Game game = getGameFromId(gameId);
        synchronized (game){
            game.detach(client);
        }
    }

    //** public methods: CLIENT ACTIONs **//
    // Used by RMI/Socket servers to handle client requests.
    // N.B. all requests are handled in a new thread to avoid blocking the controller.

    /**
     * Creates a game with the specified number of players and adds the player creating it to the game.
     * Signs up the client as an observer for the game.
     * @param client client to send the gameId.
     * @param player player creating the game.
     * @param numPlayers number of players for the game.
     */
    public void createGame(VirtualView client, Player player, int numPlayers) {
        new Thread(() -> {
            logger.info("Client" + client.getClass().getSimpleName() + " wants to create a new game with " + numPlayers + " players.");

            // game creation
            UUID id = UUID.randomUUID();
            Game game = new Game(id, numPlayers);

            // add game to the list
            addGame(game);

            // add player to the game
            joinGame(client, id, player);

            // send gameId to client
            try {
                client.updateGameId(id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /**
     * Adds a player to the game with the specified gameId and registers the client as an observer.
     * @param client to send the gameId.
     * @param gameId of the game to join.
     * @param player to add to the game.
     */
    public void joinGame(VirtualView client, UUID gameId, Player player) throws NoSuchElementException {
        new Thread(() -> {
            logger.info("Client" + client.getClass().getSimpleName() + " wants to join game with id " + gameId + " as player " + player.getNickname());

            // Sign up client as an observer (see N.B. hereunder)
            signUpAsObserver(client, gameId);

            // Notify gameId to the client
            try {
                client.updateGameId(gameId);
            } catch (Exception e) {
                logger.warning("Error sending game Id update: " + e.getMessage());
                throw new RuntimeException(e);
            }

            // Add player to the game
            try {
                Game game = getGameFromId(gameId);
                synchronized (game) {
                    game.addPlayer(player);
                }
            } catch (Exception e) {
                logger.warning("Error joining game: " + e.getMessage());

                // N.B. we need to sign up the client before joining the player
                // so that it's notified from the addPlayer, if something goes wrong,
                // we remove it here.

                removeClientAsObserver(client, gameId);
                throw new RuntimeException(e);
            }
        }).start();
    }

    /**
     * Closes a game (also when a player disconnects [unexpectedly]).
     * TODO: add check on "creator" player, i.e. only the creator can close the game.
     * TODO: use player parameter and add it to the notify (who closed the game?) for unexpected disconnections.
     * @param client client generating the request.
     * @param player associated with the client generating the request.
     */
    public void closeGame(VirtualView client, Player player) {
        new Thread(() -> {

        // get uuid of the game from the client (mapping)
        UUID uuid = gameMapping.get(client);

        // get the game object from uuid to call the end game method
        Game game = getGameFromId(uuid);

        try {
            synchronized (game) {
                game.forceEndGame();
            }
        }
        catch (Exception e) {
            logger.warning("Error calling forceEndGame: " + e.getMessage());
            throw new RuntimeException(e);
        }

        // remove client from the game's observer list
        removeClientAsObserver(client, uuid); // would be fine if moved in the Subject's notifyEndGame

        // get the clients linked to the game with uuid
        var clientsList = gameMapping.entrySet().stream()
                .filter(entry -> entry.getValue().equals(uuid))
                .map(Map.Entry::getKey)
                .toList();
        // remove the client from the mapping and the game from the list
        clientsList.forEach(gameMapping.keySet()::remove);
        // remove all the players of that game
        clientsList.forEach(playerMapping.keySet()::remove);
        // remove game from id list
        removeGameFromId(uuid);
    }).start();

    }

    /**
     * Forwards a pickOfferingCard request to the game.
     * @param gameId of the game
     * @param player selecting the card
     * @param offeringCard to be selected
     */
    public void pickOfferingCard(UUID gameId, Player player, OfferingCard offeringCard) {
        new Thread(() -> {
            // N.B.: here a client is not passed, the "pick" methods are conceptually different
            // TODO: we still should check that the request comes from the right client tho.
            logger.info(player.getNickname() + " wants to pick offering card " + offeringCard.getOrderLetter() + " in game " + gameId);

            try {
                Game game = getGameFromId(gameId);
                synchronized (game) {
                    game.selectOfferingCard(player, offeringCard);
                }
            }
            catch(Exception e) {
                logger.warning("Error calling game selectOfferingCard: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }).start();
    }

    /**
     * Forwards a pickTribeCards request to the game.
     * @param gameId of the game
     * @param player selecting the cards
     * @param characterCards selected by the player
     * @param buildingCards selected by the player
     */
    public void pickTribeCards(UUID gameId, Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) {
        new Thread(() -> {
            logger.info(player.getNickname() + " wants to pick tribe cards " + characterCards + " and " + buildingCards + " in game " + gameId);

            try {
                Game game = getGameFromId(gameId);
                synchronized (game) {
                    game.pickTribeCards(player, characterCards, buildingCards);
                }
            } catch (Exception e) {
                logger.warning("Error calling game pickTribeCards: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }).start();
    }


    /**
     * Sends an update to the client with the list of open games.
     * @param controller to get the game list.
     * @param client to send the update.
     */
    public void getGamesList(GamesController controller, VirtualView client) {
        new Thread(() -> {
            logger.info("Client" + client.getClass().getSimpleName() + " requested the games list.");
            try {

                // send the list of open games to the client
                client.updateGamesIdList(controller.getGamesList());
            } catch (Exception e) {
                logger.warning("Error sending games list: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }).start();
    }
}
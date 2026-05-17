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
    private final List<Game> gamesList = new ArrayList<>();
    private final Map<VirtualView, UUID> mapping = new HashMap<>();

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
    public List<UUID> getGamesList() {
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
    public void removeGameFromId(UUID id) throws NoSuchElementException {
        synchronized (gamesList){
            gamesList.remove(getGameFromId(id));
        }
    }

    /**
     * Signs up a client as an observer of a game (model).
     * @param client to be registered
     * @param gameId of the game
     */
    public void signUpAsObserver(VirtualView client, UUID gameId) throws NoSuchElementException {
        Game game = getGameFromId(gameId);

        // add client to game mapping
        mapping.put(client, gameId);

        synchronized (game) {
            game.attach(client);
        }
    }

    /**
     * Removes a client from the game's (model) observer list.
     * @param client to be removed
     * @param gameId of the game
     */
    public void removeClientAsObserver(VirtualView client, UUID gameId) throws NoSuchElementException {
        Game game = getGameFromId(gameId);
        synchronized (game){
            game.detach(client);
        }
    }

    //** CLIENT ACTIONS **//

    /**
     * Creates a game and adds the creator to it.
     * @param player creating the game.
     * @param numPlayers number of players for the game.
     * @return created game id.
     */
    public UUID createGame(Player player, int numPlayers) {
        try {

            // game creation
            UUID id = UUID.randomUUID();
            Game game = new Game(id, numPlayers);

            // add game to the list
            addGame(game);

            // add player to the game
            joinGame(id, player);

            return id;
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a player to the game with the given id.
     * @param gameId of the game
     * @param player to be added
     */
    public void joinGame(UUID gameId, Player player) throws NoSuchElementException {
        try {
            Game game = getGameFromId(gameId);
            synchronized (game){
                game.addPlayer(player);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes a game when a player disconnects [unexpectedly].
     */
    public void closeGame(VirtualView client) {

        // get uuid of the game from the client (mapping)
        UUID uuid = mapping.get(client);

        // get the game object from uuid to call the end game method
        Game game = getGameFromId(uuid);

        try {
            synchronized (game) {
                game.forceEndGame();
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        // remove client from the game's observer list
        removeClientAsObserver(client, uuid); // would be fine if moved in the Subject's notifyEndGame

        // remove the client from the mapping and the game from the list
        mapping.remove(client);
        removeGameFromId(uuid);
    }

    //** PLAYER ACTIONS **//

    /**
     * Forwards a pickOfferingCard request to the game.
     * @param gameId of the game
     * @param player selecting the card
     * @param card to be selected
     */
    public void pickOfferingCard(UUID gameId, Player player, OfferingCard card) {
        try{
            Game game = getGameFromId(gameId);
            synchronized (game) {
                game.selectOfferingCard(player,card);
            }
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Forwards a pickTribeCards request to the game.
     * @param gameId of the game
     * @param player selecting the cards
     * @param characterCards selected by the player
     * @param buildingCards selected by the player
     */
    public void pickTribeCards(UUID gameId, Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards){
        try {
            Game game = getGameFromId(gameId);
            synchronized (game) {
                game.pickTribeCards(player, characterCards, buildingCards);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Used by RMI/Socket servers to handle client requests.
     * A request is handled in a new thread to avoid blocking the server.
     * SERVER ACTION METHODS START
     */
        private final Logger logger = Logger.getLogger(GamesController.class.getName());

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
        public void createGame(GamesController controller, VirtualView client, Player player, int numPlayers) {
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
        public void closeGame(GamesController controller, VirtualView client, Player player, UUID gameId) {
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
        public void joinGame(GamesController controller, VirtualView client, UUID gameId, Player player) {
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
        public void pickOfferingCard(GamesController controller, UUID gameId, Player player, OfferingCard offeringCard){
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
        public void pickTribeCards(GamesController controller, UUID gameId, Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) {
            new Thread(() -> {
                logger.info("pickTribeCards request received"); // TODO: improve
                controller.pickTribeCards(gameId, player, characterCards, buildingCards);
            }).start();
        }
    }
package it.polimi.ingsw.am17.Server.Controller;

import it.polimi.ingsw.am17.CommonInterfaces.ErrorType;
import it.polimi.ingsw.am17.CommonInterfaces.InvalidOperationException;
import it.polimi.ingsw.am17.Server.Model.Game;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualClient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Manages multiple games with an eye to concurrency.
 * Receives requests from the client via a ServerSocket/RMI and forwards it to the model.
 */
public class GamesController implements ControllerInterface {
    // record (immutable data) that contains the link between a gameId and a player nickname
    private record PlayerContext(UUID gameId, String nickname) {}

    // hash map that contains clients and links them to a game and a player nickname
    private final ConcurrentHashMap<VirtualClient, PlayerContext> clientContexts;

    // hash map that contains all the (started or in lobby) games
    private final ConcurrentHashMap<UUID,Game> games;

    private final static Logger logger = Logger.getLogger(GamesController.class.getName());

    public GamesController(){
        games = new ConcurrentHashMap<>();
        clientContexts = new ConcurrentHashMap<>();
    }

    /**
     * Adds a game to the games map.
     * @param game the game to be added
     */
    private void addGame(Game game) {
       games.put(game.getId(), game);
    }

    /**
     * @return the ids list of the games that are not started yet
     */
    private List<UUID> getLobbyGamesList() {
        return games.values().stream()
                .filter(g -> !g.isStarted())
                .map(Game::getId)
                .toList(); // returns an unmodifiable list!
    }

    /**
     * @param id game id
     * @return the game in the map with the given id
     */
    private Game getGameFromId(UUID id){
        Game game = games.get(id);
        if (game == null)
            throw new InvalidOperationException(ErrorType.INVALID_GAME);
        return game;
    }

    /**
     * Removes a game from the games map.
     * @param id game id
     */
    private void removeGameFromId(UUID id) throws NoSuchElementException {
        games.remove(id);
    }

    /**
     * Adds the clients data into the context map and signs up the client as an observer of a game (model).
     * @param client to be registered
     * @param gameId of the game
     * @param nickname of the player linked to the client
     */
    private void registerClient(VirtualClient client, UUID gameId, String nickname){
        // add client to mapping
        clientContexts.put(client, new PlayerContext(gameId, nickname));
        signUpAsObserver(client, gameId);
    }

    /**
     * Signs up a client as an observer of a game (model).
     * @param client to be registered
     * @param gameId of the game
     */
    private void signUpAsObserver(VirtualClient client, UUID gameId) {
        Game game = getGameFromId(gameId);
        synchronized (game) {
            game.attach(client);
        }
    }

    /**
     * Removes a client from the game's observer list and from the client's context map.
     * @param client to be removed
     */
    private void unregisterClient(VirtualClient client) {
        removeClientAsObserver(client);
        // remove clients data from mapping
        clientContexts.remove(client);
    }

    /**
     * Removes a client from the game's (model) observer list.
     * @param client to be removed
     */
    private void removeClientAsObserver(VirtualClient client) {
        PlayerContext context = clientContexts.get(client);

        // if the client is not bound to a game and a player, the game is already closed
        if (context == null) {
            // do nothing, return early
            return;
        }

        Game game = games.get(context.gameId());
        // check the game exists before synchronizing
        if (game != null) {
            synchronized (game){
                game.detach(client);
            }
        }
    }

    /**
     * Calls updateColorError on the specified client
     *
     * @param client       the client to be notified
     * @param exception    the exception thrown
     */
    private void notifyErrorToClient(VirtualClient client, InvalidOperationException exception) {
        try {
            client.updateError(exception);
        }
        catch (Exception networkEx){
            logger.info("Could not send notification to client: " + networkEx.getMessage());
        }
    }

    /**
     * Removes client from observer list  and notifies error to the client
     * @param client                the client that made the join request
     * @param dataAdded             if true, the client was added to the maps and as an observer.
     */
    private void revertClientRegistration(VirtualClient client, boolean dataAdded) {
        // N.B. we need to sign up the client before joining the player
        // so that it's notified from the addPlayer, if something goes wrong,
        // we remove it here.
        if (dataAdded) {
            try {
                unregisterClient(client);
            } catch (Exception ex) {
                logger.warning("Error removing client's data: " + ex.getMessage());
            }
        }
    }

    /**
     * Adds a player to the game with the specified gameId and registers the client as an observer.
     * This is a utility method called by both createGame and joinGame, contains the joinGame logic.
     * @param client to send the gameId.
     * @param gameId of the game to join.
     * @param player to add to the game.
     */
    private void handleJoinGame(VirtualClient client, UUID gameId, Player player) throws Exception {
        // sign if client is added as an observer
        boolean observerAdded = false;
        try {
            // retrieve game
            Game game = getGameFromId(gameId);

            // Put client in the game's observer list and add the gameId to the client's context
            registerClient(client, gameId, player.getNickname());
            observerAdded = true;

            // Notify gameId to the client
            client.updateGameId(gameId);

            // Add player to the game
            synchronized (game) {
                // check that the game exists before adding the player
                // this check is necessary because the game can be removed from the map
                // after the get request at line 174
                if (!games.containsKey(gameId)) {
                    throw new InvalidOperationException(ErrorType.INVALID_GAME);
                }
                game.addPlayer(player);
            }
        } catch (Exception e) {
            revertClientRegistration(client, observerAdded);
            // re-throw exception to caller to notify the error to the client
            throw e;
        }
    }

    //** public methods: CLIENT ACTIONS **//
    // Used by RMI/Socket servers to handle client requests.

    /**
     * Creates a game with the specified number of players and adds the player creating it to the game.
     * Signs up the client as an observer for the game.
     * @param client client to send the gameId.
     * @param player player creating the game.
     * @param numPlayers number of players for the game.
     */
    @Override
    public void createGame(VirtualClient client, Player player, int numPlayers) {
        logger.info("Client " + client.getClass().getSimpleName() + " wants to create a new game with " + numPlayers + " players.");

        try {
             // game creation
            UUID id = UUID.randomUUID();
            Game game = new Game(id, numPlayers);

            // add game to the list
            addGame(game);

            // add player to the game
            handleJoinGame(client, id, player);
        }
        catch (InvalidOperationException e) {
            logger.info("Error creating game: " + e.getErrorType().getMessage());
            notifyErrorToClient(client, e);
        }
        catch (Exception e) {
            String message = e.getMessage();
            logger.info("Error creating game: " + message);
            notifyErrorToClient(client, new InvalidOperationException(message));
        }
    }

    /**
     * Adds a player to the game with the specified gameId and registers the client as an observer.
     * When needed, it notifies errors to the client.
     * This method is called by server classes and does not contain any joinGame logic.
     * @param client that makes the request.
     * @param gameId of the game to join.
     * @param player to add to the game.
     */
    @Override
    public void joinGame(VirtualClient client, UUID gameId, Player player) {
        logger.info("Client " + client.getClass().getSimpleName() + " wants to join game with id " + gameId + " as player " + player.getNickname());
        try {
            handleJoinGame(client, gameId, player);

        } catch (InvalidOperationException e) {
            logger.info("Error joining game: " + e.getErrorType().getMessage());
            notifyErrorToClient(client, e);

        } catch (Exception e) {
            String message = e.getMessage();
            logger.warning("Error joining game: " + message);
            notifyErrorToClient(client, new InvalidOperationException(message));
        }
    }

    /**
     * Closes a game (also when a player disconnects [unexpectedly]).
     * @param client client generating the request.
     */
    @Override
    public void closeGame(VirtualClient client) {
        try {
            // get game and player data from the map
            PlayerContext context = clientContexts.get(client);
            if (context == null){
                logger.info("Client " + client.getClass().getSimpleName() + " disconnected without an active context.");
                return;
            }

            // get game id and player nickname from the context
            UUID uuid = context.gameId();
            String nickname = context.nickname();

            // get the game object from uuid to call the end game method
            Game game = getGameFromId(uuid);

            // remove client's data before calling forceEndGame to avoid network errors when notifying through observer pattern
            unregisterClient(client);

            // close the game
            synchronized (game) {
                // check that the game exists before adding the player
                // this check is necessary because the game can be removed from the map
                // after the get request at line 280
                if (!games.containsKey(uuid)) {
                    throw new InvalidOperationException(ErrorType.INVALID_GAME);
                }
                game.forceEndGame(nickname);
            }

            // Get all the clients related to the game
            List<VirtualClient> clientsToRemove = clientContexts.entrySet().stream()
                    .filter(entry -> entry.getValue().gameId().equals(uuid))
                    .map(Map.Entry::getKey)
                    .toList();

            // remove all the clients from the game's observer list and maps
            for (VirtualClient c : clientsToRemove) {
                unregisterClient(c);
            }

            // remove game from id map
            removeGameFromId(uuid);
        }
        catch (InvalidOperationException e) {
            if (e.getErrorType() == ErrorType.INVALID_GAME) {
                logger.warning("Error calling forceEndGame: game is already closed");
            } else {
                logger.warning("Error calling forceEndGame: " + e.getErrorType().getMessage());
                notifyErrorToClient(client, e);
            }
        }
        catch (Exception e) {
            String message = e.getMessage();
            logger.warning("Error calling forceEndGame: " + message);
            notifyErrorToClient(client, new InvalidOperationException(message));
        }
    }

    /**
     * Forwards a pickOfferingCard request to the game.
     * @param client                the client who made the request
     * @param offeringCardLetter    letter of the offering card to be selected
     */
    @Override
    public void pickOfferingCard(VirtualClient client, Character offeringCardLetter) {
        try {
            PlayerContext context = clientContexts.get(client);

            // retrieve game
            Game game = getGameFromId(context.gameId());

            logger.info(context.nickname() + " wants to pick offering card " + offeringCardLetter + " in game " + context.gameId());

            synchronized (game) {
                game.selectOfferingCard(context.nickname(), offeringCardLetter);
            }
        }
        catch(InvalidOperationException e) {
            logger.warning("Error calling game selectOfferingCard: " + e.getErrorType().getMessage());
            notifyErrorToClient(client, e);
        }
        catch(Exception e) {
            String message = e.getMessage();
            logger.warning("Error calling game selectOfferingCard: " +  message);
            notifyErrorToClient(client, new InvalidOperationException(message));
        }
    }

    /**
     * Forwards a pickTribeCards request to the game.
     * @param client            the client who made the request
     * @param characterCards    selected by the player
     * @param buildingCards     selected by the player
     */
    @Override
    public void pickTribeCards(VirtualClient client, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) {
        try {
            PlayerContext context = clientContexts.get(client);
            logger.info(context.nickname() + " wants to pick tribe cards " + characterCards + " and " + buildingCards + " in game " + context.gameId());
            Game game = getGameFromId(context.gameId());
            synchronized (game) {
                game.pickTribeCards(context.nickname(), characterCards, buildingCards);
            }
        } catch (InvalidOperationException e) {
            logger.warning("Error calling game pickTribeCards: " + e.getMessage());
            notifyErrorToClient(client, e);
        }
        catch (Exception e) {
            String message = e.getMessage();
            logger.warning("Error calling game pickTribeCards: " + message);
            notifyErrorToClient(client, new InvalidOperationException(message));
        }
    }


    /**
     * Sends an update to the client with the list of open games.
     * @param client to send the update to.
     */
    @Override
    public void getGamesList(VirtualClient client) {
        logger.info("Client " + client.getClass().getSimpleName() + " requested the games list.");
        try {
            // send the list of open games (not already started) to the client
            client.updateGamesIdList(getLobbyGamesList());
        } catch (Exception e) {
            String message = e.getMessage();
            logger.warning("Error sending games list: " + message);
            notifyErrorToClient(client, new InvalidOperationException(message));
        }
    }
}
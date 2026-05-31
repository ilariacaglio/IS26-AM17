package it.polimi.ingsw.am17.Server.Controller;

import it.polimi.ingsw.am17.CommonInterfaces.ErrorType;
import it.polimi.ingsw.am17.CommonInterfaces.InvalidOperationException;
import it.polimi.ingsw.am17.Server.Model.Game;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Manages multiple games with an eye to concurrency.
 * Receives requests from the client via a ServerSocket/RMI and forwards it to the model.
 */
public class GamesController {
    private final ConcurrentHashMap<UUID,Game> games;
    private final ConcurrentHashMap<VirtualView, UUID> gameMapping;
    private final ConcurrentHashMap<VirtualView, String> playerMapping; // string field is for nickname

    private final static Logger logger = Logger.getLogger(GamesController.class.getName());

    public GamesController(){
        games = new ConcurrentHashMap<>();
        gameMapping = new ConcurrentHashMap<>();
        playerMapping = new ConcurrentHashMap<>();
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
    private List<UUID> getGamesList() {
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
     * remove a game from the games map.
     * @param id game id
     */
    private void removeGameFromId(UUID id) throws NoSuchElementException {
        if (id != null) games.remove(id);
    }

    /**
     * Signs up a client as an observer of a game (model).
     * @param client to be registered
     * @param gameId of the game
     */
    private void signUpAsObserver(VirtualView client, UUID gameId) {
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
    private void removeClientAsObserver(VirtualView client, UUID gameId) {
        Game game = getGameFromId(gameId);
        synchronized (game){
            game.detach(client);
        }
    }

    /**
     * Calls updateColorError on the specified client
     *
     * @param client       the client to be notified
     * @param exception    the exception thrown
     */
    private void notifyErrorToClient(VirtualView client, InvalidOperationException exception) {
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
     * @param gameId                the id of the game to be joined
     * @param observerAdded         if true, the client was added to the list as on observer of the game
     */
    private void rollbackObserverAdded(VirtualView client, UUID gameId, boolean observerAdded) {
        // N.B. we need to sign up the client before joining the player
        // so that it's notified from the addPlayer, if something goes wrong,
        // we remove it here.
        if (observerAdded) {
            try {
                removeClientAsObserver(client, gameId);
            } catch (Exception ex) {
                logger.warning("Error removing client as observer: " + ex.getMessage());
            }
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
    public void createGame(VirtualView client, Player player, int numPlayers) {
        logger.info("Client " + client.getClass().getSimpleName() + " wants to create a new game with " + numPlayers + " players.");

        try {
             // game creation
            UUID id = UUID.randomUUID();
            Game game = new Game(id, numPlayers);

            // add game to the list
            addGame(game);

            // add player to the game
            joinGame(client, id, player);
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
     * @param client to send the gameId.
     * @param gameId of the game to join.
     * @param player to add to the game.
     */
    public void joinGame(VirtualView client, UUID gameId, Player player) {
        logger.info("Client " + client.getClass().getSimpleName() + " wants to join game with id " + gameId + " as player " + player.getNickname());

        // sign if client is added as an observer
        boolean observerAdded = false;

        try {
            // retrieve game (also check if it exists)
            Game game = getGameFromId(gameId);

            // Sign up client as an observer
            signUpAsObserver(client, gameId);
            observerAdded = true;

            // Notify gameId to the client
            client.updateGameId(gameId);

            // Add player to the game
            synchronized (game) {
                game.addPlayer(player);
            }

            // add player to mapping
            playerMapping.put(client, player.getNickname());
        } catch (InvalidOperationException e) {
            logger.info("Error joining game: " + e.getErrorType().getMessage());
            rollbackObserverAdded(client,  gameId, observerAdded);
            notifyErrorToClient(client, e);

        } catch (Exception e) {
            String message = e.getMessage();
            logger.warning("Error joining game: " + message);
            rollbackObserverAdded(client,  gameId, observerAdded);
            notifyErrorToClient(client, new InvalidOperationException(message));
        }
    }

    /**
     * Closes a game (also when a player disconnects [unexpectedly]).
     * @param client client generating the request.
     */
    public void closeGame(VirtualView client) {
        try {
            // get uuid of the game from the client (mapping)
            UUID uuid = gameMapping.get(client);

            // get the game object from uuid to call the end game method
            Game game = getGameFromId(uuid);

            // get the nickname of the player
            String nickname = playerMapping.get(client);

            synchronized (game) {
                game.forceEndGame(nickname);
            }

            // remove client from the game's observer list
            removeClientAsObserver(client, uuid);

            // remove clients and game from maps
            gameMapping.entrySet().removeIf(entry -> {
                if (entry.getValue().equals(uuid)) {
                    playerMapping.remove(entry.getKey());
                    return true;
                }
                return false;
            });

            // remove game from id map
            removeGameFromId(uuid);
        }
        catch (InvalidOperationException e) {
            logger.warning("Error calling forceEndGame: " +  e.getErrorType().getMessage());
            notifyErrorToClient(client, e);
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
    public void pickOfferingCard(VirtualView client, Character offeringCardLetter) {
        try {
            // search for client nickname
            String nickname = playerMapping.get(client);
            // search for game id
            UUID gameId = gameMapping.get(client);

            // retrieve game
            Game game = getGameFromId(gameId);

            logger.info(nickname + " wants to pick offering card " + offeringCardLetter + " in game " + gameId);

            synchronized (game) {
                game.selectOfferingCard(nickname, offeringCardLetter);
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
    public void pickTribeCards(VirtualView client, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) {
        try {
            // search for client nickname
            String nickname = playerMapping.get(client);

            // search for game id
            UUID gameId = gameMapping.get(client);

            logger.info(nickname + " wants to pick tribe cards " + characterCards + " and " + buildingCards + " in game " + gameId);
            Game game = getGameFromId(gameId);
            synchronized (game) {
                game.pickTribeCards(nickname, characterCards, buildingCards);
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
     * @param client to send the update.
     */
    public void getGamesList(VirtualView client) {
        logger.info("Client " + client.getClass().getSimpleName() + " requested the games list.");
        try {
            // send the list of open games to the client
            client.updateGamesIdList(getGamesList());
        } catch (Exception e) {
            String message = e.getMessage();
            logger.warning("Error sending games list: " + message);
            notifyErrorToClient(client, new InvalidOperationException(message));
        }
    }
}
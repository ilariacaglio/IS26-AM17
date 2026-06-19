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
    private record ClientSession(UUID gameId, String nickname) {}

    // hash map that contains all the games (started or in lobby)
    private final ConcurrentHashMap<UUID,Game> games;

    // hash map that contains client relations to a game and a player nickname
    private final ConcurrentHashMap<VirtualClient, ClientSession> clientSessions;

    private final static Logger logger = Logger.getLogger(GamesController.class.getName());

    public GamesController(){
        games = new ConcurrentHashMap<>();
        clientSessions = new ConcurrentHashMap<>();
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
     * remove a game from the games map.
     * @param id game id
     */
    private void removeGameFromId(UUID id) throws NoSuchElementException {
        games.remove(id);
    }

    /**
     * Adds the clients data into the session map and signs up the client as an observer of a game (model).
     * @param client to be registered
     * @param gameId of the game
     * @param nickname of the player linked to the client
     */
    // todo: check name
    private void addClientData(VirtualClient client, UUID gameId, String nickname){
        // add client to mapping
        clientSessions.put(client, new ClientSession(gameId, nickname));
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
     * Removes a client from the game's observer list and from the client's session map.
     * @param client to be removed
     */
    private void removeClientsData(VirtualClient client) {
        removeClientAsObserver(client);
        // remove clients data from mapping
        clientSessions.remove(client);
    }

    /**
     * Removes a client from the game's (model) observer list.
     * @param client to be removed
     */
    private void removeClientAsObserver(VirtualClient client) {
        UUID gameIdFromMapping = clientSessions.get(client).gameId();
        Game game = games.get(gameIdFromMapping);
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
    // TODO: change this name
    private void rollbackClientsDataAdded(VirtualClient client, boolean dataAdded) {
        // N.B. we need to sign up the client before joining the player
        // so that it's notified from the addPlayer, if something goes wrong,
        // we remove it here.
        if (dataAdded) {
            try {
                removeClientsData(client);
            } catch (Exception ex) {
                logger.warning("Error removing client's data: " + ex.getMessage());
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
    // TODO: check this
    @Override
    public void joinGame(VirtualClient client, UUID gameId, Player player) {
        logger.info("Client " + client.getClass().getSimpleName() + " wants to join game with id " + gameId + " as player " + player.getNickname());

        // sign if client is added as an observer
        boolean observerAdded = false;

        try {
            // retrieve game
            Game game = getGameFromId(gameId);

            // Put client in the game's observer list and add the gameId to the client's session
            addClientData(client, gameId, player.getNickname());
            observerAdded = true;

            // Notify gameId to the client
            client.updateGameId(gameId);

            // Add player to the game
            synchronized (game) {
                // check that the game exists before adding the player
                // this check is necessary because the game can be removed from the map
                // after the get request at line 189
                if (!games.containsKey(gameId)) {
                    throw new InvalidOperationException(ErrorType.INVALID_GAME);
                }
                game.addPlayer(player);
            }
        } catch (InvalidOperationException e) {
            logger.info("Error joining game: " + e.getErrorType().getMessage());
            rollbackClientsDataAdded(client, observerAdded);
            notifyErrorToClient(client, e);

        } catch (Exception e) {
            String message = e.getMessage();
            logger.warning("Error joining game: " + message);
            rollbackClientsDataAdded(client, observerAdded);
            notifyErrorToClient(client, new InvalidOperationException(message));
        }
    }

    /**
     * Closes a game (also when a player disconnects [unexpectedly]).
     * @param client client generating the request.
     */
    // TODO: check this
    @Override
    public void closeGame(VirtualClient client) {
        try {
            // get game and player data from the map
            ClientSession session = clientSessions.get(client);
            if (session == null){
                logger.info(client + " tried to close an already closed game.");
                return;
            }

            // get game id and player nickname from the session
            UUID uuid = session.gameId();
            String nickname = session.nickname();

            // get the game object from uuid to call the end game method
            Game game = getGameFromId(uuid);

            // Get all the clients related to the game
            List<VirtualClient> clientsToRemove = new ArrayList<>();
            clientSessions.forEach((c, s) -> {
                if (s.gameId().equals(uuid)) {
                    clientsToRemove.add(c);
                }
            });

            // close the game
            synchronized (game) {
                if (!games.containsKey(uuid)) {
                    throw new InvalidOperationException(ErrorType.INVALID_GAME);
                }
                game.forceEndGame(nickname);
            }

            // remove all the clients from the game's observer list and maps
            for (VirtualClient c : clientsToRemove) {
                removeClientsData(c);
            }

            // remove game from id map
            removeGameFromId(uuid);
        }
        catch (InvalidOperationException e) {
            logger.warning("Error calling forceEndGame: " +  e.getErrorType().getMessage());
            // notify error only if game isn't already closed
            if(!e.getErrorType().equals(ErrorType.INVALID_GAME)){
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
            ClientSession session = clientSessions.get(client);

            // retrieve game
            Game game = getGameFromId(session.gameId());

            logger.info(session.nickname() + " wants to pick offering card " + offeringCardLetter + " in game " + session.gameId());

            synchronized (game) {
                game.selectOfferingCard(session.nickname(), offeringCardLetter);
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
            ClientSession session = clientSessions.get(client);
            logger.info(session.nickname() + " wants to pick tribe cards " + characterCards + " and " + buildingCards + " in game " + session.gameId());
            Game game = getGameFromId(session.gameId());
            synchronized (game) {
                game.pickTribeCards(session.nickname(), characterCards, buildingCards);
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
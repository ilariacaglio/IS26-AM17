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
 * Manages multiple games with an eye to concurrency. TODO: check
 * Receives requests from the client via a ServerSocket/RMI and forwards it to the model.
 */
public class GamesController implements ControllerInterface {
    // Immutable data structure that contains client relations to a game and a player nickname
    private record ClientSession(UUID gameId, String nickname) {}

    private final ConcurrentHashMap<UUID,Game> games;
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
     * @param nickname of the player linked to the client
     */
    private void signUpAsObserver(VirtualClient client, UUID gameId, String nickname) {
        Game game = getGameFromId(gameId);

        // add client to mapping
        clientSessions.put(client, new ClientSession(gameId, nickname));

        synchronized (game) {
            game.attach(client);
        }
    }

    /**
     * Removes a client from the game's (model) observer list.
     * @param client to be removed
     */
    private void removeClientAsObserver(VirtualClient client) {
        UUID gameIdFromMapping = clientSessions.get(client).gameId();
        Game game = games.get(gameIdFromMapping);
        clientSessions.remove(client);
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
     * @param observerAdded         if true, the client was added to the list as on observer of the game
     */
    private void rollbackObserverAdded(VirtualClient client, boolean observerAdded) {
        // N.B. we need to sign up the client before joining the player
        // so that it's notified from the addPlayer, if something goes wrong,
        // we remove it here.
        if (observerAdded) {
            try {
                removeClientAsObserver(client);
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
    @Override
    public void joinGame(VirtualClient client, UUID gameId, Player player) {
        logger.info("Client " + client.getClass().getSimpleName() + " wants to join game with id " + gameId + " as player " + player.getNickname());

        // sign if client is added as an observer
        boolean observerAdded = false;

        try {
            // retrieve game (also check if it exists)
            Game game = getGameFromId(gameId);

            // Sign up client as an observer
            signUpAsObserver(client, gameId, player.getNickname());
            observerAdded = true;

            // Notify gameId to the client
            client.updateGameId(gameId);

            // Add player to the game
            synchronized (game) {
                // check that the game exists before adding the player
                if (!games.containsKey(gameId)) {
                    throw new InvalidOperationException(ErrorType.INVALID_GAME);
                }
                game.addPlayer(player);
            }
        } catch (InvalidOperationException e) {
            logger.info("Error joining game: " + e.getErrorType().getMessage());
            rollbackObserverAdded(client, observerAdded);
            notifyErrorToClient(client, e);

        } catch (Exception e) {
            String message = e.getMessage();
            logger.warning("Error joining game: " + message);
            rollbackObserverAdded(client, observerAdded);
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
            // get uuid of the game from the client (mapping)
            ClientSession session = clientSessions.get(client);
            if (session == null){
                logger.info(client + " tried to close an already closed game.");
                return;
            }

            UUID uuid = session.gameId();
            String nickname = session.nickname();

            // get the game object from uuid to call the end game method
            Game game = getGameFromId(uuid);

            // Estraiamo i client da rimuovere prima di alterarli
            List<VirtualClient> clientsToRemove = new ArrayList<>();
            clientSessions.forEach((c, s) -> {
                if (s.gameId().equals(uuid)) {
                    clientsToRemove.add(c);
                }
            });

            synchronized (game) {
                game.forceEndGame(nickname);
            }

            // remove client from the game's observer list
            removeClientAsObserver(client);

            // remove clients and game from maps
            // Usiamo il metodo ripristinato per rimuoverli pulitamente
            for (VirtualClient c : clientsToRemove) {
                removeClientAsObserver(c);
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
            // send the list of open games to the client
            client.updateGamesIdList(getGamesList());
        } catch (Exception e) {
            String message = e.getMessage();
            logger.warning("Error sending games list: " + message);
            notifyErrorToClient(client, new InvalidOperationException(message));
        }
    }
}
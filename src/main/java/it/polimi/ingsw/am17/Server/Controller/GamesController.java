package it.polimi.ingsw.am17.Server.Controller;

import it.polimi.ingsw.am17.CommonInterfaces.ErrorType;
import it.polimi.ingsw.am17.CommonInterfaces.InvalidOperationException;
import it.polimi.ingsw.am17.Server.Model.Game;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
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
    private Game getGameFromId(UUID id){
        synchronized (gamesList){
            return gamesList
                    .stream()
                    .filter(game -> game.getId().equals(id))
                    .findFirst().orElseThrow(()->new InvalidOperationException(ErrorType.INVALID_GAME));
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

    //** public methods: CLIENT ACTIONS **//
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
        }).start();
    }

    /**
     * Adds a player to the game with the specified gameId and registers the client as an observer.
     * @param client to send the gameId.
     * @param gameId of the game to join.
     * @param player to add to the game.
     */
    public void joinGame(VirtualView client, UUID gameId, Player player) {
        new Thread(() -> {
            logger.info("Client " + client.getClass().getSimpleName() + " wants to join game with id " + gameId + " as player " + player.getNickname());

            // sign if client is added as an observer
            boolean observerAdded = false;

            try {
                // retrieve game (also check if it exists)
                Game game = getGameFromId(gameId);

                // Sign up client as an observer (see N.B. hereunder)
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
                rollbackAndNotify(client, gameId, observerAdded, e);

            } catch (Exception e) {
                String message = e.getMessage();
                logger.warning("Error joining game: " + message);
                rollbackAndNotify(client, gameId, observerAdded, new InvalidOperationException(message));
            }
        }).start();
    }

    private void rollbackAndNotify(VirtualView client, UUID gameId, boolean observerAdded, InvalidOperationException errorToNotify) {
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

        notifyErrorToClient(client, errorToNotify);
    }

    /**
     * Closes a game (also when a player disconnects [unexpectedly]).
     * TODO: add check on "creator" player, i.e. only the creator can close the game.
     * TODO: use player parameter and add it to the notify (who closed the game?) for unexpected disconnections.
     * @param client client generating the request.
     */
    public void closeGame(VirtualView client) {
        new Thread(() -> {
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
        }).start();
    }

    /**
     * Forwards a pickOfferingCard request to the game.
     * @param client                the client who made the request
     * @param offeringCardLetter    letter of the offering card to be selected
     */
    public void pickOfferingCard(VirtualView client, Character offeringCardLetter) {
        new Thread(() -> {
            logger.info("Request received by the controller.");
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
        }).start();
    }

    /**
     * Forwards a pickTribeCards request to the game.
     * @param client            the client who made the request
     * @param characterCards    selected by the player
     * @param buildingCards     selected by the player
     */
    public void pickTribeCards(VirtualView client, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) {
        new Thread(() -> {
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
        }).start();
    }


    /**
     * Sends an update to the client with the list of open games.
     * @param client to send the update.
     */
    public void getGamesList(VirtualView client) {
        new Thread(() -> {
            logger.info("Client " + client.getClass().getSimpleName() + " requested the games list.");
            try {
                // send the list of open games to the client
                client.updateGamesIdList(getGamesList());
            } catch (Exception e) {
                String message = e.getMessage();
                logger.warning("Error sending games list: " + message);
                notifyErrorToClient(client, new InvalidOperationException(message));
            }
        }).start();
    }
}
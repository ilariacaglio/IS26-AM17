package it.polimi.ingsw.am17.Server.Controller;

import it.polimi.ingsw.am17.Server.Model.Game;
import it.polimi.ingsw.am17.Server.Model.GameCard.*;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;

import java.util.*;

/**
 * Manages multiple games with an eye to concurrency.
 * Receives requests from the client via a ServerSocket/RMI and forwards it to the model.
 */
public class GamesController {
    private static final List<Game> gamesList = new ArrayList<>();
    private static final Map<VirtualView, UUID> mapping = new HashMap<>();

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
     * @return the games that are not started yet
     */
    private List<Game> getGamesNotStarted() {
        synchronized (gamesList){
            return Collections.unmodifiableList(
                    gamesList.stream()
                            .filter(g->!g.isStarted())
                            .toList()
            );
        }
    }

    /**
     * @return the ids list of the games that are not started yet
     */
    public List<UUID> getGamesList() {
        return getGamesNotStarted()
                .stream()
                .map(Game::getId)
                .toList();
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
        Game game = GamesListHandler.getGameFromId(gameId);

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
        Game game = GamesListHandler.getGameFromId(gameId);
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

        // remove client from the game's observer list
        removeClientAsObserver(client, uuid);

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
}

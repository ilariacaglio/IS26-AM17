package it.polimi.ingsw.am17.Server.Controller;

import it.polimi.ingsw.am17.Server.Model.Game;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Server.Utility.GamesListHandler;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GamesController {
    private static final Map<VirtualView, UUID> mapping = new HashMap<>();
    public GamesController(){}

    public void signUpAsObserver(VirtualView client, UUID gameId) {
        Game game = GamesListHandler.getGameFromId(gameId);

        // add client to game mapping
        mapping.put(client, gameId);

        synchronized (game) {
            game.attach(client);
        }
    }

    public void removeClientAsObserver(VirtualView client, UUID gameId){
        Game game = GamesListHandler.getGameFromId(gameId);
        synchronized (game){
            game.detach(client);
        }
    }

    /**
     * @return the ids list of the games that are not started yet
     */
    public List<UUID> getGamesList(){
        return GamesListHandler.getGamesNotStarted()
                .stream()
                .map(Game::getId)
                .toList();
    }

    /**
     * Creates a game and adds its first player
     * @param player
     * @param numPlayers
     */
    public UUID createGame(Player player, int numPlayers){
        try{
            // game creation
            UUID id = UUID.randomUUID();
            Game game = new Game(id, numPlayers);
            // add game to utility list
            GamesListHandler.addGame(game);
            synchronized (game){
                game.addPlayer(player);
            }
            return id;
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    //** CLIENT ACTIONS **//

    /**
     * Adds the player to the game
     * @param gameId
     * @param player
     */
    public void joinGame(UUID gameId, Player player){
        try{
            Game game = GamesListHandler.getGameFromId(gameId);
            synchronized (game){
                game.addPlayer(player);
            }
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes a game when a player disconnects [unexpectedly].
     */
    public void closeGame(VirtualView client) {
        UUID uuid = mapping.get(client);
        Game game = GamesListHandler.getGameFromId(uuid);
        GamesListHandler.removeGameFromId(uuid);
        removeClientAsObserver(client, uuid); //game.detach(client);
        mapping.remove(client);
        try{
            synchronized (game){
                game.forceEndGame();
            }
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    //** PLAYER ACTIONS **//

    /**
     * Picks the offering card of the player.
     * Calls it's equivalent model.game method.
     * @param gameId
     * @param player
     * @param card
     */
    public void pickOfferingCard(UUID gameId, Player player, OfferingCard card){
        try{
            Game game = GamesListHandler.getGameFromId(gameId);
            synchronized (game){
                game.selectOfferingCard(player,card);
            }
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Picks the tribe cards of the player.
     * Calls its equivalent model.game method.
     * @param gameId
     * @param player
     * @param characterCards
     * @param buildingCards
     */
    public void pickTribeCards(UUID gameId, Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards){
        try {
            Game game = GamesListHandler.getGameFromId(gameId);
            synchronized (game){
                game.pickTribeCards(player, characterCards, buildingCards);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package it.polimi.ingsw.am17.Controller;

import it.polimi.ingsw.am17.Model.Game;
import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.CharacterCard;
import it.polimi.ingsw.am17.Model.OfferingCard;
import it.polimi.ingsw.am17.Model.Player;
import it.polimi.ingsw.am17.Utility.GamesListHandler;
import java.util.List;
import java.util.UUID;

public class GameController {
    public GameController(){}

    /**
     * @return the ids list of the games that are not started yet
     */
    public List<UUID> getGamesList(){
        return GamesListHandler.getGamesNotStarted()
                .stream()
                .map(Game::getId)
                .toList();
    }

    public int getNumPlayers(UUID gameId){
        Game g = GamesListHandler.getGameFromId(gameId);
        return g.getPlayers().size();
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
            addPlayerToGame(game, player);
            return id;
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds the player to the game
     * @param gameId
     * @param player
     */
    public void joinGame(UUID gameId, Player player){
        try{
            Game game = GamesListHandler.getGameFromId(gameId);
            addPlayerToGame(game, player);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

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
            pickOfferingCard(game,player,card);
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
            pickTribeCards(game, player, characterCards, buildingCards);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void pickOfferingCard(Game game, Player player, OfferingCard card){
        game.selectOfferingCard(player,card);
    }

    private synchronized void pickTribeCards(Game game, Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards){
        game.pickTribeCards(player, characterCards, buildingCards);
    }

    private synchronized void addPlayerToGame(Game game, Player player){
        game.addPlayer(player);
    }
}

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

    /**
     * Creates a game and adds its first player
     * @param player
     * @param numPlayers
     */
    public void createGame(Player player, int numPlayers){
        try{
            // game creation
            UUID id = UUID.randomUUID();
            Game game = new Game(id, numPlayers);
            // add game to utility list
            GamesListHandler.addGame(game);
            addPlayerToGame(game, player);
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
            synchronized (this){
                game.selectOfferingCard(player,card);
            }
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    // TODO: the params types are lists?
    public void pickTribeCards(int gameId, String nickname, List<CharacterCard> characterCards, List<BuildingCard> buildingCards){
        try {
            Game game = GamesListHandler.getGameFromId(gameId);
            Player player = GamesListHandler.getPlayerFromNickname(game, nickname);
            synchronized (this) {
                game.pickTribeCards(player, characterCards, buildingCards);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void addPlayerToGame(Game game, Player player){
        game.addPlayer(player);
    }
}

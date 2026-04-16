package it.polimi.ingsw.am17.Controller;

import it.polimi.ingsw.am17.Model.Color;
import it.polimi.ingsw.am17.Model.Game;
import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.CharacterCard;
import it.polimi.ingsw.am17.Model.OfferingCard;
import it.polimi.ingsw.am17.Model.Player;
import it.polimi.ingsw.am17.Utility.CardParser;
import it.polimi.ingsw.am17.Utility.GamesListHandler;

import java.util.List;
import java.util.UUID;

public class GameController {
    public GameController(){}
    /**
     * @return the ids list of the games that are not started yet
     */
    public List<Integer> getGamesList(){
        return GamesListHandler.getGamesNotStarted()
                .stream()
                .map(Game::getId)
                .toList();
    }

    /**
     * Creates a game and adds its first player
     * @param nickname
     * @param color
     * @param numPlayers
     */
    public void createGame(String nickname, Color color, int numPlayers){
        try{
            // game creation
            UUID id = UUID.randomUUID();
            Game game = new Game(id, numPlayers);
            // add game to utility list
            GamesListHandler.addGame(game);
            addPlayerToGame(game, nickname, color);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds the player to the game
     * @param nickname
     * @param color
     */
    public void joinGame(int gameId, String nickname, Color color){
        try{
            Game game = GamesListHandler.getGameFromId(gameId);
            addPlayerToGame(game, nickname, color);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Picks the offering card of the player.
     * Calls it's equivalent model.game method.
     * @param gameId
     * @param nickname
     * @param letter
     */
    public void pickOfferingCard(int gameId, String nickname, Character letter){
        try{
            Game game = GamesListHandler.getGameFromId(gameId);
            Player player = GamesListHandler.getPlayerFromNickname(game, nickname);
            OfferingCard card = getOfferingCardFromLetter(letter);
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

    private OfferingCard getOfferingCardFromLetter(Character letter){
        return offeringCards.stream().filter(o -> o.getOrderLetter() == letter).findFirst().orElse(null);
    }

    private synchronized void addPlayerToGame(Game game, String nickname, Color color){
        game.addPlayer(new Player(nickname, color));
    }
}

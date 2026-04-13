package it.polimi.ingsw.am17.Controller;

import it.polimi.ingsw.am17.Model.Color;
import it.polimi.ingsw.am17.Model.Game;
import it.polimi.ingsw.am17.Model.Player;
import it.polimi.ingsw.am17.Utility.GamesListHandler;

import java.util.List;

public class GameController {
    // TODO: find a way to make ids incremental
    private Game game;
    int id;

    public GameController() {}

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
            game = new Game(id, numPlayers);
            // add game to utility list
            GamesListHandler.addGame(game);
            addPlayerToGame(nickname, color);
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
    public void joinGame(String nickname, Color color){
        try{
            addPlayerToGame(nickname, color);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void pickOfferingCard(){}

    public void pickTribeCards(){}

    private synchronized void addPlayerToGame(String nickname, Color color){
        game.addPlayer(new Player(nickname, color));
    }
}

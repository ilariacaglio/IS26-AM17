package it.polimi.ingsw.am17.Controller;

import it.polimi.ingsw.am17.Model.Color;
import it.polimi.ingsw.am17.Model.Game;
import it.polimi.ingsw.am17.Model.Player;
import it.polimi.ingsw.am17.Utility.GamesListHandler;

import java.util.List;
import java.util.stream.Collectors;

public class GameController {
    // TODO: find a way to make ids incremental
    private Game game;
    int id;

    public GameController() {}

    public List<Integer> getGamesList(){
        return GamesListHandler.getIncompleteGamesList()
                .stream()
                .map(Game::getId)
                .toList();
    }

    public void createGame(String nickname, Color color, int numPlayers){
        try{
            // game creation
            game = new Game(id, numPlayers);
            synchronized (this){
                // add game to utility list
                GamesListHandler.addGame(game);
                // add player to game
                game.addPlayer(new Player(nickname, color));
            }
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void joinGame(String nickname, Color color, int gameId){
        try{

        }
        catch (Exception e){

        }
    }

    public void pickOfferingCard(){}

    public void pickTribeCards(){}
}

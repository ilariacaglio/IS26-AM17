package it.polimi.ingsw.am17.Utility;

import it.polimi.ingsw.am17.Model.Game;
import it.polimi.ingsw.am17.Model.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GamesListHandler {
    private static final List<Game> gamesList = new ArrayList<>();

    /**
     * adds the game to gamesList
     * @param game the game to be added
     */
    public static void addGame(Game game){
        synchronized (gamesList){
            gamesList.add(game);
        }
    }

    /**
     * @return the games that are not started yet
     */
    public static List<Game> getGamesNotStarted(){
        synchronized (gamesList){
            return Collections.unmodifiableList(
                    gamesList.stream()
                            .filter(g->!g.isStarted())
                            .toList()
            );
        }
    }

    /**
     * @param id
     * @return the game in the list with the given id
     */
    public static Game getGameFromId(int id){
        synchronized (gamesList){
            return gamesList
                    .stream()
                    .filter(game -> game.getId() == id)
                    .findFirst().orElse(null);
        }
    }

    // TODO: questo metodo non va messo qui ma non so dove metterlo
    public static Player getPlayerFromNickname(Game game, String nickname){
        synchronized (game){
            return game.getPlayers().stream().filter(p -> p.getNickname().equals(nickname)).findFirst().orElse(null);
        }
    }
}

package it.polimi.ingsw.am17.Server.Utility;

import it.polimi.ingsw.am17.Server.Model.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
     * @param id game id
     * @return the game in the list with the given id
     */
    public static Game getGameFromId(UUID id){
        synchronized (gamesList){
            return gamesList
                    .stream()
                    .filter(game -> game.getId().equals(id))
                    .findFirst().orElseThrow();
        }
    }
}

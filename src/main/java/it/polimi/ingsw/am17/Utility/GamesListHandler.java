package it.polimi.ingsw.am17.Utility;

import it.polimi.ingsw.am17.Model.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GamesListHandler {
    private static final List<Game> incompleteGamesList = new ArrayList<>();
    public static void addGame(Game game){
        synchronized (incompleteGamesList){
            if(!game.isStarted() && !game.isEnded()){
                incompleteGamesList.add(game);
            }
        }
    }
    public static List<Game> getIncompleteGamesList(){
        synchronized (incompleteGamesList){
            return Collections.unmodifiableList(incompleteGamesList);
        }
    }
    public static Game getGameFromId(int id){
        synchronized (incompleteGamesList){
            return incompleteGamesList
                    .stream()
                    .filter(game -> game.getId() == id)
                    .findFirst().orElse(null);
        }
    }
}

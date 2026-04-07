package it.polimi.ingsw.am17.Model.GameCard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Model.Player;

import java.util.List;

public class FoodEvent extends EventCard{
    private final int pointLost;

    public int getPointLost() {
        return pointLost;
    }

    @JsonCreator
    public FoodEvent(
           @JsonProperty("pointLost") int pointLost,
           @JsonProperty("Final") boolean Final,
           @JsonProperty("era") int era){
        super(Final, era, CardType.FOOD_EVENT);
        this.pointLost = pointLost;
    }

    @Override
    public void computeScore(List<Player> list){
        for (Player player : list) {
            player.solveFoodEvent(pointLost);
        }
    }
}

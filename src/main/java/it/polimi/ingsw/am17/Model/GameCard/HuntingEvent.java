package it.polimi.ingsw.am17.Model.GameCard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Model.Player;

import java.util.List;

public class HuntingEvent extends EventCard {
    private final int pointEarned;

    public int getPointEarned() {
        return pointEarned;
    }

    @JsonCreator
    public HuntingEvent(
            @JsonProperty("pointEarned") int pointEarned,
            @JsonProperty("Final") boolean Final,
            @JsonProperty("era") int era){
        this.pointEarned = pointEarned;
        super(Final, era, CardType.HUNTING_EVENT);
    }

    @Override
    public void computeScore(List<Player> list){
        for(Player player: list){
           player.solveHuntingEvent(pointEarned);
        }
    }
}

package it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class HuntingEvent extends EventCard {
    private static final Logger logger = Logger.getLogger(HuntingEvent.class.getName());
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
    public void computeScore(List<Player> list) {
        for(Player player: list){
           player.solveHuntingEvent(pointEarned);
            logger.info("Solved Hunting Event. ");
            logger.info("Player " + player.getNickname() + " has " + player.getFood() + " food "
                    + player.getPp() + " points after Hunting Event");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HuntingEvent that = (HuntingEvent) o;
        return pointEarned == that.pointEarned;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pointEarned);
    }
}

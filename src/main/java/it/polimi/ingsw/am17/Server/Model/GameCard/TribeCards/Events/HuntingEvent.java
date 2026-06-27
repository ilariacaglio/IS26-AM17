package it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameState;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.util.Objects;
import java.util.UUID;
import java.util.Queue;
import java.util.logging.Logger;

public class HuntingEvent extends EventCard {
    private static final Logger logger = Logger.getLogger(HuntingEvent.class.getName());
    private final Integer pointEarned;

    /// needed for jackson
    public Integer getPointEarned() {
        return pointEarned;
    }

    @JsonCreator
    public HuntingEvent(
            @JsonProperty("pointEarned") Integer pointEarned,
            @JsonProperty("Final") Boolean Final,
            @JsonProperty("era") GameState era,
            @JsonProperty("id") UUID id){
        this.pointEarned = pointEarned;
        super(Final, era, CardType.HUNTING_EVENT, id);
    }

    @Override
    public void computeScore(Queue<Player> list){
        logger.info("Solving HuntingEvent. ");
        for(Player player: list){
           player.solveHuntingEvent(pointEarned);

            logger.info("Player " + player.getNickname() + " has " + player.getFood() + " food "
                    + player.getPp() + " points after Hunting Event");
        }
        logger.info("Solved Hunting Event. ");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HuntingEvent that = (HuntingEvent) o;
        return pointEarned.equals(that.pointEarned);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pointEarned);
    }

    @Override
    public String getImagePath() {
        return "/Images/Events/hunting_event_" + pointEarned +"PP.png";
    }
}

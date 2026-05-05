package it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class HuntingEvent extends EventCard {
    private final Integer pointEarned;

    public Integer getPointEarned() {
        return pointEarned;
    }

    @JsonCreator
    public HuntingEvent(
            @JsonProperty("pointEarned") Integer pointEarned,
            @JsonProperty("Final") Boolean Final,
            @JsonProperty("era") Integer era,
            @JsonProperty("id") UUID id){
        this.pointEarned = pointEarned;
        super(Final, era, CardType.HUNTING_EVENT, id);
    }

    @Override
    public void computeScore(List<Player> list){
        for(Player player: list){
           player.solveHuntingEvent(pointEarned);
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

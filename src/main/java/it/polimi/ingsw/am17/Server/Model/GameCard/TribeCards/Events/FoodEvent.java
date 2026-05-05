package it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FoodEvent extends EventCard{
    private final Integer pointLost;

    public Integer getPointLost() {
        return pointLost;
    }

    @JsonCreator
    public FoodEvent(
           @JsonProperty("pointLost") int pointLost,
           @JsonProperty("Final") boolean Final,
           @JsonProperty("era") int era,
           @JsonProperty("id") UUID id){
        super(Final, era, CardType.FOOD_EVENT, id);
        this.pointLost = pointLost;
    }

    @Override
    public void computeScore(List<Player> list){
        for (Player player : list) {
            player.solveFoodEvent(pointLost);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodEvent foodEvent = (FoodEvent) o;
        return pointLost == foodEvent.pointLost;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pointLost);
    }
}

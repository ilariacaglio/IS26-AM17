package it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.Game;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class FoodEvent extends EventCard{
    private static final Logger logger = Logger.getLogger(FoodEvent.class.getName());
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
            logger.info("Solved Food Event. ");
            logger.info("Player " + player.getNickname() + " has " + player.getFood() + " food "
                            + player.getPp() + " points after Food Event");

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

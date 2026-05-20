package it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.logging.Logger;

public class PaintingEvent extends EventCard {
    private static final Logger logger = Logger.getLogger(PaintingEvent.class.getName());
    private final Integer pointsLow;
    private final Integer pointsMax;
    private final Integer numMax;

    public Integer getNumMax() {
        return numMax;
    }

    public Integer getPointsLow() {
        return pointsLow;
    }

    public Integer getPointsMax() {
        return pointsMax;
    }

    @JsonCreator
    public PaintingEvent(
            @JsonProperty("pointsLow") Integer pointsLow,
            @JsonProperty("pointsMax") Integer pointsMax,
            @JsonProperty("numMax") Integer numMax,
            @JsonProperty("Final") Boolean Final,
            @JsonProperty("era") Integer era,
            @JsonProperty("id") UUID id) {
        super(Final, era, CardType.PAINTING_EVENT, id);
        this.pointsLow = pointsLow;
        this.pointsMax = pointsMax;
        this.numMax = numMax;
    }

    @Override
    public void computeScore(Queue<Player> list) {
        for (Player player : list) {
            player.solvePaintingEvent(numMax, pointsMax, pointsLow);
            logger.info("Solved Painting Event. ");
            logger.info("Player " + player.getNickname() + " has " + player.getFood() + " food "
                    + player.getPp() + " points after Painting Event");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaintingEvent that = (PaintingEvent) o;
        return pointsLow.equals(that.pointsLow) && pointsMax.equals(that.pointsMax) && numMax.equals(that.numMax);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointsLow, pointsMax, numMax);
    }
}
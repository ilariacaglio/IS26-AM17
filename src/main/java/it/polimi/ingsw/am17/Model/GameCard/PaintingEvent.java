package it.polimi.ingsw.am17.Model.GameCard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Model.Player;

import java.util.List;
import java.util.Objects;

public class PaintingEvent extends EventCard {
    private final int pointsLow;
    private final int pointsMax;
    private final int numMax;

    public int getNumMax() {
        return numMax;
    }

    public int getPointsLow() {
        return pointsLow;
    }

    public int getPointsMax() {
        return pointsMax;
    }

    @JsonCreator
    public PaintingEvent(
            @JsonProperty("pointsLow") int pointsLow,
            @JsonProperty("pointsMax") int pointsMax,
            @JsonProperty("numMax") int numMax,
            @JsonProperty("Final") boolean Final,
            @JsonProperty("era") int era) {
        super(Final, era, CardType.PAINTING_EVENT);
        this.pointsLow = pointsLow;
        this.pointsMax = pointsMax;
        this.numMax = numMax;
    }

    @Override
    public void computeScore(List<Player> list) {
        for (Player player : list) {
            player.solvePaintingEvent(numMax, pointsMax, pointsLow);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaintingEvent that = (PaintingEvent) o;
        return pointsLow == that.pointsLow && pointsMax == that.pointsMax && numMax == that.numMax;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointsLow, pointsMax, numMax);
    }
}
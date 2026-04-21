package it.polimi.ingsw.am17.Model.GameCard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Builder extends CharacterCard {
    private final int pointBonus;
    private final int foodReduction;

    public int getFoodReduction() {
        return foodReduction;
    }

    public int getPointBonus() {
        return pointBonus;
    }

    @JsonCreator
    public Builder(
            @JsonProperty("era") int era,
            @JsonProperty("minPlayers") int minPlayers,
            @JsonProperty("pointsBonus") int pointsBonus,
            @JsonProperty("foodReduction") int foodReduction) {
        super(era, minPlayers,CardType.BUILDER);
        this.pointBonus = pointsBonus;
        this.foodReduction = foodReduction;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Builder builder = (Builder) o;
        return pointBonus == builder.pointBonus && foodReduction == builder.foodReduction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointBonus, foodReduction);
    }
}

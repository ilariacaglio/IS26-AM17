package it.polimi.ingsw.am17.Model.GameCard;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Builder extends CharacterCard {
    private final int pointBonus;
    private final int foodReduction;

    public int getFoodReduction() {
        return foodReduction;
    }

    public int getPointBonus() {
        return pointBonus;
    }

    public Builder(
            @JsonProperty("era") int era,
            @JsonProperty("minPlayers") int minPlayers,
            @JsonProperty("pointsBonus") int pointsBonus,
            @JsonProperty("foodReduction") int foodReduction) {
        super(era, minPlayers,CardType.BUILDER);
        this.pointBonus = pointsBonus;
        this.foodReduction = foodReduction;
    }
}

package it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;

import java.util.Objects;
import java.util.UUID;

public class Builder extends CharacterCard {
    private final Integer pointBonus;
    private final Integer foodReduction;

    public Integer getFoodReduction() {
        return foodReduction;
    }

    public Integer getPointBonus() {
        return pointBonus;
    }

    @JsonCreator
    public Builder(
            @JsonProperty("era") Integer era,
            @JsonProperty("minPlayers") Integer minPlayers,
            @JsonProperty("pointBonus") Integer pointBonus,
            @JsonProperty("foodReduction") Integer foodReduction,
            @JsonProperty("id") UUID id) {
        super(era, minPlayers, CardType.BUILDER, id);
        this.pointBonus = pointBonus;
        this.foodReduction = foodReduction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Builder builder = (Builder) o;
        if(!this.getId().equals(builder.getId())) return false;
        return pointBonus.equals(builder.pointBonus) && foodReduction.equals(builder.foodReduction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointBonus, foodReduction);
    }

    public String getDetails() {
        return super.getDetails() + "(-" + foodReduction + "F, +" + pointBonus + "P" + ")";
    }

    @Override
    public String getImagePath()
    {
        return "/Images/Builder/builder_" + pointBonus +"PP_" + foodReduction +"F.png";
    }

    @Override
    public String getImagePath()
    {
        return "/Images/Builder/builder_" + pointBonus +"PP_" + foodReduction +"F.png";
    }
}

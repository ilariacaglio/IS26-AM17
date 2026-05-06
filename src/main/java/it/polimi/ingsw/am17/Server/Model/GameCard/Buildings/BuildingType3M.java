package it.polimi.ingsw.am17.Server.Model.GameCard.Buildings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;

import java.util.List;
import java.util.Objects;

/**
 * Gives extra points for each character of given type.
 * Final effect
 * MULTIPLE
 */
public class BuildingType3M extends BuildingCard {
    private final Integer pointsFromEachCharacter; // amount from each characterType
    private final CardType characterType;
    private static final int era = 3;

    @JsonCreator
    public BuildingType3M(
            @JsonProperty("foodCost") Integer foodCost,
            @JsonProperty("bonusPoints") Integer bonusPoints,
            @JsonProperty("characterType") CardType characterType,
            @JsonProperty("pointsFromEachCharacter") Integer pointsFromEachCharacter) {
        super(era, foodCost, bonusPoints);
        this.characterType = characterType;
        this.pointsFromEachCharacter = pointsFromEachCharacter;
    }

    @SuppressWarnings("unused") // needed for jackson
    public Integer getPointsFromEachCharacter() {
        return pointsFromEachCharacter;
    }

    @Override
    public int GetAdditionalFinalPoints(List<CharacterCard> playerCharacterCards) {
        int characterCount = (int) playerCharacterCards.stream().filter(card -> card.getCardType() == characterType).count();
        return characterCount * pointsFromEachCharacter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuildingType3M that = (BuildingType3M) o;
        return pointsFromEachCharacter.equals(that.pointsFromEachCharacter) && characterType == that.characterType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointsFromEachCharacter, characterType);
    }

    @Override
    public String toString() {
        return super.toString() + " Effect: +PP/each " + characterType.toString();
    }
}

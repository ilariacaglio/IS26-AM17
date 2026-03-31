package it.polimi.ingsw.am17.Model.GameCard;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Gives extra points for each character of given type.
 * Final effect
 * MULTIPLE
 */
public class BuildingType3M extends BuildingCard {
    private final int pointsFromEachCharacter; // amount from each characterType
    private final CardType characterType;
    private static final int era = 3;
    public BuildingType3M(
            @JsonProperty("foodCost") int foodCost,
            @JsonProperty("bonusPoints") int bonusPoints,
            @JsonProperty("characterType") CardType characterType,
            @JsonProperty("pointsFromEachCharacter") int pointsFromEachCharacter) {
        super(era, foodCost, bonusPoints);
        this.characterType = characterType;
        this.pointsFromEachCharacter = pointsFromEachCharacter;
    }

    @Override
    public int FinalPoints(List<CharacterCard> playerCharacterCards) {
        int characterCount = (int) playerCharacterCards.stream().filter(card -> card.getCardType() == characterType).count();
        return characterCount * pointsFromEachCharacter;
    }
}

package it.polimi.ingsw.am17.Model.GameCard;

import java.util.List;

/**
 * Gives extra points for each character of given type.
 * Final effect
 * MULTIPLE
 */
public class BuildingType3M extends BuildingCard {
    private final int pointsFromEachCharacter; // amount from each characterType
    private final CardType characterType;

    public BuildingType3M(int foodCost, int bonusPoints, CardType characterType, int pointsFromEachCharacter) {
        super(3, foodCost, bonusPoints);
        this.characterType = characterType;
        this.pointsFromEachCharacter = pointsFromEachCharacter;
    }

    @Override
    public int FinalPoints(List<CharacterCard> playerCharacterCards) {
        int characterCount = (int) playerCharacterCards.stream().filter(card -> card.getCardType() == characterType).count();
        return characterCount * pointsFromEachCharacter;
    }
}

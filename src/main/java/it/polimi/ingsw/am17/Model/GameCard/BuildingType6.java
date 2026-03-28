package it.polimi.ingsw.am17.Model.GameCard;

import java.util.List;

/**
 * Get double the points from Builders
 * Final effect
 * SINGLETON
 */
public class BuildingType6 extends BuildingCard {
    private static final int era = 2;
    private static final int foodCost = 5;
    private static final int bonusPoints = 4;
    public BuildingType6() {
        super(era, foodCost, bonusPoints);
    }

    @Override
    public int FinalPoints(List<CharacterCard> playerCharacterCards) {
        return (int) playerCharacterCards.stream().filter(characterCard -> characterCard.getCardType() == CardType.BUILDER).count();
    }
}

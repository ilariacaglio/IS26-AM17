package it.polimi.ingsw.am17.Model.GameCard;

import java.util.List;

/**
 * Get double the points from Builders
 * Final effect
 * SINGLETON
 */
public class BuildingType6 extends BuildingCard {
    public BuildingType6() {
        super(2, 5, 6);
    }

    @Override
    public int FinalPoints(List<CharacterCard> playerCharacterCards) {
        return (int) playerCharacterCards.stream().filter(characterCard -> characterCard.getCardType() == CardType.BUILDER).count();
    }
}

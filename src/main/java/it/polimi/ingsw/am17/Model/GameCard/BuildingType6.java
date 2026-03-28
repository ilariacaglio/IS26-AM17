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
    public int FinalPoints(List<CharacterCard> characterCards) {
        // get builders with stream magic
        List<Builder> builders = characterCards.stream()
                .filter(card -> card.getCardType() == CardType.BUILDER)
                .map(card -> (Builder) card)
                .toList();

        // return the sum of bonus points ONCE (as they should be counted once already)
        return builders.stream().mapToInt(Builder::getPointBonus).sum();
    }
}

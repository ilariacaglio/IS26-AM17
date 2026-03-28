package it.polimi.ingsw.am17.Model.GameCard;

import java.util.List;

import static it.polimi.ingsw.am17.Model.GameCard.CardType.ARTIST;

/**
 * Get one food during PaintingEvent for each artist
 * Event effect
 * SINGLETON
 */
public class BuildingType5 extends BuildingCard {
    private static final int era = 2;
    private static final int foodCost = 5;
    private static final int bonusPoints = 6;
    public BuildingType5() {
        super(era, foodCost, bonusPoints);
    }

    @Override
    public int FoodBonus(List<CharacterCard> playerCharacterCards) {
        return (int) playerCharacterCards.stream().filter(card -> card.getCardType() == ARTIST).count();
    }
}

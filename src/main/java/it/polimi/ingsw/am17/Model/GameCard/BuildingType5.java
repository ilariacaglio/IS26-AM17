package it.polimi.ingsw.am17.Model.GameCard;

import java.util.List;

import static it.polimi.ingsw.am17.Model.GameCard.CardType.ARTIST;

/**
 * Get one food during PaintingEvent for each artist
 * Event effect
 * SINGLETON
 */
public class BuildingType5 extends BuildingCard {
    public BuildingType5() {
        super(2, 5, 6);
    }


    @Override
    public int FoodBonus(List<CharacterCard> playerCharacterCards) {
        return (int) playerCharacterCards.stream().filter(card -> card.getCardType() == ARTIST).count();
    }
}

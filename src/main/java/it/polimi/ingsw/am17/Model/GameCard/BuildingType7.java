package it.polimi.ingsw.am17.Model.GameCard;

import java.util.List;

/**
 * Get +1 extra food and +1 pp for each hunter during hunterEvent
 * Event effect
 * SINGLETON
 */
public class BuildingType7 extends BuildingCard {
    public BuildingType7() {
        super(2, 7, 2);
    }

    @Override
    public int FoodBonus(List<CharacterCard> playerCharacterCards) {
        return (int) playerCharacterCards.stream().filter(characterCard -> characterCard.getCardType() == CardType.HUNTER).count();
    }

    @Override
    public int PointsBonus(List<CharacterCard> playerCharacterCards) {
        return (int) playerCharacterCards.stream().filter(characterCard -> characterCard.getCardType() == CardType.HUNTER).count();
    }

    // NOTE: counts two times! Can I share that calculation? No because different methods...
}

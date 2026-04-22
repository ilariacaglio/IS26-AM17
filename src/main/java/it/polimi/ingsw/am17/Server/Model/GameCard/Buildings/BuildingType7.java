package it.polimi.ingsw.am17.Server.Model.GameCard.Buildings;

import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;

import java.util.List;

/**
 * Get +1 extra food and +1 pp for each hunter during hunterEvent
 * Event effect
 * SINGLETON
 */
public class BuildingType7 extends BuildingCard {
    private static final int era = 2;
    private static final int foodCost = 7;
    private static final int bonusPoints = 2;
    public BuildingType7() {
        super(era, foodCost, bonusPoints);
    }

    @Override
    public int AddFoodPerHunterInHuntingEvent(List<CharacterCard> playerCharacterCards) {
        return (int) playerCharacterCards.stream().filter(characterCard -> characterCard.getCardType() == CardType.HUNTER).count();
    }

    @Override
    public int AddPointPerHunterInHuntingEvent(List<CharacterCard> playerCharacterCards) {
        return (int) playerCharacterCards.stream().filter(characterCard -> characterCard.getCardType() == CardType.HUNTER).count();
    }

    // NOTE: counts two times! Can I share that calculation? No because different methods...
}

package it.polimi.ingsw.am17.Server.Model.GameCard.Buildings;

import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameState;

import java.util.List;
import java.util.logging.Logger;

/**
 * Get +1 extra food and +1 pp for each hunter during hunterEvent
 * Event effect
 * SINGLETON
 */
public class BuildingType7 extends BuildingCard {
    private static final GameState era = GameState.ERA2;
    private static final int foodCost = 7;
    private static final int bonusPoints = 2;

    private static final Logger logger = Logger.getLogger(BuildingType7.class.getName());

    public BuildingType7() {
        super(era, foodCost, bonusPoints);
    }


    // N.B.: counts two times! Can I share that calculation? No because different methods...

    @Override
    public int AddFoodPerHunterInHuntingEvent(List<CharacterCard> playerCharacterCards) {
        int foodBonus = (int) playerCharacterCards.stream()
                .filter(characterCard -> characterCard.getCardType() == CardType.HUNTER)
                .count();
        logger.info("Getting +" + foodBonus + " food from BuildingType7");
        return foodBonus;
    }

    @Override
    public int AddPointPerHunterInHuntingEvent(List<CharacterCard> playerCharacterCards) {
        int bonusPoints = (int) playerCharacterCards.stream()
                .filter(characterCard -> characterCard.getCardType() == CardType.HUNTER)
                .count();
        logger.info("Getting +" + bonusPoints + " points from BuildingType7");
        return bonusPoints;
    }

    @Override
    public String toString() {
        return super.toString() + " Effect: +1PP+1F/hunter in HunterEvent] ";
    }

    @Override
    public String getImagePath()
    {
        return "/Images/Buildings/building2_7.png";
    }

}

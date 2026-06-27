package it.polimi.ingsw.am17.Server.Model.GameCard.Buildings;

import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameState;

import java.util.List;
import java.util.logging.Logger;

/**
 * Gives +25 pp.
 * Final effect
 * SINGLETON
 */
public class BuildingType1 extends BuildingCard {
    private static final GameState era = GameState.ERA3;
    private static final int foodCost = 10;
    private static final int bonusPoints = 0;

    private static final Logger logger = Logger.getLogger(BuildingType1.class.getName());

    public BuildingType1() {
        super(era, foodCost, bonusPoints);
    }

    @Override
    public int GetAdditionalFinalPoints(List<CharacterCard> playerCharacterCards) {
        logger.info("Getting +25PP from BuildingType1");
        return 25;
    }

    @Override
    public String toString() {
        return super.toString() + " Effect: +25PP] ";
    }

    @Override
    public String getImagePath()
    {
        return "/Images/Buildings/building3_1.png";
    }

}

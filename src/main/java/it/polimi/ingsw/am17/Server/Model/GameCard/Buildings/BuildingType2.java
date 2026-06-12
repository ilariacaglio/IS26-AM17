package it.polimi.ingsw.am17.Server.Model.GameCard.Buildings;

import it.polimi.ingsw.am17.Server.Model.GameState;

import java.util.logging.Logger;

/**
 * Allows to get one more card from the upper row.
 * Game effect
 * SINGLETON
 */
public class BuildingType2 extends BuildingCard {
    private static final GameState era = GameState.ERA3;
    private static final int foodCost = 9;
    private static final int bonusPoints = 3;
    public BuildingType2() {
        super(era, foodCost, bonusPoints);
    }

    private static final Logger logger = Logger.getLogger(BuildingType2.class.getName());

    @Override
    public boolean hasOneMoreMove() {
        logger.info("Getting one more move from BuildingType2");
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " Effect: +1 card from the upper row] ";
    }

    @Override
    public String getImagePath()
    {
        return "/Images/Buildings/building3_2.png";
    }

}

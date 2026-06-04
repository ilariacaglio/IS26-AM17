package it.polimi.ingsw.am17.Server.Model.GameCard.Buildings;

import it.polimi.ingsw.am17.Server.Model.GameState;

import java.util.logging.Logger;

/**
 * Get double the points from RitualEvent if "won".
 * Event effect
 * SINGLETON
 */
public class BuildingType8 extends BuildingCard {
    private static final GameState era = GameState.ERA2;
    private static final int foodCost = 7;
    private static final int bonusPoints = 0;

    private static final Logger logger = Logger.getLogger(BuildingType1.class.getName());

    public BuildingType8() {
        super(era, foodCost, bonusPoints);
    }

    @Override
    public boolean hasDoubleRitualEventPoints() { // Tribes effect
        logger.info("Doubling pp from BuildingType8");
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " Effect: x2PP in RitualEvent if won] ";
    }

    @Override
    public String getImagePath()
    {
        return "/Images/Buildings/building2_8.png";
    }

}

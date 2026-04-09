package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.OfferingCard;
import it.polimi.ingsw.am17.Model.Player;

/**
 * Get +1 food when getting (not losing) food from turn order (also last round).
 * Card effect
 * SINGLETON
 */
public class BuildingType11 extends BuildingCard {
    private static final int era = 1;
    private static final int foodCost = 3;
    private static final int bonusPoints = 3;
    public BuildingType11() {
        super(era, foodCost, bonusPoints);
    }

    @Override
    public int GetFoodBonusFromTurnOrder() {
        return 1;
    }
}

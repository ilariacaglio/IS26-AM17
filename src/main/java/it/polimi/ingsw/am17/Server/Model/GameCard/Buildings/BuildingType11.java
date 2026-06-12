package it.polimi.ingsw.am17.Server.Model.GameCard.Buildings;

import it.polimi.ingsw.am17.Server.Model.GameState;

import java.util.logging.Logger;

/**
 * Get +1 food when getting (not losing) food from turn order (also last round).
 * Card effect
 * SINGLETON
 */
public class BuildingType11 extends BuildingCard {
    private static final GameState era = GameState.ERA1;
    private static final int foodCost = 3;
    private static final int bonusPoints = 3;
    public BuildingType11() {
        super(era, foodCost, bonusPoints);
    }

    @Override
    public int GetMoreFoodFromTurnOrderCard() {
        return 1;
    }

    @Override
    public String toString() {
        return super.toString() + " Effect: +1F if getting food from turn order] ";
    }

    @Override
    public String getImagePath()
    {
        return "/Images/Buildings/building1_11.png";
    }

}

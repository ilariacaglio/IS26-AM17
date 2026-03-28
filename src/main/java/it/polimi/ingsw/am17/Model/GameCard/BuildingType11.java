package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.Player;

/**
 * Get +1 food when getting (not losing) food from turn order (also last round).
 * Card effect
 * SINGLETON
 */
public class BuildingType11 extends BuildingCard {
    public BuildingType11() {
        super(2, 6, 4);
    }

    @Override
    public int FoodBonus(Player player) {
        return 0; // TODO: where is the logic for food from turn order?
    }
}

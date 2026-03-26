package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.Player;

/**
 * Get double the points from Builders
 * Final effect
 * SINGLETON
 */
public class BuildingType6 extends BuildingCard {
    public BuildingType6() {
        super(2, 5, 6);
    }

    @Override
    public void resolveEffect(Player player) {
        // TODO: depends on how's the logic to get points at the end of the game
    }
}

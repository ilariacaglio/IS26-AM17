package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.Player;

/**
 * Implements +25pp building
 * SINGLETON
 * Final effect
 */
public class BuildingTypeA extends BuildingCard {
    public BuildingTypeA() {
        super(3, 10, 0);
    }

    @Override
    public void resolveEffect(Player player) {
        player.addPp(25);
    }
}

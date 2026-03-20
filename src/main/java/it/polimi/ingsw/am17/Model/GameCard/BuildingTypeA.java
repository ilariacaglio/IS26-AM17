package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.Player;

/**
 * Gives +25 pp.
 * Final effect
 * SINGLETON
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

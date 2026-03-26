package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.Player;

/**
 * Get +1 extra food and +1 pp for each hunter during hunterEvent
 * Event effect
 * SINGLETON
 */
public class BuildingType7 extends BuildingCard {
    public BuildingType7() {
        super(2, 5, 6);
    }

    @Override
    public void resolveEffect(Player player) {
        int numHunters = 0;
        // TODO: get numHunters and make sure it's called during event?
        player.addFood(numHunters);
        player.addPp(numHunters);
    }
}

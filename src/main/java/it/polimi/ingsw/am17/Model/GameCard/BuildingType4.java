package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.CardRow;
import it.polimi.ingsw.am17.Model.Player;

/**
 * Get 6 points for each 6 different set of characters.
 * Final effect
 * SINGLETON
 */
public class BuildingType4 extends BuildingCard {
    public BuildingType4() {
        super(2, 5, 6);
    }

    @Override
    public int getFinalPoints(CardRow playerTribeRow) {
        int numOfSets = 0;
        // TODO: count sets of 6 different characters
        player.addPp(6 * numOfSets);
    }
}

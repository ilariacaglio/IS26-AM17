package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.CardRow;
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
    public int getFinalPoints(CardRow playerTribeRow) {
        // TODO: get builders points and return them (not doubled)
        return -99999;
    }
}

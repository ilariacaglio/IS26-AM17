package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.CardRow;
import it.polimi.ingsw.am17.Model.Player;

/**
 * Gives +25 pp.
 * Final effect
 * SINGLETON
 */
public class BuildingType1 extends BuildingCard {
    public BuildingType1() {
        super(3, 10, 0);
    }

    @Override
    public int FinalPoints(CardRow playerTribeRow) {
        return 25;
    }
}

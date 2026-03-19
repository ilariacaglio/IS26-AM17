package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.OfferingCard;
import it.polimi.ingsw.am17.Model.Player;

/**
 * Implements get one more card from upper row building
 * SINGLETON
 * Game effect
 */
public class BuildingTypeB extends BuildingCard {
    public BuildingTypeB() {
        super(3, 9, 3);
    }

    @Override
    public void resolveEffect(Player player) {
        OfferingCard pseudoOfferingCard = new OfferingCard(2, 'Z', 0, 1, 0);
        // TODO: make sure the player has not his offering card anymore and that this is called before the game stops considering new turns.
        player.setOfferingCard(pseudoOfferingCard);
    }
}

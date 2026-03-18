package it.polimi.ingsw.am17.Model.GameCard;

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
        // TODO: idea è fare una pseudo offeringCard.
        player.setOfferingCard();
    }
}

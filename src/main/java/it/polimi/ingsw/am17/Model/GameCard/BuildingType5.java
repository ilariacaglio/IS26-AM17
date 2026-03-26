package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.Player;

/**
 * Get one food during PaintingEvent for each artist
 * Event effect
 * SINGLETON
 */
public class BuildingType5 extends BuildingCard {
    public BuildingType5() {
        super(2, 5, 6);
    }

    @Override
    public void resolveEffect(Player player) {
        int numArtists = 0;
        // TODO: count numArtists

        player.addFood(numArtists);
    }
}

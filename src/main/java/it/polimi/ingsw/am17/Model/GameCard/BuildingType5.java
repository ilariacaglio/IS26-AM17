package it.polimi.ingsw.am17.Model.GameCard;

import java.util.List;

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
    public int FoodBonus(List<GameCard> playerCards) {
        int numArtists = 0;
        // TODO: count numArtists

        return numArtists;
    }
}

package it.polimi.ingsw.am17.Model.GameCard;

import java.util.List;

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
    public int FoodBonus(List<GameCard> playerTribeRow) {
        int numHunters = 0;
        // TODO
        return numHunters;
    }

    @Override
    public int getBonusPoints() {
        int numHunters = 0;
        // TODO
        return numHunters;
    }

    // NOTE: counts two times! Can I share that calculation? No because different methods...
}

package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.Player;

import java.util.List;

/**
 * Adds 3 stars when counting for the RitualEvent
 * Event effect
 * SINGLETON
 */
public class BuildingType9 extends BuildingCard {
    private static final int era = 2;
    private static final int foodCost = 6;
    private static final int bonusPoints = 4;
    public BuildingType9() {
        super(era, foodCost, bonusPoints);
    }

    @Override
    public int GiveBonusStarInRitualEvent(List<CharacterCard> characterCards) {
        return 3;
    }
}

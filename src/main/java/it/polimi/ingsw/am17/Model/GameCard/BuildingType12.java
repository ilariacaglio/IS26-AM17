package it.polimi.ingsw.am17.Model.GameCard;

/**
 * Do not lose points from RitualEvent
 * Event effect
 * SINGLETON
 */
public class BuildingType12 extends BuildingCard {
    public BuildingType12() {
        super(1, 5, 2);
    }

    @Override
    public boolean isShieldFromRitualEvent() {
        return true;
    }
}

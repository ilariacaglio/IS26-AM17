package it.polimi.ingsw.am17.Model.GameCard;

/**
 * Get double the points from RitualEvent if "won". Get the points even if tied.
 * Event effect
 * SINGLETON
 */
public class BuildingType8 extends BuildingCard {
    public BuildingType8() {
        super(2, 7, 0);
    }
    public boolean DoubleRitualEventPoints() { return true; } // Tribes effect

}

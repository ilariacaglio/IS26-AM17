package it.polimi.ingsw.am17.Model.GameCard;

/**
 * Get double the points from RitualEvent if "won". Get the points even if tied.
 * Event effect
 * SINGLETON
 */
public class BuildingType8 extends BuildingCard {
    private static final int era = 2;
    private static final int foodCost = 7;
    private static final int bonusPoints = 0;
    public BuildingType8() {
        super(era, foodCost, bonusPoints);
    }
    public boolean DoubleRitualEventPoints() { return true; } // Tribes effect

}

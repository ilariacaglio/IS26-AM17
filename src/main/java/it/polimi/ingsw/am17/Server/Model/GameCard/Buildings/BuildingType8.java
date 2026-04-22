package it.polimi.ingsw.am17.Server.Model.GameCard.Buildings;

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

    @Override
    public boolean hasDoubleRitualEventPoints() { return true; } // Tribes effect

}

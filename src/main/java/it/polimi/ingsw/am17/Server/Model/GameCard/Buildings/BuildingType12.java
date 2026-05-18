package it.polimi.ingsw.am17.Server.Model.GameCard.Buildings;

/**
 * Do not lose points from RitualEvent
 * Event effect
 * SINGLETON
 */
public class BuildingType12 extends BuildingCard {
    private static final int era = 1;
    private static final int foodCost = 5;
    private static final int bonusPoints = 2;
    public BuildingType12() {
        super(era, foodCost, bonusPoints);
    }

    @Override
    public boolean isShieldedFromRitualEvent() {
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " Effect: -0PP in RitualEvent] ";
    }

    @Override
    public String getImagePath()
    {
        return "/Images/Buildings/building1_12.png";
    }

}

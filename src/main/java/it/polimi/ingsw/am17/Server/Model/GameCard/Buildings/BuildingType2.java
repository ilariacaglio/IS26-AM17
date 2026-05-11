package it.polimi.ingsw.am17.Server.Model.GameCard.Buildings;

/**
 * Allows to get one more card from the upper row.
 * Game effect
 * SINGLETON
 */
public class BuildingType2 extends BuildingCard {
    private static final int era = 3;
    private static final int foodCost = 10;
    private static final int bonusPoints = 0;
    public BuildingType2() {
        super(era, foodCost, bonusPoints);
    }

    @Override
    public boolean hasOneMoreMove() {
//        OfferingCard pseudoOfferingCard = new OfferingCard(2, 'Z', 0, 1, 0);
//        pseudoOfferingCard.setPlayer(player);
//        // TODO: make sure the player has not his offering card anymore and that this is called before the game stops considering new turns.
//        return pseudoOfferingCard;
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " Effect: +25PP] ";
    }

    @Override
    public String getImagePath()
    {
        return "/Images/Buildings/building3_2.png";
    }
}

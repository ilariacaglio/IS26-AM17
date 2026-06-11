package it.polimi.ingsw.am17.Server.Model.GameCard.Buildings;

import it.polimi.ingsw.am17.Server.Model.GameState;

/**
 * Allows to get one more card from the upper row.
 * Game effect
 * SINGLETON
 */
public class BuildingType2 extends BuildingCard {
    private static final GameState era = GameState.ERA3;
    private static final int foodCost = 3;
    private static final int bonusPoints = 0;
    public BuildingType2() {
        super(era, foodCost, bonusPoints);
    }

    @Override
    public boolean hasOneMoreMove() {
//        // TODO: make sure the player has not his offering card anymore and that this is called before the game stops considering new turns.
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " Effect: +1 card from the upper row] ";
    }

    @Override
    public String getImagePath()
    {
        return "/Images/Buildings/building3_2.png";
    }

}

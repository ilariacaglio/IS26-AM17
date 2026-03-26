package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.Player;

public class BuildingCard extends GameCard {
    private final int foodCost;
    private final int bonusPoints;

    public BuildingCard(int era, int foodCost, int bonusPoints) {
        super(era);
        this.foodCost = foodCost;
        this.bonusPoints = bonusPoints;
    }

    public int getFoodCost() {
        return foodCost;
    }

    public int getBonusPoints() {
        return bonusPoints;
    }

    public int finalPoints() { return 0; } // get points from
    public int foodDiscount() { return 0; }
    public int
}

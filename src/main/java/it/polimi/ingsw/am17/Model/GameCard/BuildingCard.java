package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.Player;

public class BuildingCard extends GameCard{
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

    // apply building effect to player
    public void resolveEffect(Player player) {
    }

    public int getBonusPoints() {
        return bonusPoints;
    }
}

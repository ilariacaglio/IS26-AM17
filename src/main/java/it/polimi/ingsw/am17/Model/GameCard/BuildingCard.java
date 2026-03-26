package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.CardRow;
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

    // effects implemented
    public int FinalPoints(CardRow playerTribeRow) { return 0; }
    public int FoodDiscount(CardRow playerTribeRow) { return 0; }
    public int FoodBonus(CardRow playerTribeRow) { return 0; }
    public int PointsBonus(CardRow playerTribeRow) { return 0; }
    public int StarBonus(CardRow playerTribeRow) { return 0; }
    public boolean isShieldFromRitualEvent() { return false; }
    public void playExtraCard(Player player) { return; }
}

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
    public int getFinalPoints(CardRow playerTribeRow) { return 0; }
    public int getFoodDiscount(CardRow playerTribeRow) { return 0; }
    public int getFoodBonus(CardRow playerTribeRow) { return 0; }
    public void playExtraCard(Player player) { return; }
}

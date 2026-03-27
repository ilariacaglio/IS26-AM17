package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.Player;

import java.util.List;

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
    public int FinalPoints(List<CharacterCard> playerCharacterCards) { return 0; }
    public int FoodDiscount(List<CharacterCard> playerCharacterCards) { return 0; }
    public int FoodBonus(List<CharacterCard> playerCharacterCards) { return 0; }
    public int PointsBonus(List<GameCard> playerTribeRow) { return 0; }
    public int StarBonus(List<GameCard> playerTribeRow) { return 0; }
    public boolean isShieldFromRitualEvent() { return false; }
    public void playExtraCard(Player player) { return; }
}

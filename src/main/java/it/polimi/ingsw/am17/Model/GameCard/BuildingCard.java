package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.Player;

import java.util.List;

public class BuildingCard {
    private final int foodCost;
    private final int bonusPoints;
    private final int era;

    public BuildingCard(int era, int foodCost, int bonusPoints) {
        this.era = era;
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
    public int FinalPoints(List<CharacterCard> characterCards) { return 0; }
    public int FoodDiscount(List<CharacterCard> characterCards) { return 0; }
    public int FoodBonus(List<CharacterCard> characterCards) { return 0; } // EventEffect: PaintingEvent
    public int FoodBonus(List<CharacterCard> characterCards, CharacterCard newCard) { return 0; } // CardEffect
    public int FoodBonus(Player player) { return 0; } // TurnEffect
    public int PointsBonus(List<CharacterCard> characterCards) { return 0; }
    public int StarBonus(List<CharacterCard> characterCards) { return 0; } // EventEffect: returns true if a
    public boolean isShieldFromRitualEvent() { return false; } // EventEffect: returns true if a player should not lose points from rituals
    public void playExtraCard(Player player) { return; }
    public boolean isDoubleRitualEventPoints() { return false; } // Tribes effect
}

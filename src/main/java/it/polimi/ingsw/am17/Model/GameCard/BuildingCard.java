package it.polimi.ingsw.am17.Model.GameCard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class BuildingCard implements Serializable {
    private final int foodCost;
    private final int bonusPoints;
    private final int era;

    @JsonCreator
    public BuildingCard(
            @JsonProperty("era") int era,
            @JsonProperty("foodCost") int foodCost,
            @JsonProperty("bonusPoints") int bonusPoints) {
        this.era = era;
        this.foodCost = foodCost;
        this.bonusPoints = bonusPoints;
    }

    public int getFoodCost() {
        return foodCost;
    }
    public int getEra() { return era; }
    public int getBonusPoints() { return bonusPoints; }

    // effects implemented
    public int GetAdditionalFinalPoints(List<CharacterCard> characterCards) { return 0; }
    public int GetFoodDiscountInFoodEvent(List<CharacterCard> characterCards) { return 0; }
    public int AddFoodPerHunterInHuntingEvent(List<CharacterCard> characterCards) { return 0; } // EventEffect: HuntingEvent
    public int AddFoodPerHunterInPaintingEvent(List<CharacterCard> characterCards) { return 0; } // EventEffect: PaintingEvent
    public int GetFoodBonusFromCardAcquisition(List<CharacterCard> characterCards, CharacterCard newCard) { return 0; } // CardEffect
    public int GetFoodBonusFromTurnOrder() { return 0; } // TurnEffect
    public int AddPointPerHunterInHuntingEvent(List<CharacterCard> characterCards) { return 0; }
    public int GiveBonusStarInRitualEvent(List<CharacterCard> characterCards) { return 0; } // EventEffect: returns true if a
    public boolean isShieldedFromRitualEvent() { return false; } // EventEffect: returns true if a player should not lose points from rituals
    public boolean hasOneMoreMove() { return false; }
    public boolean hasDoubleRitualEventPoints() { return false; } // Tribes effect
}

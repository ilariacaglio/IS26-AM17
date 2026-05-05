package it.polimi.ingsw.am17.Server.Model.GameCard.Buildings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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
    public int AddFoodPerArtistInPaintingEvent(List<CharacterCard> characterCards) { return 0; } // EventEffect: PaintingEvent
    public int GetFoodBonusFromCardAcquisition(List<CharacterCard> characterCards, CharacterCard newCard) { return 0; } // CardEffect
    public int GetFoodBonusFromTurnOrder() { return 0; } // TurnEffect
    public int AddPointPerHunterInHuntingEvent(List<CharacterCard> characterCards) { return 0; }
    public int GiveBonusStarInRitualEvent(List<CharacterCard> characterCards) { return 0; } // EventEffect: returns true if a
    public boolean isShieldedFromRitualEvent() { return false; } // EventEffect: returns true if a player should not lose points from rituals
    public boolean hasOneMoreMove() { return false; }
    public boolean hasDoubleRitualEventPoints() { return false; } // Tribes effect

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuildingCard that = (BuildingCard) o;
        return foodCost == that.foodCost && bonusPoints == that.bonusPoints && era == that.era;
    }

    @Override
    public int hashCode() {
        return Objects.hash(foodCost, bonusPoints, era);
    }

    @Override
    public String toString() {
        return foodCost + "F " + bonusPoints + "BP";
    }
}

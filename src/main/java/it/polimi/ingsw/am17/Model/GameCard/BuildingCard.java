package it.polimi.ingsw.am17.Model.GameCard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Model.OfferingCard;
import it.polimi.ingsw.am17.Model.Player;

import java.util.List;

public class BuildingCard {
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
    public int FinalPoints(List<CharacterCard> characterCards) { return 0; }
    public int FoodDiscount(List<CharacterCard> characterCards) { return 0; }
    public int FoodBonusFromHunters(List<CharacterCard> characterCards) { return 0; } // EventEffect: PaintingEvent
    public int FoodBonusFromArtists(List<CharacterCard> characterCards) { return 0; } // EventEffect: PaintingEvent
    public int FoodBonusFromCardAcquisition(List<CharacterCard> characterCards, CharacterCard newCard) { return 0; } // CardEffect
    public int FoodBonusFromTurnOrder(OfferingCard offeringCard) { return 0; } // TurnEffect
    public int PointsBonus(List<CharacterCard> characterCards) { return 0; }
    public int StarBonus(List<CharacterCard> characterCards) { return 0; } // EventEffect: returns true if a
    public boolean isShieldFromRitualEvent() { return false; } // EventEffect: returns true if a player should not lose points from rituals
    public boolean playOneMoreMove() { return false; }
    public boolean isDoubleRitualEventPoints() { return false; } // Tribes effect
}

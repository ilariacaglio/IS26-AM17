package it.polimi.ingsw.am17.Server.Model.GameCard.Buildings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.GameCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameState;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class BuildingCard extends GameCard implements Serializable {
    private final Integer foodCost;
    private final Integer bonusPoints;

    @JsonCreator
    public BuildingCard(
            @JsonProperty("era") GameState era,
            @JsonProperty("foodCost") Integer foodCost,
            @JsonProperty("bonusPoints") Integer bonusPoints) {
        super(true, era);
        this.foodCost = foodCost;
        this.bonusPoints = bonusPoints;
    }

    public Integer getFoodCost() {
        return foodCost;
    }
    public Integer getBonusPoints() { return bonusPoints; }

    // end game
    public int GetAdditionalFinalPoints(List<CharacterCard> characterCards) { return 0; }

    // events
    public int GetFoodDiscountInFoodEvent(List<CharacterCard> characterCards) { return 0; }
    public int AddFoodPerHunterInHuntingEvent(List<CharacterCard> characterCards) { return 0; }
    public int AddFoodPerArtistInPaintingEvent(List<CharacterCard> characterCards) { return 0; }
    public int AddPointPerHunterInHuntingEvent(List<CharacterCard> characterCards) { return 0; }
    public int GiveBonusStarInRitualEvent(List<CharacterCard> characterCards) { return 0; } // EventEffect: returns true if a

    /**
     * @return true if a player should not lose points from ritual events.
     */
    public boolean isShieldedFromRitualEvent() { return false; }
    public boolean hasDoubleRitualEventPoints() { return false; }

    // card acquisition
    public int GetFoodBonusFromCardAcquisition(List<CharacterCard> characterCards, CharacterCard newCard) { return 0; }

    // turn order
    /** When (and only when!) you get food from the turn order card, get additional food.
     * @return additional amount of food that should be given.
     */
    public int GetMoreFoodFromTurnOrderCard() { return 0; } // TurnEffect
    public boolean hasOneMoreMove() { return false; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuildingCard that = (BuildingCard) o;
        return foodCost.equals(that.foodCost) && bonusPoints.equals(that.bonusPoints) && getEra().equals(that.getEra());
    }

    @Override
    public int hashCode() {
        return Objects.hash(foodCost, bonusPoints, getEra());
    }

    @Override
    public String toString() {
        return "[" + foodCost + "F " + bonusPoints + "BP";
    }

    public String getImagePath(){
        return "";
    }

}

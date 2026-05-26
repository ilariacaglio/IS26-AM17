package it.polimi.ingsw.am17.Server.Model.GameCard.Buildings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameState;

import java.util.List;
import java.util.Objects;

/**
 * Save 1 food for each characterType when FoodEvent
 * Event effect
 * MULTIPLE
 */
public class BuildingType13M extends BuildingCard {
    private final CardType characterType;

    @SuppressWarnings("unused") // needed for jackson
    public CardType getCharacterType() {
        return characterType;
    }

    @JsonCreator
    public BuildingType13M(
            @JsonProperty("era") GameState era,
            @JsonProperty("foodCost") int foodCost,
            @JsonProperty("bonusPoints") int bonusPoints,
            @JsonProperty("characterType") CardType characterType) {
        super(era, foodCost, bonusPoints);
        this.characterType = characterType;
    }

    @Override
    public int GetFoodDiscountInFoodEvent(List<CharacterCard> playerCharacterCards) {
        return (int) playerCharacterCards.stream().filter(card -> card.getCardType() == characterType).count();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuildingType13M that = (BuildingType13M) o;
        return characterType == that.characterType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(characterType);
    }

    @Override
    public String toString() {
        return super.toString() + " Effect: -1PP/" + characterType.toString() + " in FoodEvent] ";
    }
    @Override
    public String getImagePath()
    {
        return "/Images/Buildings/building"+ getEra() +"_13_"+characterType.toString().toLowerCase()+".png";
    }
}
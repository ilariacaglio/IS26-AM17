package it.polimi.ingsw.am17.Model.GameCard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Model.Player;

import java.util.List;

/**
 * Save 1 food for each characterType when FoodEvent
 * Event effect
 * MULTIPLE
 */
public class BuildingType13M extends BuildingCard {
    private final CardType characterType;
    @JsonCreator
    public BuildingType13M(
            @JsonProperty("era") int era,
            @JsonProperty("foodCost") int foodCost,
            @JsonProperty("bonusPoints") int bonusPoints,
            @JsonProperty("characterType") CardType characterType) {
        super(era, foodCost, bonusPoints);
        this.characterType = characterType;
    }

    @Override
    public int FoodDiscount(List<CharacterCard> playerCharacterCards) {
        return (int) playerCharacterCards.stream().filter(card -> card.getCardType() == characterType).count();
    }
}
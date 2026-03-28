package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.Player;

import java.util.List;

/**
 * Save 1 food for each characterType when FoodEvent
 * Event effect
 * MULTIPLE
 */
public class BuildingType13M extends BuildingCard {
    private final CardType characterType;
    public BuildingType13M(int era, int foodCost, int bonusPoints, CardType characterType) {
        super(era, foodCost, bonusPoints);
        this.characterType = characterType;
    }

    @Override
    public int FoodDiscount(List<CharacterCard> playerCharacterCards) {
        return (int) playerCharacterCards.stream().filter(card -> card.getCardType() == characterType).count();
    }
}
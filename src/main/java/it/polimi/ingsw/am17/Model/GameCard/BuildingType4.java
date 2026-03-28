package it.polimi.ingsw.am17.Model.GameCard;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Get 6 points for each 6 different set of characters.
 * Final effect
 * SINGLETON
 */
public class BuildingType4 extends BuildingCard {
    private static final int era = 2;
    private static final int foodCost = 5;
    private static final int bonusPoints = 6;
    public BuildingType4() {
        super(era, foodCost, bonusPoints);
    }

    @Override
    public int FinalPoints(List<CharacterCard> playerCharacterCards) {

        // Count how many cards of each character type with some stream magic
        Map<CardType, Long> typeCount = playerCharacterCards.stream()
                .collect(Collectors.groupingBy(
                        CharacterCard::getCardType,
                        Collectors.counting()
                ));

        // Return the minimum of the types (so you're sure you have all the other characters, it's the same as the set of 6 characters) times the points
        return 6 * Collections.min(typeCount.values()).intValue();
    }
}

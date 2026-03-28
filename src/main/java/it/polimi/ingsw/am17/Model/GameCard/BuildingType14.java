package it.polimi.ingsw.am17.Model.GameCard;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Get 5 food each time you complete a set of 6 cards.
 * Card effect
 * SINGLETON
 */
public class BuildingType14 extends BuildingCard {
    public BuildingType14() {
        super(2, 6, 4);
    }

    @Override
    public int FoodBonus(List<CharacterCard> characterCards, CharacterCard newCard) {

        // TODO: check if working
        // Count how many cards of each character type with some stream magic
        Map<CardType, Long> typeCount = characterCards.stream()
                .collect(Collectors.groupingBy(
                        CharacterCard::getCardType,
                        Collectors.counting()
                ));

        // If you're missing any characterType different than the new one, you don't have a new set
        for  (Map.Entry<CardType, Long> entry : typeCount.entrySet()) {
            if (entry.getKey() != newCard.getCardType())
                if (entry.getValue() == 0) {
                    return 0;
                }
        }

        // else you do and get the points
        return 5;
    }
}

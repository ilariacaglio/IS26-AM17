package it.polimi.ingsw.am17.Server.Model.GameCard.Buildings;

import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Get 6 points for each set of 6 different Character types.
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
    public int GetAdditionalFinalPoints(List<CharacterCard> playerCharacterCards) {
        // Group by character type and count occurrences
        Map<CardType, Long> typeCounts = playerCharacterCards.stream()
                .collect(Collectors.groupingBy(CharacterCard::getCardType, Collectors.counting()));

        // If not all 6 character types are present, return 0
        if (typeCounts.size() < 6) {
            return 0;
        }

        // Return 6 points for each complete set of 6 different character types
        // The number of complete sets is determined by the character type with the fewest cards
        return bonusPoints * Collections.min(typeCounts.values()).intValue();
    }

    @Override
    public String toString() {
        return super.toString() + " Effect: +6PP/each 6 unique characters] ";
    }
    @Override
    public String getImagePath()
    {
        return "/Images/Buildings/building2_4.png";
    }
    @Override
    public String getImagePath()
    {
        return "/Images/Buildings/building2_4.png";
    }
}

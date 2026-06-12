package it.polimi.ingsw.am17.Server.Model.GameCard.Buildings;

import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameState;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Get 6 points for each set of 6 different Character types.
 * Final effect
 * SINGLETON
 */
public class BuildingType4 extends BuildingCard {
    private static final GameState era = GameState.ERA2;
    private static final int foodCost = 5;
    private static final int bonusPoints = 6;

    private static final Logger logger = Logger.getLogger(BuildingType1.class.getName());

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
        int totalPoints = bonusPoints * Collections.min(typeCounts.values()).intValue();
        logger.info("Getting +" + totalPoints + "PP from BuildingType4");
        return totalPoints;
    }

    @Override
    public String toString() {
        return super.toString() + " Effect: +6PP/each 6 unique characters when game ends] ";
    }
    @Override
    public String getImagePath()
    {
        return "/Images/Buildings/building2_4.png";
    }
}

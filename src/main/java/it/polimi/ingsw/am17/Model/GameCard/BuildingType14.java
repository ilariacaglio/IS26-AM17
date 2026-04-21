package it.polimi.ingsw.am17.Model.GameCard;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Get 5 food each time you complete a set of 6 cards.
 * Card effect
 * SINGLETON
 */
public class BuildingType14 extends BuildingCard {
    private static final int era = 1;
    private static final int foodCost = 6;
    private static final int bonusPoints = 4;
    private final int numberOfCharacter = (int) Arrays.stream(CardType.values())
            .filter(type -> !type.name().endsWith("_EVENT"))
            .count();
    public BuildingType14() {
        super(era, foodCost, bonusPoints);
    }

    @Override
    public int GetFoodBonusFromCardAcquisition(List<CharacterCard> characterCards, CharacterCard newCard) {

        //create frequency array
        int[] presenceByCharacterType = new int[numberOfCharacter];

        //Populate frequency array
        for(CharacterCard card : characterCards)
        {
            presenceByCharacterType[card.getCardType().ordinal()]++;
        }

        //check min value of frequency array
        int minValueBefore = Arrays.stream(presenceByCharacterType).min().orElse(0);

        //Increment the count for the specific type of the new card
        presenceByCharacterType[newCard.getCardType().ordinal()]++;

        //check new min value of frequency array
        int minValueAfter = Arrays.stream(presenceByCharacterType).min().orElse(0);

        if(minValueAfter > minValueBefore)
            return 5;
        else
            return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuildingType14 that = (BuildingType14) o;
        return numberOfCharacter == that.numberOfCharacter;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(numberOfCharacter);
    }
}

package it.polimi.ingsw.am17.Model.GameCard;

import java.util.Arrays;
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
    private static final int era = 1;
    private static final int foodCost = 6;
    private static final int bonusPoints = 4;
    public BuildingType14() {
        super(era, foodCost, bonusPoints);
    }

    @Override
    public int FoodBonusFromCardAcquisition(List<CharacterCard> characterCards, CharacterCard newCard) {


        int[] presenceByCharacterType = new int[6];

        for(CharacterCard card : characterCards)
        {
            presenceByCharacterType[card.getCardType().ordinal()]++;
        }

        //check min value of characterCards
        int minValueBefore = Arrays.stream(presenceByCharacterType).min().orElse(0);

        //count the new card
        presenceByCharacterType[newCard.getCardType().ordinal()]++;

        //check min value of characterCards with new card
        int minValueAfter = Arrays.stream(presenceByCharacterType).min().orElse(0);

        if(minValueAfter > minValueBefore)
            return 5;
        else
            return 0;
    }
}

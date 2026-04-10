package it.polimi.ingsw.am17.Model.GameCard;

import java.util.List;
import java.util.Objects;

/**
 * Get 3 food each time you pick an inventor you already have.
 * Card effect
 * SINGLETON
 */
public class BuildingType10 extends BuildingCard {
    private static final int era = 1;
    private static final int foodCost = 3;
    private static final int bonusPoints = 4;
    public BuildingType10() {
        super(era, foodCost, bonusPoints);
    }

    @Override
    public int GetFoodBonusFromCardAcquisition(List<CharacterCard> characterCards, CharacterCard newCard) {

        // if new card is inventor
        if (newCard.getCardType() == CardType.INVENTOR) {
            Inventor newInventor = (Inventor) newCard;

            // get inventors had already with stream magic
            List<Inventor> inventors = characterCards.stream()
                    .filter(card -> card.getCardType() == CardType.INVENTOR)
                    .map(card -> (Inventor) card)
                    .toList();

            for (Inventor inventor : inventors) {
                if (Objects.equals(inventor.getIcon(), newInventor.getIcon())) {
                    return 3;
                }
            }
        }

        // Else return 0
        return 0;
    }

}

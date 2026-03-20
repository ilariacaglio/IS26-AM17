package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.CardRow;
import it.polimi.ingsw.am17.Model.OfferingCard;
import it.polimi.ingsw.am17.Model.Player;

/**
 * Gives extra points for each character of given type.
 * Final effect
 * MULTIPLE
 */
public class BuildingTypeC extends BuildingCard {
//    private string characterType;
    private int points; // Extra points for each characterType

    public BuildingTypeC(int foodCost, int bonusPoints, CharacterCard characterType) {
        super(3, foodCost, bonusPoints);
        this.characterType = characterType;
        this.points = bonusPoints;
    }

    @Override
    public void resolveEffect(CardRow cardRow) {
        for (GameCard card : cardRow.getCards()) {
            if (card instanceof Hunter) {

            }
        }
    }
}

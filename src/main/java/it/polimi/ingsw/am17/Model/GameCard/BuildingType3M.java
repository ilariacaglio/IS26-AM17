package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.CardRow;
import it.polimi.ingsw.am17.Model.Player;

/**
 * Gives extra points for each character of given type.
 * Final effect
 * MULTIPLE
 */
public class BuildingType3M extends BuildingCard {
    private int pointsFromEachCharacter; // Extra pointsFromEachCharacter for each characterType

    public BuildingType3M(int foodCost, int bonusPoints, CharacterCard characterType, int pointsFromEachCharacter) {
        super(3, foodCost, bonusPoints);
        this.characterType = characterType;
        this.pointsFromEachCharacter = pointsFromEachCharacter;
    }

    @Override
    public int FinalPoints(CardRow playerTribeRow) {
        CardRow playerRow = player.getCardRow();
        int count = 0;
        for (GameCard card : playerRow.getCards()) {
            // TODO: implement with attribute check
//            if card is of class characterType {
//                count++;
//            }
        }

        player.addPp(count * pointsFromEachCharacter);
    }
}

package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.CardRow;
import it.polimi.ingsw.am17.Model.Player;

/**
 * Gives extra pointsFromEachCharacter for each character of given type.
 * Final effect
 * MULTIPLE
 */
public class BuildingTypeC extends BuildingCard {
    private int pointsFromEachCharacter; // Extra pointsFromEachCharacter for each characterType

    public BuildingTypeC(int foodCost, int bonusPoints, CharacterCard characterType, int pointsFromEachCharacter) {
        super(3, foodCost, bonusPoints);
        this.characterType = characterType;
        this.pointsFromEachCharacter = pointsFromEachCharacter;
    }

    @Override
    public void resolveEffect(Player player) {
        CardRow playerRow = player.getCardRow();
        int count = 0;
        for (GameCard card : playerRow.getCards()) {
            if card is of class characterType {
                count++;
            }
        }

        player.addPp(count * pointsFromEachCharacter);
    }
}

package it.polimi.ingsw.am17.Server.Utility;

import it.polimi.ingsw.am17.CommonInterfaces.ErrorType;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.InvalidOperationException;

import java.util.ArrayList;
import java.util.List;

public class MoveValidator {
    /**
     * Validates card selection for the player action "pickTribeCards".
     *
     * @param numToSelectFromUpper Number of cards that must be selected (if possible!) from the upper row.
     * @param numToSelectFromLower Number of cards that must be selected (if possible!) from the lower row.
     * @param characterCards       Selected character cards.
     * @param buildingCards        Selected building cards.
     */
    public static Exception validateCardChoice(int numToSelectFromUpper, int numToSelectFromLower, List<CharacterCard> characterCards,
                                    List<BuildingCard> buildingCards, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                                    List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) {

        // get character cards from tribe rows (filter out events)
        List<CharacterCard> upperRowCharacterCards = new ArrayList<>(upperRow.stream()
                .filter(card -> card.getCardType().isCharacter())
                .map(CharacterCard.class::cast)
                .toList());
        List<CharacterCard> lowerRowCharacterCards = new ArrayList<>(lowerRow.stream()
                .filter(card -> card.getCardType().isCharacter())
                .map(CharacterCard.class::cast)
                .toList());

        // decrease the number of cards the player has to (still) select when a match is found
        for (CharacterCard card : characterCards) {
            if (upperRowCharacterCards.contains(card)) {
                numToSelectFromUpper--;
                upperRowCharacterCards.remove(card);
            } else if (lowerRowCharacterCards.contains(card)) {
                numToSelectFromLower--;
                lowerRowCharacterCards.remove(card);
            } else {
                return new InvalidOperationException(ErrorType.INVALID_CHARACTER_CARD);
            }
        }
        for (BuildingCard card : buildingCards) {
            if (upperBuildingRow.contains(card)) {
                numToSelectFromUpper--;
            } else if (lowerBuildingRow.contains(card)) {
                numToSelectFromLower--;
            } else {
                return new InvalidOperationException(ErrorType.INVALID_BUILDING_CARD);
            }
        }

        // if the player still has cards to select (counters != 0),
        // AND it is possible to select more cards (i.e. row not empty), the choice is not valid.
        if ((numToSelectFromUpper != 0 && !upperRowCharacterCards.isEmpty()) || (numToSelectFromLower != 0 && !lowerRowCharacterCards.isEmpty())) {
            return new InvalidOperationException(ErrorType.INVALID_CARDS_NUMBER);
        }
        return null;

    }
}

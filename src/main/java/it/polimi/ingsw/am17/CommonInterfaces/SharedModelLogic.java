package it.polimi.ingsw.am17.CommonInterfaces;

import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;

public class SharedModelLogic {
    private static final Logger logger = Logger.getLogger(SharedModelLogic.class.getName());

    /**
     * Dequeues and enqueues the player to allow other players to play.
     */
    public static void movePlayerInQueue(Queue<Player> orderedPlayers) {
        logger.info("Moving player in queue");
        // remove player from queue
        Player lastPlayer = orderedPlayers.poll();
        // add player as last element of queue
        orderedPlayers.add(lastPlayer);
    }

    /**
     * Checks that every player is set into an offering card
     * @return true if they are, false otherwise
     */
    public static boolean isEveryPlayerInOfferingCard(Queue<Player> orderedPlayers, List<OfferingCard> offeringCards) {
        logger.info("Checking if every  player has an offering card");
        return orderedPlayers.stream()
                .allMatch(player ->
                        offeringCards.stream()
                                .anyMatch(
                                        card -> card.getPlayer() != null
                                                && card.getPlayer().equals(player))
        );
    }

    /**
     * @return true if the player is in an offering card (excluding building2OfferingCard), false otherwise
     */
    public static boolean isPlayerInOfferingCard(Player player, List<OfferingCard> offeringCards) {
        logger.info("Checking if player " + player.getNickname() + " has an offering card");
        return offeringCards.stream().anyMatch(o -> player.equals(o.getPlayer()));
    }

    /**
     * @return true if every player of the game is not into an offering card (excluding building2OfferingCard), false otherwise
     */
    private static boolean noPlayerInOfferingCards(List<OfferingCard> offeringCards) {
        logger.info("Checking if no player has an offering card");
        for (OfferingCard oc : offeringCards) {
            if(oc.getPlayer() != null)
                return false;
        }
        return true;
    }

    /**
     * @return the offering card object in the list that matches the given letter
     */
    public static OfferingCard getOfferingCardFromLetter(Character offeringCardLetter, List<OfferingCard> offeringCards) {
        return offeringCards.stream()
                .filter(c -> offeringCardLetter.equals(c.getOrderLetter()))
                .findFirst().orElse(null);
    }

    /**
     * @return leftmost offering card with player in the offering track.
     */
    public static OfferingCard getNextOccupiedOfferingCard(List<OfferingCard> offeringCards, OfferingCard building2OC) {

        // lookup in normal offering cards
        OfferingCard offeringCard = offeringCards.stream()
                .filter(card -> card.getPlayer() != null)
                .min(Comparator.comparing(OfferingCard::getOrderLetter))
                .orElse(null);

        // if not found, lookup in building2OfferingCard
        if (offeringCard == null && building2OC.getPlayer() != null) offeringCard = building2OC;

        return offeringCard;
    }

    /**
     * Checks if it is the turn of the given player.
     * @return true if it is players turn, false otherwise.
     */
    public static boolean isPlayerTurn(Player playerToCheck, Queue<Player> orderedPlayers,
                                       boolean isPickOCPhase, List<OfferingCard> offeringCards, OfferingCard building2OC) {
        if (!isPickOCPhase){
            // if is pick tribe cards phase
            // If all standard turns are completed and the Building 2 extra turn is booked,
            // restrict the current turn exclusively to the Building 2 owner.
            if (noPlayerInOfferingCards(offeringCards) && building2OC.getPlayer() != null)
                return playerToCheck.hasBuilding2();
        }

        // default check
        // if is pick offering card phase the player has to be at head of the queue
        // same in pick tribes without building type 2
        return isPlayerHeadInQueue(playerToCheck, orderedPlayers);
    }

    /**
     * This method is used to check if it is the player turn in
     * the offering card choice phase by comparing it to the one at the head of the queue
     * @return true if the given player is at the head of the queue, false otherwise.
     */
    private static boolean isPlayerHeadInQueue(Player playerToCheck, Queue<Player> orderedPlayers){
        logger.info("Checking if it is " + playerToCheck.getNickname() + "'s turn");
        return playerToCheck.equals(orderedPlayers.peek());
    }

    /**
     * Validates player action "pickOfferingCard".
     */
    public static void validateOfferingCardTurnAction(Character offeringCardLetter, Player player, List<OfferingCard> offeringCards, Queue<Player> orderedPlayers) {
        logger.info("Validating offering card choice");

        // check if is player turn (in this case is enough checking if the player is in the head of the queue)
        if (!isPlayerHeadInQueue(player, orderedPlayers))
            throw new InvalidOperationException(ErrorType.OUT_OF_TURN);

        // check if letter is null
        if(offeringCardLetter == null)
            throw new InvalidOperationException(ErrorType.MISSING_OFFERING_CARD_LETTER);

        // get offering card from letter
        OfferingCard offeringCard = getOfferingCardFromLetter(offeringCardLetter, offeringCards);

        //check if offeringCard is valid
        if (offeringCard == null)
            throw new InvalidOperationException(ErrorType.INVALID_OFFERING_CARD_LETTER);

        // check if player is not in an Offering Card already
        if (isPlayerInOfferingCard(player,offeringCards))
            throw new InvalidOperationException(ErrorType.OFFERING_CARD_ALREADY_SELECTED);

        // check if offering card is free
        if(offeringCard.getPlayer() != null)
            throw new InvalidOperationException(ErrorType.OFFERING_CARD_ALREADY_SELECTED);

    }

    /**
     * Validates player action "pickTribeCards".
     */
    public static void validateTribesCardTurnAction(Player player, Queue<Player> orderedPlayers,
                                                    List<OfferingCard> offeringCards, OfferingCard building2OC,
                                                    List<CharacterCard> characterCards, List<BuildingCard> buildingCards,
                                                    List<TribesCard> upperRow, List<TribesCard> lowerRow,
                                                    List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) {
        logger.info("Validating tribes card choice");

        // check if it is player turn
        if(!isPlayerTurn(player, orderedPlayers, false, offeringCards, building2OC))
            throw new InvalidOperationException(ErrorType.OUT_OF_TURN);

        // get next occupied offering card
        OfferingCard currentOffering = getNextOccupiedOfferingCard(offeringCards, building2OC);

        int numToSelectFromUpper = currentOffering.getNumCardsUpper();
        int numToSelectFromLower = currentOffering.getNumCardsLower();

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
                throw new InvalidOperationException(ErrorType.INVALID_CHARACTER_CARD);
            }
        }
        for (BuildingCard card : buildingCards) {
            if (upperBuildingRow.contains(card)) {
                numToSelectFromUpper--;
            } else if (lowerBuildingRow.contains(card)) {
                numToSelectFromLower--;
            } else {
                throw  new InvalidOperationException(ErrorType.INVALID_BUILDING_CARD);
            }
        }

        // if the player still has cards to select (counters != 0),
        // AND it is possible to select more cards (i.e. row not empty), the choice is not valid.
        if ((numToSelectFromUpper != 0 && !upperRowCharacterCards.isEmpty()) || (numToSelectFromLower != 0 && !lowerRowCharacterCards.isEmpty())) {
            throw new InvalidOperationException(ErrorType.INVALID_CARDS_NUMBER);
        }

        // selection legal: obtain cards
        try {
            player.addCards(characterCards, buildingCards);
        } catch (InvalidOperationException e) {
            if (e.getErrorType()==ErrorType.INSUFFICIENT_FOOD_BUILDINGS) {
                throw e;
            } else {
                throw new InvalidOperationException(ErrorType.UNKNOWN);
            }
        }
    }

    /**
     * @return the turn food for the specified amount of players
     */
    public static int[] getTurnFoodPoints(int numPlayers) {
        logger.fine("Getting turn food points for " + numPlayers + " players.");

        return switch (numPlayers) {
            case 2 -> new int[]{1, -1};
            case 3 -> new int[]{2, 0, -1};
            case 4 -> new int[]{2, 1, 0, -1};
            case 5 -> new int[]{3, 1, 0, 0, -1};
            default -> throw new IllegalStateException("Wrong number of players");
        };
    }

    /**
     * Handles offering cards and players queue after a tribes card selection (both for normal case and buildingTwo edge case)
     * Moves the player in the queue and frees up the offering card (turn logic).
     */
    public static void handleOfferingCardsAndPlayersQueue(Player player, List<OfferingCard> offeringCards, OfferingCard buildingTwoOfferingCard, Queue<Player> orderedPlayers) {
        // if a player is in a (normal) offering card...
        if (isPlayerInOfferingCard(player,offeringCards)) {
            // this update was called for a usual card selection
            // remove player from its offering card
            offeringCards.stream()
                    .filter(o -> o.getPlayer()!= null && o.getPlayer().equals(player))
                    .findFirst()
                    .ifPresent(o -> o.setPlayer(null));

            // if the player had the building2, book an extra turn
            if (player.hasBuilding2()) {
                buildingTwoOfferingCard.setPlayer(player);
            }
            // since the move is normal the queue should be updated
            SharedModelLogic.movePlayerInQueue(orderedPlayers);
        }
        // if this update was called but the player is not in a (normal) offering card
        // it means this was a move made from the extra offering card (BuildingTwo)
        else {
            buildingTwoOfferingCard.setPlayer(null);
            // N.B. the queue is not updated here!
            // this is because in the real game the player is already on the turn order card
        }
    }
}

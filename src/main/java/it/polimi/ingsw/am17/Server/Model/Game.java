package it.polimi.ingsw.am17.Server.Model;

import it.polimi.ingsw.am17.Server.Model.Decks.BuildingDeck;
import it.polimi.ingsw.am17.Server.Model.Decks.TribesDeck;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events.EventCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Utility.MoveValidator;

import static it.polimi.ingsw.am17.Server.Utility.CardParser.loadOfferingCards;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Game extends Subject {
    private final UUID id;
    private final int numPlayers;
    private int currentEra;

    private Stack<Player> orderedPlayer;

    private final List<OfferingCard> offeringCards;

    private final TribesDeck tribesDeck;
    private final List<TribesCard> upperRow;
    private List<TribesCard> lowerRow;

    private final BuildingDeck buildingDeck;

    private List<BuildingCard> upperBuildingRow;
    private List<BuildingCard> lowerBuildingRow;

    private final OfferingCard building2OfferingCard = new OfferingCard(2, 'Z', 0, 1, 0);
    private final int[] turnFoodPoints;

    public Game(UUID id, int numPlayers) {
        this.id = id;

        checkNumPlayers(numPlayers);

        this.numPlayers = numPlayers;
        currentEra = 0;
        orderedPlayer = new Stack<>();
        offeringCards = loadOfferingCards(numPlayers);

        tribesDeck = new TribesDeck(numPlayers);
        upperRow = new ArrayList<>();
        lowerRow = new ArrayList<>();

        buildingDeck = new BuildingDeck(numPlayers);
        upperBuildingRow = new ArrayList<>();
        lowerBuildingRow = new ArrayList<>();

        turnFoodPoints = getTurnFoodPoints();
    }

    private void checkNumPlayers(int numPlayers) {
        if (numPlayers < 2 || numPlayers > 5) {
            throw new IllegalArgumentException("Wrong number of players");
        }
    }

    private int[] getTurnFoodPoints() {
        return switch (numPlayers) {
            case 2 -> new int[]{1, -1};
            case 3 -> new int[]{2, 0, -1};
            case 4 -> new int[]{2, 1, 0, -1};
            case 5 -> new int[]{3, 1, 0, 0, -1};
            default -> throw new IllegalStateException("Wrong number of players");
        };
    }

    public boolean isStarted() {
        return currentEra > 0;
    }

    public boolean isEnded() {
        return currentEra < 0;
    }

    /**
     * @return leftmost offering card with player in the offering track.
     */
    private OfferingCard getNextOccupiedOfferingCard() {
        OfferingCard offCard = offeringCards.stream()
                .filter(card -> card.getPlayer() != null)
                .min(Comparator.comparing(OfferingCard::getOrderLetter))
                .orElse(null);
        if (offCard != null) {
            return offCard;
        } else if (building2OfferingCard.getPlayer() == null) {
            return null;
        } else {
            return building2OfferingCard;
        }
    }

    /**
     * Adds a player to the game.
     *
     * @param p player to add to the game
     */
    public void addPlayer(Player p) {
        if (isStarted()) {
            throw new IllegalStateException("The game has already started.");
        }
        if (orderedPlayer.stream().anyMatch(player -> player.getNickname().equals(p.getNickname()))) {
            throw new IllegalStateException("The game has already a player with the same nickname.");
        }
        if (orderedPlayer.stream().anyMatch(player -> player.getColor().equals(p.getColor()))) {
            String message = "The game has already a player with the same color. Unused colors: ";
            //Get All colors
            EnumSet<Color> unusedColors = EnumSet.allOf(Color.class);
            //Remove the colors that are currently in use
            orderedPlayer.forEach(player -> unusedColors.remove(player.getColor()));
            message = message.concat(unusedColors.toString());
            throw new IllegalStateException(message);
        }

        /*double check
        if (orderedPlayer.size() >= numPlayers) {
            throw new IllegalStateException("The game lobby is full (max " + numPlayers + " players).");
        }
        */
        if (orderedPlayer.contains(p)) {
            throw new IllegalArgumentException("This player is already in the lobby.");
        }

        orderedPlayer.add(p);
        notifyPlayerStack(orderedPlayer);

        //if we reached the number of players for the game we start the game
        if (orderedPlayer.size() == numPlayers)
            nextEra();
    }

    /**
     * Goes to the next era.
     * If the game is not started, it starts.
     * Notifies the observers.
     */
    private void nextEra() {
        switch (currentEra) {
            case 0:
                era1();
                notifyEra(currentEra);
                break;
            case 1:
                era2();
                notifyEra(currentEra);
                break;
            case 2:
                era3();
                notifyEra(currentEra);
                break;
            default:
                throw new IllegalStateException("Invalid era");
        }


        // notifyBuildingCards(upperBuildingRow, lowerBuildingRow);
    }

    private void moveDownBuildingCards() {
        lowerBuildingRow = new ArrayList<>(upperBuildingRow);
        upperBuildingRow.clear();
    }

    /**
     * Gives the starting food to players
     */
    private void giveFoodToPlayers() {
        int[] startingFood = {2, 3, 3, 4, 4};
        for (int i = 0; i < numPlayers && i < startingFood.length; i++) {
            orderedPlayer.get(i).addFood(startingFood[i]);
        }
    }

    /**
     * Starts the game by entering the first era.
     */
    private void era1() {
        if (isStarted()) {
            throw new IllegalStateException("The game has already started.");
        }

        // Set era and shuffle players
        this.currentEra = 1;
        Collections.shuffle(orderedPlayer);
        // notifyPlayerStack(orderedPlayer);

        giveFoodToPlayers();

        // Populate the rows
        int targetLowerRowSize = numPlayers + 1;
        int targetUpperRowSize = numPlayers + 4;

        while (lowerRow.size() < targetLowerRowSize) {
            TribesCard drawnCard = tribesDeck.Draw();

            if (drawnCard.getCardType().isCharacter()) {
                lowerRow.add(drawnCard);
            } else if (upperRow.size() < targetUpperRowSize) {
                upperRow.add(drawnCard);
            }
        }

        while (upperRow.size() < targetUpperRowSize) {
            upperRow.add(tribesDeck.Draw());
        }

        upperBuildingRow = new ArrayList<>(buildingDeck.drawAllEra1());

        // notifyTribesCards(upperRow,lowerRow);
        notifyStartGame(orderedPlayer, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow, offeringCards);
    }

    /**
     * Sets up the second era.
     */
    private void era2() {
        currentEra = 2;
        moveDownBuildingCards();
        upperBuildingRow = new ArrayList<>(buildingDeck.drawAllEra2());
    }

    /**
     * Sets up the third era.
     */
    private void era3() {
        currentEra = 3;
        lowerBuildingRow.clear();
        moveDownBuildingCards();
        upperBuildingRow = new ArrayList<>(buildingDeck.drawAllEra3());
    }

    private void turnOrderFoodBonus() {
        int i = 0;
        for (Player p : orderedPlayer) {
            //check if turnFood > 0
            if (turnFoodPoints[i] < 0) {
                //if not, check if player can pay the food (food price is not higher than 1)
                if (p.getFood() < 1)
                    p.addPp(-2);
                else
                    p.addFood(turnFoodPoints[i]);
            } else {
                //check if player has food bonus from buildings
                int foodFromBuilding = p.addFoodToTurnFood();
                p.addFood(turnFoodPoints[i] + foodFromBuilding);
            }
            i++;
        }
    }

    /**
     * Ends the current round by solving events.
     */
    // this method is public only for testing reasons
    public void endRound() {
        turnOrderFoodBonus();
        // Get events from the lower row.
        List<EventCard> events = lowerRow.stream()
                .filter(card -> card.getCardType().isEvent())
                .map(EventCard.class::cast)
                .toList();

        // 1. Solve events leaving food_event(s) last.
        events.stream().filter(e -> e.getCardType() != CardType.FOOD_EVENT).toList().forEach(
                event -> event.computeScore(orderedPlayer)
        );
        events.stream().filter(e -> e.getCardType() == CardType.FOOD_EVENT).toList().forEach(
                event -> event.computeScore(orderedPlayer)
        );

        // 2. 3. 4. Reorganize cards.
        lowerRow = new ArrayList<>(upperRow);
        upperRow.clear();
        for (int i = 0; i < numPlayers + 4; i++) {
            try {
                TribesCard c = tribesDeck.Draw();
                if (c.getEra() != currentEra) nextEra();
                upperRow.add(c);
            }
            // If the deck is empty, end the game. N.B. This is how we decided to handle game ending.
            catch (IllegalStateException e) {
                if (e.getMessage().equals("No more cards left in the deck.")) {
                    endGame();
                }
            }
        }

        // remove player from buildingType2 offering card
        building2OfferingCard.setPlayer(null);

        //notifyPlayerStack(orderedPlayer);
        //notifyTribesCards(upperRow, lowerRow);
        notifyEndTurn(orderedPlayer, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow);
    }

    /**
     * Ends the game.
     */
    private void endGame() {
        this.currentEra = -1; //put era to -1 to signal game has ended
        // Solve events
        // Get all events from both rows. N.B. we solve the food events from BOTH rows at the end.
        List<EventCard> events = Stream.concat(lowerRow.stream(), upperRow.stream())
                .filter(card -> card.getCardType().isEvent())
                .map(EventCard.class::cast)
                .toList();

        // Solve events leaving food_event(s) last.
        events.stream().filter(e -> e.getCardType() != CardType.FOOD_EVENT).toList().forEach(
                event -> event.computeScore(orderedPlayer)
        );
        events.stream().filter(e -> e.getCardType() == CardType.FOOD_EVENT).toList().forEach(
                event -> event.computeScore(orderedPlayer)
        );

        for (Player player : orderedPlayer) {
            player.calculateFinalPoints();
        }

        notifyEra(currentEra);
        notifyPlayerStack(orderedPlayer);
    }

    /**
     * Validates card selection for the player action "pickTribeCards".
     *
     * @param numToSelectFromUpper Number of cards that must be selected (if possible!) from the upper row.
     * @param numToSelectFromLower Number of cards that must be selected (if possible!) from the lower row.
     * @param characterCards       Selected character cards.
     * @param buildingCards        Selected building cards.
     */
    private void validateCardChoice(int numToSelectFromUpper, int numToSelectFromLower, List<CharacterCard> characterCards,
                                    List<BuildingCard> buildingCards) throws Exception {

        Exception e = MoveValidator.validateCardChoice(numToSelectFromUpper, numToSelectFromLower,
                characterCards, buildingCards, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow);
        if(e != null)
            throw e;

    }

    /**
     * check if the values are plausible and set player to offering card
     * notify observer
     *
     * @param player       player that chose the offering card
     * @param offeringCard offering card picked
     */
    public void selectOfferingCard(Player player, OfferingCard offeringCard) {

        //check if is player turn
        if (!player.equals(orderedPlayer.peek())) {
            throw new IllegalStateException("It is not the player's turn.");
        }

        //check card is not null
        if (offeringCard == null) {
            throw new IllegalStateException("No offering card selected");
        }

        //check if offeringCard is valid
        if (!offeringCards.contains(offeringCard) && !offeringCard.equals(building2OfferingCard)) {
            throw new IllegalStateException("Illegal card selection. (Card not in any offering)");
        }

        //search for offering card index in list
        int index = offeringCards.indexOf(offeringCard);
        OfferingCard selectedOc = offeringCards.get(index);

        // check if offering card is free
        if(selectedOc.getPlayer() != null) {
            throw new IllegalStateException("Illegal card selection. (Card already selected)");
        }

        //set player to offeringCard
        selectedOc.setPlayer(orderedPlayer.peek());

        orderedPlayer.pop();
        if(orderedPlayer.isEmpty()){
            //order player stack for next turn
            orderedPlayer.addAll(offeringCards.stream()
                .filter(card -> card.getPlayer() != null)
                .sorted(Comparator.comparing(OfferingCard::getOrderLetter).reversed())
                .map(OfferingCard::getPlayer)
                .collect(Collectors.toCollection(Stack::new)));
        }

        notifyPlayerStack(orderedPlayer);
        notifyPlayerSelectOfferingCard(player, offeringCards.get(index));
    }

    /**
     * Emulates a player action (picking cards).
     * @param player            the player that has picked the cards
     * @param characterCards TODO: check null
     * @param buildingCards  TODO: check null
     */
    public void pickTribeCards(Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws Exception {
        // Get leftmost occupied offering card.
        OfferingCard currentOffering = getNextOccupiedOfferingCard();

        // if a player has selected the offering card with letter A
        if (currentOffering.getOrderLetter()=='A') {
            handleOfferingCardWithLetterA(currentOffering);
            // recalculate next occupied offering card
            currentOffering = getNextOccupiedOfferingCard();
        }

        // Check if the player is current next player
        if (!player.equals(currentOffering.getPlayer())) {
            throw new IllegalStateException("It is not the player's turn.");
        }
        player = currentOffering.getPlayer();
        // check if cards selection is legal based on the offeringCard
        int numUpper = currentOffering.getNumCardsUpper();
        int numLower = currentOffering.getNumCardsLower();
        validateCardChoice(numUpper, numLower, characterCards, buildingCards);

        // selection legal: obtain cards
        try {
            player.addCards(characterCards, buildingCards);
        } catch (IllegalStateException e) {
            if (e.getMessage().equals("Not enough food to buy building cards")) {
                throw new IllegalStateException("Not enough food to buy building cards");
            } else {
                throw new IllegalStateException("unknown error");
            }
        }
        upperBuildingRow.removeAll(buildingCards); // if not present, no worries
        lowerBuildingRow.removeAll(buildingCards); // if not present, no worries
        upperRow.removeAll(characterCards); // if not present, no worries
        lowerRow.removeAll(characterCards); // if not present, no worries

        //if the player has the buildingType2 card set it to offering card
        if (building2OfferingCard.getPlayer() == null) {
            addPlayerToBT2OfferingCard(player);
        }

        currentOffering.setPlayer(null);

        OfferingCard nextOfferingCard = getNextOccupiedOfferingCard();

        notifyPlayerSelectTribesCard(player, characterCards, buildingCards);
        //check everybody played his base turn
        if (nextOfferingCard == null) {
            // the round has ended
            endRound();
        }
//        else {
//            // TODO: improve, too much data
//            notifyOfferingCards(offeringCards);
//            notifyTribesCards(upperRow, lowerRow);
//            notifyBuildingCards(upperBuildingRow, lowerBuildingRow);
//        }

    }

    private void handleOfferingCardWithLetterA(OfferingCard offeringCard) {
        // give +3 food to the player
        offeringCard.getPlayer().addFood(3);
        // remove player from offering card
        offeringCard.setPlayer(null);
    }


    /**
     * Checks if the player has the BuildingType2 card and sets it to buildingType2OfferingCard
     *
     * @param player the player to be set
     */
    private void addPlayerToBT2OfferingCard(Player player) {
        //Important: there is a singular buildingType2 per game
        if (player.hasBuilding2()) {
            building2OfferingCard.setPlayer(player);
        }
    }

    public UUID getId() {
        return id;
    }

    public List<Player> getPlayers() {
        return orderedPlayer;
    }

    /// only for testing
    public int getCurrentEra() {
        return currentEra;
    }

    /// only for testing
    public List<TribesCard> getUpperRow() {
        return upperRow;
    }

    /// only for testing
    public List<BuildingCard> getUpperBuildingRow() {
        return upperBuildingRow;
    }

    /// only for testing
    public List<BuildingCard> getLowerBuildingRow() {
        return lowerBuildingRow;
    }

    /// only for testing
    public List<TribesCard> getLowerRow() {
        return lowerRow;
    }

    /// only for testing
    public List<OfferingCard> getOfferingCards() {
        return offeringCards;
    }

    /// only for testing
    public Player getCurrentPlayer() {
        return orderedPlayer.peek();
    }
}

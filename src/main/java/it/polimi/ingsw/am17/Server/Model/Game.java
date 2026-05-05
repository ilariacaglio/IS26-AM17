package it.polimi.ingsw.am17.Server.Model;

import it.polimi.ingsw.am17.Server.Model.Decks.BuildingDeck;
import it.polimi.ingsw.am17.Server.Model.Decks.TribesDeck;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events.EventCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import static it.polimi.ingsw.am17.Server.Utility.CardParser.loadOfferingCards;

import java.util.*;
import java.util.logging.Logger;
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

    private final Logger logger = Logger.getLogger(Game.class.getName());

    public Game(UUID id, int numPlayers) {
        logger.info("Instantiated new game for " + numPlayers + " players with id: " + id);

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
        logger.fine("Checking number of players: " + numPlayers);

        if (numPlayers < 2 || numPlayers > 5) {
            throw new IllegalArgumentException("Wrong number of players");
        }
    }

    private int[] getTurnFoodPoints() {
        logger.fine("Getting turn food points for " + numPlayers + " players.");

        return switch (numPlayers) {
            case 2 -> new int[]{1, -1};
            case 3 -> new int[]{2, 0, -1};
            case 4 -> new int[]{2, 1, 0, -1};
            case 5 -> new int[]{3, 1, 0, 0, -1};
            default -> throw new IllegalStateException("Wrong number of players");
        };
    }

    public boolean isStarted() {
        logger.fine("Checking if game is started.");
        return currentEra > 0;
    }

    public boolean isEnded() {
        return currentEra < 0;
    }

    /**
     * @return leftmost offering card with player in the offering track.
     */
    private OfferingCard getNextOccupiedOfferingCard() {
        logger.fine("Getting next occupied offering card.");

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
        logger.info("Adding player " + p.getNickname() + " to game with id " + id);

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
        logger.info("Going to next era.");

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
        logger.fine("Moving down building cards.");

        lowerBuildingRow = new ArrayList<>(upperBuildingRow);
        upperBuildingRow.clear();
    }

    /**
     * Gives the starting food to players
     */
    private void giveFoodToPlayers() {
        logger.fine("Giving starting food to players.");

        int[] startingFood = {2, 3, 3, 4, 4};
        for (int i = 0; i < numPlayers && i < startingFood.length; i++) {
            // stack contains players in reverse order
            // reverse indexes in get
            orderedPlayer.get(numPlayers-1-i).addFood(startingFood[i]);
        }
    }

    /**
     * Starts the game by entering the first era.
     */
    private void era1() {
        logger.info("Starting game (switching to era 1).");

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
        logger.info("Switching to era 2.");

        currentEra = 2;
        moveDownBuildingCards();
        upperBuildingRow = new ArrayList<>(buildingDeck.drawAllEra2());
    }

    /**
     * Sets up the third era.
     */
    private void era3() {
        logger.info("Switching to era 3.");

        currentEra = 3;
        lowerBuildingRow.clear();
        moveDownBuildingCards();
        upperBuildingRow = new ArrayList<>(buildingDeck.drawAllEra3());
    }

    private void turnOrderFoodBonus() {
        logger.fine("Calculating turn order food bonus.");

        // reverse index to match stack
        int i = numPlayers - 1;
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
            i--;
        }
    }

    /**
     * Ends the current round by solving events.
     */
    // this method is public only for testing reasons
    public void endRound() {
        logger.info("Ending round.");

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
        logger.info("Ending game.");

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
    private void validateCardChoice(int numToSelectFromUpper, int numToSelectFromLower, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) {
        logger.fine("Validating card selection.");

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
                throw new IllegalStateException("Illegal character selection. (Card not in any row)");
            }
        }
        for (BuildingCard card : buildingCards) {
            if (upperBuildingRow.contains(card)) {
                numToSelectFromUpper--;
            } else if (lowerBuildingRow.contains(card)) {
                numToSelectFromLower--;
            } else {
                throw new IllegalStateException("Illegal building selection. (Card not in any row)");
            }
        }

        // if the player still has cards to select (counters != 0),
        // AND it is possible to select more cards (i.e. row not empty), the choice is not valid.
        if ((numToSelectFromUpper != 0 && !upperRowCharacterCards.isEmpty()) || (numToSelectFromLower != 0 && !lowerRowCharacterCards.isEmpty())) {
            throw new IllegalStateException("Illegal card selection. (Wrong number of cards)");
        }

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

        logger.info("Player " + player.getNickname() + " wants offering card " + offeringCard.getOrderLetter());
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
    public void pickTribeCards(Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) {
        logger.info("Player " + player.getNickname() + " wants to pick tribe cards " + characterCards + " and " + buildingCards);

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
        logger.fine("Handling offering card with letter A.");

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
        logger.fine("Adding player to buildingType2 offering card.");

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

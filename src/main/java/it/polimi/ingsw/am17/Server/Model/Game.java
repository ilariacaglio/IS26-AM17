package it.polimi.ingsw.am17.Server.Model;

import it.polimi.ingsw.am17.Server.Model.Decks.BuildingDeck;
import it.polimi.ingsw.am17.Server.Model.Decks.TribesDeck;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events.EventCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Utility.DatabaseManager;

import it.polimi.ingsw.am17.Server.Utility.MoveValidator;
import it.polimi.ingsw.am17.Server.Utility.TurnFoodHandler;

import static it.polimi.ingsw.am17.Server.Utility.CardParser.loadOfferingCards;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Game extends Subject {
    private final UUID id;
    private final int numPlayers;
    private int currentEra;

    private final Queue<Player> orderedPlayers;

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
        orderedPlayers = new LinkedList<>();
        offeringCards = loadOfferingCards(numPlayers);

        tribesDeck = new TribesDeck(numPlayers);
        upperRow = new ArrayList<>();
        lowerRow = new ArrayList<>();

        buildingDeck = new BuildingDeck(numPlayers);
        upperBuildingRow = new ArrayList<>();
        lowerBuildingRow = new ArrayList<>();

        turnFoodPoints = TurnFoodHandler.getTurnFoodPoints(numPlayers);
    }

    private void checkNumPlayers(int numPlayers) {
        logger.fine("Checking number of players: " + numPlayers);

        if (numPlayers < 2 || numPlayers > 5) {
            throw new IllegalArgumentException("Wrong number of players");
        }
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
        if (orderedPlayers.stream().anyMatch(player -> player.getNickname().equals(p.getNickname()))) {
            throw new IllegalStateException("The game has already a player with the same nickname.");
        }
        if (orderedPlayers.stream().anyMatch(player -> player.getColor().equals(p.getColor()))) {
            String message = "The game has already a player with the same color. Unused colors: ";
            //Get All colors
            EnumSet<Color> unusedColors = EnumSet.allOf(Color.class);
            //Remove the colors that are currently in use
            orderedPlayers.forEach(player -> unusedColors.remove(player.getColor()));
            message = message.concat(unusedColors.toString());
            throw new IllegalStateException(message);
        }

        orderedPlayers.add(p);
        notifyPlayerQueue(orderedPlayers);

        //if we reached the number of players for the game we start the game
        if (orderedPlayers.size() == numPlayers)
            nextEra();
    }

    /**
     * Removes a player from the game AND ENDS THE GAME (re-join not implemented).
     * TODO: player that closed the game
     */
    public void forceEndGame() {
        logger.severe("Forcibly closed game with id: " + id);
        this.currentEra = -1;
        notifyEndGame();
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
        int i = 0;
        for (Player player : orderedPlayers) {
            player.addFood(startingFood[i]);
            i++;
        }
    }

    private void shuffleQueue() {
        List<Player> players = new ArrayList<>(orderedPlayers);
        Collections.shuffle(players);
        orderedPlayers.clear();
        orderedPlayers.addAll(players);
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
        shuffleQueue();

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

        notifyStartGame(orderedPlayers, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow, offeringCards);
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

        int i = 0;
        for (Player p : orderedPlayers) {
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
        logger.info("Ending round.");

        turnOrderFoodBonus();
        // Get events from the lower row.
        List<EventCard> events = lowerRow.stream()
                .filter(card -> card.getCardType().isEvent())
                .map(EventCard.class::cast)
                .toList();

        // 1. Solve events leaving food_event(s) last.
        events.stream().filter(e -> e.getCardType() != CardType.FOOD_EVENT).toList().forEach(
                event -> event.computeScore(orderedPlayers)
        );
        events.stream().filter(e -> e.getCardType() == CardType.FOOD_EVENT).toList().forEach(
                event -> event.computeScore(orderedPlayers)
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
                    return;
                }
            }
        }

        // remove player from buildingType2 offering card
        building2OfferingCard.setPlayer(null);

        notifyEndTurn(orderedPlayers, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow);
    }

    /**
     * Ends the game.
     */
    private void endGame() {
        logger.info("Ending game.");

        //put era to -1 to signal game has ended
        this.currentEra = -1;
        // Solve events
        // Get all events from both rows. N.B. we solve the food events from BOTH rows at the end.
        List<EventCard> events = Stream.concat(lowerRow.stream(), upperRow.stream())
                .filter(card -> card.getCardType().isEvent())
                .map(EventCard.class::cast)
                .toList();

        // Solve events leaving food_event(s) last.
        events.stream().filter(e -> e.getCardType() != CardType.FOOD_EVENT).toList().forEach(
                event -> event.computeScore(orderedPlayers)
        );
        events.stream().filter(e -> e.getCardType() == CardType.FOOD_EVENT).toList().forEach(
                event -> event.computeScore(orderedPlayers)
        );

        for (Player player : orderedPlayers) {
            player.calculateFinalPoints();
        }

        // insert data into db
        DatabaseManager.insertGameData(this);

        notifyEra(currentEra);
        notifyPlayerQueue(orderedPlayers);
        notifyRanking(DatabaseManager.getRanking(this.numPlayers));
    }

    /**
     * Validates card selection for the player action "pickTribeCards".
     *
     * @param numToSelectFromUpper Number of cards that must be selected (if possible!) from the upper row.
     * @param numToSelectFromLower Number of cards that must be selected (if possible!) from the lower row.
     * @param characterCards       Selected character cards.
     * @param buildingCards        Selected building cards.
     */
    private void validateCardChoice(int numToSelectFromUpper, int numToSelectFromLower, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws Exception{
        logger.fine("Validating card selection.");

        Exception e = MoveValidator.validateCardChoice(numToSelectFromUpper, numToSelectFromLower,
                characterCards, buildingCards, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow);
        if(e != null)
            throw e;

    }

    /**
     * check if the values are plausible and set player to offering card
     * notify observer
     *
     * @param nickname              nickname of the player
     * @param offeringCardLetter    offering card picked
     */
    public void selectOfferingCard(String nickname, Character offeringCardLetter) {
        // get player from nickname
        Player player = orderedPlayers.stream().filter(p->nickname.equals(p.getNickname()))
                .findFirst().orElseThrow();

        //check if is player turn
        if (!player.equals(orderedPlayers.peek())) {
            throw new IllegalStateException("It is not the player's turn.");
        }

        // get offering card from letter
        OfferingCard selectedOc = offeringCards.stream()
                .filter(c -> offeringCardLetter.equals(c.getOrderLetter()))
                .findFirst().orElse(null);

        //check if offeringCard is valid
        if (selectedOc == null && offeringCardLetter.equals('Z')) {
            selectedOc = building2OfferingCard;
        }
        else {
            throw new IllegalStateException("Illegal card selection. (Card not in any offering)");
        }

        logger.info("Player " + nickname + " wants offering card " + offeringCardLetter);

        // check if offering card is free
        if(selectedOc.getPlayer() != null) {
            throw new IllegalStateException("Illegal card selection. (Card already selected)");
        }

        //set player to offeringCard
        selectedOc.setPlayer(player);

        // dequeue and enqueue the player in last position
        movePlayerInQueue();

        // when all player have an offering card recalculate queue
        if (allPlayersPickedOfferingCards()) {
            recalculatePlayerQueue();
        }

        // notify changes
        notifyPlayerQueue(orderedPlayers);
        notifyPlayerSelectOfferingCard(player, selectedOc);
    }

    /**
     * Replaces player queue with new order from offering cards
     */
    private void recalculatePlayerQueue() {
        // get players from offering cards
        List <Player> playersList = offeringCards.stream()
                .filter(card -> card.getPlayer()!=null)
                .sorted(Comparator.comparing(OfferingCard::getOrderLetter))
                .map(OfferingCard::getPlayer)
                .toList();
        // insert them in queue
        orderedPlayers.clear();
        orderedPlayers.addAll(playersList);
    }

    /**
     * Checks that every player is set into an offering card
     * @return true if they are, false otherwise
     */
    private Boolean allPlayersPickedOfferingCards() {
        return orderedPlayers.stream().allMatch(player ->
                offeringCards.stream().anyMatch(card -> card.getPlayer() != null && card.getPlayer().equals(player))
        );
    }

    /**
     * Emulates a player action (picking cards).
     * @param player            the player that has picked the cards
     * @param characterCards    the character cards picked by the player
     * @param buildingCards     the building cards picked by the player
     */
    public void pickTribeCards(Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards)  {
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
        try {
            validateCardChoice(numUpper, numLower, characterCards, buildingCards);
        }catch (Exception e)
        {
            throw new RuntimeException(e);
        }

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
        movePlayerInQueue();

        OfferingCard nextOfferingCard = getNextOccupiedOfferingCard();

        notifyPlayerSelectTribesCard(player, characterCards, buildingCards);
        //check everybody played his base turn
        if (nextOfferingCard == null) {
            // the round has ended
            endRound();
        }
    }

    /**
     * Adds food to player in the offering card with letter A, then sets player to null
     * @param offeringCard reference to offering card with letter A
     */
    private void handleOfferingCardWithLetterA(OfferingCard offeringCard) {
        logger.fine("Handling offering card with letter A.");

        // give +3 food to the player
        offeringCard.getPlayer().addFood(3);
        // remove player from offering card
        offeringCard.setPlayer(null);
        // move the player to last position in queue
        movePlayerInQueue();
    }

    /**
     * Dequeues and enqueues the player
     */
    private void movePlayerInQueue() {
        // remove player from queue
        Player lastPlayer = orderedPlayers.poll();
        // add player as last element of queue
        orderedPlayers.add(lastPlayer);
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

    public List<Player> getPlayersList() {
        return orderedPlayers.stream().toList();
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
        return orderedPlayers.peek();
    }
}

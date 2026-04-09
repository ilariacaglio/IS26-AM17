package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.polimi.ingsw.am17.Utility.CardParser.loadOfferingCards;

public class Game extends Subject {
    private final int id;
    private final int numPlayers;
    private int currentEra;

    private final List<Player> players;

    private final List<OfferingCard> offeringCards;

    private final TribesDeck tribesDeck;
    private List<TribesCard> upperRow;
    private List<TribesCard> lowerRow;

    private final BuildingDeck buildingDeck;

    private List<BuildingCard> upperBuildingRow;
    private List<BuildingCard> lowerBuildingRow;

    public Game(int id, int numPlayers) {
        this.id = id;
        this.numPlayers = numPlayers;
        currentEra = 0;
        players = new ArrayList<>(numPlayers);
        offeringCards = loadOfferingCards(numPlayers);

        tribesDeck = new TribesDeck(numPlayers);
        upperRow = new ArrayList<>();
        lowerRow = new ArrayList<>();

        buildingDeck = new BuildingDeck(numPlayers);
        upperBuildingRow = new ArrayList<>();
        lowerBuildingRow = new ArrayList<>();
    }

    boolean isStarted() {
        return currentEra != 0;
    }

    /**
     * @return leftmost offering card with player in the offering track.
     */
    private OfferingCard getNextOccupiedOfferingCard() {
        return offeringCards.stream()
                .filter(card -> card.getPlayer() != null)
                .min(Comparator.comparing(OfferingCard::getOrderLetter))
                .orElse(null);
    }

    /**
     * @return leftmost player in the offering track.
     */
    private Player getNextPlayerForTribesSelection() {
        return getNextOccupiedOfferingCard().getPlayer();
    }

    /**
     * @return leftmost offering card with player in the offering track from last round.
     */
    private OfferingCard getNextOccupiedOfferingCardFromPreviousRound() {
        return offeringCards.stream()
                .filter(card -> card.getLastPlayer() != null)
                .min(Comparator.comparing(OfferingCard::getOrderLetter))
                .orElse(null);
    }

    /**
     * @return leftmost player in the offering track from previous round (i.e. upmost player in turn order card).
     */
    private Player getNextPlayerForOfferingCardSelection() {
        return getNextOccupiedOfferingCardFromPreviousRound().getPlayer();
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
        if (players.size() >= numPlayers) {
            throw new IllegalStateException("The game lobby is full (max " + numPlayers + " players).");
        }
        if (players.contains(p)) {
            throw new IllegalArgumentException("This player is already in the lobby.");
        }

        players.add(p);

        //if we reached the number of players for the game we start the game
        if(players.size() == numPlayers)
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
                break;
            case 1:
                era2();
                break;
            case 2:
                era3();
                break;
            default:
                throw new IllegalStateException("Invalid era");
        }

        // TODO: notifyObserver(GameState gameState);
    }

    private void moveDownBuildingCards() {
        lowerBuildingRow = new ArrayList<>(upperBuildingRow);
        upperBuildingRow.clear();
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
        Collections.shuffle(players); // TODO: check if already shuffled by controller

        // Populate the rows
        int targetLowerRowSize = numPlayers + 1;
        int targetUpperRowSize = numPlayers + 4;

        while (lowerRow.size() < targetLowerRowSize) {
            TribesCard drawnCard = tribesDeck.Draw();

            if (drawnCard.getCardType().isCharacter()) {
                lowerRow.add(drawnCard);
            } else if (upperRow.size()< targetUpperRowSize) {
                upperRow.add(drawnCard);
            }
        }

        while (upperRow.size() < targetUpperRowSize) {
            upperRow.add(tribesDeck.Draw());
        }

        upperBuildingRow = new ArrayList<>(buildingDeck.drawAllEra1());
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

    /**
     * Ends the current round by solving events.
     */
    public void endRound() {
        // Get events from the lower row.
        List<EventCard> events = lowerRow.stream()
                .filter(card -> card.getCardType().isEvent())
                .map(EventCard.class::cast)
                .toList();

        // 1. Solve events leaving food_event(s) last.
        events.stream().filter(e -> e.getCardType() != CardType.FOOD_EVENT).toList().forEach(
                event -> event.computeScore(players)
        );
        events.stream().filter(e -> e.getCardType() == CardType.FOOD_EVENT).toList().forEach(
                event -> event.computeScore(players)
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

        // TODO: notifyObserver(GameState gameState);
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
                event -> event.computeScore(players)
        );
        events.stream().filter(e -> e.getCardType() == CardType.FOOD_EVENT).toList().forEach(
                event -> event.computeScore(players)
        );

        for (Player player : players) {
            player.calculateFinalPoints();
        }

        // TODO: notifyObserver(GameState gameState);
    }

    private void validateCardChoice(int numUpper, int numLower, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) {
        List<CharacterCard> upperRowCharacterCards = upperRow.stream()
                .filter(card -> card.getCardType().isCharacter())
                .map(CharacterCard.class::cast)
                .toList();
        List<CharacterCard> lowerRowCharacterCards = upperRow.stream()
                .filter(card -> card.getCardType().isCharacter())
                .map(CharacterCard.class::cast)
                .toList();


        for (CharacterCard card : characterCards) {
            if (upperRowCharacterCards.contains(card)) {
                numUpper--;
            } else if (lowerRowCharacterCards.contains(card)) {
                numLower--;
            } else {
                throw new IllegalStateException("Illegal character selection. (Card not in any row)");
            }
        }
        for (BuildingCard card : buildingCards) {
            if (upperBuildingRow.contains(card)) {
                numUpper--;
            } else if (lowerBuildingRow.contains(card)) {
                numLower--;
            } else {
                throw new IllegalStateException("Illegal building selection. (Card not in any row)");
            }
        }
        if (numUpper != 0 || numLower != 0) {
            throw new IllegalStateException("Illegal card selection. (Wrong number of cards)");
        }

    }

    /**
     * check if the values are plausible and set player to offering card
     * notify observer
     * @param player player that chose the offering card
     * @param offeringCard offering card picked
     */
    public void selectOfferingCard(Player player, OfferingCard offeringCard) {

        // Check if the player is current next player
        if (player != getNextPlayerForOfferingCardSelection()) {
            throw new IllegalStateException("It is not the player's turn.");
        }

        offeringCard.setPlayer(player);
    }

    /**
     * Emulates a player action (picking cards).
     * TODO: FoodBonusFromTurnOrder
     * @param player
     * @param characterCards TODO: check null
     * @param buildingCards TODO: check null
     */
    public void playerAction(Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) {
        // Get leftmost occupied offering card.
        OfferingCard currentOffering = getNextOccupiedOfferingCard();

        // Check if the player is current next player
        if (player != currentOffering.getPlayer()) {
            throw new IllegalStateException("It is not the player's turn.");
        }

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
            }
            else {
                throw new IllegalStateException("unknown error");
            }
        }
        upperBuildingRow.removeAll(buildingCards); // if not present, no worries
        lowerBuildingRow.removeAll(buildingCards); // if not present, no worries
        upperRow.removeAll(characterCards); // if not present, no worries
        lowerRow.removeAll(characterCards); // if not present, no worries

        currentOffering.setPlayer(null);

        OfferingCard nextOfferingCard = getNextOccupiedOfferingCard();

        //check everybody played his base turn
        if(nextOfferingCard == null)
        {
            //if we have a buildingType2 in game do building action

            for(Player p : players)
            {
                //check if a player has buildingType2
                //Important: there is a singular buildingType2 per game

                if(p.hasBuilding2()) {
                    //set nextOfferingCard to BuildingType2 Offering Card
                    nextOfferingCard = new OfferingCard(2, 'Z', 0, 1, 0);
                    nextOfferingCard.setPlayer(p);
                    break;
                }
            }
        }

        //GamseState = new GameState
        //notifyObserver
    }

    /// only for testing
    protected List<Player> getPlayers(){
        return players;
    }
    /// only for testing
    protected int getCurrentEra(){
        return currentEra;
    }
    /// only for testing
    protected List<TribesCard> getUpperRow(){
        return upperRow;
    }
    ///only for testing
    protected List<BuildingCard> getUpperBuildingRow(){
        return upperBuildingRow;
    }
    ///only for testing
    protected List<BuildingCard> getLowerBuildingRow(){
        return lowerBuildingRow;
    }
    ///only for testing
    protected List<TribesCard> getLowerRow(){
        return lowerRow;
    }
    ///only for testing
    protected List<OfferingCard> getOfferingCards(){
        return offeringCards;
    }

    @Override
    public void attach(Observer observer) {

    }


    @Override
    public void detach(Observer observer) {

    }


    @Override
    public void notifyObserver() {

    }
}

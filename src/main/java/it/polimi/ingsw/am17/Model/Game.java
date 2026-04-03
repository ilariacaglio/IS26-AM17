package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.*;

import java.util.*;
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
     * @return leftmost player in the offering track.
     */
    private Player getNextPlayer() {
        OfferingCard nextPlayerOfferingCard = offeringCards.stream()
                .min(Comparator.comparing(OfferingCard::getOrderLetter))
                .filter(card -> card.getPlayer() != null)
                .orElseThrow(() -> new IllegalStateException("No offering cards with players available"));

        Player nextPlayer = nextPlayerOfferingCard.getPlayer();
        nextPlayerOfferingCard.setPlayer(null);
        return nextPlayer;
    }

    /**
     * Adds a player to the game.
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

        notifyObserver(GameState gameState); // TODO
    }

    private void moveDownBuildingCards() {
        lowerBuildingRow = upperBuildingRow;
    }

    /**
     * Starts the game by entering the first era.
     */
    public void era1() {
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
            } else {
                upperRow.add(drawnCard);
            }
        }

        while (upperRow.size() < targetUpperRowSize) {
            upperRow.add(tribesDeck.Draw());
        }

        upperBuildingRow = buildingDeck.drawEra1();
    }

    /**
     * Sets up the second era.
     */
    private void era2() {
        currentEra = 2;
        moveDownBuildingCards();
        upperBuildingRow = buildingDeck.drawEra2();


    }

    /**
     * Sets up the third era.
     */
    private void era3() {
        currentEra = 3;
        lowerBuildingRow.clear();
        moveDownBuildingCards();
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
        lowerRow = upperRow;
        for (int i = 0; i < numPlayers+4; i++) {
            try {
                TribesCard c = tribesDeck.Draw();
                if(c.getEra() != currentEra) nextEra();
                upperRow.add(c);
            }
            // If the deck is empty, end the game. N.B. This is how we decided to handle game ending.
            catch (IllegalStateException e) {
                if (e.getMessage().equals("No more cards left in the deck.")) {
                    endGame();
                }
            }
        }

        notifyObserver(GameState gameState); // TODO
    }

    /**
     * Ends the game.
     * TODO: review
     */
    public void endGame() {

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

        notifyObserver(GameState gameState); // TODO
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

    public List<OfferingCard> getOfferingCards() {
        return this.offeringCards;
    }

    public List<Player> getPlayers() {
        return players;
    }
}

package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.*;

import java.util.*;

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
        upperRow = null;
        lowerRow = null;

        buildingDeck = new BuildingDeck(numPlayers);
        upperBuildingRow = null;
        lowerBuildingRow = null;
    }

    boolean isStarted() {
        return currentEra != 0;
    }

    public Player getNextPlayer() {
        OfferingCard nextPlayerOfferingCard = offeringCards.stream()
                .min(Comparator.comparing(OfferingCard::getOrderLetter))
                .filter(card -> card.getPlayer() != null)
                .orElseThrow(() -> new IllegalStateException("No offering cards with players available"));

        Player nextPlayer = nextPlayerOfferingCard.getPlayer();
        nextPlayerOfferingCard.setPlayer(null);
        return nextPlayer;
    }

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

    public void start() {
        if (isStarted()) {
            throw new IllegalStateException("The game has already started.");
        }

        // Set era and shuffle players
        this.currentEra = 1;
        Collections.shuffle(players);

        // Populate card rows
        for (int i = 0; i < numPlayers+1; i++) {
            lowerRow.add(tribesDeck.Draw());
        }
        for (int i = 0; i < numPlayers+4; i++) {
            upperRow.add(tribesDeck.Draw());
        }
        // TODO: missing logic to remove events from lower row

        upperBuildingRow = buildingDeck.drawEra1();
    }

    private void moveDownBuildingCards() {
        lowerBuildingRow = upperBuildingRow;
    }

    private void era2() {
        currentEra = 2;
        moveDownBuildingCards();
        upperBuildingRow = buildingDeck.drawEra2();

    }

    private void era3() {
        currentEra = 3;
        lowerBuildingRow.clear();
        moveDownBuildingCards();
    }

    private void nextEra() {
        switch (currentEra) {
            case 1:
                era2();
                break;
            case 2:
                era3();
                break;
            default:
                throw new IllegalStateException("Invalid era");
        }
    }

    public void end() {
        for(TribesCard card : upperRow.getCards()) {
            if(!card.getCardType().isCharacter()) {
                ((EventCard)card).computeScore(players);
            }
        }

        for(TribesCard card : lowerRow.getCards()) {
            if(!card.getCardType().isCharacter()) {
                ((EventCard)card).computeScore(players);
            }
        }

        for(Player player : players) {
            //call player to add its point
            player.calculateFinalPoints();
        }
    }

    public void endRound() {
        lowerRow = upperRow;
        for (int i = 0; i < numPlayers+4; i++) {
            TribesCard c = tribesDeck.Draw();
            if(c.getEra() != currentEra) nextEra();
            upperRow.add(c);
        }
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

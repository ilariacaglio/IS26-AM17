package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.*;

import javax.smartcardio.Card;
import java.util.*;

public class Game extends Subject {
    private final int id;
    private final int numPlayers;
    private boolean started;
    private final List<Player> players;
    private int currentEra;
    private List<OfferingCard> offeringCards;
    private GameRow upperRow;
    private GameRow lowerRow;
    private Deck deck;

    private GameRow upperBuildingRow;
    private GameRow lowerBuildingRow;

    private BuildingDeck buildingDeckEra1;
    private BuildingDeck buildingDeckEra2;
    private BuildingDeck buildingDeckEra3;

    private int currentPlayerIndex = 0;
    private char currentTurnLetter = 'a';

    public Game(int numPlayers) {
        Random r = new Random();
        this.id = r.nextInt();
        this.numPlayers = numPlayers;
        lowerBuildingRow = new GameRow();
        upperBuildingRow = new GameRow();
        lowerRow = new GameRow();
        upperRow = new GameRow();
        players = new ArrayList<Player>(numPlayers);
    }


    public int getNumPlayers() {
        return  numPlayers;
    }


    public List<Player> getPlayers() {
        return players;
    }


    public Player getNextPlayer() {
        if(currentPlayerIndex == 0)
        {
            players.sort(Comparator.comparing(p -> p.getOfferingCard().getOrderLetter()));
        }
        if(currentPlayerIndex >= players.size())
        {
            throw new IllegalStateException("current player is higher then number of player");
        }
        return players.get(currentPlayerIndex++);
    }


    public void addPlayer(Player p) {
        if(players.stream().count() < numPlayers && numPlayers > 0){
            players.add(p);
        }
        else {
            throw new IllegalStateException("The game lobby is full. Cannot add more players.");
        }
    }


    public boolean isStarted() {
        return started;
    }


    public void start() {
        this.started = true;
        this.currentEra = 1;
        Collections.shuffle(players);
        offeringCards = null; // va fatto metodo per parsare le carte dal json

        deck = new Deck();
        buildingDeckEra1 = new BuildingDeck(numPlayers);
        buildingDeckEra2 = new BuildingDeck(numPlayers);
        buildingDeckEra3 = new BuildingDeck(numPlayers);

        for (int i = 0; i < numPlayers+1; i++) {
            lowerRow.addCard(deck.Draw());
        }
        for (int i = 0; i < numPlayers+4; i++) {
            upperRow.addCard(deck.Draw());
        }
        var buildingCard = buildingDeckEra1.drawAll();

        for(GameCard card : buildingCard) {
            upperBuildingRow.addCard(card);
        }
    }


    public void end() {
        for(GameCard card : upperRow.getCards()) {
            if(card instanceof EventCard eventCard) {
                eventCard.computeScore(players);
            }
        }

        for(GameCard card : lowerRow.getCards()) {
            if(card instanceof EventCard eventCard) {
                eventCard.computeScore(players);
            }
        }

        for(Player player : players) {
            //call player to add its point
            player.calculateFinalPoints();
        }
    }


    public Player getNextTurn() {
        for(Player player : players) {
            if(player.getOfferingCard().getOrderLetter() == currentTurnLetter) {
                currentTurnLetter++;
                return player;
            }
        }
        throw new IllegalStateException("There is no player with the current turn letter");
    }


    public void endTurn(){
        lowerRow = new GameRow();
        for(GameCard card : upperRow.getCards()) {
            lowerRow.addCard(card);
        }
        upperRow = new GameRow();

        boolean newEra = false;

        for (int i = 0; i < numPlayers+4; i++) {
            GameCard c = deck.Draw();
            if(c.getEra() != currentEra) {
                newEra = true;
                currentEra++;
            }
            upperRow.addCard(deck.Draw());
        }

        if(newEra)
            changeEra();

        currentTurnLetter = 'a';
        currentPlayerIndex = 0;
    }


    public void changeEra(){
        if(currentEra == 3) {
            //remove all card from lowerBuildingRow
            lowerBuildingRow = new GameRow();
        }

        //add buildingCard card in lowerRow
        for (GameCard card : upperBuildingRow.getCards()){
            lowerBuildingRow.addCard(card);
        }

        //remove buildingCard card in upperRow
        upperBuildingRow = new GameRow();

        //add buildingCard card in upperRow
        switch (currentEra){
            case 2:
                for (GameCard card : buildingDeckEra2.drawAll()){
                    upperBuildingRow.addCard(card);
                }
                break;
            case 3:
                for (GameCard card : buildingDeckEra3.drawAll()){
                    upperBuildingRow.addCard(card);
                }
                break;
            default:
                throw new IllegalStateException("We are in a wrong era");
        }
    }


    public void resolveEvent(){
        for(GameCard card : lowerRow.getCards()) {
            if(card instanceof EventCard eventCard) {
                eventCard.computeScore(players);
            }
        }
    }


    public int getId(){
        return id;
    }

    public void removeBuildingCardFromRow(BuildingCard card){
        //check if a row contains the card, if so removes it
        if (upperBuildingRow.getCards().contains(card))
            upperBuildingRow.removeCard(card);
        else if (lowerBuildingRow.getCards().contains(card))
            lowerBuildingRow.removeCard(card);
        else //if no row contains the card throw exception
            throw new IllegalStateException("No card row contains this card");
    }

    public void removeTribeCardFromRow(TribesCard card) {
        //check if a row contains the card, if so removes it
        if (upperBuildingRow.getCards().contains(card))
            upperBuildingRow.removeCard(card);
        else if (lowerBuildingRow.getCards().contains(card))
            lowerBuildingRow.removeCard(card);
        else //if no row contains the card throw exception
            throw new IllegalStateException("No building row contains this card");
    }

    /// only to use for testing
    public int getLowerRowSize() {
        return lowerRow.getCards().size();
    }
    /// only to use for testing
    public int getUpperRowSize() {
        return upperRow.getCards().size();
    }
    /// only to use for testing
    public int getUpperBuildingRowSize() {
        return upperBuildingRow.getCards().size();
    }
    /// only to use for testing
    public char getCurrentTurnLetter() {
        return currentTurnLetter;
    }
    /// only to use for testing
    public int getCurrentEra() {
        return currentEra;
    }
    /// only to use for testing
    public CardRow getUpperRow() {
        return upperRow;
    }
    /// only to use for testing
    public CardRow getLowerRow() {
        return lowerRow;
    }
    /// only to use for testing
    public CardRow getUpperBuildingRow() {
        return upperBuildingRow;
    }
    /// only to use for testing
    public CardRow getLowerBuildingRow() {
        return lowerBuildingRow;
    }
    /// only to use for testing
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
    /// only to use for testing
    public int getPlayersSize() {
        return players.size();
    }
    /// only to use for testing
    public List<Player> getPlayersList(){
        return players;
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

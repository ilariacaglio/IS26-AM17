package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.*;

import java.util.*;

import static it.polimi.ingsw.am17.Utility.CardParser.loadOfferingCards;

public class Game extends Subject {
    private final int id;
    private final int numPlayers;
    private boolean started;
    private final List<Player> players;
    private int currentEra;
    private final List<OfferingCard> offeringCards;
    private final List<Character> offeringCardLetters;
    private TribeGameRow upperRow;
    private TribeGameRow lowerRow;
    private final TribesDeck deck;

    private BuildingGameRow upperBuildingRow;
    private BuildingGameRow lowerBuildingRow;

    private final BuildingDeck buildingDeckEra1;
    private final BuildingDeck buildingDeckEra2;
    private final BuildingDeck buildingDeckEra3;

    private int currentPlayerIndex;
    private int currentTurnLetterIndex;

    public Game(int numPlayers) {
        Random r = new Random();
        this.id = r.nextInt();
        this.numPlayers = numPlayers;
        lowerBuildingRow = new BuildingGameRow();
        upperBuildingRow = new BuildingGameRow();
        lowerRow = new TribeGameRow();
        upperRow = new TribeGameRow();
        players = new ArrayList<Player>(numPlayers);
        deck = new TribesDeck(numPlayers);
        buildingDeckEra1 = new BuildingDeck(numPlayers,1);
        buildingDeckEra2 = new BuildingDeck(numPlayers,2);
        buildingDeckEra3 = new BuildingDeck(numPlayers,3);
        offeringCards = loadOfferingCards(numPlayers);
        offeringCardLetters = new ArrayList<Character>();
        for(OfferingCard c: offeringCards){
            offeringCardLetters.add(c.getOrderLetter());
        }
        currentPlayerIndex= 0;
        currentTurnLetterIndex = 0;
    }

    public int getNumPlayers() {
        return  numPlayers;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Player getNextPlayer() {
        if(currentPlayerIndex == 0) {
            players.sort(Comparator.comparing(p -> p.getOfferingCard().getOrderLetter()));
            // set player offering card to null
            for(Player p: players) {
                p.freeOfferingCard();;
            }
        }
        if(currentPlayerIndex >= players.size()) {
            throw new IllegalStateException("current player is higher then number of player");
        }
        return players.get(currentPlayerIndex++);
    }

    public void addPlayer(Player p) {
        if(numPlayers > 1 && players.size() < numPlayers){
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
        for (int i = 0; i < numPlayers+1; i++) {
            lowerRow.addCard(deck.Draw());
        }
        for (int i = 0; i < numPlayers+4; i++) {
            upperRow.addCard(deck.Draw());
        }
        var buildingCard = buildingDeckEra1.drawAll();

        for(BuildingCard card : buildingCard) {
            upperBuildingRow.addCard(card);
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

    public Player getNextTurn() {
        while(currentTurnLetterIndex < offeringCardLetters.size()){
            for(Player player : players) {
                if(player.getOfferingCard().getOrderLetter() == offeringCardLetters.get(currentTurnLetterIndex)) {
                    currentTurnLetterIndex++;
                    return player;
                }
            }
            currentTurnLetterIndex++;
        }
        throw new IllegalStateException("There is no next player");
    }

    public void endTurn(){
        lowerRow = new TribeGameRow();
        for(TribesCard card : upperRow.getCards()) {
            lowerRow.addCard(card);
        }
        upperRow = new TribeGameRow();

        boolean newEra = false;

        for (int i = 0; i < numPlayers+4; i++) {
            TribesCard c = deck.Draw();
            if(c.getEra() != currentEra) {
                newEra = true;
                currentEra++;
            }
            upperRow.addCard(c);
        }

        if(newEra)
            changeEra();

        currentTurnLetterIndex = 0;
        currentPlayerIndex = 0;
    }

    public void changeEra(){
        if(currentEra == 3) {
            //remove all card from lowerBuildingRow
            lowerBuildingRow = new BuildingGameRow();
        }

        //add buildingCard card in lowerRow
        for (BuildingCard card : upperBuildingRow.getCards()){
            lowerBuildingRow.addCard(card);
        }

        //remove buildingCard card in upperRow
        upperBuildingRow = new BuildingGameRow();

        //add buildingCard card in upperRow
        switch (currentEra){
            case 2:
                for (BuildingCard card : buildingDeckEra2.drawAll()){
                    upperBuildingRow.addCard(card);
                }
                break;
            case 3:
                for (BuildingCard card : buildingDeckEra3.drawAll()){
                    upperBuildingRow.addCard(card);
                }
                break;
            default:
                throw new IllegalStateException("We are in a wrong era");
        }
    }


    public void resolveEvent(){
        for(TribesCard card : lowerRow.getCards()) {
            if(!card.getCardType().isCharacter()) {
                ((EventCard)card).computeScore(players);
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
        if (upperRow.getCards().contains(card))
            upperRow.removeCard(card);
        else if (lowerRow.getCards().contains(card))
            lowerRow.removeCard(card);
        else //if no row contains the card throw exception
            throw new IllegalStateException("No tribe row contains this card");
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
    public int getCurrentTurnLetterIndex() {
        return currentTurnLetterIndex;
    }
    /// only to use for testing
    public int getCurrentEra() {
        return currentEra;
    }
    /// only to use for testing
    public TribeGameRow getUpperRow() {
        return upperRow;
    }
    /// only to use for testing
    public TribeGameRow getLowerRow() {
        return lowerRow;
    }
    /// only to use for testing
    public BuildingGameRow getUpperBuildingRow() {
        return upperBuildingRow;
    }
    /// only to use for testing
    public BuildingGameRow getLowerBuildingRow() {
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
    /// only to use for testing
    public void setCurrentEra(int currentEra) {
        this.currentEra = currentEra;
    }
    /// only to use for testing
    public TribesDeck getDeck(){
        return deck;
    }
    /// only to use for testing
    public BuildingDeck getBuildingDeckEra1(){
        return buildingDeckEra1;
    }
    /// only to use for testing
    public BuildingDeck getBuildingDeckEra2(){
        return buildingDeckEra2;
    }
    /// only to use for testing
    public BuildingDeck getBuildingDeckEra3(){
        return buildingDeckEra3;
    }
    /// only to use for testing
    public void setOfferingCardLetters(List<Character> offeringCardLetters) {
        this.offeringCardLetters.addAll(offeringCardLetters);
    }
    /// only to use for testing
    public List<OfferingCard> getOfferingCards(){
        return Collections.unmodifiableList(offeringCards);
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

package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.Builder;
import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.EventCard;
import it.polimi.ingsw.am17.Model.GameCard.GameCard;

import java.util.*;

public class Game extends Subject {
    private final int id;
    private final int numPlayers;
    private boolean started;
    private List<Player> players;
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
    }


    public int getNumPlayers()
    {
        return  numPlayers;
    }


    public List<Player> getPlayers() {
        return players;
    }


    public Player getNextPlayer()
    {
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


    public boolean isStarted()
    {
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

        for(GameCard card : buildingCard)
        {
            upperBuildingRow.addCard(card);
        }
    }


    public void end() {
        for(GameCard card : upperRow.getCards())
        {
            if(card instanceof EventCard eventCard) {
                eventCard.computeScore(players);
            }
        }

        for(GameCard card : lowerRow.getCards())
        {
            if(card instanceof EventCard eventCard) {
                eventCard.computeScore(players);
            }
        }

        for(Player player : players)
        {
            //call player to add its point
            //player.calcuteFinalPoint();
        }

    }


    public Player getNextTurn() {
        for(Player player : players)
        {
            if(player.getOfferingCard().getOrderLetter() == currentTurnLetter) {
                currentTurnLetter++;
                return player;
            }
        }
        throw new IllegalStateException("There is no player with the current turn letter");
    }


    public void endTurn(){
        lowerRow = new GameRow();
        for(GameCard card : upperRow.getCards())
        {
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
        for(GameCard card : lowerRow.getCards())
        {
            if(card instanceof EventCard eventCard)
            {
                eventCard.computeScore(players);
            }
        }
    }


    public int getId(){
        return id;
    }


    public void removeCardFromRow(GameCard card)
    {
        //check if card is building
        if(! (card instanceof BuildingCard)) {

            //check if a row contains the card, if so removes it
            if (upperRow.getCards().contains(card))
                upperRow.removeCard(card);
            else if (lowerRow.getCards().contains(card))
                lowerRow.removeCard(card);
            else //if no row contains the card throw exception
                throw new IllegalStateException("No card row contains this card");
        } else
        {

            //check if a row contains the card, if so removes it
            if (upperBuildingRow.getCards().contains(card))
                upperBuildingRow.removeCard(card);
            else if (lowerBuildingRow.getCards().contains(card)) {
                lowerBuildingRow.removeCard(card);
            }else //if no row contains the card throw exception
                throw new IllegalStateException("No building row contains this card");
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

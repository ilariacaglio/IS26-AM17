package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.GameCard;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game extends Subject {
    private int id;
    private int numPlayers;
    private boolean started;
    private List<Player> players;
    private int currentEra;
    private List<OfferingCard> offeringCards;
    private GameRow upperRow;
    private GameRow lowerRow;
    private Deck deck;
    private BuildingDeck buildingDeckEra1;
    private BuildingDeck buildingDeckEra2;
    private BuildingDeck buildingDeckEra3;

    private int currentPlayerIndex = 0;
    private char currentTurnLetter = 'a';

    public Game(int numPlayers)
    {
        Random r = new Random();
        this.id = r.nextInt();
        this.numPlayers = numPlayers;
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
        return players.get(currentPlayerIndex++);
    }
    public void addPlayer(Player p)
    {

        if(players.stream().count() < numPlayers && numPlayers > 0){
            p.setColor(Color.values()[(int) players.stream().count()]);
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
    public void start()
    {
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
            upperRow.addCard(card);
        }
    }
    public void end()
    {

    }
    public Player getNextTurn()
    {
        for(Player player : players)
        {
            if(player.getOfferingCard().getOrderLetter() == currentTurnLetter) {
                currentTurnLetter++;
                return player;
            }
        }
        throw new IllegalStateException("There is no player with the current turn letter");
    }
    public void endTurn(){}
    public void changeEra(

    ){}
    public void resolveEvent(){}
    public int getId(){
        return id;
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

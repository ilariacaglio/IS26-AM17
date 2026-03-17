package it.polimi.ingsw.am17.Model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game implements Subject {
    private int id;
    private int numPlayers;
    private boolean started;
    private List<Player> players;
    private int currentEra;
    private List<OfferingCard> offeringCards;

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
        return null;
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

    }
    public void end()
    {

    }
    public void getNextTurn()
    {

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

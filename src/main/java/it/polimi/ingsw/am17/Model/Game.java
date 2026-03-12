package it.polimi.ingsw.am17.Model;

import java.util.List;

public class Game implements Subject {
    private int id;
    private int numPlayers;
    private boolean started;
    private List<Player> players;

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
    {}
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
    public void changeEra(){}
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

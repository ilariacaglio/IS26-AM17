package it.polimi.ingsw.am17.Model;

public abstract class Subject {
    abstract void attach(Observer observer);
    abstract void detach(Observer observer);
    abstract void notifyObserver();
}

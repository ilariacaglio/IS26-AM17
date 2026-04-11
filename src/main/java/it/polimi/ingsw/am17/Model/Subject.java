package it.polimi.ingsw.am17.Model;

public abstract class Subject {
    void attach(Observer observer) {};
    void detach(Observer observer) {};
    abstract void notifyObserver();
}

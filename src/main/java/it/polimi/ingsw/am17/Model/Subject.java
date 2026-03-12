package it.polimi.ingsw.am17.Model;

public interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObserver();
}

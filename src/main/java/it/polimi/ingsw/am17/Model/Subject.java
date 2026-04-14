package it.polimi.ingsw.am17.Model;

import java.util.ArrayList;
import java.util.List;

public abstract class Subject {
    private final List<Observer> observers = new ArrayList<>();

    /**
     * Attach an observer to the subject (start observing).
     * @param observer to be attached
     */
    void attach(Observer observer) {
        observers.add(observer);
    };


    /**
     * Do we need this? TODO
     */
    void detach(Observer observer) {

    };

    abstract void notifyObserver();
}

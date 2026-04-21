package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.TribesCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class Subject {
    private final List<Observer> observers = new ArrayList<>();

    /**
     * Attach an observer to the subject (start observing).
     * @param observer to be attached
     */
    public void attach(Observer observer) {
        observers.add(observer);

    };

    void notifyEra(int era) {
        for (Observer observer : observers) {
            observer.updateEra(era);
        }
    }

    void notifyPlayerStack(Stack<Player> orderedPlayer) {
        for (Observer observer : observers) {
            observer.updatePlayerStack(orderedPlayer);
        }
    }

    void notifyOfferingCards(List<OfferingCard> offeringCards) {
        for (Observer observer : observers) {
            observer.updateOfferingCards(offeringCards);
        }
    }

    void notifyTribesCards(List<TribesCard> upperRow, List<TribesCard> lowerRow) {
        for (Observer observer : observers) {
            observer.updateTribesCards(upperRow, lowerRow);
        }
    }

    void notifyBuildingCards(List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) {
        for (Observer observer : observers) {
            observer.updateBuildingCards(upperBuildingRow, lowerBuildingRow);
        }
    }
}

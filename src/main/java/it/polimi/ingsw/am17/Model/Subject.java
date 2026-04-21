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
        for (Observer observer : new ArrayList<>(observers)) {
            try {
                observer.updateEra(era);
            } catch (Exception e) {
                System.err.println("Client not reachable");
            }
        }
    }

    void notifyPlayerStack(Stack<Player> orderedPlayer) {
        for (Observer observer : new ArrayList<>(observers)) {
            try {
                observer.updatePlayerStack(orderedPlayer);
            } catch (Exception e) {
                System.err.println("Client not reachable");
            }
        }
    }

    void notifyOfferingCards(List<OfferingCard> offeringCards) {
        for (Observer observer : new ArrayList<>(observers)) {
            try {
                observer.updateOfferingCards(offeringCards);
            } catch (Exception e) {
                System.err.println("Client not reachable");
            }
        }
    }

    void notifyTribesCards(List<TribesCard> upperRow, List<TribesCard> lowerRow) {
        for (Observer observer : new ArrayList<>(observers)) {
            try {
                observer.updateTribesCards(upperRow, lowerRow);
            } catch (Exception e) {
                System.err.println("Client not reachable");
            }
        }
    }

    void notifyBuildingCards(List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) {
        for (Observer observer : new ArrayList<>(observers)) {
            try {
                observer.updateBuildingCards(upperBuildingRow, lowerBuildingRow);
            } catch (Exception e) {
                System.err.println("Client not reachable");
            }
        }
    }
}

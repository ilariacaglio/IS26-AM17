package it.polimi.ingsw.am17.Server.Model;

import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.CommonInterfaces.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public abstract class Subject {
    private final List<Observer> observers = new ArrayList<>();

    /**
     * Attach an observer to the subject (start observing).
     * @param observer to be attached
     */
    public void attach(Observer observer) {
        observers.add(observer);
    };

    /**
     * Detach an observer to the subject (stop observing).
     * @param observer to be detached
     */
    public void detach(Observer observer) {
        observers.remove(observer);
    }

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


    void notifyPlayerSelectOfferingCard(Player player, OfferingCard offeringCard){
        for (Observer observer : new ArrayList<>(observers)) {
            try {
                observer.updatePlayerSelectOfferingCard(player, offeringCard);
            } catch (Exception e) {
                System.err.println("Client not reachable");
            }
        }
    }

    void notifyPlayerSelectTribesCard(Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards){
        for (Observer observer : new ArrayList<>(observers)) {
            try {
                observer.updatePlayerSelectTribeCards(player, characterCards, buildingCards);
            } catch (Exception e) {
                System.err.println("Client not reachable");
            }
        }
    }

    void notifyEndTurn(Stack<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                       List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow){
        Stack<Player> newStack = players.stream()
                .map(p -> {
                    Player copy = new Player(p.getNickname(), p.getColor());
                    copy.addFood(p.getFood());
                    copy.addPp(p.getPp());
                    // Since you didn't set the cards, they remain null/empty by default
                    return copy;
                })
                .collect(Collectors.toCollection(Stack::new));
        for (Observer observer : new ArrayList<>(observers)) {
            try {
                observer.updateEndTurn(newStack, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow);
            } catch (Exception e) {
                System.err.println("Client not reachable");
            }
        }
    }

    void notifyStartGame(Stack<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                       List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow, List<OfferingCard> offeringCards){
        for (Observer observer : new ArrayList<>(observers)) {
            try {
                observer.updateStartGame(players, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow, offeringCards);
            } catch (Exception e) {
                System.err.println("Client not reachable");
            }
        }
    }
}

package it.polimi.ingsw.am17.Model;
import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.TribesCard;

import java.rmi.Remote;
import java.util.List;
import java.util.Stack;
import java.rmi.RemoteException;

public interface Observer extends Remote {
   // abstract void update();
   void updateEra(int era) throws RemoteException;

    void updatePlayerStack(Stack<Player> orderedPlayer) throws RemoteException;

    void updateOfferingCards(List<OfferingCard> offeringCards) throws RemoteException;

    void updateTribesCards(List<TribesCard> upperRow, List<TribesCard> lowerRow) throws RemoteException;

    void updateBuildingCards(List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) throws RemoteException;
}

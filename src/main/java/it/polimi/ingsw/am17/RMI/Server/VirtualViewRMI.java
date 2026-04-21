package it.polimi.ingsw.am17.RMI.Server;

import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.TribesCard;
import it.polimi.ingsw.am17.Model.OfferingCard;
import it.polimi.ingsw.am17.Model.Player;
import it.polimi.ingsw.am17.VirtualView;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

public interface VirtualViewRMI extends Remote, VirtualView {
    void updateGameId(UUID gameId) throws RemoteException;
    void updateEra(int era) throws RemoteException;
    void updatePlayerStack(Stack<Player> orderedPlayer) throws RemoteException;
    void updateOfferingCards(List<OfferingCard> offeringCards) throws RemoteException;
    void updateTribesCards(List<TribesCard> upperRow, List<TribesCard> lowerRow) throws RemoteException;
    void updateBuildingCards(List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) throws RemoteException;
}

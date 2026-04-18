package it.polimi.ingsw.am17.RMI.Client;

import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.CharacterCard;
import it.polimi.ingsw.am17.Model.OfferingCard;
import it.polimi.ingsw.am17.Model.Player;
import it.polimi.ingsw.am17.RMI.Server.VirtualViewRMI;
import it.polimi.ingsw.am17.VirtualServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

public interface VirtualServerRMI extends Remote, VirtualServer {
    void connect(VirtualViewRMI client) throws RemoteException;
    // controller methods
    @Override
    void getGamesList() throws RemoteException;
    @Override
    void createGame(Player player, int numPlayers)  throws RemoteException;
    @Override
    void joinGame(UUID gameId, Player player)  throws RemoteException;
    @Override
    void pickOfferingCard(UUID gameId, Player player, OfferingCard card)  throws RemoteException;
    @Override
    void pickTribeCards(UUID gameId, Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws RemoteException;
}

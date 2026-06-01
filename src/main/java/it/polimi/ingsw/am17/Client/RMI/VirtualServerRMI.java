package it.polimi.ingsw.am17.Client.RMI;

import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualServer;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import it.polimi.ingsw.am17.Server.RMI.VirtualViewRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

public interface VirtualServerRMI extends Remote, VirtualServer {
    void connect(VirtualViewRMI client) throws RemoteException;
    void ping(VirtualViewRMI client) throws RemoteException;

    // controller methods
    @Override
    void getGamesList(VirtualView client) throws RemoteException;
    @Override
    void createGame(VirtualView client, Player player, int numPlayers)  throws RemoteException;
    @Override
    void closeGame(VirtualView client)  throws RemoteException;
    @Override
    void joinGame(VirtualView client,UUID gameId, Player player)  throws RemoteException;
    @Override
    void pickOfferingCard(VirtualView client, Character offeringCardLetter)  throws RemoteException;
    @Override
    void pickTribeCards(VirtualView client, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws RemoteException;
}

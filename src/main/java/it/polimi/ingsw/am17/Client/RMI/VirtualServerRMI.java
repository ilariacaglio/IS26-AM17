package it.polimi.ingsw.am17.Client.RMI;

import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualServer;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualClient;
import it.polimi.ingsw.am17.Server.RMI.VirtualClientRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

public interface VirtualServerRMI extends Remote, VirtualServer {
    void connect(VirtualClientRMI client) throws RemoteException;
    void ping(VirtualClientRMI client) throws RemoteException;

    // controller methods
    @Override
    void getGamesList(VirtualClient client) throws RemoteException;
    @Override
    void createGame(VirtualClient client, Player player, int numPlayers)  throws RemoteException;
    @Override
    void closeGame(VirtualClient client)  throws RemoteException;
    @Override
    void joinGame(VirtualClient client, UUID gameId, Player player)  throws RemoteException;
    @Override
    void pickOfferingCard(VirtualClient client, Character offeringCardLetter)  throws RemoteException;
    @Override
    void pickTribeCards(VirtualClient client, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws RemoteException;
}

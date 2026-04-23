package it.polimi.ingsw.am17.Client.RMI;

import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualServer;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

public interface VirtualServerRMI extends Remote, VirtualServer {
    void connect(VirtualView client) throws RemoteException;
    // controller methods

    void getGamesList(VirtualView client) throws RemoteException;
    @Override
    void createGame(VirtualView client, Player player, int numPlayers)  throws RemoteException;
    @Override
    void joinGame(VirtualView client,UUID gameId, Player player)  throws RemoteException;
    @Override
    void pickOfferingCard(UUID gameId, Player player, OfferingCard card)  throws RemoteException;
    @Override
    void pickTribeCards(UUID gameId, Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws RemoteException;
}

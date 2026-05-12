package it.polimi.ingsw.am17.Client.RMI;

import it.polimi.ingsw.am17.Client.ClientInterface;
import it.polimi.ingsw.am17.Client.ClientUpdateMethods;
import it.polimi.ingsw.am17.Client.UserInterface.CLI;
import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Client.UserInterface.UI;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Server.RMI.VirtualViewRMI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ClientRMI extends UnicastRemoteObject implements VirtualViewRMI, ClientInterface {
    private VirtualServerRMI server;
    private ClientModel model;
    private UI userInterface;

    public ClientRMI() throws RemoteException {
        super();
    }

    public void start(String ip, boolean graphic) throws RemoteException, NotBoundException {
        final String serverName = "MesosRMIServer";

        Registry registry = LocateRegistry.getRegistry(ip, 1099);
        this.server = (VirtualServerRMI) registry.lookup(serverName);
        this.model = new ClientModel();
        if(graphic){
            // TODO: gui
        }
        else {
            userInterface=new CLI(server,this, model);
        }
        this.server.connect(this);
        userInterface.start();
    }

    @Override
    public void updateEra(int era) throws RemoteException {
        ClientUpdateMethods.updateEra(model,userInterface,era);
    }

    @Override
    public void updatePlayerQueue(Queue<Player> orderedPlayer) throws RemoteException {
        ClientUpdateMethods.updatePlayerQueue(model,userInterface,orderedPlayer);
    }

    @Override
    public void ping() {

    }

    @Override
    public void updateGameId(UUID gameId) throws RemoteException {
        ClientUpdateMethods.updateGameId(model,userInterface,gameId);
    }

    @Override
    public void updateGamesIdList(List<UUID> gamesIdList) throws RemoteException {
        ClientUpdateMethods.updateGamesIdList(model,userInterface,gamesIdList);
    }

    @Override
    public void updateStartGame(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                                List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow, List<OfferingCard> offeringCards) throws RemoteException {
        ClientUpdateMethods.updateStartGame(model,userInterface,players,upperRow,lowerRow,upperBuildingRow,lowerBuildingRow,offeringCards);
    }

    @Override
    public void notifyPlayerDisconnection(String nickname) {
        ClientUpdateMethods.updatePlayerDisconnection(model, userInterface, nickname);
    }

    @Override
    public void updateEndTurn(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow, List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) throws RemoteException {
        ClientUpdateMethods.updateEndTurn(model, userInterface,players,upperRow,lowerRow,upperBuildingRow,lowerBuildingRow);
    }

    @Override
    public void updatePlayerSelectOfferingCard(Player player, OfferingCard offeringCard) throws RemoteException {
        ClientUpdateMethods.updatePlayerSelectOfferingCard(model,userInterface,player,offeringCard);
    }

    @Override
    public void updatePlayerSelectTribeCards(Player player, List<CharacterCard> tribesCards, List<BuildingCard> buildingCards) throws RemoteException {
        ClientUpdateMethods.updatePlayerSelectTribeCards(model,userInterface,player,tribesCards,buildingCards);
    }
}
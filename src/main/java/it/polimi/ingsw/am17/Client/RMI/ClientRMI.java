package it.polimi.ingsw.am17.Client.RMI;

import it.polimi.ingsw.am17.Client.Client;
import it.polimi.ingsw.am17.Client.UserInterface.CLI;
import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Client.UserInterface.UI;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Server.RMI.VirtualViewRMI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

public class ClientRMI extends UnicastRemoteObject implements VirtualViewRMI, Client {
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
        // call model to update era
        model.setCurrentEra(era);
        // UI communication
        // spostare nella CLI
        if(era == 1)
        {
            System.out.println("è iniziata la partita");
        }else
        {
            System.out.println("è iniziata la era successiva");
        }
    }

    @Override
    public void updatePlayerStack(Stack<Player> orderedPlayer) throws RemoteException {
        model.setOrderedPlayers(orderedPlayer);
        // UI communication
        userInterface.drawInterface(model);
    }

    @Override
    public void updateOfferingCards(List<OfferingCard> offeringCards) throws RemoteException {
        model.setOfferingCards(offeringCards);
        userInterface.drawInterface(model);
    }

    @Override
    public void updateTribesCards(List<TribesCard> upperRow, List<TribesCard> lowerRow) throws RemoteException {
        model.setTribeCards(upperRow, lowerRow);
        userInterface.drawInterface(model);
    }

    @Override
    public void updateBuildingCards(List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) throws RemoteException {
       model.setBuildingCards(upperBuildingRow, lowerBuildingRow);
       userInterface.drawInterface(model);
    }

    @Override
    public void updateGameId(UUID gameId) throws RemoteException {
        model.setGameId(gameId);
        // UI communication
        userInterface.printGameId(gameId);
    }

    @Override
    public void updateGamesIdList(List<UUID> gamesIdList) throws RemoteException {
        model.setGameIdList(gamesIdList);
        // TODO: call user interface
    }
}
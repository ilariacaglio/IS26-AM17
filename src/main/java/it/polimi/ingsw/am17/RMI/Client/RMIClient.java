package it.polimi.ingsw.am17.RMI.Client;

import it.polimi.ingsw.am17.Client.CLI;
import it.polimi.ingsw.am17.Client.GameClient;
import it.polimi.ingsw.am17.Client.UI;
import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.TribesCard;
import it.polimi.ingsw.am17.Model.OfferingCard;
import it.polimi.ingsw.am17.Model.Player;
import it.polimi.ingsw.am17.RMI.Server.VirtualViewRMI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

public class RMIClient extends UnicastRemoteObject implements VirtualViewRMI{
    private VirtualServerRMI server;
    private GameClient model;
    private UI userInterface;

    public RMIClient() throws RemoteException {
        super();
    }

    public void start(String ip, boolean graphic) throws RemoteException, NotBoundException {
        final String serverName = "MesosRMIServer";

        Registry registry = LocateRegistry.getRegistry(ip, 1099);
        this.server = (VirtualServerRMI) registry.lookup(serverName);
        this.model = new GameClient();
        //model.registerObserver(view);
        if(graphic){
            // TODO: gui
        }
        else {
            userInterface=new CLI(server,this, model);
        }

        run();
    }

    private void run() throws RemoteException {
        this.server.connect(this);
        userInterface.start();
    }

    @Override
    public void updateEra(int era) throws RemoteException {
        // call model to update era
        model.currentEra = era;
        // UI communication
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
        model.orderedPlayer = orderedPlayer;
        // UI communication
        userInterface.drawInterface(model);
    }

    @Override
    public void updateOfferingCards(List<OfferingCard> offeringCards) throws RemoteException {
        model.offeringCards = offeringCards;
        userInterface.drawInterface(model);
    }

    @Override
    public void updateTribesCards(List<TribesCard> upperRow, List<TribesCard> lowerRow) throws RemoteException {
        model.upperRow = upperRow;
        model.lowerRow = lowerRow;
        userInterface.drawInterface(model);
    }

    @Override
    public void updateBuildingCards(List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) throws RemoteException {
        model.upperBuildingRow = upperBuildingRow;
        model.lowerBuildingRow = lowerBuildingRow;

        userInterface.drawInterface(model);
    }

    @Override
    public void updateGameId(UUID gameId) throws RemoteException {
        model.id = gameId;
        // UI communication
        userInterface.printGameId(gameId);
    }

    @Override
    public void updateGamesIdList(List<UUID> gamesIdList) throws RemoteException {

    }
}
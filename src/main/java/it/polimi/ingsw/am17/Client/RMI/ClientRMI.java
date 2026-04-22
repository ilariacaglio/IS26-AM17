package it.polimi.ingsw.am17.Client.RMI;

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
import java.util.List;
import java.util.Stack;
import java.util.UUID;

public class ClientRMI extends UnicastRemoteObject implements VirtualViewRMI{
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

        run();
    }

    private void run() throws RemoteException {
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

    @Override
    public void updateStartGame(Stack<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                                List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow,  List<OfferingCard> offeringCards) throws RemoteException {
        model.orderedPlayer = players;
        model.upperRow = upperRow;
        model.lowerRow = lowerRow;
        model.upperBuildingRow = upperBuildingRow;
        model.lowerBuildingRow = lowerBuildingRow;
        model.offeringCards = offeringCards;

        userInterface.drawInterface(model);
    }

    @Override
    public void updateEndTurn(Stack<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow, List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) throws RemoteException {
        for(Player player : players)
        {
            model.orderedPlayer.stream()
                    .filter(p -> p.equals(player))
                    .findFirst()
                    .ifPresent(p -> updatePlayerValue(p, player));
        }
        model.upperBuildingRow = upperBuildingRow;
        model.lowerBuildingRow = lowerBuildingRow;
        model.upperRow = upperRow;
        model.lowerRow = lowerRow;

        userInterface.drawInterface(model);
    }

    private void updatePlayerValue(Player oldP, Player newP)
    {
        oldP.addPp(newP.getPp()- oldP.getPp());
        oldP.addFood(newP.getFood() - oldP.getFood());
    }

    @Override
    public void updatePlayerSelectOfferingCard(Player player, OfferingCard offeringCard) throws RemoteException {
        model.offeringCards.stream()
                .filter(o -> o.equals(offeringCard))
                .findFirst()
                .ifPresent(o -> o.setPlayer(player));

        userInterface.drawInterface(model);
    }

    @Override
    public void updatePlayerSelectTribesCard(Player player, List<CharacterCard> tribesCards, List<BuildingCard> buildingCards) throws RemoteException {
        //set new value for player
        for (int i = 0; i < model.orderedPlayer.size(); i++) {
            if (model.orderedPlayer.get(i).equals(player)) {
                model.orderedPlayer.set(i, player);
                break;
            }
        }

        model.offeringCards.stream()
                .filter(o -> o.getPlayer().equals(player))
                .findFirst()
                .ifPresent(o -> o.setPlayer(null));

        model.upperRow.removeAll(tribesCards);
        model.lowerRow.removeAll(tribesCards);

        model.upperBuildingRow.removeAll(buildingCards);
        model.lowerBuildingRow.removeAll(buildingCards);

        userInterface.drawInterface(model);
    }
}
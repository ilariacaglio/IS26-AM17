package it.polimi.ingsw.am17.Client.RMI;

import it.polimi.ingsw.am17.Client.ClientInterface;
import it.polimi.ingsw.am17.Client.UserInterface.CLI;
import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Client.UserInterface.UI;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Server.RMI.VirtualViewRMI;
import it.polimi.ingsw.am17.Server.Utility.RankingEntry;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientRMI extends UnicastRemoteObject implements VirtualViewRMI, ClientInterface {
    private VirtualServerRMI server;
    private ClientModel model;

    private final static Logger logger = Logger.getLogger(ClientRMI.class.getName());

    public ClientRMI() throws RemoteException {
        super();
    }

    public void start(String ip, boolean graphic) throws RemoteException, NotBoundException {
        final String serverName = "MesosRMIServer";

        Registry registry = LocateRegistry.getRegistry(ip, 1099);
        this.server = (VirtualServerRMI) registry.lookup(serverName);
        // Todo: remove null when gui
        UI userInterface = null;
        if(graphic){
            // TODO: gui
        }
        else {
            userInterface = new CLI(server,this);
        }
        this.server.connect(this);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(pinger(server, executor), 1, 1, java.util.concurrent.TimeUnit.SECONDS);
        this.model = new ClientModel(userInterface);
        userInterface.setModel(model);
        this.model.startInterface();
    }

    private Runnable pinger(VirtualServerRMI server, ScheduledExecutorService executor) {
        return () -> {
            logger.setLevel(Level.FINER);
            logger.fine("Starting heartbeat thread.");

            try {
                server.ping();
                logger.finer("Server pinged");
            } catch (RemoteException e) {
                logger.severe("Server disconnected! " + server);
                executor.shutdown();
                System.exit(1); // TODO review status, ok exiting here?
            }
        };
    }

    @Override
    public void updateEra(int era) throws RemoteException {
        // call model to update era
        model.setCurrentEra(era);
    }

    @Override
    public void updatePlayerQueue(Queue<Player> orderedPlayers) throws RemoteException {
        model.updatePlayerQueue(orderedPlayers);
    }

    @Override
    public void ping() {

    }

    @Override
    public void updateGameId(UUID gameId) throws RemoteException {
        model.setGameId(gameId);
    }

    @Override
    public void updateGamesIdList(List<UUID> gameIdsList) throws RemoteException {
        model.setGameIdList(gameIdsList);
    }

    @Override
    public void updateStartGame(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                                List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow, List<OfferingCard> offeringCards) throws RemoteException {
        model.updateStartGame(players, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow, offeringCards);
    }

    @Override
    public void notifyEndGame() throws RemoteException {
        model.updateGameEndedByUser();
    }

    @Override
    public void updateRanking(List<RankingEntry> ranking) throws RemoteException {
        model.updateRanking(ranking);
    }

    @Override
    public void updateEndTurn(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow, List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) throws RemoteException {
        model.updateEndTurn(players,upperRow,lowerRow,upperBuildingRow,lowerBuildingRow);
    }

    @Override
    public void updatePlayerSelectOfferingCard(Player player, OfferingCard offeringCard) throws RemoteException {
        model.updatePlayerSelectOfferingCard(player,offeringCard);
    }

    @Override
    public void updatePlayerSelectTribeCards(Player player, List<CharacterCard> tribesCards, List<BuildingCard> buildingCards) throws RemoteException {
        model.updatePlayerSelectTribeCards(player,tribesCards,buildingCards);
    }

    @Override
    public void updateNotifyError(Exception e) throws RemoteException {
        model.updateNotifyError(e);
    }
}
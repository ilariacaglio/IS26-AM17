package it.polimi.ingsw.am17.Client.RMI;

import it.polimi.ingsw.am17.Client.UserInterface.CLI;
import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Client.UserInterface.GUI;
import it.polimi.ingsw.am17.Client.UserInterface.UI;
import it.polimi.ingsw.am17.CommonInterfaces.InvalidOperationException;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameState;
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
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Sets up the RMI connection with the server.
 * Receives requests from the server to update the ClientModel.
 */
public class ClientRMI extends UnicastRemoteObject implements VirtualViewRMI {
    private final static Logger logger = Logger.getLogger(ClientRMI.class.getName());

    ScheduledExecutorService heartbeater = Executors.newSingleThreadScheduledExecutor();
    int failedHeartbeats = 0;
    ScheduledExecutorService heartwatcher = Executors.newSingleThreadScheduledExecutor();
    long lastHeartbeatReceived = System.currentTimeMillis();


    private final ClientModel model;

    public ClientRMI(String ip, int port, String serverName, boolean graphic) throws RemoteException, NotBoundException {
        super();  // needed for UnicastRemoteObject

        // Set up the RMI registry
        Registry registry = LocateRegistry.getRegistry(ip, port);
        VirtualServerRMI server = (VirtualServerRMI) registry.lookup(serverName);
        server.connect(this);
        logger.info("RMI Client connected to server " + serverName);

        // launch a thread to ping the server every second
        heartbeater.scheduleAtFixedRate(() -> {
            try {
                logger.finer("Pinging server");
                server.ping();
                failedHeartbeats = 0;
            } catch (RemoteException e) {
                logger.warning("Failed sending heartbeat to server: " + serverName + " with error: " + e.getMessage());
                failedHeartbeats++;
                if (failedHeartbeats > 3) {
                    logger.severe("Too many failed heartbeats, server considered dead.");
                    onServerDisconnection();
                }
            }
        }, 1, 1, java.util.concurrent.TimeUnit.SECONDS);

        // create a heartbeat watcher
        heartwatcher.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            long diff = now - lastHeartbeatReceived;

            if (diff > 5000) {
                logger.severe("No heartbeat received in " + diff + "ms, server considered dead.");
                onServerDisconnection();
            }
        }, 10, 5, TimeUnit.SECONDS);

        UI userInterface;
        if(graphic){
            userInterface = new GUI(server, this);
        }
        else {
            userInterface = new CLI(server,this);
        }
        this.model = new ClientModel(userInterface);
        userInterface.setModel(model);
        this.model.startInterface(); // N.B. not threaded?
    }

    /**
     * Manages a server disconnection by stopping the heartbeat thread and exiting the program.
     */
    private void onServerDisconnection() {
        logger.severe("Server disconnected, shutting down.");
        heartbeater.shutdown();
        heartwatcher.shutdown();
//        model.updateGameEndedByUser(); // TODO: improve communication to UI of disconnection.
        System.exit(1);
    }

    /**
     * Forwarded to the model.
     * @throws RemoteException remotely called!
     */
    @Override
    public void updateGameState(GameState gameState) throws RemoteException {
        // call model to update era
        model.setGameState(gameState);
    }

    /**
     * Allows a server to ping the client.
     * @throws RemoteException remotely called!
     */
    @Override
    public void ping() throws RemoteException {
        logger.finer("Received ping");
        lastHeartbeatReceived = System.currentTimeMillis();
    }

    /**
     * Forwarded to the model.
     * @throws RemoteException remotely called!
     */
    @Override
    public void updatePlayerQueue(Queue<Player> orderedPlayers) throws RemoteException {
        model.updatePlayerQueue(orderedPlayers);
    }

    /**
     * Forwarded to the model.
     * @throws RemoteException remotely called!
     */
    @Override
    public void updateGameId(UUID gameId) throws RemoteException {
        model.setGameId(gameId);
    }

    /**
     * Forwarded to the model.
     * @throws RemoteException remotely called!
     */
    @Override
    public void updateGamesIdList(List<UUID> gameIdsList) throws RemoteException {
        model.setGameIdList(gameIdsList);
    }

    /**
     * Forwarded to the model.
     * @throws RemoteException remotely called!
     */
    @Override
    public void updateStartGame(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                                List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow, List<OfferingCard> offeringCards) throws RemoteException {
        model.updateStartGame(players, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow, offeringCards);
    }

    /**
     * Forwarded to the model.
     * @throws RemoteException remotely called!
     */
    @Override
    public void updateEndTurn(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow, List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) throws RemoteException {
        model.updateEndTurn(players,upperRow,lowerRow,upperBuildingRow,lowerBuildingRow);
    }

    /**
     * Forwarded to the model.
     * @throws RemoteException remotely called!
     */
    @Override
    public void updatePlayerSelectOfferingCard(Player player, OfferingCard offeringCard) throws RemoteException {
        model.updatePlayerSelectOfferingCard(player,offeringCard);
    }

    /**
     * Forwarded to the model.
     * @throws RemoteException remotely called!
     */
    @Override
    public void updatePlayerSelectTribeCards(Player player, List<CharacterCard> tribesCards, List<BuildingCard> buildingCards) throws RemoteException {
        model.updatePlayerSelectTribeCards(player,tribesCards,buildingCards);
    }

    @Override
    public void notifyEndGame(String disconnectedPlayer, List<RankingEntry> ranking, Queue<Player> orderedPlayers) throws RemoteException {
        model.updateEndGame(disconnectedPlayer, ranking,orderedPlayers);
    }

    /**
     * Forwarded to the model
     * @throws RemoteException  remotely called!
     */
    @Override
    public void updateError(InvalidOperationException exception) throws RemoteException {
        model.updateNotifyError(exception);
    }

}
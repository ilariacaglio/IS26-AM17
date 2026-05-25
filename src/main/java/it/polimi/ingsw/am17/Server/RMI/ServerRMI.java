package it.polimi.ingsw.am17.Server.RMI;

import it.polimi.ingsw.am17.Server.Controller.GamesController;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Client.RMI.VirtualServerRMI;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Sets up and starts an RMI server to handle requests from multiple clients.
 */
public class ServerRMI extends UnicastRemoteObject implements VirtualServerRMI {
    private final Logger logger = Logger.getLogger(ServerRMI.class.getName());

    ScheduledExecutorService heartbeater = Executors.newSingleThreadScheduledExecutor();
    ScheduledExecutorService heartwatcher = Executors.newSingleThreadScheduledExecutor();
    long lastHeartbeatReceived = System.currentTimeMillis();
    final GamesController controller;
    final List<VirtualViewRMI> clients;

    /**
     * Create and start an RMI server.
     * @param controller main controller
     * @param port port to bind the server to
     * @throws RemoteException if the RMI registry cannot be created.
     */
    public ServerRMI(GamesController controller, int port, String serverName) throws RemoteException {
        super(); // needed for UnicastRemoteObject

        this.controller = controller;
        clients = new ArrayList<>();

        // Set up the RMI server
        Registry registry = LocateRegistry.createRegistry(port);
        registry.rebind(serverName, this);
        logger.info("RMI Server started on port " + port + " with name " + serverName);
    }

    /**
     * Adds a new client to the list of connected clients and starts pinging it.
     * @param client client to be added.
     * @throws RemoteException remotely called!
     */
    @Override
    public void connect(VirtualView client) throws RemoteException {
        synchronized (this.clients) {
            this.clients.add((VirtualViewRMI) client);
            logger.info("RMI Client connected " + client.getClass().getSimpleName());
        }

        // TODO: fix RejectedExecutionException on "unclean restart"
        heartbeater.scheduleAtFixedRate(pinger((VirtualViewRMI) client, heartbeater), 1, 1, java.util.concurrent.TimeUnit.SECONDS);

        heartwatcher.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            long diff = now - lastHeartbeatReceived;

            if (diff > 5000) {
                logger.severe("No heartbeat received in " + diff + "ms, client considered dead.");
                clients.remove(client);
                controller.closeGame(client);
                heartbeater.shutdown();
                heartwatcher.shutdown();
            }
        }, 10, 5, TimeUnit.SECONDS);
    }

    /**
     * Pings a client and removes it from the list of connected clients when it fails.
     * @param client client to be pinged.
     * @param heartbeater to stop
     * @return Runnable to pass the executor.
     */
    private Runnable pinger(VirtualViewRMI client, ScheduledExecutorService heartbeater) {
        AtomicInteger failedHeartbeats = new AtomicInteger();

        return () -> {
            logger.fine("Starting heartbeat thread for RMI client");

            try {
                logger.finer("Pinging client " + client.getClass().getSimpleName());
                client.ping();
                failedHeartbeats.set(0);
            } catch (RemoteException e) {
                failedHeartbeats.getAndIncrement();
                logger.info("Failed heartbeat (Count: " + failedHeartbeats + "): " + e.getMessage());
                if (failedHeartbeats.get() > 3) {
                    logger.severe("Too many failed heartbeats, client considered dead.");
                    clients.remove(client);
                    controller.closeGame(client);
                    heartbeater.shutdown();
                    heartwatcher.shutdown();
                }
            }
        };
    }

    /**
     * Forwarded to the controller.
     * @throws RemoteException remotely called!
     */
    @Override
    public void getGamesList(VirtualView client) throws RemoteException {
        controller.getGamesList(client);
    }


    /**
     * Forwarded to the controller.
     * @throws RemoteException remotely called!
     */
    @Override
    public void createGame(VirtualView client, Player player, int numPlayers) throws RemoteException {
        controller.createGame(client, player, numPlayers);
    }

    /**
     * Forwarded to the controller.
     * @throws RemoteException remotely called!
     */
    @Override
    public void closeGame(VirtualView client) throws RemoteException {
        controller.closeGame(client);
    }

    /**
     * Forwarded to the controller.
     * @throws RemoteException remotely called!
     */
    @Override
    public void joinGame(VirtualView client, UUID gameId, Player player) throws RemoteException {
        controller.joinGame(client, gameId, player);
    }

    /**
     * Forwarded to the controller.
     * @throws RemoteException remotely called!
     */
    @Override
    public void pickOfferingCard(VirtualView client, Character offeringCardLetter) throws RemoteException {
        controller.pickOfferingCard(client, offeringCardLetter);
    }

    /**
     * Forwarded to the controller.
     * @throws RemoteException remotely called!
     */
    @Override
    public void pickTribeCards(VirtualView client, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws RemoteException {
        controller.pickTribeCards(client, characterCards, buildingCards);
    }

    /**
     * Allows a client to ping the server.
     * @throws RemoteException remotely called!
     */
    @Override
    public void ping() throws RemoteException {
        logger.finer("Received ping");
        lastHeartbeatReceived = System.currentTimeMillis();
    }
}

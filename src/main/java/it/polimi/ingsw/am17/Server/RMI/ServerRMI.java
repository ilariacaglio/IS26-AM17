package it.polimi.ingsw.am17.Server.RMI;

import it.polimi.ingsw.am17.Server.Controller.ControllerInterface;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Client.RMI.VirtualServerRMI;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualClient;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Sets up and starts an RMI server to handle requests from multiple clients.
 */
public class ServerRMI extends UnicastRemoteObject implements VirtualServerRMI {
    private final Logger logger = Logger.getLogger(ServerRMI.class.getName());

    private final ConcurrentHashMap<VirtualClient, AtomicLong> lastHeartbeats = new ConcurrentHashMap<>();

    final ControllerInterface controller;

    /**
     * Create and start an RMI server.
     * @param controller main controller
     * @param port port to bind the server to
     * @throws RemoteException if the RMI registry cannot be created.
     */
    public ServerRMI(ControllerInterface controller, int port, String serverName) throws RemoteException {
        super(); // needed for UnicastRemoteObject

        this.controller = controller;

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
    public void connect(VirtualClientRMI client) throws RemoteException {
        logger.info("RMI Client connected " + client.getClass().getSimpleName());

        ScheduledExecutorService heartbeater = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService heartwatcher = Executors.newSingleThreadScheduledExecutor();
        lastHeartbeats.put(client, new AtomicLong(System.currentTimeMillis()));

        heartbeater.scheduleAtFixedRate(pinger(client, heartbeater, heartwatcher), 1, 1, java.util.concurrent.TimeUnit.SECONDS);

        heartwatcher.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            long diff = now - lastHeartbeats.get(client).get();
            if (diff > 5000) {
                logger.severe("No heartbeat received in " + diff + "ms, RMI client considered dead.");
                onClientDisconnection(client, heartbeater, heartwatcher);

            }
        }, 10, 5, TimeUnit.SECONDS);
    }

    /**
     * Allows a client to ping the server.
     * @throws RemoteException remotely called!
     */
    @Override
    public void ping(VirtualClientRMI client) throws RemoteException {
        logger.fine("Received ping");
        AtomicLong ts = lastHeartbeats.get(client);
        if (ts != null) ts.set(System.currentTimeMillis());
        else logger.warning("Received ping from client " + client.getClass().getSimpleName() + " but it is not registered. (Resurrection?)");
    }

    /**
     * Pings a client and removes it from the list of connected clients when it fails.
     * @param client client to be pinged.
     * @param heartbeater to stop
     * @return Runnable to pass the executor.
     */
    private Runnable pinger(VirtualClientRMI client, ScheduledExecutorService heartbeater, ScheduledExecutorService heartwatcher) {
        AtomicInteger failedHeartbeats = new AtomicInteger();

        return () -> {
            logger.fine("Starting heartbeat thread for RMI client");

            try {
                logger.finer("Pinging RMI client " + client.getClass().getSimpleName());
                client.ping();
                failedHeartbeats.set(0);
            } catch (RemoteException e) {
                failedHeartbeats.getAndIncrement();
                logger.info("Failed heartbeat (Count: " + failedHeartbeats + "): " + e.getMessage());
                if (failedHeartbeats.get() > 3) {
                    logger.severe("Too many failed heartbeats, RMI client considered dead.");
                    onClientDisconnection(client, heartbeater, heartwatcher);
                }
            }
        };
    }

    private void onClientDisconnection(VirtualClientRMI client, ScheduledExecutorService heartbeater, ScheduledExecutorService heartwatcher) {
        logger.warning("Removing RMI Client" + client.getClass().getSimpleName());
        lastHeartbeats.remove(client);
        controller.closeGame(client);
        heartbeater.shutdown();
        heartwatcher.shutdown();
    }

    /**
     * Forwarded to the controller.
     * @throws RemoteException remotely called!
     */
    @Override
    public void getGamesList(VirtualClient client) throws RemoteException {
        controller.getGamesList(client);
    }

    /**
     * Forwarded to the controller.
     * @throws RemoteException remotely called!
     */
    @Override
    public void createGame(VirtualClient client, Player player, int numPlayers) throws RemoteException {
        controller.createGame(client, player, numPlayers);
    }

    /**
     * Forwarded to the controller.
     * @throws RemoteException remotely called!
     */
    @Override
    public void closeGame(VirtualClient client) throws RemoteException {
        controller.closeGame(client);
    }

    /**
     * Forwarded to the controller.
     * @throws RemoteException remotely called!
     */
    @Override
    public void joinGame(VirtualClient client, UUID gameId, Player player) throws RemoteException {
        controller.joinGame(client, gameId, player);
    }

    /**
     * Forwarded to the controller.
     * @throws RemoteException remotely called!
     */
    @Override
    public void pickOfferingCard(VirtualClient client, Character offeringCardLetter) throws RemoteException {
        controller.pickOfferingCard(client, offeringCardLetter);
    }

    /**
     * Forwarded to the controller.
     * @throws RemoteException remotely called!
     */
    @Override
    public void pickTribeCards(VirtualClient client, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws RemoteException {
        controller.pickTribeCards(client, characterCards, buildingCards);
    }
}

package it.polimi.ingsw.am17.Server.RMI;

import it.polimi.ingsw.am17.Server.Controller.GamesController;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Client.RMI.VirtualServerRMI;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import it.polimi.ingsw.am17.Server.ServerInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

/**
 * Sets up and starts an RMI server to handle requests from multiple clients.
 */
public class ServerRMI extends UnicastRemoteObject implements VirtualServerRMI, ServerInterface {
    private final Logger logger = Logger.getLogger(ServerRMI.class.getName());

    final GamesController controller;
    final List<VirtualViewRMI> clients;

    /**
     * Create and start an RMI server.
     * @param controller main controller
     * @param port port to bind the server to
     * @throws RemoteException if the RMI registry cannot be created.
     */
    public ServerRMI(GamesController controller, int port) throws RemoteException {
        this.controller = controller;
        clients = new ArrayList<>();

        // Set up the RMI server
        final String serverName = "MesosRMIServer";
        Registry registry = LocateRegistry.createRegistry(port);
        registry.rebind(serverName, this);
        logger.info("RMI Server started on port" + port + " with name " + serverName);
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
            logger.info("RMI Client connected" + client.toString());
        }

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(pinger((VirtualViewRMI) client, executor), 1, 1, java.util.concurrent.TimeUnit.SECONDS);
    }

    /**
     * Pings a client and removes it from the list of connected clients when it fails.
     * @param client client to be pinged.
     * @param executor to stop
     * @return Runnable to pass the executor.
     */
    private Runnable pinger(VirtualViewRMI client, ScheduledExecutorService executor) {
        return () -> {
            logger.fine("Starting heartbeat thread for RMI client");

            try {
                client.ping();
                logger.finer("Client pinged");
            } catch (RemoteException e) {
                logger.severe("Client disconnected! " + client);
                clients.remove(client);
                controller.closeGame(client);
                executor.shutdown();
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
    public void ping() throws RemoteException {}
}

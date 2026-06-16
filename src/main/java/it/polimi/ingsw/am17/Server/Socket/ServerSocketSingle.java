package it.polimi.ingsw.am17.Server.Socket;

import it.polimi.ingsw.am17.CommonInterfaces.Message;
import it.polimi.ingsw.am17.CommonInterfaces.MessageType;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualServer;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualClient;
import it.polimi.ingsw.am17.Server.Controller.ControllerInterface;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Handles one socket incoming connection by forwarding requests to the controller.
 */
public class ServerSocketSingle implements Runnable, VirtualServer {
    private static final Logger logger = Logger.getLogger(ServerSocketSingle.class.getName());

    ScheduledExecutorService heartbeater = Executors.newSingleThreadScheduledExecutor();
    ScheduledExecutorService heartwatcher = Executors.newSingleThreadScheduledExecutor();
    long lastHeartbeatReceived = System.currentTimeMillis();
    int failedHeartbeats = 0;

    private final Socket socket;
    private final ControllerInterface controller;
    private final VirtualClient client;
    private final ObjectMapper mapper;

    /**
     * Constructor. Launches threads to detect disconnections.
     * @param socket socket of the new client.
     * @param controller the controller.
     * @param client virtual client to forward inside the requests.
     */
    public ServerSocketSingle(Socket socket, ControllerInterface controller, VirtualClientSocket client) {
        this.socket = socket;
        this.controller = controller;
        this.client = client;
        this.mapper = new ObjectMapper();

        // create a heartbeat thread to ping the new client
        heartbeater.scheduleAtFixedRate(() -> {
            try {
                logger.finer("Sending heartbeat to socket: " + socket.getRemoteSocketAddress());
                new Message(MessageType.HEARTBEAT).send(socket);
                failedHeartbeats = 0;
            } catch (Exception e) {
                logger.severe("Failed sending heartbeat to socket: " + socket.getRemoteSocketAddress() + " with error: " + e.getMessage());
                failedHeartbeats++;
                if (failedHeartbeats > 7) {
                    logger.severe("Too many failed heartbeats, client considered dead.");
                    onClientDisconnection();
                }
            }
        }, 1, 1, TimeUnit.SECONDS);

        // create a heartbeat receiver to detect dead clients
        heartwatcher.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            long diff = now - lastHeartbeatReceived;

            if (diff > 10000) {
                logger.severe("No heartbeat received in " + diff + "ms, client considered dead.");
                onClientDisconnection();
            }
        }, 10, 5, TimeUnit.SECONDS);
    }

    /**
     * Execute runnable code that will handle each client request.
     */
    @Override
    public void run() {

        // Read socket input stream
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {

                // deserialize the message
                Message message = mapper.readValue(line, Message.class);

                // logging
                if (message.getType() != MessageType.HEARTBEAT) logger.info("Received message:" + mapper.writeValueAsString(message));
                else logger.fine("Received heartbeat");

                // handle request
                switch (message.getType()) {
                    case GET_GAMES_LIST -> getGamesList(client);
                    case CREATE_GAME -> createGame(client, message.getPlayer(), message.getNumPlayers());
                    case JOIN_GAME -> joinGame(client, message.getGameId(), message.getPlayer());
                    case CLOSE_GAME -> closeGame(client);
                    case PICK_OFFERING_CARD -> pickOfferingCard(client, message.getOfferingCardLetter());
                    case PICK_TRIBE_CARDS -> pickTribeCards(client, message.getCharacterCards(), message.getBuildingCards());
                    case HEARTBEAT -> recordHeartbeat();
                    default -> System.err.println("Unknown message type: " + message.getType());
                }
            }
        } catch (Exception e) {
            System.err.println("Error handling message: " + e.getMessage());
        } finally {
            onClientDisconnection();
        }
    }

    /**
     * Record time of the last heartbeat received.
     */
    private void recordHeartbeat() {
        lastHeartbeatReceived = System.currentTimeMillis();
        logger.finest("Received heartbeat from server.");
    }

    /**
     * Handle client disconnection. (Closes the socket.)
     */
    private synchronized void onClientDisconnection() {
        heartbeater.shutdownNow();
        heartwatcher.shutdownNow();

        try {
            socket.close();
        } catch (IOException e) {
            logger.severe("Error closing socket: " + e.getMessage());
        }

        try {
            closeGame(client);
        } catch (Exception e) {
            logger.warning("Could not close game for client socket: " + e.getMessage());
        }
    }

    /**
     * Forwarded to the controller.
     */
    @Override
    public void getGamesList(VirtualClient client) {
        controller.getGamesList(client);
    }

    /**
     * Forwarded to the controller.
     */
    @Override
    public void createGame(VirtualClient client, Player player, int numPlayers) {
        controller.createGame(client, player, numPlayers);
    }

    /**
     * Forwarded to the controller.
     */
    @Override
    public void closeGame(VirtualClient client) {
        controller.closeGame(client);
    }

    /**
     * Forwarded to the controller.
     */
    @Override
    public void joinGame(VirtualClient client, UUID gameId, Player player) {
        controller.joinGame(client, gameId, player);
    }

    /**
     * Forwarded to the controller.
     */
    @Override
    public void pickOfferingCard(VirtualClient client, Character offeringCardLetter) {
        controller.pickOfferingCard(client, offeringCardLetter);
    }

    /**
     * Forwarded to the controller.
     */
    @Override
    public void pickTribeCards(VirtualClient client, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws RemoteException {
        controller.pickTribeCards(client, characterCards, buildingCards);
    }
}

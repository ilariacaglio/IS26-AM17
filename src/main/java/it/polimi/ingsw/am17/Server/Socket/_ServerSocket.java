package it.polimi.ingsw.am17.Server.Socket;

import it.polimi.ingsw.am17.CommonInterfaces.Message;
import it.polimi.ingsw.am17.CommonInterfaces.MessageType;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualServer;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import it.polimi.ingsw.am17.Server.Controller.GamesController;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Forwards requests from a single client to the controller.
 */
public class _ServerSocket implements Runnable, VirtualServer {
    private static final Logger logger = Logger.getLogger(_ServerSocket.class.getName());
    long lastHeartbeatReceived = System.currentTimeMillis();

    private final Socket socket;
    private final GamesController controller;
    private final VirtualView client;
    private final ObjectMapper mapper;

    public _ServerSocket(Socket socket, GamesController controller, VirtualView client) {
        this.socket = socket;
        this.controller = controller;
        this.client = client;
        this.mapper = new ObjectMapper();
    }

    @Override
    public void run() {
        // todo comments

        // create a heartbeat receiver
        ScheduledExecutorService heartwatcher = Executors.newSingleThreadScheduledExecutor();
        heartwatcher.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            long diff = now - lastHeartbeatReceived;

            if (diff > 5000) {
                logger.severe("No heartbeat received in " + diff + "ms, client dead.");
                System.exit(1);
            }
        }, 1, 1, TimeUnit.SECONDS);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                Message message = mapper.readValue(line, Message.class);
                logger.setLevel(Level.FINE);
                logger.fine("Parsing message:" + line);
                if(message.getType() != MessageType.HEARTBEAT) logger.info("Received message:" + mapper.writeValueAsString(this));
                else logger.fine("Received heartbeat");
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
            try {
                socket.close();
            } catch (Exception ignored) {}
        }
    }

    private void recordHeartbeat() {
        lastHeartbeatReceived = System.currentTimeMillis();
        logger.finest("Received heartbeat from server.");
    }

    @Override
    public void getGamesList(VirtualView client) {
        controller.getGamesList(client);
    }

    @Override
    public void createGame(VirtualView client, Player player, int numPlayers) {
        controller.createGame(client, player, numPlayers);
    }

    @Override
    public void closeGame(VirtualView client) {
        controller.closeGame(client);
    }

    @Override
    public void joinGame(VirtualView client, UUID gameId, Player player) {
        controller.joinGame(client, gameId, player);
    }

    @Override
    public void pickOfferingCard(VirtualView client, Character offeringCardLetter) {
        controller.pickOfferingCard(client, offeringCardLetter);
    }

    @Override
    public void pickTribeCards(VirtualView client, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws RemoteException {
        controller.pickTribeCards(client, characterCards, buildingCards);
    }
}

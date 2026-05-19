package it.polimi.ingsw.am17.Client.Socket;

import it.polimi.ingsw.am17.Client.ClientInterface;
import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Client.UserInterface.CLI;
import it.polimi.ingsw.am17.Client.UserInterface.UI;
import it.polimi.ingsw.am17.CommonInterfaces.Message;
import it.polimi.ingsw.am17.CommonInterfaces.MessageType;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Server.Utility.RankingEntry;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Sets up the socket connection with the server.
 * Receives requests from the server to update the ClientModel.
 */
public class ClientSocket implements VirtualView, ClientInterface {
    VirtualServerSocket server;
    ClientModel model;
    Socket socket;
    ObjectMapper mapper;
    long lastHeartbeatReceived = System.currentTimeMillis();

    private final Logger logger = Logger.getLogger(ClientSocket.class.getName());

    public ClientSocket() {
        mapper = new ObjectMapper();
    }

    public void start(String host, boolean gui) throws IOException {
        // create the socket
        Socket socket = new Socket(host, 5000);

        // add socket to this class to receive messages
        this.socket = socket;

        // create a VirtualServer to handle sending requests
        server = new VirtualServerSocket(socket);

        // set the logger level
//        logger.setLevel(Level.FINE);

        // TODO: comments
        // handle incoming messages
        new Thread(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    Message message = mapper.readValue(line, Message.class);
                    if(message.getType() != MessageType.HEARTBEAT) logger.info("Received message: " + message.toString());
                    switch (message.getType()) {
                        case UPDATE_GAME_ID -> updateGameId(message.getGameId());
                        case UPDATE_GAMES_ID_LIST -> updateGamesIdList(message.getGamesIdList());
                        case UPDATE_ERA -> updateEra(message.getEra());
                        case UPDATE_PLAYERS_DATA -> updatePlayerQueue(message.getOrderedPlayer());
                        case UPDATE_PLAYER_SELECT_OFFERING_CARD ->
                                updatePlayerSelectOfferingCard(message.getPlayer(), message.getOfferingCard());
                        case UPDATE_PLAYER_SELECT_TRIBE_CARDS -> updatePlayerSelectTribeCards(message.getPlayer(), message.getCharacterCards(), message.getBuildingCards());
                        case UPDATE_END_TURN ->
                                updateEndTurn(message.getOrderedPlayer(), message.getUpperRow(), message.getLowerRow(), message.getUpperBuildingRow(), message.getLowerBuildingRow());
                        case UPDATE_START_GAME ->
                                updateStartGame(message.getOrderedPlayer(), message.getUpperRow(), message.getLowerRow(), message.getUpperBuildingRow(), message.getLowerBuildingRow(), message.getOfferingCards());
                        case UPDATE_RANKING ->  updateRanking(message.getRanking());
                        case END_GAME -> notifyEndGame();
                        case HEARTBEAT -> recordHeartbeat();
                        default -> System.err.println("Unknown message type: " + message.getType());
                    }
                }
            } catch (Exception e) {
                System.err.println("Disconnected from server: " + e.getMessage());
            }
        }).start();

        // create a heartbeat thread
        ScheduledExecutorService heartbeater = Executors.newSingleThreadScheduledExecutor();
        heartbeater.scheduleAtFixedRate((pinger(socket)), 1, 1, TimeUnit.SECONDS);

        // create a heartbeat receiver
        ScheduledExecutorService heartwatcher = Executors.newSingleThreadScheduledExecutor();
        heartwatcher.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            long diff = now - lastHeartbeatReceived;

            if (diff > 5000) {
                logger.severe("No heartbeat received in " + diff + "ms, server considered dead.");
                System.exit(1);
            }
        }, 1, 1, TimeUnit.SECONDS);

        // Todo: remove null when gui
        UI userInterface = null;
        if(gui) {
            // TODO: gui
        } else {
            userInterface = new CLI(server,this);
        }

        model = new ClientModel(userInterface);
        userInterface.setModel(model); // TODO: circular!! Update with granular UI updates
        model.startInterface();  // note: not threaded
    }

    private Runnable pinger(Socket socket) {
        return () -> {
            try {
                logger.finer("Sending heartbeat to socket: " + socket.getRemoteSocketAddress());
                new Message(MessageType.HEARTBEAT).send(socket); // this is not actually handled
            } catch (Exception e) {
                logger.severe("Socket server disconnected! (Failed heartbeat: " + e.getMessage() + ") Was at: " + socket.getRemoteSocketAddress());
                System.exit(1);
            }
        };
    }

    @Override
    public void updateEra(int era) {
        model.setCurrentEra(era);
    }

    @Override
    public void updatePlayerQueue(Queue<Player> orderedPlayers) {
       model.updatePlayerQueue(orderedPlayers);
    }

    @Override
    public void updateGameId(UUID gameId) {
       model.setGameId(gameId);
    }

    @Override
    public void updateGamesIdList(List<UUID> gameIdsList) {
        model.setGameIdList(gameIdsList);
    }

    @Override
    public void updateStartGame(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                                List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow, List<OfferingCard> offeringCards) {
        model.updateStartGame(players, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow, offeringCards);
    }

    @Override
    public void updateRanking(List<RankingEntry> ranking) {
        model.updateRanking(ranking);
    }

    @Override
    public void notifyEndGame() {
        model.updateGameEndedByUser();
    }

    @Override
    public void updateEndTurn(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow, List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) {
        model.updateEndTurn(players, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow);
    }

    @Override
    public void updatePlayerSelectOfferingCard(Player player, OfferingCard offeringCard) {
        model.updatePlayerSelectOfferingCard(player, offeringCard);
    }

    @Override
    public void updatePlayerSelectTribeCards(Player player, List<CharacterCard> tribesCards, List<BuildingCard> buildingCards) {
        model.updatePlayerSelectTribeCards(player, tribesCards, buildingCards);
    }
}

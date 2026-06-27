package it.polimi.ingsw.am17.Client.Socket;

import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Client.ServerAdapter;
import it.polimi.ingsw.am17.Client.UserInterface.CLI;
import it.polimi.ingsw.am17.Client.UserInterface.GUI;
import it.polimi.ingsw.am17.Client.UserInterface.UI;
import it.polimi.ingsw.am17.CommonInterfaces.InvalidOperationException;
import it.polimi.ingsw.am17.CommonInterfaces.Message;
import it.polimi.ingsw.am17.CommonInterfaces.MessageType;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualClient;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.GameState;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Server.Utility.RankingEntry;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Sets up the socket connection with the server.
 * Receives requests from the server to update the ClientModel.
 * Sends requests to the server when the user does an action (e.g. picks cards).
 */
public class ClientSocket implements VirtualClient, ServerAdapter {
    private final Logger logger = Logger.getLogger(ClientSocket.class.getName());

    ScheduledExecutorService heartbeater = Executors.newSingleThreadScheduledExecutor();
    ScheduledExecutorService heartwatcher = Executors.newSingleThreadScheduledExecutor();
    int failedHeartbeats;
    long lastHeartbeatReceived = System.currentTimeMillis();

    VirtualServerSocket server;
    ClientModel model;
    Socket socket;
    ObjectMapper mapper;

    public ClientSocket(String host, int port, boolean gui) throws IOException  {
        mapper = new ObjectMapper();

        // create the socket and add it to this class to receive messages
        this.socket = new Socket(host, port);
        logger.info("Connected to server: " + socket.getRemoteSocketAddress());

        // create a VirtualServer to handle sending requests
        server = new VirtualServerSocket(socket);

        // handle incoming messages in a new thread
        ExecutorService messageReceiver = Executors.newSingleThreadExecutor();
        messageReceiver.execute(() -> {
            // read socket input stream
            try (BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {

                    // deserialize the message
                    Message message = mapper.readValue(line, Message.class);

                    // logging
                    if (message.getType() != MessageType.HEARTBEAT) logger.info("Received message:" + mapper.writeValueAsString(message));
                    else logger.fine("Received heartbeat");

                    // handle request
                    switch (message.getType()) {
                        case UPDATE_GAME_ID -> updateGameId(message.getGameId());
                        case UPDATE_GAMES_ID_LIST -> updateGamesIdList(message.getGamesIdList());
                        case UPDATE_GAME_STATE -> updateGameState(message.getGameState());
                        case UPDATE_PLAYERS_DATA -> updatePlayerQueue(message.getOrderedPlayer());
                        case UPDATE_PLAYER_SELECT_OFFERING_CARD ->
                                updatePlayerSelectOfferingCard(message.getPlayer(), message.getOfferingCard());
                        case UPDATE_PLAYER_SELECT_TRIBE_CARDS -> updatePlayerSelectTribeCards(message.getPlayer(), message.getCharacterCards(), message.getBuildingCards());
                        case UPDATE_END_TURN ->
                                updateEndTurn(message.getOrderedPlayer(), message.getUpperRow(), message.getLowerRow(), message.getUpperBuildingRow(), message.getLowerBuildingRow());
                        case UPDATE_START_GAME ->
                                updateStartGame(message.getOrderedPlayer(), message.getUpperRow(), message.getLowerRow(), message.getUpperBuildingRow(), message.getLowerBuildingRow(), message.getOfferingCards());
                        case END_GAME -> updateEndGame(message.getRanking(), message.getOrderedPlayer());
                        case END_GAME_FORCED -> updateForceEndGame(message.getDisconnectedPlayerNickname());
                        case HEARTBEAT -> recordHeartbeat();
                        case UPDATE_ERROR -> updateError(message.getException());
                        default -> System.err.println("Unknown message type: " + message.getType());
                    }
                }
            } catch (Exception e) {
                System.err.println("Disconnected from server: " + e.getMessage());
            }
        });

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
                new Message(MessageType.HEARTBEAT).send(socket);
                failedHeartbeats = 0;
            } catch (Exception e) {
                logger.warning("Failed sending heartbeat to socket: " + socket.getRemoteSocketAddress() + " with error: " + e.getMessage());
                failedHeartbeats++;
                if (failedHeartbeats > 7) {
                    logger.severe("Too many failed heartbeats, server considered dead.");
                    onServerDisconnection();
                }
            }
        }, 1, 1, TimeUnit.SECONDS);

        // create a heartbeat receiver
        heartwatcher.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            long diff = now - lastHeartbeatReceived;

            if (diff > 10000) {
                logger.severe("No heartbeat received in " + diff + "ms, server considered dead.");
                onServerDisconnection();
            }
        }, 10, 5, TimeUnit.SECONDS);


        UI userInterface;
        if(gui) {
            userInterface = new GUI(this);
        } else {
            userInterface = new CLI(this);
        }

        model = new ClientModel(userInterface);
        userInterface.setModel(model);
        this.model.startInterface();
    }

    private void onServerDisconnection() {
        logger.severe("Server disconnected, shutting down.");
        heartbeater.shutdownNow();
        heartwatcher.shutdownNow();
        model.updateForcedEndGame("Server " + server.getClass() );
        System.exit(1);
    }

    private void recordHeartbeat() {
        lastHeartbeatReceived = System.currentTimeMillis();
        logger.finest("Received heartbeat from server.");
    }

    @Override
    public void updateGameState(GameState gameState) {
        model.updateGameState(gameState);
    }

    @Override
    public void updatePlayerQueue(Queue<Player> orderedPlayers) {
       model.updatePlayerQueue(orderedPlayers);
    }

    @Override
    public void updateGameId(UUID gameId) {
       model.updateGameId(gameId);
    }

    @Override
    public void updateGamesIdList(List<UUID> gameIdsList) {
        model.updateGameIdList(gameIdsList);
    }

    @Override
    public void updateStartGame(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                                List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow, List<OfferingCard> offeringCards) {
        model.updateStartGame(players, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow, offeringCards);
    }

    @Override
    public void updateEndGame(List<RankingEntry> ranking, Queue<Player> orderedPlayers) {
        model.updateEndGame(ranking, orderedPlayers);
    }

    @Override
    public void updateForceEndGame(String disconnectedPlayer) {
        model.updateForcedEndGame("Player " + disconnectedPlayer);
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

    @Override
    public void updateError(InvalidOperationException exception) {
        model.updateError(exception);
    }

    @Override
    public void getGamesList() {
        try {
            server.getGamesList(this);
        } catch (Exception e) {
            System.out.println("Network error: " + e.getMessage());
        }
    }

    @Override
    public void createGame(Player player, int numPlayers) {
        try {
            server.createGame(this, player, numPlayers);
        } catch (Exception e) {
            System.out.println("Network error: " + e.getMessage());
        }
    }

    @Override
    public void closeGame() {
        try {
            server.closeGame(this);
        } catch (Exception e) {
            System.out.println("Network error: " + e.getMessage());
        }
    }

    @Override
    public void joinGame(UUID gameId, Player player) {
        try {
            server.joinGame(this, gameId, player);
        } catch (Exception e) {
            System.out.println("Network error: " + e.getMessage());
        }
    }

    @Override
    public void pickOfferingCard(Character offeringCardLetter) {
        try {
            server.pickOfferingCard(this, offeringCardLetter);
        } catch (Exception e) {
            System.out.println("Network error: " + e.getMessage());
        }
    }

    @Override
    public void pickTribeCards(List<CharacterCard> characterCards, List<BuildingCard> buildingCards) {
        try {
            server.pickTribeCards(this, characterCards, buildingCards);
        } catch (Exception e) {
            System.out.println("Network error: " + e.getMessage());
        }
    }
}

package it.polimi.ingsw.am17.Server.Socket;

import it.polimi.ingsw.am17.CommonInterfaces.Message;
import it.polimi.ingsw.am17.CommonInterfaces.MessageType;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualServer;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import it.polimi.ingsw.am17.Server.Controller.GamesController;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Server.ServerActionMethods;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Forwards requests from a single client to the controller.
 */
public class _ServerSocket implements Runnable, VirtualServer {
    private static final Logger logger = Logger.getLogger(_ServerSocket.class.getName());


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
                    case PICK_OFFERING_CARD -> pickOfferingCard(message.getGameId(), message.getPlayer(), message.getOfferingCard());
                    case PICK_TRIBE_CARDS -> pickTribeCards(message.getGameId(), message.getPlayer(), message.getCharacterCards(), message.getBuildingCards());
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

    @Override
    public void getGamesList(VirtualView client) throws RemoteException {
        ServerActionMethods.getGamesList(this.controller, client);
    }

    @Override
    public void createGame(VirtualView client, Player player, int numPlayers) throws RemoteException {
        ServerActionMethods.createGame(this.controller, client, player, numPlayers);
    }

    @Override
    public void closeGame(VirtualView client, Player player, UUID gameId) throws Exception {
        ServerActionMethods.closeGame(this.controller, client, player, gameId);
    }

    @Override
    public void joinGame(VirtualView client, UUID gameId, Player player) throws RemoteException {
        ServerActionMethods.joinGame(this.controller, client, gameId, player);
    }

    @Override
    public void pickOfferingCard(UUID gameId, Player player, OfferingCard card) throws RemoteException {
        ServerActionMethods.pickOfferingCard(this.controller, gameId, player, card);
    }

    @Override
    public void pickTribeCards(UUID gameId, Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws RemoteException {
        ServerActionMethods.pickTribeCards(this.controller, gameId, player, characterCards, buildingCards);
    }
}

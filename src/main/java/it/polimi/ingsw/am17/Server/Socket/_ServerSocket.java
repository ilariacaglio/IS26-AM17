package it.polimi.ingsw.am17.Server.Socket;

import it.polimi.ingsw.am17.CommonInterfaces.Message;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualServer;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import it.polimi.ingsw.am17.Server.Controller.GamesController;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Server.RMI.ServerRMI;
import it.polimi.ingsw.am17.Server.RMI.VirtualViewRMI;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

/**
 * Forwards requests from a single client to the controller.
 */
public class _ServerSocket implements Runnable, VirtualServer {
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

                switch (message.getType()) {
                    case GET_GAMES_LIST -> getGamesList(client);
                    case CREATE_GAME -> createGame(client, message.getPlayer(), message.getNumPlayers());
                    case JOIN_GAME -> joinGame(client, message.getGameId(), message.getPlayer());
                    case PICK_OFFERING_CARD -> pickOfferingCard(message.getGameId(), message.getPlayer(), message.getCard());
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

    /**
     * @return the games id list to the client (unicast)
     * @throws RemoteException
     */
    @Override
    public void getGamesList(VirtualView client) throws RemoteException {
        new Thread(()->{
            System.err.println("getGamesList request received");
            try {
                ((VirtualViewSocket)client).updateGamesIdList(this.controller.getGamesList());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /**
     * Creates the game with the specified number of players and adds the player to the game
     * @param player
     * @param numPlayers
     * @throws RemoteException
     */
    @Override
    public void createGame(VirtualView client, Player player, int numPlayers) throws RemoteException {
        new Thread(()->{
            System.err.println("createGame request received");
            // game id generation
            UUID id = this.controller.createGame(player, numPlayers);
            // the client signs up as observer for the game
            controller.signUpAsObserver(client, id);
            // send gameId to client
            try {
                ((VirtualViewRMI)client).updateGameId(id);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /**
     * Adds the player to the game with the specified gameId
     * @param gameId
     * @param player
     * @throws RemoteException
     */
    @Override
    public void joinGame(VirtualView client, UUID gameId, Player player) throws RemoteException {
        new Thread(() -> {
            try {
                System.err.println("joinGame request received");
                // Esegue le operazioni di logica
                controller.signUpAsObserver(client, gameId);
                // Notifica il client
                client.updateGameId(gameId);
                this.controller.joinGame(gameId, player);
            } catch (Exception e) {
                System.err.println("Errore durante la joinGame: " + e.getMessage());
                try {
                    // Remove observer
                    controller.removeClientAsObserver(client, gameId);
                } catch (Exception ignored) {}
            }
        }).start();
    }

    /**
     * Picks the player offering card
     * @param gameId
     * @param player
     * @param card
     * @throws RemoteException
     */
    @Override
    public void pickOfferingCard(UUID gameId, Player player, OfferingCard card) throws RemoteException{
        new Thread(()->{
            System.err.println("pickOfferingCard request received");
            this.controller.pickOfferingCard(gameId, player, card);
        }).start();
    }

    /**
     * Picks the player tribe cards
     * @param gameId
     * @param player
     * @param characterCards
     * @param buildingCards
     * @throws RemoteException
     */
    @Override
    public void pickTribeCards(UUID gameId, Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws RemoteException {
        new Thread(()->{System.err.println("pickTribeCards request received");
            this.controller.pickTribeCards(gameId, player, characterCards, buildingCards);
        }).start();
    }
}

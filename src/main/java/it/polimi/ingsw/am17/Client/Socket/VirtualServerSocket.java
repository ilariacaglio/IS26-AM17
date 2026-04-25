package it.polimi.ingsw.am17.Client.Socket;

import it.polimi.ingsw.am17.CommonInterfaces.Message;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualServer;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.CommonInterfaces.MessageType;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.UUID;

/**
 * Forwards requests from the client to the server
 */
public class VirtualServerSocket implements VirtualServer {
    Socket server;
    ObjectMapper mapper;

    public VirtualServerSocket(String host, int port) throws IOException {
        server = new Socket(host, port);
        mapper = new ObjectMapper();
    }

    public void start(VirtualView view) {
        // TODO: comments
        new Thread(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    Message message = mapper.readValue(line, Message.class);
                    handleUpdate(message, view);
                }
            } catch (Exception e) {
                System.err.println("Disconnected from server: " + e.getMessage());
            }
        }).start();
    }

    private void handleUpdate(Message message, VirtualView client) throws Exception {
        switch (message.getType()) {
            case UPDATE_GAME_ID -> client.updateGameId(message.getGameId());
            case UPDATE_GAMES_ID_LIST -> client.updateGamesIdList(message.getGamesIdList());
            case UPDATE_ERA -> client.updateEra(message.getEra());
            case UPDATE_PLAYER_STACK -> client.updatePlayerStack(message.getOrderedPlayer());
            case UPDATE_OFFERING_CARDS -> client.updateOfferingCards(message.getOfferingCards());
            case UPDATE_TRIBES_CARDS -> client.updateTribesCards(message.getUpperRow(), message.getLowerRow());
            case UPDATE_BUILDING_CARDS -> client.updateBuildingCards(message.getUpperBuildingRow(), message.getLowerBuildingRow());
            default -> System.err.println("Unknown message type: " + message.getType());
        }
    }

    @Override
    public void getGamesList(VirtualView client) throws Exception {
        Message message = new Message(MessageType.GET_GAMES_LIST);
        sendMessage(message);
    }

    @Override
    public void createGame(VirtualView client, Player player, int numPlayers) throws Exception {
        Message message = new Message(MessageType.CREATE_GAME);
        message.setPlayer(player);
        message.setNumPlayers(numPlayers);
        sendMessage(message);
    }

    @Override
    public void joinGame(VirtualView client, UUID gameId, Player player) throws Exception {
        Message message = new Message(MessageType.JOIN_GAME);
        message.setGameId(gameId);
        message.setPlayer(player);
        sendMessage(message);
    }

    @Override
    public void pickOfferingCard(UUID gameId, Player player, OfferingCard card) throws Exception {
        Message message = new Message(MessageType.PICK_OFFERING_CARD);
        message.setGameId(gameId);
        message.setPlayer(player);
        message.setCard(card);
        sendMessage(message);
    }

    @Override
    public void pickTribeCards(UUID gameId, Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws Exception {
        Message message = new Message(MessageType.PICK_TRIBE_CARDS);
        message.setGameId(gameId);
        message.setPlayer(player);
        message.setCharacterCards(characterCards);
        message.setBuildingCards(buildingCards);
        sendMessage(message);
    }

    private void sendMessage(Message message) throws Exception {
        String json = mapper.writeValueAsString(message);
        PrintWriter out = new PrintWriter(server.getOutputStream(), true);
        out.println(json);
    }
}

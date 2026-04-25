package it.polimi.ingsw.am17.Server.Socket;

import it.polimi.ingsw.am17.CommonInterfaces.Message;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import it.polimi.ingsw.am17.CommonInterfaces.MessageType;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import tools.jackson.databind.ObjectMapper;

import java.net.Socket;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

/**
 * Forwards requests from the controller to a single client.
 */
public class VirtualViewSocket implements VirtualView {
    private final Socket socket;
    private final ObjectMapper mapper;

    public VirtualViewSocket(Socket socket) {
        this.socket = socket;
        this.mapper = new ObjectMapper();
    }

    @Override
    public void updateGameId(UUID gameId) throws Exception {
        Message message = new Message(MessageType.UPDATE_GAME_ID);
        message.setGameId(gameId);
        sendMessage(message);
    }

    @Override
    public void updateGamesIdList(List<UUID> gamesIdList) throws Exception {
        Message message = new Message(MessageType.UPDATE_GAMES_ID_LIST);
        message.setGamesIdList(gamesIdList);
        sendMessage(message);
    }

    @Override
    public void updateEra(int era) throws Exception {
        Message message = new Message(MessageType.UPDATE_ERA);
        message.setEra(era);
        sendMessage(message);
    }

    @Override
    public void updatePlayerStack(Stack<Player> orderedPlayer) throws Exception {
        Message message = new Message(MessageType.UPDATE_PLAYER_STACK);
        message.setOrderedPlayer(orderedPlayer);
        sendMessage(message);
    }

    @Override
    public void updateOfferingCards(List<OfferingCard> offeringCards) throws Exception {
        Message message = new Message(MessageType.UPDATE_OFFERING_CARDS);
        message.setOfferingCards(offeringCards);
        sendMessage(message);
    }

    @Override
    public void updateTribesCards(List<TribesCard> upperRow, List<TribesCard> lowerRow) throws Exception {
        Message message = new Message(MessageType.UPDATE_TRIBES_CARDS);
        message.setUpperRow(upperRow);
        message.setLowerRow(lowerRow);
        sendMessage(message);
    }

    @Override
    public void updateBuildingCards(List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) throws Exception {
        Message message = new Message(MessageType.UPDATE_BUILDING_CARDS);
        message.setUpperBuildingRow(upperBuildingRow);
        message.setLowerBuildingRow(lowerBuildingRow);
        sendMessage(message);
    }

    private void sendMessage(Message message) throws Exception {
        mapper.writeValue(socket.getOutputStream(), message);
        System.err.println("Sending message:" + mapper.writeValueAsString(message));
    }
}

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

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.UUID;

/**
 * Forwards requests from the client to the server
 */
public class VirtualServerSocket implements VirtualServer {
    Socket socket;
    ObjectMapper mapper;

    public VirtualServerSocket(Socket socket) throws IOException {
        this.socket = socket;
        mapper = new ObjectMapper();
    }

    @Override
    public void getGamesList(VirtualView client) throws Exception {
        Message message = new Message(MessageType.GET_GAMES_LIST);
        message.send(socket);
    }

    @Override
    public void createGame(VirtualView client, Player player, int numPlayers) throws Exception {
        Message message = new Message(MessageType.CREATE_GAME);
        message.setPlayer(player);
        message.setNumPlayers(numPlayers);
        message.send(socket);
    }

    @Override
    public void joinGame(VirtualView client, UUID gameId, Player player) throws Exception {
        Message message = new Message(MessageType.JOIN_GAME);
        message.setGameId(gameId);
        message.setPlayer(player);
        message.send(socket);
    }

    @Override
    public void pickOfferingCard(UUID gameId, Player player, OfferingCard card) throws Exception {
        Message message = new Message(MessageType.PICK_OFFERING_CARD);
        message.setGameId(gameId);
        message.setPlayer(player);
        message.setCard(card);
        message.send(socket);
    }

    @Override
    public void pickTribeCards(UUID gameId, Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws Exception {
        Message message = new Message(MessageType.PICK_TRIBE_CARDS);
        message.setGameId(gameId);
        message.setPlayer(player);
        message.setCharacterCards(characterCards);
        message.setBuildingCards(buildingCards);
        message.send(socket);
    }
    
}

package it.polimi.ingsw.am17.Client.Socket;

import it.polimi.ingsw.am17.CommonInterfaces.VirtualServer;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.UUID;

public class VirtualServerSocket implements VirtualServer {
    Socket server;
    ObjectMapper mapper;

    public VirtualServerSocket(String host, int port) throws IOException {
        server = new Socket(host, port);
        mapper = new ObjectMapper();
    }

    @Override
    public void getGamesList(VirtualView client) throws Exception {
        mapper.writeValue(socket.getOutputStream(), msg);
    }

    @Override
    public void createGame(VirtualView client, Player player, int numPlayers) throws Exception {

    }

    @Override
    public void joinGame(VirtualView client, UUID gameId, Player player) throws Exception {

    }

    @Override
    public void pickOfferingCard(UUID gameId, Player player, OfferingCard card) throws Exception {

    }

    @Override
    public void pickTribeCards(UUID gameId, Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws Exception {

    }
}

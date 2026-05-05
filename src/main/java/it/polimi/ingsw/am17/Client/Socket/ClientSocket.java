package it.polimi.ingsw.am17.Client.Socket;

import it.polimi.ingsw.am17.Client.ClientInterface;
import it.polimi.ingsw.am17.Client.ClientUpdateMethods;
import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Client.UserInterface.CLI;
import it.polimi.ingsw.am17.Client.UserInterface.UI;
import it.polimi.ingsw.am17.CommonInterfaces.Message;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.*;

/**
 * Sets up the socket connection with the server.
 * Receives requests from the server to update the ClientModel.
 */
public class ClientSocket implements VirtualView, ClientInterface {
    VirtualServerSocket server;
    ClientModel model;
    UI userInterface;
    Socket socket;
    ObjectMapper mapper;

    public ClientSocket() {
        this.model = new ClientModel();
        mapper = new ObjectMapper();
    }

    public void start(String host, boolean gui) throws IOException {
        // create the socket
        Socket socket = new Socket(host, 5000);

        // add socket to this class to receive messages
        this.socket = socket;

        // create a VirtualServer to handle sending requests
        server = new VirtualServerSocket(socket);

        // TODO: comments
        // handle incoming messages
        new Thread(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("Received: " + line);
                    Message message = mapper.readValue(line, Message.class);
                    switch (message.getType()) {
                        case UPDATE_GAME_ID -> updateGameId(message.getGameId());
                        case UPDATE_GAMES_ID_LIST -> updateGamesIdList(message.getGamesIdList());
                        case UPDATE_ERA -> updateEra(message.getEra());
                        case UPDATE_PLAYER_STACK -> updatePlayerQueue(message.getOrderedPlayer());
                        case UPDATE_PLAYER_SELECT_OFFERING_CARD ->
                                updatePlayerSelectOfferingCard(message.getPlayer(), message.getOfferingCard());
                        case UPDATE_PLAYER_SELECT_TRIBE_CARDS -> updatePlayerSelectTribeCards(message.getPlayer(), message.getCharacterCards(), message.getBuildingCards());
                        case UPDATE_END_TURN ->
                                updateEndTurn(message.getOrderedPlayer(), message.getUpperRow(), message.getLowerRow(), message.getUpperBuildingRow(), message.getLowerBuildingRow());
                        case UPDATE_START_GAME ->
                                updateStartGame(message.getOrderedPlayer(), message.getUpperRow(), message.getLowerRow(), message.getUpperBuildingRow(), message.getLowerBuildingRow(), message.getOfferingCards());
                        default -> System.err.println("Unknown message type: " + message.getType());
                    }
                }
            } catch (Exception e) {
                System.err.println("Disconnected from server: " + e.getMessage());
            }
        }).start();

        if (gui) {
            // TODO: gui
        } else {
            userInterface = new CLI(server, this, model);
            userInterface.start(); // note: not threaded
        }
    }

    @Override
    public void updateEra(int era) {
        ClientUpdateMethods.updateEra(model, userInterface, era);
    }

    @Override
    public void updatePlayerQueue(Queue<Player> orderedPlayer) {
        ClientUpdateMethods.updatePlayerQueue(model, userInterface, orderedPlayer);
    }

    @Override
    public void updateGameId(UUID gameId) {
        ClientUpdateMethods.updateGameId(model, userInterface, gameId);
    }

    @Override
    public void updateGamesIdList(List<UUID> gamesIdList) {
        ClientUpdateMethods.updateGamesIdList(model, userInterface, gamesIdList);
    }

    @Override
    public void updateStartGame(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                                List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow, List<OfferingCard> offeringCards) {

        ClientUpdateMethods.updateStartGame(model, userInterface, players, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow, offeringCards);
    }

    @Override
    public void updateEndTurn(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow, List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) {
        ClientUpdateMethods.updateEndTurn(model, userInterface, players, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow);
    }

    @Override
    public void updatePlayerSelectOfferingCard(Player player, OfferingCard offeringCard) {
        ClientUpdateMethods.updatePlayerSelectOfferingCard(model, userInterface, player, offeringCard);
    }

    @Override
    public void updatePlayerSelectTribeCards(Player player, List<CharacterCard> tribesCards, List<BuildingCard> buildingCards) {
        ClientUpdateMethods.updatePlayerSelectTribeCards(model, userInterface, player, tribesCards, buildingCards);
    }
}

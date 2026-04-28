package it.polimi.ingsw.am17.Client.Socket;

import it.polimi.ingsw.am17.Client.ClientInterface;
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
import java.util.List;
import java.util.Stack;
import java.util.UUID;

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
                        case UPDATE_PLAYER_STACK -> updatePlayerStack(message.getOrderedPlayer());
                        case UPDATE_PLAYER_SELECT_OFFERING_CARD -> updatePlayerSelectOfferingCard(message.getPlayer(), message.getOfferingCard());
                        case UPDATE_END_TURN -> updateEndTurn(message.getOrderedPlayer(), message.getUpperRow(), message.getLowerRow(), message.getUpperBuildingRow(), message.getLowerBuildingRow());
                        case UPDATE_START_GAME -> updateStartGame(message.getOrderedPlayer(), message.getUpperRow(), message.getLowerRow(), message.getUpperBuildingRow(), message.getLowerBuildingRow(), message.getOfferingCards());
                        default -> System.err.println("Unknown message type: " + message.getType());
                    }
                }
            } catch (Exception e) {
                System.err.println("Disconnected from server: " + e.getMessage());
            }
        }).start();

        if (gui){
            // TODO: gui
        }
        else {
            userInterface=new CLI(server,this, model);
            userInterface.start(); // note: not threaded
        }
    }

    @Override
    public void updateEra(int era) {
        // call model to update era
        model.setCurrentEra(era);
        // UI communication
        // spostare nella CLI
        if(era == 1)
        {
            System.out.println("è iniziata la partita");
        }else
        {
            System.out.println("è iniziata la era successiva");
        }
    }

    @Override
    public void updatePlayerStack(Stack<Player> orderedPlayer) {
        model.setOrderedPlayers(orderedPlayer);
        // UI communication
        userInterface.drawInterface(model);
    }

    @Override
    public void updateGameId(UUID gameId) {
        model.setGameId(gameId);
        // UI communication
        userInterface.printGameId(gameId);
    }

    @Override
    public void updateGamesIdList(List<UUID> gamesIdList) {
        model.setGameIdList(gamesIdList);
        // TODO: call user interface
    }

    @Override
    public void updateStartGame(Stack<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                                List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow,  List<OfferingCard> offeringCards) {

        model.setOrderedPlayers(players);
        model.setTribeCards(upperRow, lowerRow);
        model.setBuildingCards(upperBuildingRow, lowerBuildingRow);
        model.setOfferingCards(offeringCards);

        userInterface.drawInterface(model);
    }

    @Override
    public void updateEndTurn(Stack<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow, List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) {
        for(Player player : players)
        {
            updatePlayerValue(model.getPlayer(player), player);
        }
        model.setBuildingCards(upperBuildingRow, lowerBuildingRow);
        model.setTribeCards(upperRow, lowerRow);

        userInterface.drawInterface(model);
    }

    private void updatePlayerValue(Player oldP, Player newP)
    {
        oldP.addPp(newP.getPp()- oldP.getPp());
        oldP.addFood(newP.getFood() - oldP.getFood());
    }

    @Override
    public void updatePlayerSelectOfferingCard(Player player, OfferingCard offeringCard) {
        model.setPlayerOfferingCard(offeringCard, player);

        userInterface.drawInterface(model);
    }

    @Override
    public void updatePlayerSelectTribeCards(Player player, List<CharacterCard> tribesCards, List<BuildingCard> buildingCards) {
        //set new value for player
        for (int i = 0; i < model.orderedPlayer.size(); i++) {
            if (model.orderedPlayer.get(i).equals(player)) {
                model.orderedPlayer.set(i, player);
                break;
            }
        }

        model.offeringCards.stream()
                .filter(o -> o.getPlayer().equals(player))
                .findFirst()
                .ifPresent(o -> o.setPlayer(null));

        model.upperRow.removeAll(tribesCards);
        model.lowerRow.removeAll(tribesCards);

        model.upperBuildingRow.removeAll(buildingCards);
        model.lowerBuildingRow.removeAll(buildingCards);

        userInterface.drawInterface(model);
    }
}

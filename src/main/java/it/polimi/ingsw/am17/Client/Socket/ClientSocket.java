package it.polimi.ingsw.am17.Client.Socket;

import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Client.UserInterface.CLI;
import it.polimi.ingsw.am17.Client.UserInterface.UI;
import it.polimi.ingsw.am17.CommonInterfaces.Message;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

/**
 * Sets up the socket connection with the server.
 * Receives requests from the server to update the ClientModel.
 */
public class ClientSocket implements VirtualView {
    VirtualServerSocket server;
    ClientModel model;
    UI userInterface;
    Socket socket;
    ObjectMapper mapper;

    public ClientSocket() throws RemoteException {
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
                        case UPDATE_OFFERING_CARDS -> updateOfferingCards(message.getOfferingCards());
                        case UPDATE_TRIBES_CARDS -> updateTribesCards(message.getUpperRow(), message.getLowerRow());
                        case UPDATE_BUILDING_CARDS -> updateBuildingCards(message.getUpperBuildingRow(), message.getLowerBuildingRow());
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
    public void updateEra(int era) throws RemoteException {
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
    public void updatePlayerStack(Stack<Player> orderedPlayer) throws RemoteException {
        model.setOrderedPlayers(orderedPlayer);
        // UI communication
        userInterface.drawInterface(model);
    }

    @Override
    public void updateOfferingCards(List<OfferingCard> offeringCards) throws RemoteException {
        model.setOfferingCards(offeringCards);
        userInterface.drawInterface(model);
    }

    @Override
    public void updateTribesCards(List<TribesCard> upperRow, List<TribesCard> lowerRow) throws RemoteException {
        model.setTribeCards(upperRow, lowerRow);
        userInterface.drawInterface(model);
    }

    @Override
    public void updateBuildingCards(List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) throws RemoteException {
        model.setBuildingCards(upperBuildingRow, lowerBuildingRow);
        userInterface.drawInterface(model);
    }

    @Override
    public void updateGameId(UUID gameId) throws RemoteException {
        model.setGameId(gameId);
        // UI communication
        userInterface.printGameId(gameId);
    }

    @Override
    public void updateGamesIdList(List<UUID> gamesIdList) throws RemoteException {
        model.setGameIdList(gamesIdList);
        // TODO: call user interface
    }
}

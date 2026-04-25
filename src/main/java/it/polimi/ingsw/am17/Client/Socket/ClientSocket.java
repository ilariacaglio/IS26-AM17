package it.polimi.ingsw.am17.Client.Socket;

import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Client.RMI.ClientRMI;
import it.polimi.ingsw.am17.Client.UserInterface.CLI;
import it.polimi.ingsw.am17.Client.UserInterface.UI;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

/**
 * Forwards requests from the server (Game -> VirtualView) to update the ClientModel.
 */
public class ClientSocket implements VirtualView {
    VirtualServerSocket server;
    ClientModel model;
    UI userInterface;

    public ClientSocket() throws RemoteException {
        ClientRMI temp = new ClientRMI(); // TODO: check same implementation
        this.model = new ClientModel();
    }

    public void start(String host, boolean gui) throws IOException {

        // create and start a VirtualServer to handle sending requests
        server = new VirtualServerSocket(host, 5000);
        server.start(this);

        try {
            server.getGamesList(this);
        } catch (Exception e) {
            System.err.println("Error requesting games list: " + e.getMessage());
        }

        if (gui){
            // TODO: gui
        }
        else {
            userInterface=new CLI(server,this, model);
            userInterface.start();
        }

    }

    @Override
    public void updateGameId(UUID gameId) throws Exception {

    }

    @Override
    public void updateGamesIdList(List<UUID> gamesIdList) throws Exception {

    }

    @Override
    public void updateEra(int era) throws Exception {

    }

    @Override
    public void updatePlayerStack(Stack<Player> orderedPlayer) throws Exception {

    }

    @Override
    public void updateOfferingCards(List<OfferingCard> offeringCards) throws Exception {

    }

    @Override
    public void updateTribesCards(List<TribesCard> upperRow, List<TribesCard> lowerRow) throws Exception {

    }

    @Override
    public void updateBuildingCards(List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) throws Exception {

    }
}

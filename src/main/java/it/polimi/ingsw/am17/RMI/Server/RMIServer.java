package it.polimi.ingsw.am17.RMI.Server;

import it.polimi.ingsw.am17.Controller.GameController;
import it.polimi.ingsw.am17.GameState;
import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.CharacterCard;
import it.polimi.ingsw.am17.Model.OfferingCard;
import it.polimi.ingsw.am17.Model.Player;
import it.polimi.ingsw.am17.RMI.Client.VirtualServerRMI;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RMIServer extends UnicastRemoteObject implements VirtualServerRMI {
    final GameController controller;
    final List<VirtualViewRMI> clients = new ArrayList<>();

    public RMIServer() throws RemoteException {
        super();
        controller = new GameController();
    }

    static void main(String[] args) throws RemoteException {
        final String serverName = "MesosRMIServer";
        VirtualServerRMI server = new RMIServer();
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind(serverName, server);
        System.out.println("Server ready");
    }

    @Override
    public void connect(VirtualViewRMI client) throws RemoteException {
        synchronized (this.clients) {
            this.clients.add(client);
        }
    }

    /**
     * @return the games id list to the client (unicast)
     * @throws RemoteException
     */
    @Override
    public List<UUID> getGamesList() throws RemoteException {
        System.err.println("getGamesList request received");
        return this.controller.getGamesList();
    }


    @Override
    public void createGame(Player player, int numPlayers) throws RemoteException {
        System.err.println("createGame request received");
        this.controller.createGame(player, numPlayers);
    }

    @Override
    public void joinGame(UUID gameId, Player player) throws RemoteException {
        System.err.println("joinGame request received");
        this.controller.joinGame(gameId, player);

        GameState currentState = new GameState();
        //TODO: get real state of controller
        //(GameState currentState = this.controller.getGameState(); )

        synchronized (this.clients) {
            for (VirtualViewRMI client : clients) {
                client.showUpdate(currentState);

            }
        }
    }

    @Override
    public void pickOfferingCard(UUID gameId, Player player, OfferingCard card) throws RemoteException{
        System.err.println("pickOfferingCard request received");
        this.controller.pickOfferingCard(gameId, player, card);

        GameState currentState = new GameState();
        //TODO: same as joinGame()
        synchronized (this.clients) {
            for (VirtualViewRMI client : clients) {
                client.showUpdate(currentState);
            }
        }
        }


    @Override
    public void pickTribeCards(UUID gameId, Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws RemoteException {
        System.err.println("pickTribeCards request received");
        this.controller.pickTribeCards(gameId, player, characterCards, buildingCards);

        GameState currentState = new GameState();
        //TODO: same as joinGame()
        synchronized (this.clients) {
            for (VirtualViewRMI client : clients) {
                client.showUpdate(currentState);
            }
        }
    }


}

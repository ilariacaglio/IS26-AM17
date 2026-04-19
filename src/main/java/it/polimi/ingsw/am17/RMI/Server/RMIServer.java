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
    final List<VirtualViewRMI> clients;
    final List<Lobby> games;

    public RMIServer() throws RemoteException {
        super();
        controller = new GameController();
        clients = new ArrayList<>();
        games = new ArrayList<>();
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

    /**
     * Creates the game with the specified number of players and adds the player to the game
     * @param player
     * @param numPlayers
     * @throws RemoteException
     */
    @Override
    public void createGame(VirtualViewRMI client, Player player, int numPlayers) throws RemoteException {
        System.err.println("createGame request received");
        UUID id = this.controller.createGame(player, numPlayers);
        Lobby gameLobby = new Lobby(id);
        gameLobby.addClient(client);
        games.add(gameLobby);
        // TODO: observer??
    }

    /**
     * Adds the player to the game with the specified gameId
     * @param gameId
     * @param player
     * @throws RemoteException
     */
    @Override
    public void joinGame(VirtualViewRMI client, UUID gameId, Player player) throws RemoteException {
        System.err.println("joinGame request received");
        this.controller.joinGame(gameId, player);
        Lobby gameLobby = games.stream().filter(l->l.getGameID().equals(gameId)).findFirst().orElse(null);
        if (gameLobby != null) {
            gameLobby.addClient(client);
        }
        // TODO: call method in lobby for update
        //observer
//        GameState currentState = new GameState();

        //(GameState currentState = this.controller.getGameState(); )

//        synchronized (this.clients) {//TODO: update only clients in lobby
//            for (VirtualViewRMI client : clients) {
//                client.showUpdate(currentState);
//
//            }
//        }
    }

    @Override
    public void pickOfferingCard(UUID gameId, Player player, OfferingCard card) throws RemoteException{
        System.err.println("pickOfferingCard request received");
        this.controller.pickOfferingCard(gameId, player, card);
        //TODO: call method in lobby for update
    }


    @Override
    public void pickTribeCards(UUID gameId, Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws RemoteException {
        System.err.println("pickTribeCards request received");
        this.controller.pickTribeCards(gameId, player, characterCards, buildingCards);
        //TODO: call method in lobby for update
        //observer
//        GameState currentState = new GameState();
        //TODO: same as joinGame()
//        synchronized (this.clients) {
//            for (VirtualViewRMI client : clients) {
//                client.showUpdate(currentState);
//            }
//        }
    }


}

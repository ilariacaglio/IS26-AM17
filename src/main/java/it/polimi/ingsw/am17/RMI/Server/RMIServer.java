package it.polimi.ingsw.am17.RMI.Server;

import it.polimi.ingsw.am17.Controller.GameController;
import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.CharacterCard;
import it.polimi.ingsw.am17.Model.OfferingCard;
import it.polimi.ingsw.am17.Model.Player;
import it.polimi.ingsw.am17.RMI.Client.VirtualServerRMI;
import it.polimi.ingsw.am17.VirtualView;

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

    public RMIServer() throws RemoteException {
        super();
        controller = new GameController();
        clients = new ArrayList<>();
    }

    static void main(String[] args) throws RemoteException {
        final String serverName = "MesosRMIServer";
        VirtualServerRMI server = new RMIServer();
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind(serverName, server);
        System.out.println("Server ready");
    }

    @Override
    public void connect(VirtualView client) throws RemoteException {
        synchronized (this.clients) {
            this.clients.add((VirtualViewRMI) client);
        }
    }

    /**
     * @return the games id list to the client (unicast)
     * @throws RemoteException
     */
    @Override
    public void getGamesList(VirtualView client) throws RemoteException {
        new Thread(()->{
            System.err.println("getGamesList request received");
            try {
                ((VirtualViewRMI)client).updateGamesIdList(this.controller.getGamesList());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /**
     * Creates the game with the specified number of players and adds the player to the game
     * @param player
     * @param numPlayers
     * @throws RemoteException
     */
    @Override
    public void createGame(VirtualView client, Player player, int numPlayers) throws RemoteException {
        new Thread(()->{
            System.err.println("createGame request received");
            // game id generation
            UUID id = this.controller.createGame(player, numPlayers);
            // the client signs up as observer for the game
            controller.signUpAsObserver(client, id);
            // send gameId to client
            try {
                ((VirtualViewRMI)client).updateGameId(id);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /**
     * Adds the player to the game with the specified gameId
     * @param gameId
     * @param player
     * @throws RemoteException
     */
    @Override
    public void joinGame(VirtualView client, UUID gameId, Player player) throws RemoteException {
        new Thread(() -> {
            try {
                System.err.println("joinGame request received");
                // Esegue le operazioni di logica
                controller.signUpAsObserver(client, gameId);
                // Notifica il client
                client.updateGameId(gameId);
                this.controller.joinGame(gameId, player);
            } catch (Exception e) {
                System.err.println("Errore durante la joinGame: " + e.getMessage());
                try {
                    // Remove observer
                    // controller.removeObserver(client, gameId);
                } catch (Exception ignored) {}
            }
        }).start();
    }

    /**
     * Picks the player offering card
     * @param gameId
     * @param player
     * @param card
     * @throws RemoteException
     */
    @Override
    public void pickOfferingCard(UUID gameId, Player player, OfferingCard card) throws RemoteException{
        new Thread(()->{
            System.err.println("pickOfferingCard request received");
            this.controller.pickOfferingCard(gameId, player, card);
        }).start();
    }

    /**
     * Picks the player tribe cards
     * @param gameId
     * @param player
     * @param characterCards
     * @param buildingCards
     * @throws RemoteException
     */
    @Override
    public void pickTribeCards(UUID gameId, Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws RemoteException {
        new Thread(()->{System.err.println("pickTribeCards request received");
            this.controller.pickTribeCards(gameId, player, characterCards, buildingCards);
        }).start();
    }
}

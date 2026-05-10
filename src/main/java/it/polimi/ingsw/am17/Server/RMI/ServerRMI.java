package it.polimi.ingsw.am17.Server.RMI;

import it.polimi.ingsw.am17.Client.Socket.ClientSocket;
import it.polimi.ingsw.am17.Server.Controller.GamesController;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Client.RMI.VirtualServerRMI;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import it.polimi.ingsw.am17.Server.ServerActionMethods;
import it.polimi.ingsw.am17.Server.ServerInterface;
import it.polimi.ingsw.am17.ServerLauncher;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class ServerRMI extends UnicastRemoteObject implements VirtualServerRMI, ServerInterface {
    final GamesController controller;
    final List<VirtualViewRMI> clients;

    private final Logger logger = Logger.getLogger(ServerRMI.class.getName());


    public ServerRMI(GamesController controller) throws RemoteException {
        super();
        this.controller = controller;
        clients = new ArrayList<>();
    }

    public static void start(GamesController controller) throws RemoteException {
        final String serverName = "MesosRMIServer";
        VirtualServerRMI server = new ServerRMI(controller);
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind(serverName, server);
        System.out.println("Server ready");
    }

    @Override
    public void connect(VirtualView client) throws RemoteException {
        synchronized (this.clients) {
            this.clients.add((VirtualViewRMI) client);
            logger.info("RMI Client connected" + client.toString());
        }

        // heartbeat for each client
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                try {
                    ((VirtualViewRMI) client).ping();
                    logger.fine("Client pinged");
                } catch (RemoteException e) {
                    logger.severe("Client unreachable");
                    clients.remove(client);
                    // call for closeGame or similar, passing the dead client.
                    // throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void pinger(VirtualView client) throws RemoteException {

    }

    @Override
    public void getGamesList(VirtualView client) throws RemoteException {
        ServerActionMethods.getGamesList(this.controller, client);
    }

    @Override
    public void createGame(VirtualView client, Player player, int numPlayers) throws RemoteException {
        ServerActionMethods.createGame(this.controller, client, player, numPlayers);
    }

    @Override
    public void joinGame(VirtualView client, UUID gameId, Player player) throws RemoteException {
        ServerActionMethods.joinGame(this.controller, client, gameId, player);
    }

    @Override
    public void pickOfferingCard(UUID gameId, Player player, OfferingCard card) throws RemoteException {
        ServerActionMethods.pickOfferingCard(this.controller, gameId, player, card);
    }

    @Override
    public void pickTribeCards(UUID gameId, Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws RemoteException {
        ServerActionMethods.pickTribeCards(this.controller, gameId, player, characterCards, buildingCards);
    }


}

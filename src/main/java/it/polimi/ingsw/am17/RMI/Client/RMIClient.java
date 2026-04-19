package it.polimi.ingsw.am17.RMI.Client;

import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.TribesCard;
import it.polimi.ingsw.am17.Model.OfferingCard;
import it.polimi.ingsw.am17.Model.Player;
import it.polimi.ingsw.am17.RMI.Server.VirtualViewRMI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

public class RMIClient extends UnicastRemoteObject implements VirtualViewRMI{
    private final VirtualServerRMI server;
//    private final ClientModel model;

    public RMIClient(VirtualServerRMI server /*,ClientModel model*/) throws RemoteException {
        super();
        this.server = server;
        //this.model = model;
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        final String serverName = "MesosRMIServer";

        Registry registry = LocateRegistry.getRegistry(args[0], 1099);
        VirtualServerRMI server = (VirtualServerRMI) registry.lookup(serverName);

        //ClientModel model = new ClientModel();
        //CLIView view = new CLIView();
        //model.registerObserver(view);

        //new RmiClient(server, model).run();
    }

    private void run() throws RemoteException {
        this.server.connect(this);
        this.runCli();
    }

    private void runCli() throws RemoteException {
        // TODO: cli implementation
        /*Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            int command = scan.nextInt();

            if (command == 0) {
                server.reset();
            } else {
                server.add(command);
            }
        }*/
    }

    // TODO
    public void updateEra(int era) {
        // call model to update era
    }

    // TODO
    public void updatePlayerStack(Stack<Player> orderedPlayer) {

    }

    // TODO
    public void updateOfferingCards(List<OfferingCard> offeringCards) {

    }

    // TODO
    public void updateTribesCards(List<TribesCard> upperRow, List<TribesCard> lowerRow) {

    }

    // TODO
    public void updateBuildingCards(List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) {

    }

    // TODO
    @Override
    public void updateGameId(UUID gameId) {

    }

    // TODO: DELETE WHEN SAFE
    @Override
    public void update() {

    }
}
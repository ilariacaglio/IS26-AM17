package it.polimi.ingsw.am17.RMI.Client;

import it.polimi.ingsw.am17.GameState;
import it.polimi.ingsw.am17.RMI.Server.VirtualViewRMI;
import it.polimi.ingsw.am17.VirtualServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class RMIClient extends UnicastRemoteObject implements VirtualViewRMI{
    private final VirtualServerRMI server;
    //TODO: add ClientModel from view
    //private final ClientModel model;

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


    @Override
    public void showUpdate(GameState state) throws RemoteException {

    }

    @Override
    public void reportError(String details) throws RemoteException {

    }

    @Override
    public void showUpdate(List<UUID> idList) throws RemoteException {

    }
}

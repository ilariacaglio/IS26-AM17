package it.polimi.ingsw.am17.Client;

import it.polimi.ingsw.am17.Client.RMI.ClientRMI;
import it.polimi.ingsw.am17.Client.Socket.ClientSocket;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;

/**
 * Launches a client.
 * Use --ip ... TODO
 * Use --debug to raise logging level. TODO
 * Use --socket to use socket communication (instead or RMI).
 * Use --gui to launch a graphical interface.
 */
public class ClientLauncher {

    static void main(String[] args) throws IOException, NotBoundException {
        boolean gui = Arrays.asList(args).contains("--gui");
        boolean socket = Arrays.asList(args).contains("--socket");

        if (socket) {
            new ClientSocket("127.0.0.1", gui);
        } else {
            new ClientRMI("127.0.0.1",1099, "MesosRMIServer", gui);
        }
    }
}

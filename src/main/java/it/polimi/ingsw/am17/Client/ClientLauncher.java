package it.polimi.ingsw.am17.Client;

import it.polimi.ingsw.am17.Client.RMI.ClientRMI;
import it.polimi.ingsw.am17.Client.Socket.ClientSocket;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;

public class ClientLauncher {

    public static void main(String[] args) throws IOException, NotBoundException {
        boolean gui = Arrays.asList(args).contains("--gui");
        boolean socket = Arrays.asList(args).contains("--socket");

        if (socket) {
            new ClientSocket().start("kerbless.me", gui);
        } else {
            new ClientRMI().start("kerbless.me", gui);
        }
    }
}

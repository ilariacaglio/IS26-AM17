package it.polimi.ingsw.am17.Client;

import it.polimi.ingsw.am17.Client.RMI.ClientRMI;
import it.polimi.ingsw.am17.Client.Socket.ClientSocket;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;

public class Start {

    static void main(String[] args) throws RemoteException, NotBoundException {
        boolean gui = Arrays.asList(args).contains("--gui");
        boolean socket = Arrays.asList(args).contains("--socket");

        if (socket) {
            new ClientSocket().start("127.0.0.1", gui);
        } else {
            new ClientRMI().start("127.0.0.1", gui);
        }
    }
}

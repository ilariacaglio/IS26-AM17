package it.polimi.ingsw.am17.Client;

import it.polimi.ingsw.am17.Client.RMI.ClientRMI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Start {
    public static void main() throws RemoteException, NotBoundException {
        // TODO: ask gui or cli
        // TODO: ask rmi or socket
        new ClientRMI().start("127.0.0.1",false);
    }
}

package it.polimi.ingsw.am17.Client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public interface ClientInterface {
    public void start(String ip, boolean graphic) throws RemoteException, NotBoundException;
}

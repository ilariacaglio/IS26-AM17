package it.polimi.ingsw.am17.Server;

import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;

import java.rmi.RemoteException;

public interface Server {
    public void connect(VirtualView client) throws RemoteException;

}

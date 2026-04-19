package it.polimi.ingsw.am17.RMI.Server;

import it.polimi.ingsw.am17.VirtualView;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface VirtualViewRMI extends Remote, VirtualView {
    void updateGameId(UUID gameId) throws RemoteException;
}

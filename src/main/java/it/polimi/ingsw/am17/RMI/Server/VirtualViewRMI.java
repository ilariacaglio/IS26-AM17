package it.polimi.ingsw.am17.RMI.Server;

import it.polimi.ingsw.am17.GameState;
import it.polimi.ingsw.am17.VirtualView;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

public interface VirtualViewRMI extends Remote, VirtualView {
    @Override
    void showUpdate(GameState state) throws RemoteException;
    @Override
    void reportError(String details) throws RemoteException;
    // TODO: is this possible??
    void showUpdate(List<UUID> idList) throws RemoteException;
}

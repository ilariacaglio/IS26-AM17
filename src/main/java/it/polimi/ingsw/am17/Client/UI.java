package it.polimi.ingsw.am17.Client;

import it.polimi.ingsw.am17.VirtualServer;
import it.polimi.ingsw.am17.VirtualView;

import java.rmi.RemoteException;

public class UI {
    private final VirtualServer virtualServer;
    private final VirtualView client;
    private GameClient game;
    public UI(VirtualServer server, VirtualView client, GameClient game, VirtualServer virtualServer, VirtualView client1){
        this.virtualServer = virtualServer;
        this.client = client;
        this.game = game;
    }
}

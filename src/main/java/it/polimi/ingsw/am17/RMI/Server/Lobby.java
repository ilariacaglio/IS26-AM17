package it.polimi.ingsw.am17.RMI.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Lobby {
    private final UUID gameID;
    private final List<VirtualViewRMI> clientsList;
    public Lobby(UUID gameID) {
        this.gameID = gameID;
        clientsList= new ArrayList<>();
    }

    /**
     * Add the client to the clients list
     * @param client
     */
    // TODO: is synchronized necessary?
    public synchronized void addClient (VirtualViewRMI client) {
        clientsList.add(client);
    }

    public UUID getGameID () {
        return gameID;
    }

    public void broadcast(String message) {
        for (VirtualViewRMI client : clientsList) {
            //update
        }
    }
}

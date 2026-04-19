package it.polimi.ingsw.am17;

import it.polimi.ingsw.am17.Model.Observer;

import java.util.UUID;

public interface VirtualView extends Observer {
    // TODO: chiamato quando il client si registra come observer
    void updateGameId(UUID gameId) throws Exception;
}

package it.polimi.ingsw.am17;

import it.polimi.ingsw.am17.Model.Observer;

import java.util.UUID;

public interface VirtualView extends Observer {
    void updateGameId(UUID gameId) throws Exception;
    void updatePlayerNumber(int playerNumber) throws Exception;
}

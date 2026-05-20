package it.polimi.ingsw.am17.Client.UserInterface;
import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.util.UUID;

public interface UI {
    void start();
    void drawInterface(ClientModel game, String errorMessage);
    void printGameId(UUID gameId);
    void printEra();
    void printGamesList();
    void setLocalPlayer();
    Player getLocalPlayer();
    void setModel(ClientModel model);
}

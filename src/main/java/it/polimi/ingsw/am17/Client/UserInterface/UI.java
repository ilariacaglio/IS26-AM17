package it.polimi.ingsw.am17.Client.UserInterface;
import it.polimi.ingsw.am17.Client.Model.ClientModel;

import java.util.UUID;

public interface UI {
    void start();
    void drawInterface(ClientModel game);
    void printGameId(UUID gameId);
    void printEra();
}

package it.polimi.ingsw.am17.Client;
import java.util.UUID;

public interface UI {
    void start();
    void drawInterface(GameClient game);
    void printGameId(UUID gameId);
}

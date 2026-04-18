package it.polimi.ingsw.am17;

public interface VirtualView {
    void showUpdate(GameState state) throws Exception;
    void reportError(String details) throws Exception;
}

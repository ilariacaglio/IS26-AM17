package it.polimi.ingsw.am17.Client.UserInterface;
import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Server.Model.Color;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.util.List;
import java.util.UUID;

public interface UI {
    void start();
    void updateInterfaceFromPickTribes();
    void updateInterfaceFromPickOffering();
    void updateInterfaceFromEndTurn();
    void updateInterfacePlayerQueue();
    void drawInterface(String errorMessage);
    void printGameId(UUID gameId);
    void printEra();
    void printGamesList();
    void setLocalPlayer();
    Player getLocalPlayer();
    void setModel(ClientModel model);
    void setAvailableColors(List<Color> availableColors);
    boolean isBuilding2EffectUsed();
    void setBuilding2EffectUsed(boolean building2EffectUsed);
}

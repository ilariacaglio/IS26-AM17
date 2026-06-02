package it.polimi.ingsw.am17.Client.UserInterface;
import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Server.Model.Color;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.util.List;

public interface UI {
    void start();

    // updateInterface methods notify the UI of changes in the model so it can react accordingly
    // (observer-like pattern)
    void updateInterfaceFromIdChange();
    void updateInterfaceFromGameStateChange();

    void updateInterfaceFromPickTribes();
    void updateInterfaceFromPickOffering();
    void updateInterfaceFromEndTurn();

    // old methods
    void drawInterface(String errorMessage);
    void setDisplayEra(boolean displayEra);
    void printGamesList();
    void setLocalPlayer();
    Player getLocalPlayer();
    void setModel(ClientModel model);
    void setAvailableColors(List<Color> availableColors);
    boolean isBuilding2EffectUsed();
    void setBuilding2EffectUsed(boolean building2EffectUsed);

}

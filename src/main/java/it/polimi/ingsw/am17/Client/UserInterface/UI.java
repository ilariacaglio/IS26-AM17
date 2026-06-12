package it.polimi.ingsw.am17.Client.UserInterface;

import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.CommonInterfaces.InvalidOperationException;

public interface UI {
    void start();
    void setModel(ClientModel model);

    // updateInterface methods notify the UI of changes in the model so it can react accordingly
    // (observer-like pattern)
    void updateInterfaceFromGameIdChange();
    void updateInterfaceFromGameIdListChange();
    void updateInterfaceFromGameStateChange();
    void updateInterfaceFromPlayerQueueChange();
    void updateInterfaceFromPlayerSelectOfferingCard();
    void updateInterfaceFromPlayerSelectTribeCards();
    void updateInterfaceFromEndTurn();
    void updateInterfaceFromStartGame();
    void updateInterfaceFromEndGame();
    void updateInterfaceFromForcedEndGame(String disconnectedPlayer);
    void updateInterfaceFromErrorMessage(InvalidOperationException exception);
}

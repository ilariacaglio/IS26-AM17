package it.polimi.ingsw.am17.Client.UserInterface;

public interface UI {
    void start();

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
    void updateInterfaceFromErrorMessage();
}

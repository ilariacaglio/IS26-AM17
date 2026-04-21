package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.TribesCard;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Stack;

public interface Observer {
    void updateEra(int era) throws Exception;
    void updatePlayerStack(Stack<Player> orderedPlayer)  throws Exception;
    void updateOfferingCards(List<OfferingCard> offeringCards)  throws Exception;
    void updateTribesCards(List<TribesCard> upperRow, List<TribesCard> lowerRow)  throws Exception;
    void updateBuildingCards(List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow)  throws Exception;
}

package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.TribesCard;

import java.util.List;
import java.util.Stack;

public interface Observer {
    void updateEra(int era);
    void updatePlayerStack(Stack<Player> orderedPlayer);
    void updateOfferingCards(List<OfferingCard> offeringCards);
    void updateTribesCards(List<TribesCard> upperRow, List<TribesCard> lowerRow);
    void updateBuildingCards(List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow);
}

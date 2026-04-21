package it.polimi.ingsw.am17;

import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.TribesCard;
import it.polimi.ingsw.am17.Model.Observer;
import it.polimi.ingsw.am17.Model.OfferingCard;
import it.polimi.ingsw.am17.Model.Player;

import java.util.List;
import java.util.Stack;
import java.util.UUID;

public interface VirtualView extends Observer {
    void updateGameId(UUID gameId) throws Exception;
    void updateGamesIdList(List<UUID> gamesIdList) throws Exception;
    void updateEra(int era) throws Exception;
    void updatePlayerStack(Stack<Player> orderedPlayer) throws Exception;
    void updateOfferingCards(List<OfferingCard> offeringCards) throws Exception;
    void updateTribesCards(List<TribesCard> upperRow, List<TribesCard> lowerRow) throws Exception;
    void updateBuildingCards(List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) throws Exception;
}

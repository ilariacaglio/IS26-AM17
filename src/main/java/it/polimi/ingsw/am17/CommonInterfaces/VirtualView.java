package it.polimi.ingsw.am17.CommonInterfaces;

import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.util.List;
import java.util.Stack;
import java.util.UUID;

public interface VirtualView extends Observer {
    void updateOfferingCards(List<OfferingCard> offeringCards) throws Exception;
    void updateTribesCards(List<TribesCard> upperRow, List<TribesCard> lowerRow) throws Exception;
    void updateBuildingCards(List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) throws Exception;

    //new
    void updateGameId(UUID gameId) throws Exception;
    void updateGamesIdList(List<UUID> gamesIdList) throws Exception;

    void updateEra(int era) throws Exception;

    void updatePlayerStack(Stack<Player> orderedPlayer) throws Exception;

    void updatePlayerSelectOfferingCard(Player player, OfferingCard offeringCard) throws Exception;

    void updatePlayerSelectTribesCard(Player player, List<CharacterCard> tribesCards, List<BuildingCard> buildingCards) throws Exception;

    void updateEndTurn(Stack<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                       List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) throws  Exception;

    void updateStartGame(Stack<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                         List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow,  List<OfferingCard> offeringCards) throws  Exception;
}

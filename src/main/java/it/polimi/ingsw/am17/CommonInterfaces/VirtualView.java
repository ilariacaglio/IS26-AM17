package it.polimi.ingsw.am17.CommonInterfaces;

import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Server.Utility.RankingEntry;

import java.util.*;

public interface VirtualView extends Observer {
    //new
    void updateGameId(UUID gameId) throws Exception;
    void updateGamesIdList(List<UUID> gamesIdList) throws Exception;

    void updateEra(int era) throws Exception;

    void updatePlayerQueue(Queue<Player> orderedPlayer) throws Exception;

    void updatePlayerSelectOfferingCard(Player player, OfferingCard offeringCard) throws Exception;

    void updatePlayerSelectTribeCards(Player player, List<CharacterCard> tribesCards, List<BuildingCard> buildingCards) throws Exception;

    void updateEndTurn(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                       List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) throws  Exception;

    void updateStartGame(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                         List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow,  List<OfferingCard> offeringCards) throws  Exception;

    void updateRanking(List<RankingEntry> ranking) throws Exception;

    void notifyPlayerDisconnection(String nickname);
}

package it.polimi.ingsw.am17.CommonInterfaces;

import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameState;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Server.Utility.RankingEntry;

import java.util.*;

/**
 * Represents a client in the observer pattern (observer).
 */
public interface VirtualView {
    // lobby methods
    void updateGameId(UUID gameId) throws Exception;
    void updateGamesIdList(List<UUID> gamesIdList) throws Exception;

    // game methods
    void updatePlayerQueue(Queue<Player> orderedPlayer) throws Exception;
    void updateStartGame(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                         List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow,  List<OfferingCard> offeringCards) throws  Exception;
    void updateGameState(GameState era) throws Exception;
    void updateEndTurn(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                       List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) throws  Exception;

    // player methods
    void updatePlayerSelectOfferingCard(Player player, OfferingCard offeringCard) throws Exception;
    void updatePlayerSelectTribeCards(Player player, List<CharacterCard> tribesCards, List<BuildingCard> buildingCards) throws Exception;

    // ending methods
    void notifyEndGame(String disconnectedPlayer, List<RankingEntry> ranking, Queue<Player> orderedPlayers) throws Exception;

    // notify errors
    void updateError(InvalidOperationException exception) throws Exception;
}

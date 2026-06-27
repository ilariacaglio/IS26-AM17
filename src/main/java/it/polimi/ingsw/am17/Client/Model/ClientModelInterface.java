package it.polimi.ingsw.am17.Client.Model;

import it.polimi.ingsw.am17.CommonInterfaces.InvalidOperationException;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.GameState;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Server.Utility.RankingEntry;

import java.util.List;
import java.util.Queue;
import java.util.UUID;

/**
 * updateInterface methods to change the local state of the game
 * and notify the UI of changes so it can react accordingly
 * (observer pattern)
 */
public interface ClientModelInterface {
    void updateGameId(UUID gameId);
    void updateGameIdList(List<UUID> gamesIdList);
    void updateGameState(GameState gameState);
    void updatePlayerQueue(Queue<Player> playerQueue);
    void updatePlayerSelectOfferingCard(Player player, OfferingCard offeringCard);

    /**
     *@param player player with the cards picked already inside
     *@param characterCards list of character cards picked by player
     *@param buildingCards list of building cards picked by player
     */
    void updatePlayerSelectTribeCards(Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards);
    void updateEndTurn(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                       List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow);
    void updateStartGame(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                         List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow, List<OfferingCard> offeringCards);
    void updateEndGame(List<RankingEntry> ranking, Queue<Player> orderedPlayers);
    void updateForcedEndGame(String disconnectedPlayer);
    void updateError(InvalidOperationException exception);
}

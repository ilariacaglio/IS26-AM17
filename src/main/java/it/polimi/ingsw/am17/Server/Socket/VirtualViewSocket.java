package it.polimi.ingsw.am17.Server.Socket;

import it.polimi.ingsw.am17.CommonInterfaces.InvalidOperationException;
import it.polimi.ingsw.am17.CommonInterfaces.Message;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import it.polimi.ingsw.am17.CommonInterfaces.MessageType;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.GameState;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Server.Utility.RankingEntry;

import java.net.Socket;
import java.util.*;

/**
 * Forwards requests from the controller to a single client.
 */
public class VirtualViewSocket implements VirtualView {
    private final Socket socket;

    public VirtualViewSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void updateGameId(UUID gameId) throws Exception {
        Message message = new Message(MessageType.UPDATE_GAME_ID);
        message.setGameId(gameId);
        message.send(socket);
    }

    @Override
    public void updateGamesIdList(List<UUID> gamesIdList) throws Exception {
        Message message = new Message(MessageType.UPDATE_GAMES_ID_LIST);
        message.setGamesIdList(gamesIdList);
        message.send(socket);
    }

    @Override
    public void updateGameState(GameState era) throws Exception {
        Message message = new Message(MessageType.UPDATE_GAME_STATE);
        message.setGameState(era);
        message.send(socket);
    }

    @Override
    public void updatePlayerQueue(Queue<Player> orderedPlayer) throws Exception {
        Message message = new Message(MessageType.UPDATE_PLAYERS_DATA);
        message.setOrderedPlayer(orderedPlayer);
        message.send(socket);
    }

    @Override
    public void updatePlayerSelectOfferingCard(Player player, OfferingCard offeringCard) throws Exception {
        Message message = new Message(MessageType.UPDATE_PLAYER_SELECT_OFFERING_CARD);
        message.setPlayer(player);
        message.setOfferingCard(offeringCard);
        message.send(socket);
    }

    @Override
    public void updatePlayerSelectTribeCards(Player player, List<CharacterCard> tribesCards, List<BuildingCard> buildingCards) throws Exception {
        Message message = new Message(MessageType.UPDATE_PLAYER_SELECT_TRIBE_CARDS);
        message.setPlayer(player);
        message.setCharacterCards(tribesCards);
        message.setBuildingCards(buildingCards);
        message.send(socket);
    }

    @Override
    public void updateEndTurn(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow, List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) throws Exception {
        Message message = new Message(MessageType.UPDATE_END_TURN);
        message.setOrderedPlayer(players);
        message.setUpperRow(upperRow);
        message.setLowerRow(lowerRow);
        message.setUpperBuildingRow(upperBuildingRow);
        message.setLowerBuildingRow(lowerBuildingRow);
        message.send(socket);
    }

    @Override
    public void updateStartGame(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow, List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow, List<OfferingCard> offeringCards) throws Exception {
        Message message = new Message(MessageType.UPDATE_START_GAME);
        message.setOrderedPlayer(players);
        message.setUpperRow(upperRow);
        message.setLowerRow(lowerRow);
        message.setUpperBuildingRow(upperBuildingRow);
        message.setOfferingCards(offeringCards);
        message.send(socket);
    }

    @Override
    public void updateError(InvalidOperationException exception) throws Exception {
        Message message = new Message(MessageType.UPDATE_ERROR);
        message.setException(exception);
        message.send(socket);
    }

    @Override
    public void updateEndGame(List<RankingEntry> ranking, Queue<Player> orderedPlayers) throws Exception {
        Message message = new Message(MessageType.END_GAME);
        message.setRanking(ranking);
        message.setOrderedPlayer(orderedPlayers);
        message.send(socket);
    }

    @Override
    public void updateForceEndGame(String disconnectedPlayer) throws Exception {
        Message message = new Message(MessageType.END_GAME_FORCED);
        message.setDisconnectedPlayerNickname(disconnectedPlayer);
    }

}

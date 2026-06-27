package it.polimi.ingsw.am17.Server.RMI;

import it.polimi.ingsw.am17.CommonInterfaces.InvalidOperationException;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualClient;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.GameState;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Server.Utility.RankingEntry;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class ClientRMIWrapper implements VirtualClient {
    private final VirtualClient client;
    private static final ExecutorService remoteRMICallsExecutor = Executors.newCachedThreadPool();
    private static final Logger logger = Logger.getLogger(ClientRMIWrapper .class.getName());

    public ClientRMIWrapper(VirtualClient client){
        this.client = client;
    }

    @Override
    public void updateGameId(UUID gameId) {
        remoteRMICallsExecutor.submit(() -> {
            try {
                client.updateGameId(gameId);
            } catch (Exception e) {
                logger.severe("RMI: Failed to notify game id: " + e.getMessage());
            }
        });
    }

    @Override
    public void updateGamesIdList(List<UUID> gamesIdList) {
        remoteRMICallsExecutor.submit(() -> {
            try {
                client.updateGamesIdList(gamesIdList);
            } catch (Exception e) {
                logger.severe("RMI: Failed to notify game ids list: " + e.getMessage());
            }
        });
    }

    @Override
    public void updatePlayerQueue(Queue<Player> orderedPlayer) throws RemoteException {
        remoteRMICallsExecutor.submit(() -> {
            try {
                client.updatePlayerQueue(orderedPlayer);
            } catch (Exception e) {
                logger.severe("RMI: Failed to notify players list: " + e.getMessage());
            }
        });
    }

    @Override
    public void updateStartGame(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow, List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow, List<OfferingCard> offeringCards) {
        remoteRMICallsExecutor.submit(() -> {
            try {
                client.updateStartGame(players, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow, offeringCards);
            } catch (Exception e) {
                logger.severe("RMI: Failed to notify start game: " + e.getMessage());
            }
        });
    }

    @Override
    public void updateGameState(GameState era) {
        remoteRMICallsExecutor.submit(() -> {
            try {
                client.updateGameState(era);
            } catch (Exception e) {
                logger.severe("RMI: Failed to notify game state: " + e.getMessage());
            }
        });
    }

    @Override
    public void updateEndTurn(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow, List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow) {
        remoteRMICallsExecutor.submit(() -> {
            try {
                client.updateEndTurn(players, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow);
            } catch (Exception e) {
                logger.severe("RMI: Failed to notify end turn: " + e.getMessage());
            }
        });
    }

    @Override
    public void updatePlayerSelectOfferingCard(Player player, OfferingCard offeringCard) {
        remoteRMICallsExecutor.submit(() -> {
            try {
                client.updatePlayerSelectOfferingCard(player, offeringCard);
            } catch (Exception e) {
                logger.severe("RMI: Failed to notify offering card selection: " + e.getMessage());
            }
        });
    }

    @Override
    public void updatePlayerSelectTribeCards(Player player, List<CharacterCard> tribesCards, List<BuildingCard> buildingCards) {
        remoteRMICallsExecutor.submit(() -> {
            try {
               client.updatePlayerSelectTribeCards(player, tribesCards, buildingCards);
            } catch (Exception e) {
                logger.severe("RMI: Failed to notify tribe cards selection: " + e.getMessage());
            }
        });
    }

    @Override
    public void updateEndGame(List<RankingEntry> ranking, Queue<Player> orderedPlayers) {
        remoteRMICallsExecutor.submit(() -> {
            try {
                client.updateEndGame(ranking, orderedPlayers);
            } catch (Exception e) {
                logger.severe("RMI: Failed to notify end game: " + e.getMessage());
            }
        });
    }

    @Override
    public void updateForceEndGame(String disconnectedPlayer) {
        remoteRMICallsExecutor.submit(() -> {
            try {
                client.updateForceEndGame(disconnectedPlayer);
            } catch (Exception e) {
                logger.severe("RMI: Failed to notify forced end game: " + e.getMessage());
            }
        });
    }

    @Override
    public void updateError(InvalidOperationException exception) {
        remoteRMICallsExecutor.submit(() -> {
            try {
                client.updateError(exception);
            } catch (Exception e) {
                logger.severe("RMI: Failed to notify error: " + e.getMessage());
            }
        });
    }

    @Override
    public int hashCode() {
        return this.client.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        // Self-check
        if (this == o) return true;

        // Null check
        if (o == null) return false;

        if (this.getClass() == o.getClass()) {
            ClientRMIWrapper otherWrapper = (ClientRMIWrapper) o;
            return Objects.equals(this.client, otherWrapper.client);
        }

        if (o instanceof VirtualClient) {
            return Objects.equals(this.client, o);
        }

        return false;
    }
}

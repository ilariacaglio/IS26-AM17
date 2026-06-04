package it.polimi.ingsw.am17.Server.Model;

import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import it.polimi.ingsw.am17.Server.Utility.RankingEntry;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Implements a subject in the observer pattern (an observable "game").
 */
public abstract class Subject {
    private final static Logger logger = Logger.getLogger(Subject.class.getName());
    // thread-safe list of clients
    private final List<VirtualView> clients = new CopyOnWriteArrayList<>();
    private final ExecutorService notifyService = Executors.newCachedThreadPool();

    /**
     * Attach a client to the subject (start observing).
     * @param client to be attached
     */
    public void attach(VirtualView client) {
        clients.add(client);
        logger.info("Added client " + client.getClass().getSimpleName() + " as observer.");
    }

    /**
     * Detach a client to the subject (stop observing).
     * @param client to be detached
     */
    public void detach(VirtualView client) {
        clients.remove(client);
        logger.info("Removed client " + client.getClass().getSimpleName() + " from observers.");

    }

    /**
     * Notifies the start of a new era
     * @param era   the era that has started
     */
    void notifyGameState(GameState era) {
        for (VirtualView client : clients) {
            notifyService.submit(() -> {
                try {
                    client.updateGameState(era);
                } catch (Exception e) {
                    logger.severe("Failed to notify game state: " + e.getMessage());
                }
            });
        }
    }

    void notifyPlayerQueue(Queue<Player> orderedPlayer) {
        for (VirtualView client : clients) {
            notifyService.submit(() -> {
                try {
                    client.updatePlayerQueue(orderedPlayer);
                } catch (Exception e) {
                    logger.severe("Failed to notify player queue" + e.getMessage());
                }
            });
        }
    }

    void notifyPlayerSelectOfferingCard(Player player, OfferingCard offeringCard){
        for (VirtualView client : clients) {
            notifyService.submit(() -> {
                try {
                    client.updatePlayerSelectOfferingCard(player, offeringCard);
                } catch (Exception e) {
                    logger.severe("Failed to notify offering card selection: " + e.getMessage());
                }
            });
        }
    }

    void notifyPlayerSelectTribesCard(Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards){
        for (VirtualView client : clients) {
            notifyService.submit(() -> {
                try {
                    client.updatePlayerSelectTribeCards(player, characterCards, buildingCards);
                } catch (Exception e) {
                    logger.severe("Failed to notify tribes card selection: " + e.getMessage());
                }
            });
        }
    }

    void notifyEndTurn(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                       List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow){
        Queue<Player> newQueue = buildQueueWithoutPlayers(players);
        for (VirtualView client : clients) {
            notifyService.submit(() -> {
                try {
                    client.updateEndTurn(newQueue, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow);
                } catch (Exception e) {
                    logger.severe("Failed to notify end turn: " + e.getMessage());
                }
            });
        }
    }

    void notifyStartGame(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                       List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow, List<OfferingCard> offeringCards){
        for (VirtualView client : clients) {
            notifyService.submit(() -> {
                try {
                    client.updateStartGame(players, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow, offeringCards);
                } catch (Exception e) {
                    logger.severe("Failed to notify game start: " + e.getMessage());
                }
            });
        }
    }

    void notifyEndGame(List<RankingEntry> ranking, Queue<Player> orderedPlayers) {
        Queue<Player> newQueue = buildQueueWithoutPlayers(orderedPlayers);
        for (VirtualView client : clients) {
            notifyService.submit(() -> {
                logger.info("Calling notifyEndGame on client " + client.getClass().getSimpleName());
                try {
                    client.notifyEndGame(ranking, newQueue);

                } catch (Exception e) {
                    logger.severe("Failed to notify end game: " + e.getMessage());
                }
            });
        }
        clients.clear();
    }

    void notifyForceEndGame(String disconnectedPlayer) {
        for (VirtualView client : clients) {
            notifyService.submit(() -> {
                logger.info("Calling notifyForceEndGame on client " + client.getClass().getSimpleName());
                try {
                    client.notifyForceEndGame(disconnectedPlayer);

                } catch (Exception e) {
                    logger.severe("Failed to notify end game: " + e.getMessage());
                }
            });
        }
        clients.clear();
    }

    /**
     * @return the given queue without the cards field in player object
     */
    private LinkedList<Player> buildQueueWithoutPlayerCards(Queue<Player> orderedPlayers) {
        return orderedPlayers.stream()
                .map(p -> {
                    // Creates a defensive copy of the player, intentionally omitting their cards
                    Player copy = new Player(p.getNickname(), p.getColor());
                    copy.addFood(p.getFood());
                    copy.addPp(p.getPp());
                    return copy;
                })
                .collect(Collectors.toCollection(LinkedList::new));
    }
}

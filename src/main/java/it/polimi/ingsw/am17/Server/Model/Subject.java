package it.polimi.ingsw.am17.Server.Model;

import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualClient;
import it.polimi.ingsw.am17.Server.Utility.RankingEntry;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Implements a subject in the observer pattern (an observable "game").
 */
public abstract class Subject {
    private final static Logger logger = Logger.getLogger(Subject.class.getName());
    private final List<VirtualClient> clients = new ArrayList<>();

    /**
     * Attach a client to the subject (start observing).
     * @param client to be attached
     */
    public void attach(VirtualClient client) {
        clients.add(client);
        logger.info("Added client " + client.getClass().getSimpleName() + " as observer.");
    }

    /**
     * Detach a client to the subject (stop observing).
     * @param client to be detached
     */
    public void detach(VirtualClient client) {
        clients.remove(client);
        logger.info("Removed client " + client.getClass().getSimpleName() + " from observers.");

    }

    /**
     * Notifies the start of a new era
     * @param era   the era that has started
     */
    void notifyGameState(GameState era) {
        for (VirtualClient client : clients) {
            try {
                client.updateGameState(era);
            } catch (Exception e) {
                logger.severe("Failed to notify game state: " + e.getMessage());
            }
        }
    }

    /**
     * Notifies new player queue to clients
     */
    void notifyPlayerQueue(Queue<Player> orderedPlayer) {
        for (VirtualClient client : clients) {
            try {
                client.updatePlayerQueue(orderedPlayer);
            } catch (Exception e) {
                logger.severe("Failed to notify player queue" + e.getMessage());
            }
        }
    }

    /**
     * Notifies that a player has picked an offering card to all the observers
     */
    void notifyPlayerSelectOfferingCard(Player player, OfferingCard offeringCard){
        for (VirtualClient client : clients) {
            try {
                client.updatePlayerSelectOfferingCard(player, offeringCard);
            } catch (Exception e) {
                logger.severe("Failed to notify offering card selection: " + e.getMessage());
            }
        }
    }

    /**
     * Notifies that a player has picked tribe cards to all the observers
     */
    void notifyPlayerSelectTribesCard(Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards){
        for (VirtualClient client : clients) {
            try {
                client.updatePlayerSelectTribeCards(player, characterCards, buildingCards);
            } catch (Exception e) {
                logger.severe("Failed to notify tribes card selection: " + e.getMessage());
            }
        }
    }

    /**
     * Notifies the end of the turn to all the observers
     */
    void notifyEndTurn(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                       List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow){
        Queue<Player> newQueue = buildQueueWithoutPlayerCards(players);
        for (VirtualClient client : clients) {
            try {
                client.updateEndTurn(newQueue, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow);
            } catch (Exception e) {
                logger.severe("Failed to notify end turn: " + e.getMessage());
            }
        }
    }

    /**
     * Notifies the start of the game to all the observers
     */
    void notifyStartGame(Queue<Player> players, List<TribesCard> upperRow, List<TribesCard> lowerRow,
                       List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow, List<OfferingCard> offeringCards){
        for (VirtualClient client : clients) {
            try {
                client.updateStartGame(players, upperRow, lowerRow, upperBuildingRow, lowerBuildingRow, offeringCards);
            } catch (Exception e) {
                logger.severe("Failed to notify game start: " + e.getMessage());
            }
        }
    }

    /**
     * Notifies the end of the game to all the observers
     */
    void notifyEndGame(List<RankingEntry> ranking, Queue<Player> orderedPlayers) {
        Queue<Player> newQueue = buildQueueWithoutPlayerCards(orderedPlayers);
        for (VirtualClient client : clients) {
            logger.info("Calling notifyEndGame on client " + client.getClass().getSimpleName());
            try {
                client.updateEndGame(ranking, newQueue);
            } catch (Exception e) {
                logger.severe("Failed to notify end game: " + e.getMessage());
            }
        }
        clients.clear();
    }

    /**
     * Notifies the game has ended due to the disconnection of a player to all the observers
     */
    void notifyForceEndGame(String disconnectedPlayer) {
        for (VirtualClient client : clients) {
            logger.info("Calling notifyForceEndGame on client " + client.getClass().getSimpleName());
            try {
                client.updateForceEndGame(disconnectedPlayer);
            } catch (Exception e) {
                logger.severe("Failed to notify end game: " + e.getMessage());
            }
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

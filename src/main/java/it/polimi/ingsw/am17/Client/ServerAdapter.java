package it.polimi.ingsw.am17.Client;

import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This is the interface exposed by the network layer and called by the ui
 */
public interface ServerAdapter {
    CompletableFuture<Void> getGamesList();
    CompletableFuture<Void> createGame(Player player, int numPlayers);
    CompletableFuture<Void> closeGame();
    CompletableFuture<Void> joinGame(UUID gameId, Player player);
    CompletableFuture<Void> pickOfferingCard(Character offeringCardLetter);
    CompletableFuture<Void> pickTribeCards(List<CharacterCard> characterCards, List<BuildingCard> buildingCards);
}

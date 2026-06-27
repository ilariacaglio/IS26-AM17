package it.polimi.ingsw.am17.Client;

import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.util.List;
import java.util.UUID;

/**
 * This is the interface exposed by the network layer and called by the ui
 * We chose an interface and not a utility class to highlight abstraction
 */
public interface ServerAdapter {
    void getGamesList();
    void createGame(Player player, int numPlayers);
    void closeGame();
    void joinGame(UUID gameId, Player player);
    void pickOfferingCard(Character offeringCardLetter);
    void pickTribeCards(List<CharacterCard> characterCards, List<BuildingCard> buildingCards);
}

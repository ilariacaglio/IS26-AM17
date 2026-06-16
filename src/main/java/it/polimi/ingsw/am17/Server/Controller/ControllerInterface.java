package it.polimi.ingsw.am17.Server.Controller;

import it.polimi.ingsw.am17.CommonInterfaces.VirtualClient;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.util.List;
import java.util.UUID;

/**
 * Interface exposed by the controller to the network layer.
 * Used by ServerRMI and ServerSocket
 */
public interface ControllerInterface {
    void createGame(VirtualClient client, Player player, int numPlayers);
    void joinGame(VirtualClient client, UUID gameId, Player player);
    void closeGame(VirtualClient client);
    void pickOfferingCard(VirtualClient client, Character offeringCardLetter);
    void pickTribeCards(VirtualClient client, List<CharacterCard> characterCards, List<BuildingCard> buildingCards);
    void getGamesList(VirtualClient client);
}

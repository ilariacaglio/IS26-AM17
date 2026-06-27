package it.polimi.ingsw.am17.CommonInterfaces;

import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.util.List;
import java.util.UUID;

/**
 * Interface that specifies the requests that can be made from a client to the server
 */
public interface VirtualServer {
    void getGamesList(VirtualClient client) throws Exception;
    void createGame(VirtualClient client, Player player, int numPlayers)  throws Exception;
    void closeGame(VirtualClient client) throws Exception;
    void joinGame(VirtualClient client, UUID gameId, Player player)  throws Exception;
    void pickOfferingCard(VirtualClient client, Character offeringCardLetter)  throws Exception;
    void pickTribeCards(VirtualClient client, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws Exception;
}

package it.polimi.ingsw.am17;

import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.CharacterCard;
import it.polimi.ingsw.am17.Model.OfferingCard;
import it.polimi.ingsw.am17.Model.Player;
import it.polimi.ingsw.am17.RMI.Server.VirtualViewRMI;

import java.util.List;
import java.util.UUID;

public interface VirtualServer {
    List<UUID> getGamesList() throws Exception;
    void createGame(VirtualViewRMI client, Player player, int numPlayers)  throws Exception;
    void joinGame(VirtualViewRMI client, UUID gameId, Player player)  throws Exception;
    void pickOfferingCard(UUID gameId, Player player, OfferingCard card)  throws Exception;
    void pickTribeCards(UUID gameId, Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws Exception;
}

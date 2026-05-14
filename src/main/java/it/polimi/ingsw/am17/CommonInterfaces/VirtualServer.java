package it.polimi.ingsw.am17.CommonInterfaces;

import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.util.List;
import java.util.UUID;

public interface VirtualServer {
    void getGamesList(VirtualView client) throws Exception;
    void createGame(VirtualView client, Player player, int numPlayers)  throws Exception;
    void closeGame(VirtualView client, Player player, UUID gameId) throws Exception;
    void joinGame(VirtualView client, UUID gameId, Player player)  throws Exception;
    void pickOfferingCard(UUID gameId, Player player, OfferingCard card)  throws Exception;
    void pickTribeCards(UUID gameId, Player player, List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws Exception;
}

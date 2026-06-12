package it.polimi.ingsw.am17.Server.Controller;

import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.util.List;
import java.util.UUID;

public interface ControllerInterface {
    void createGame(VirtualView client, Player player, int numPlayers);
    void joinGame(VirtualView client, UUID gameId, Player player);
    void closeGame(VirtualView client);
    void pickOfferingCard(VirtualView client, Character offeringCardLetter);
    void pickTribeCards(VirtualView client, List<CharacterCard> characterCards, List<BuildingCard> buildingCards);
    void getGamesList(VirtualView client);
}

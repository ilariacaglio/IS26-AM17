package it.polimi.ingsw.am17.Server.Model;

import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;

import java.util.List;
import java.util.UUID;

public interface ModelInterface {
    UUID getId();
    void addPlayer(Player p);
    void selectOfferingCard(String nickname, Character offeringCardLetter);
    void pickTribeCards(String nickname, List<CharacterCard> characterCards, List<BuildingCard> buildingCards);
    void forceEndGame(String nickname);
    void endGame();
}

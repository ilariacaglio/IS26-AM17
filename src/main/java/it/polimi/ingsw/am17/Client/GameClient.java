package it.polimi.ingsw.am17.Client;

import it.polimi.ingsw.am17.Model.BuildingDeck;
import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.TribesCard;
import it.polimi.ingsw.am17.Model.OfferingCard;
import it.polimi.ingsw.am17.Model.Player;
import it.polimi.ingsw.am17.Model.TribesDeck;

import java.util.List;
import java.util.Stack;
import java.util.UUID;

public class GameClient {

    UUID id;
    int numPlayers;
    int currentEra;

    Stack<Player> orderedPlayer;

    List<OfferingCard> offeringCards;

    TribesDeck tribesDeck;
    List<TribesCard> upperRow;
    List<TribesCard> lowerRow;

    BuildingDeck buildingDeck;

    List<BuildingCard> upperBuildingRow;
    List<BuildingCard> lowerBuildingRow;

}

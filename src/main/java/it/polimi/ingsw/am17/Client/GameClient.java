package it.polimi.ingsw.am17.Client;

import it.polimi.ingsw.am17.Model.BuildingDeck;
import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.TribesCard;
import it.polimi.ingsw.am17.Model.OfferingCard;
import it.polimi.ingsw.am17.Model.Player;
import it.polimi.ingsw.am17.Model.TribesDeck;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

public class GameClient {

    public UUID id;
    public int numPlayers;
    public int currentEra = 0;

    public Stack<Player> orderedPlayer = new Stack<>();

    public List<OfferingCard> offeringCards = new ArrayList<>();

    public List<TribesCard> upperRow = new ArrayList<>();
    public List<TribesCard> lowerRow = new ArrayList<>();

    public List<BuildingCard> upperBuildingRow = new ArrayList<>();
    public List<BuildingCard> lowerBuildingRow  = new ArrayList<>();
}

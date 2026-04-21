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

    public List<UUID> gamesIdList = new ArrayList<>();

    public Stack<Player> orderedPlayer = new Stack<>();

    public List<OfferingCard> offeringCards = new ArrayList<>();

    public List<TribesCard> upperRow = new ArrayList<>();
    public List<TribesCard> lowerRow = new ArrayList<>();

    public List<BuildingCard> upperBuildingRow = new ArrayList<>();
    public List<BuildingCard> lowerBuildingRow  = new ArrayList<>();

    public void setGameId(UUID id) {
        this.id = id;
    }

    public void setCurrentEra(int currentEra){
        this.currentEra = currentEra;
    }

    public void setOrderedPlayers(Stack<Player> orderedPlayers){
        this.orderedPlayer = orderedPlayers;
    }

    public void setOfferingCards(List<OfferingCard> offeringCards){
        this.offeringCards = offeringCards;
    }

    public void setTribeCards(List<TribesCard> upperRow, List<TribesCard> lowerRow){
        this.upperRow = upperRow;
        this.lowerRow = lowerRow;
    }

    public void setBuildingCards(List<BuildingCard> upperBuildingRow, List<BuildingCard> lowerBuildingRow){
        this.upperBuildingRow = upperBuildingRow;
        this.lowerBuildingRow = lowerBuildingRow;
    }

    public void setGameIdList(List<UUID> gamesIdList){
        this.gamesIdList = new  ArrayList<>(gamesIdList);
    }
}

package it.polimi.ingsw.am17.Client.Model;

import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

public class ClientModel {
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
    public Player getPlayer(Player player)
    {
        return orderedPlayer.stream()
            .filter(p -> p.equals(player))
            .findFirst().orElse(null);

    }

    public void setPlayerOfferingCard(OfferingCard offeringCard, Player player)
    {
        offeringCards.stream()
                .filter(o -> o.equals(offeringCard))
                .findFirst()
                .ifPresent(o -> o.setPlayer(player));
    }
}

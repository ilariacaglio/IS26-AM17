package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.GameCard;

import java.util.List;

public class BuildingDeck {
    private List<BuildingCard> buildingCards;

    public BuildingDeck(int numPlayer)
    {
        buildingCards = null; //creare metodo per inizializzare il mazzo
    }

    public List<BuildingCard> drawAll(){
        return buildingCards;
    }
}

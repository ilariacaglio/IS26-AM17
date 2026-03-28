package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;

import java.util.ArrayList;
import java.util.List;

public class BuildingGameRow extends BuildingCardRow {
    private List<BuildingCard> cardList;

    public BuildingGameRow(){
        cardList = new ArrayList<>();
    }

    public void removeCard(BuildingCard card){
        cardList.remove(card);
    }

    public List<BuildingCard> getCardList(){ return cardList; }

}

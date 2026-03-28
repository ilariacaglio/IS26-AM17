package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;

import java.util.ArrayList;
import java.util.List;

public class BuildingCardRow {
    private final List<BuildingCard> cardList;

    public BuildingCardRow(){
        cardList = new ArrayList<>();
    }

    public void addCard(BuildingCard card){
        cardList.add(card);
    }

    public List<BuildingCard> getCards() {
        return cardList;
    }

}

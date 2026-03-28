package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.TribesCard;

import java.util.ArrayList;
import java.util.List;

public class TribeGameRow extends TribeCardRow{
    private List<TribesCard> cardList;

    public TribeGameRow(){
        cardList = new ArrayList<>();
    }

    public void removeCard(TribesCard card){
        cardList.remove(card);
    }

    public List<TribesCard> getCardList(){return cardList;}

}

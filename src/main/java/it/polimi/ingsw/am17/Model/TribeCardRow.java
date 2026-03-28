package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.TribesCard;

import java.util.ArrayList;
import java.util.List;

public class TribeCardRow {
    private final List<TribesCard> cardList;

    public TribeCardRow(){
        cardList = new ArrayList<>();
    }

    public void addCard(TribesCard card){
        cardList.add(card);
    }

    public List<TribesCard> getCards() {
        return cardList;
    }

}

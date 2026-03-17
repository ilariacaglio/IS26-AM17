package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.GameCard;

import java.util.ArrayList;
import java.util.List;

public class CardRow {
    private List<GameCard> cardList;

    public CardRow(){
        cardList = new ArrayList<>();
    }

    public void addCard(GameCard card){
        cardList.add(card);
    }

    public void showCards()
    {}

    public List<GameCard> getCards() {
        return cardList;
    }
}

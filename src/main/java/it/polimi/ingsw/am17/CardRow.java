package it.polimi.ingsw.am17;

import javax.smartcardio.Card;
import java.util.ArrayList;
import java.util.List;

public class CardRow {
    private List<GameCard> cards;

    public CardRow(){
        cards = new ArrayList<>();
        }

    public void addCard(GameCard c){
        cards.add(c);
        }

    public List<GameCard> getCards() {
        return cards;
    }


}

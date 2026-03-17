package it.polimi.ingsw.am17;

import java.util.ArrayList;
import java.util.List;

public class GameRow extends CardRow{
    private List<GameCard> cards;
//lascio lo stesso nome della lista di CardRow, perchè penso siano uguali, o forse no?

    public GameRow(){
        cards = new ArrayList<>();
    }

    public void removeCard(GameCard c) {

        cards.remove(c);
    }


}

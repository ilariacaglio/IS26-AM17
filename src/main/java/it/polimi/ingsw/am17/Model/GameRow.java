//will be eliminated
package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.GameCard;

import java.util.ArrayList;
import java.util.List;

public class GameRow extends CardRow {
    private List<GameCard> cardList;

    public GameRow(){
        cardList = new ArrayList<>();
    }

    public void removeCard(GameCard card){
        cardList.remove(card);
    }


}

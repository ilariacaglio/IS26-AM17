package it.polimi.ingsw.am17.Model.GameCard;

import java.io.Serializable;

public class TribesCard implements Serializable {
    private final CardType cardType;
    private final int era;

    public CardType getCardType() {
        return cardType;
    }
    public int getEra() {
        return era;
    }

    public TribesCard(int era,CardType cardType){
        this.cardType=cardType;
        this.era=era;
    }
}

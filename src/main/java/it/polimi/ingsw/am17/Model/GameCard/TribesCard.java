package it.polimi.ingsw.am17.Model.GameCard;

public class TribesCard {
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

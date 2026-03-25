package it.polimi.ingsw.am17.Model.GameCard;

public class TribesCard extends GameCard {
    private final CardType cardType;
    public CardType getCardType() {
        return cardType;
    }
    public TribesCard(int era,CardType cardType){
        super(era);
        this.cardType=cardType;
    }
}

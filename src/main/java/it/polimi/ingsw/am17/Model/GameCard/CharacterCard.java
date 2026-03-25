package it.polimi.ingsw.am17.Model.GameCard;

public class CharacterCard extends TribesCard {
    private int minPlayer;

    public int getMinPlayer() {
        return minPlayer;
    }

    public CharacterCard(int era, int minPlayer, CardType cardType) {
        super(era,  cardType);
        this.minPlayer = minPlayer;
    }
}

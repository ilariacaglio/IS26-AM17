package it.polimi.ingsw.am17.Model.GameCard;

public class CharacterCard extends TribesCard {
    private int minPlayer;

    public int getMinPlayer() {
        return minPlayer;
    }

    public CharacterCard(int era, int minPlayer){
        super(era);
        this.minPlayer = minPlayer;
    }
}

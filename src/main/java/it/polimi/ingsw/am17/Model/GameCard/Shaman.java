package it.polimi.ingsw.am17.Model.GameCard;


public class Shaman extends CharacterCard {
    private int stars;

    public int getStars() {
        return stars;
    }

    public Shaman(int era, int minPlayer, int stars){
        super(era, minPlayer);
        this.stars = stars;
    }
}

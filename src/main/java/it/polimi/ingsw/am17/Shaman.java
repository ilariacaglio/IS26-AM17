package it.polimi.ingsw.am17;

import javax.smartcardio.Card;

public class Shaman extends CharacterCard {
    private int stars;

    public Shaman(int era, int minPlayers, int stars){
        super(era, minPlayers);
        this.stars = stars;
    }

}

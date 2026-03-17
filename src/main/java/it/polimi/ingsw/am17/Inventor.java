package it.polimi.ingsw.am17;

public class Inventor extends CharacterCard{
    private String icon;

    public  Inventor(int era, int minPlayers, String icon){
        super(era, minPlayers);
        this.icon = icon;
    }
}

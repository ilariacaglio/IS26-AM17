package it.polimi.ingsw.am17.Model.GameCard;

public class Inventor extends CharacterCard {
    private String icon;

    public String getIcon() {
        return icon;
    }

    public  Inventor(int era, int minPlayers, String icon){
        super(era, minPlayers);
        this.icon = icon;
    }
}

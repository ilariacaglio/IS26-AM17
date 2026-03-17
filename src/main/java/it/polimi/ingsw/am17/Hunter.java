package it.polimi.ingsw.am17;

public class Hunter extends CharacterCard{
    private boolean withIcon;

    public Hunter(int era, int minPlayers, boolean withIcon) {
        super(era, minPlayers);
        this.withIcon = withIcon;

    }
}

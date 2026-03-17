package it.polimi.ingsw.am17.Model.GameCard;



public class Hunter extends CharacterCard {
    private boolean withIcon;

    public boolean isWithIcon() {
        return withIcon;
    }

    public Hunter(int era, int minPlayer, boolean withIcon) {
        super(era, minPlayer);
        this.withIcon = withIcon;

    }
}

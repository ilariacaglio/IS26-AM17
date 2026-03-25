package it.polimi.ingsw.am17.Model.GameCard;



public class Hunter extends CharacterCard {
    private boolean withIcon;

    public boolean isWithIcon() {
        return withIcon;
    }

    public Hunter(int era, int minPlayer, boolean withIcon,CardType cardType) {
        super(era, minPlayer, cardType);
        this.withIcon = withIcon;

    }
}

package it.polimi.ingsw.am17.Model.GameCard;

public class Hunter extends CharacterCard {
    private final boolean withIcon;

    public boolean isWithIcon() {
        return withIcon;
    }

    public Hunter(int era, int minPlayers, boolean withIcon) {
        super(era, minPlayers, CardType.HUNTER);
        this.withIcon = withIcon;
    }
}

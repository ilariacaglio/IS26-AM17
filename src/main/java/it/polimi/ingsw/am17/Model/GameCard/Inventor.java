package it.polimi.ingsw.am17.Model.GameCard;

public class Inventor extends CharacterCard {
    private final InventorIconType icon;

    public InventorIconType getIcon() {
        return icon;
    }

    public  Inventor(int era, int minPlayers, InventorIconType icon) {
        super(era, minPlayers, CardType.INVENTOR);
        this.icon = icon;
    }
}
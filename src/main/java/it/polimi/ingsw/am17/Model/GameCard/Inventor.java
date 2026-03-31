package it.polimi.ingsw.am17.Model.GameCard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Inventor extends CharacterCard {
    private final InventorIconType icon;

    public InventorIconType getIcon() {
        return icon;
    }

    @JsonCreator
    public  Inventor(
            @JsonProperty("era") int era,
            @JsonProperty("minPlayers") int minPlayers,
            @JsonProperty("icon") InventorIconType icon) {
        super(era, minPlayers, CardType.INVENTOR);
        this.icon = icon;
    }
}
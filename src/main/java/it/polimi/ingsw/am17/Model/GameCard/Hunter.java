package it.polimi.ingsw.am17.Model.GameCard;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Hunter extends CharacterCard {
    private final boolean withIcon;

    public boolean isWithIcon() {
        return withIcon;
    }

    public Hunter(
            @JsonProperty("era") int era,
            @JsonProperty("minPlayers") int minPlayers,
            @JsonProperty("withIcon") boolean withIcon) {
        super(era, minPlayers, CardType.HUNTER);
        this.withIcon = withIcon;
    }
}

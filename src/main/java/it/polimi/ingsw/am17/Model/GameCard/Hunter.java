package it.polimi.ingsw.am17.Model.GameCard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Hunter extends CharacterCard {
    private final boolean withIcon;

    public boolean isWithIcon() {
        return withIcon;
    }

    @JsonCreator
    public Hunter(
            @JsonProperty("era") int era,
            @JsonProperty("minPlayers") int minPlayers,
            @JsonProperty("withIcon") boolean withIcon) {
        super(era, minPlayers, CardType.HUNTER);
        this.withIcon = withIcon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hunter hunter = (Hunter) o;
        return withIcon == hunter.withIcon;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(withIcon);
    }
}

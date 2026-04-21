package it.polimi.ingsw.am17.Model.GameCard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inventor inventor = (Inventor) o;
        return icon == inventor.icon;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(icon);
    }

    public String toString() {
        return super.toString() + "(" + icon.toString() + ")";
    }
}
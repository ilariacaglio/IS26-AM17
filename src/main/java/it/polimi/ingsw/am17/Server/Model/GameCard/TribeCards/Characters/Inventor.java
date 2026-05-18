package it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;

import java.util.Objects;
import java.util.UUID;

public class Inventor extends CharacterCard {
    private final InventorIconType icon;

    public InventorIconType getIcon() {
        return icon;
    }

    @JsonCreator
    public  Inventor(
            @JsonProperty("era") int era,
            @JsonProperty("minPlayers") int minPlayers,
            @JsonProperty("icon") InventorIconType icon,
            @JsonProperty("id") UUID id) {
        super(era, minPlayers, CardType.INVENTOR, id);
        this.icon = icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inventor inventor = (Inventor) o;
        if(!this.getId().equals(inventor.getId())) return false;
        return icon.equals(inventor.icon);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(icon);
    }

    @Override
    public String getDetails() {
        return super.getDetails() + "(" + icon.toString() + ")";
    }

    @Override
    public String getImagePath() {
        return "/Images/Inventor/inventor_"+ icon.toString() +".png";
    }

    @Override
    public String getImagePath() {
        return "/Images/Inventor/inventor_"+ icon.toString() +".png";
    }
}
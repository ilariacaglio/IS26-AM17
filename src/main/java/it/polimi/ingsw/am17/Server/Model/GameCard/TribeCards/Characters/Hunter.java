package it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;

import java.util.Objects;
import java.util.UUID;

public class Hunter extends CharacterCard {
    private final Boolean withIcon;

    public Boolean isWithIcon() {
        return withIcon;
    }

    @JsonCreator
    public Hunter(
            @JsonProperty("era") Integer era,
            @JsonProperty("minPlayers") Integer minPlayers,
            @JsonProperty("withIcon") Boolean withIcon,
            @JsonProperty("id") UUID id) {
        super(era, minPlayers, CardType.HUNTER, id);
        this.withIcon = withIcon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hunter hunter = (Hunter) o;
        if(!this.getId().equals(hunter.getId())) return false;
        return withIcon.equals(hunter.withIcon);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(withIcon);
    }

    @Override
    public String toString() {
        if (withIcon) return super.toString() + "+";
        else return super.toString();
    }
    @Override
    public String getImagePath()
    {
        if(withIcon)
            return "/Images/Hunter/hunter_withIcon.png";
        return "/Images/Hunter/hunter.png";
    }
}

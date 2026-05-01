package it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;

import java.util.Objects;

public class Shaman extends CharacterCard {
    private final int stars;

    public int getStars() {
        return stars;
    }

    @JsonCreator
    public Shaman(
            @JsonProperty("era") int era,
            @JsonProperty("stars") int minPlayer,
            @JsonProperty("minPlayers") int stars){
        super(era, minPlayer, CardType.SHAMAN);
        this.stars = stars;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shaman shaman = (Shaman) o;
        if(!this.getId().equals(shaman.getId())) return false;
        return stars == shaman.stars;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(stars);
    }
}

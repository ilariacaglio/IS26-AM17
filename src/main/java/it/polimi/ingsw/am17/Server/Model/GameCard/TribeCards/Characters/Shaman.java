package it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameState;

import java.util.Objects;
import java.util.UUID;

public class Shaman extends CharacterCard {
    private final Integer stars;

    public Integer getStars() {
        return stars;
    }

    @JsonCreator
    public Shaman(
            @JsonProperty("era") GameState era,
            @JsonProperty("minPlayers") Integer minPlayers,
            @JsonProperty("stars") Integer stars,
            @JsonProperty("id") UUID id){
        super(era, minPlayers, CardType.SHAMAN, id);
        this.stars = stars;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shaman shaman = (Shaman) o;
        if(!this.getId().equals(shaman.getId())) return false;
        return stars.equals(shaman.stars);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(stars);
    }

    @Override
    public String getDetails() {
        return super.getDetails() + " " + stars + "★";
    }

    @Override
    public String getImagePath() {
        return "/Images/Shaman/shaman_"+ stars+".png";
    }


}

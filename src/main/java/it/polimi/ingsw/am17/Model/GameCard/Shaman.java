package it.polimi.ingsw.am17.Model.GameCard;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
}

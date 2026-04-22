package it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;

public class Artist extends CharacterCard {
    @JsonCreator
    public Artist(
            @JsonProperty("era") int era,
            @JsonProperty("minPlayers") int minPlayers){
        super(era, minPlayers, CardType.ARTIST);
    }
}

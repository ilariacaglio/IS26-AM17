package it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameState;

import java.util.UUID;

public class Artist extends CharacterCard {
    @JsonCreator
    public Artist(
            @JsonProperty("era") GameState era,
            @JsonProperty("minPlayers") int minPlayers,
            @JsonProperty("id") UUID id){
        super(era, minPlayers, CardType.ARTIST, id);
    }
}

package it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;

import java.util.UUID;

public class Binder extends CharacterCard {
    @JsonCreator
    public Binder(
            @JsonProperty("era") int era,
            @JsonProperty("minPlayers") int minPlayers,
            @JsonProperty("id") UUID id){
        super(era, minPlayers, CardType.BINDER, id);
    }
}

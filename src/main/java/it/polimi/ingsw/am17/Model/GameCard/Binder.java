package it.polimi.ingsw.am17.Model.GameCard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Binder extends CharacterCard{
    @JsonCreator
    public Binder(
            @JsonProperty("era") int era,
            @JsonProperty("minPlayers") int minPlayers){
        super(era, minPlayers,CardType.BINDER);
    }
}

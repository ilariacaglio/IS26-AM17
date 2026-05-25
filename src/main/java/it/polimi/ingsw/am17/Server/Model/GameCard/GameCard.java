package it.polimi.ingsw.am17.Server.Model.GameCard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameState;

import java.io.Serializable;

public class GameCard implements Serializable {
    private final boolean isBuilding;
    private final GameState era;

    public boolean getIsBuilding() {
        return isBuilding;
    }
    public GameState getEra() {
        return era;
    }

    @JsonCreator
    public GameCard(
            @JsonProperty("isBuilding") boolean isBuilding,
            @JsonProperty("era") GameState era) {
        this.isBuilding = isBuilding;
        this.era = era;
    }
}
package it.polimi.ingsw.am17.Server.Model.GameCard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.UUID;

public class GameCard implements Serializable {
    private final boolean isBuilding;
    private final Integer era;
    private final UUID id;

    public boolean getIsBuilding() {
        return isBuilding;
    }
    public Integer getEra() {
        return era;
    }

    @JsonCreator
    public GameCard(
            @JsonProperty("isBuilding") boolean isBuilding,
            @JsonProperty("era") Integer era,
            @JsonProperty("id") UUID id) {
        this.isBuilding = isBuilding;
        this.era = era;

        // If id is null generate a new one, otherwise use id. TODO: make more robust
        this.id = (id == null) ? UUID.randomUUID() : id;
    }

    public UUID getId() {
        return id;
    }
}
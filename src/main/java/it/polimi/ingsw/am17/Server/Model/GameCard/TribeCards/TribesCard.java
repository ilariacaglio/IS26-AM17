package it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.UUID;

public abstract class TribesCard implements Serializable {
    private final CardType cardType;
    private final Integer era;
    private final UUID id;

    public CardType getCardType() {
        return cardType;
    }
    public int getEra() {
        return era;
    }

    @JsonCreator
    public TribesCard(@JsonProperty("era") Integer era, @JsonProperty("cardType") CardType cardType, @JsonProperty("id") UUID id){
        this.cardType=cardType;
        this.era=era;
        //If id is null generate a new one, otherwise use id.
        this.id = (id == null) ? UUID.randomUUID() : id;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String toString() {
        return cardType.toString();
    }

    public abstract String getImagePath();
}

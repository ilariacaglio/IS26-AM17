package it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.UUID;

public class TribesCard implements Serializable {
    private final CardType cardType;
    private final int era;
    private final UUID id;

    public CardType getCardType() {
        return cardType;
    }
    public int getEra() {
        return era;
    }

    @JsonCreator
    public TribesCard(@JsonProperty("era") int era, @JsonProperty("cardType") CardType cardType){
        this.cardType=cardType;
        this.era=era;
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String toString() {
        return cardType.toString();
    }
}

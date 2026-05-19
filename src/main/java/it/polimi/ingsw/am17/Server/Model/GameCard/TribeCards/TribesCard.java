package it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.GameCard;

import java.io.Serializable;
import java.util.UUID;

public class TribesCard extends GameCard implements Serializable {
    private final CardType cardType;

    public CardType getCardType() {
        return cardType;
    }

    @JsonCreator
    public TribesCard(@JsonProperty("era") Integer era, @JsonProperty("cardType") CardType cardType, @JsonProperty("id") UUID id){
        super(false, era, id);
        this.cardType=cardType;
    }

    @Override
    public String toString() {
        return "[" + getDetails() +"] ";
    }

    protected String getDetails(){
        return cardType.toString();
    }
}

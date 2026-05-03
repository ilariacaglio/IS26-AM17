package it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;

import java.util.Objects;

public class CharacterCard extends TribesCard {
    private int minPlayer;

    public int getMinPlayer() {
        return minPlayer;
    }

    @JsonCreator
    public CharacterCard(
            @JsonProperty("era") int era,
            @JsonProperty("minPlayer") int minPlayer,
            @JsonProperty("cardType") CardType cardType) {
        super(era,  cardType);
        this.minPlayer = minPlayer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharacterCard that = (CharacterCard) o;
        if(!this.getId().equals(that.getId())) return false;
        return minPlayer == that.minPlayer;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(minPlayer);
    }
}

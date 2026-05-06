package it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;

import java.util.Objects;
import java.util.UUID;

public class CharacterCard extends TribesCard {
    private Integer minPlayers;

    public Integer getMinPlayers() {
        return minPlayers;
    }

    @JsonCreator
    public CharacterCard(
            @JsonProperty("era") Integer era,
            @JsonProperty("minPlayers") Integer minPlayers,
            @JsonProperty("cardType") CardType cardType,
            @JsonProperty("id") UUID id) {
        super(era,  cardType, id);
        this.minPlayers = minPlayers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharacterCard that = (CharacterCard) o;
        if(!this.getId().equals(that.getId())) return false;
        return minPlayers.equals(that.minPlayers);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(minPlayers);
    }
}

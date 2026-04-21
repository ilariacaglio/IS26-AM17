package it.polimi.ingsw.am17.Model.GameCard;

import java.util.Objects;

public class CharacterCard extends TribesCard {
    private int minPlayer;

    public int getMinPlayer() {
        return minPlayer;
    }

    public CharacterCard(int era, int minPlayer, CardType cardType) {
        super(era,  cardType);
        this.minPlayer = minPlayer;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CharacterCard that)) return false;
        return minPlayer == that.minPlayer;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(minPlayer);
    }
}

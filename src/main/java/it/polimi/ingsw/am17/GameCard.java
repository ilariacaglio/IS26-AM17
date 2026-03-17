package it.polimi.ingsw.am17;

import java.util.Objects;

public class GameCard {
    private int era;



    public GameCard(int era) {
        this.era = era;


    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GameCard gameCard = (GameCard) o;
        return era == gameCard.era;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(era);
    }
}

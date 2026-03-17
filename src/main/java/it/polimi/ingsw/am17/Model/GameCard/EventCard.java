package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.Player;

import java.util.List;

public class EventCard extends TribesCard{
    private boolean Final;

    public EventCard(boolean Final, int era) {
        this.Final = Final;
        super(era);
    }

    public void computeScore(List<Player> list){}

}

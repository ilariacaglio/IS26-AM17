package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.Player;

import java.util.List;

public class EventCard extends TribesCard{
    private boolean Final;

    public EventCard(boolean Final, int era,CardType cardType) {
        this.Final = Final;
        super(era,cardType);
    }

    public void computeScore(List<Player> list){}

}

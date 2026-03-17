package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.Player;

import java.util.List;

public class HuntingEvent extends EventCard {
    private int pointEarned;

    public int getPointEarned() {
        return pointEarned;
    }

    public HuntingEvent(int pointEarned, boolean Final, int era){
        this.pointEarned = pointEarned;
        super(Final, era);
    }

    @Override
    public void computeScore(List<Player> list){}
}

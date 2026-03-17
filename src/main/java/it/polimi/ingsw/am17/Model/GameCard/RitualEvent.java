package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.Player;

import java.util.List;

public class RitualEvent extends EventCard {
    private int pointMax;
    private int pointMin;

    public int getPointMax() {
        return pointMax;
    }

    public int getPointMin() {
        return pointMin;
    }

    public RitualEvent(boolean Final, int era, int pointMax, int pointMin){
        super(Final, era);
        this.pointMax = pointMax;
        this.pointMin = pointMin;
    }
    @Override
    public void computeScore(List<Player> list){}
}

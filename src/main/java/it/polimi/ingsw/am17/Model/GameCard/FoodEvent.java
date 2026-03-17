package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.Player;

import java.util.List;

public class FoodEvent extends EventCard{
    private int pointLost;

    public int getPointLost() {
        return pointLost;
    }

    public FoodEvent(int pointLost, boolean Final, int era){
        super(Final, era);
        this.pointLost = pointLost;
    }

    @Override
    public void computeScore(List<Player> list){}
}

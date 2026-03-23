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
    public void computeScore(List<Player> list){
        for(int i=0; i<list.size(); i++){
            long numHunter = list.get(i).getPlayerCards().stream()
                    .filter(c -> c instanceof Hunter)
                    .count();

            if(numHunter!=0){
                int gainFood = Math.toIntExact(numHunter);
                int gainPp = Math.toIntExact(pointEarned * numHunter);

                list.get(i).addFood(gainFood);
                list.get(i).addPp(gainPp);

            }


        }
    }
}

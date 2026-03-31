package it.polimi.ingsw.am17.Model.GameCard;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Model.Player;

import java.util.List;

public class RitualEvent extends EventCard {
    private final int pointMax;
    private final int pointMin;

    public int getPointMax() {
        return pointMax;
    }

    public int getPointMin() {
        return pointMin;
    }

    public RitualEvent(@JsonProperty("Final") boolean Final,
                       @JsonProperty("era") int era,
                       @JsonProperty("pointMax") int pointMax,
                       @JsonProperty("pointMin") int pointMin){
        super(Final, era, CardType.RITUAL_EVENT);
        this.pointMax = pointMax;
        this.pointMin = pointMin;
    }
    @Override
    public void computeScore(List<Player> list){
        int[] stars = new int[list.size()];

        for(int i = 0; i < list.size(); i++){
            stars[i] = list.get(i).getPlayerCards().stream()
                    .filter(c -> c instanceof Shaman)
                    .mapToInt(c -> ((Shaman)c).getStars())
                    .sum();
        }

        int max = stars[0];
        int min = stars[0];

        for(int i = 1; i < stars.length; i++){
            if(stars[i] > max){ max = stars[i]; }
            if(stars[i] < min){ min = stars[i]; }
        }

        boolean allEqual = (max == min);

        for(int i = 0; i < list.size(); i++){
            if(allEqual){
                list.get(i).addPp(pointMax);
                list.get(i).addPp(pointMin*(-1));
            }
            else{
                if(stars[i] == max){
                    list.get(i).addPp(pointMax);
                }
                if(stars[i] == min){
                    list.get(i).addPp(pointMin*(-1));
                }
            }
        }

    }
}

package it.polimi.ingsw.am17.Model.GameCard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Model.Player;

import java.util.List;
import java.util.Objects;

public class RitualEvent extends EventCard {
    private final int pointMax;
    private final int pointMin;

    public int getPointMax() {
        return pointMax;
    }

    public int getPointMin() {
        return pointMin;
    }

    @JsonCreator
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
        //array for counting stars of each player
        int[] stars = new int[list.size()];
        //counting stars icon for each player
        for(int i=0; i<list.size(); i++){
            stars[i] = list.get(i).calculateStarPoints();
        }

        int max = stars[0];
        int min = stars[0];
        //find players who have max and min number of stars
        for(int i = 1; i < stars.length; i++){
            if(stars[i] > max){ max = stars[i]; }
            if(stars[i] < min){ min = stars[i]; }
        }
        //if max==min, it means that all players have the same stars number
        boolean allEqual = (max == min);

        //add Pp based on the number of the stars for each player
        for(int i = 0; i < list.size(); i++){

            boolean doublePoints= list.get(i).hasDoubleRitualEventPoints();
            boolean shield = list.get(i).hasShieldFromRitualEvent();

            //give or take Pp
            if(allEqual){//give and then take Pp for each player
                list.get(i).addPp(pointMax);
                list.get(i).addPp(pointMin*(-1));
                if(doublePoints){//if player has BuildingType8
                    list.get(i).addPp(pointMax);
                }
            }
            else{
                if(stars[i] == max){
                    list.get(i).addPp(pointMax);
                    if(doublePoints){//if player has BuildingType8
                        list.get(i).addPp(pointMax);
                    }
                }
                if(stars[i] == min){
                    if(shield){//protected from losing Pp by BuildingType12
                        list.get(i).addPp(0);
                    }
                    else {
                        list.get(i).addPp(pointMin * (-1));
                    }
                }
            }
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RitualEvent that = (RitualEvent) o;
        return pointMax == that.pointMax && pointMin == that.pointMin;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointMax, pointMin);
    }
}

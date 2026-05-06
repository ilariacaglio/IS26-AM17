package it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.Player;

import java.util.Objects;
import java.util.Queue;
import java.util.UUID;

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
    public RitualEvent(@JsonProperty("Final") Boolean Final,
                       @JsonProperty("era") Integer era,
                       @JsonProperty("pointMax") Integer pointMax,
                       @JsonProperty("pointMin") Integer pointMin,
                       @JsonProperty("id") UUID id){
        super(Final, era, CardType.RITUAL_EVENT, id);
        this.pointMax = pointMax;
        this.pointMin = pointMin;
    }
    @Override
    public void computeScore(Queue<Player> list){
        //array for counting stars of each player
        int[] stars = new int[list.size()];
        //counting stars icon for each player
        int index = 0;
        for (Player player : list) {
            stars[index] = player.calculateStarPoints();
            index++;
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
        int j = 0;
        for (Player player : list) {
            boolean doublePoints= player.hasDoubleRitualEventPoints();
            boolean shield = player.hasShieldFromRitualEvent();
            //give or take Pp
            if(allEqual){//give and then take Pp for each player
                player.addPp(pointMax);
                player.addPp(pointMin*(-1));
            }
            else{
                if(stars[j] == max){
                    player.addPp(pointMax);
                    if(doublePoints){//if player has BuildingType8
                        player.addPp(pointMax);
                    }
                }
                if(stars[j] == min){
                    if(shield){//protected from losing Pp by BuildingType12
                        player.addPp(0);
                    }
                    else {
                        player.addPp(pointMin * (-1));
                    }
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RitualEvent that = (RitualEvent) o;
        return pointMax == that.pointMax && pointMin == that.pointMin;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointMax, pointMin);
    }
}

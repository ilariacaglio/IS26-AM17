package it.polimi.ingsw.am17.Model.GameCard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Model.Player;

import java.util.List;

public class HuntingEvent extends EventCard {
    private final int pointEarned;

    public int getPointEarned() {
        return pointEarned;
    }

    @JsonCreator
    public HuntingEvent(
            @JsonProperty("pointEarned") int pointEarned,
            @JsonProperty("Final") boolean Final,
            @JsonProperty("era") int era){
        this.pointEarned = pointEarned;
        super(Final, era, CardType.HUNTING_EVENT);
    }

    @Override
    public void computeScore(List<Player> list){
        for(Player player: list){
           //count number of hunter
           long numHunter = player.getPlayerTribeCards().stream()
                   .filter(c -> c.getCardType().equals(CardType.HUNTER))
                    .count();
           //if player has hunter cards, they get food and PP
           if(numHunter!=0){
                int gainFood = Math.toIntExact(numHunter);
                int gainPp = Math.toIntExact(pointEarned * numHunter);

                player.addFood(gainFood);
                player.addPp(gainPp);

           }

           int additionalFood=0;
           int additionalPp=0;

           List<CharacterCard> characterList = player.getPlayerTribeCards().stream()
                    .map(c-> (CharacterCard)c)
                    .toList();
           //find additional food and PP given by buildingCard
           for(BuildingCard c: player.getPlayerBuildingCards()){
                additionalFood = additionalFood + c.FoodBonus(characterList);
                additionalPp = additionalPp + c.PointsBonus(characterList);
           }
           //add additionalFood and additionalPp
           player.addFood(additionalFood);
           player.addPp(additionalPp);

        }
    }
}

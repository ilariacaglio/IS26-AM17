package it.polimi.ingsw.am17.Model.GameCard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Model.Player;

import java.util.List;

public class FoodEvent extends EventCard{
    private final int pointLost;

    public int getPointLost() {
        return pointLost;
    }

    @JsonCreator
    public FoodEvent(
           @JsonProperty("pointLost") int pointLost,
           @JsonProperty("Final") boolean Final,
           @JsonProperty("era") int era){
        super(Final, era, CardType.FOOD_EVENT);
        this.pointLost = pointLost;
    }

    @Override
    public void computeScore(List<Player> list){
        for (Player player : list) {
            int food = player.getFood();
            //count number of Binder
            long numBinder = player.getPlayerTribeCards().stream()
                    .filter(c -> c.getCardType().equals(CardType.BINDER))
                    .count();
            //count foodDiscount given by BuildingCard
            int foodDiscount =0;

            List<CharacterCard> characterList = player.getPlayerTribeCards().stream()
                    .map(c-> (CharacterCard)c)
                    .toList();

            for(BuildingCard c: player.getPlayerBuildingCards()){
                foodDiscount= foodDiscount + c.FoodDiscount(characterList);
            }
            //count totalDiscount given by numBinder and foodDiscount
            int totalDiscount = Math.toIntExact((3*numBinder) + foodDiscount);
            //count totalCards, witch are all the player cards
            int totalCards = player.getPlayerTribeCards().size();
            //find foodPrice, witch is what the player has to pay
            int foodPrice = totalCards - totalDiscount;
            //if foodPrice<0, the player doesn't lose pp nor food
            if(foodPrice<=0){
                player.addPp(0);
                player.addFood(0);
            }//if food is not enough, player loses pp and all the food he has
            else if ((food < foodPrice) && (foodPrice>0)) {
                int remaining = foodPrice - food;

                int lostPp = pointLost * remaining;

                player.addPp(lostPp * (-1));
                player.addFood(food * (-1));
            } //if food is enough
            else {
                player.addFood(foodPrice * (-1));
            }

        }
    }
}

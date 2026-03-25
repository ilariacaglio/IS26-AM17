package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.Player;

import java.util.List;

public class FoodEvent extends EventCard{
    private int pointLost;

    public int getPointLost() {
        return pointLost;
    }

    public FoodEvent(int pointLost, boolean Final, int era,CardType cardType){
        super(Final, era,cardType);
        this.pointLost = pointLost;
    }

    @Override
    public void computeScore(List<Player> list){
        for(int i=0; i<list.size(); i++){
            int size = list.get(i).getPlayerCards().size();
            int food = list.get(i).getFood();
            int foodPrice = size;

            long numBinder = list.get(i).getPlayerCards().stream()
                    .filter(c -> c instanceof Binder)
                    .count();

            if(numBinder!=0){
                foodPrice = Math.toIntExact((foodPrice - (3 * numBinder)));
            }

            if(food<foodPrice){
                int remaining = foodPrice - food;

                int lostPp= pointLost*remaining;

                list.get(i).addPp(lostPp*(-1));
                list.get(i).addFood(food*(-1));
            }

            else{
                list.get(i).addFood(foodPrice*(-1));
            }

        }
    }
}

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
        for (Player player : list) {
            int size = player.getPlayerTribeCards().size();
            int food = player.getFood();
            int foodPrice = size;

            long numBinder = player.getPlayerTribeCards().stream()
                    .filter(c -> c instanceof Binder)
                    .count();

            if (numBinder != 0) {
                foodPrice = Math.toIntExact((foodPrice - (3 * numBinder)));
            }

            // TODO: get buildings, check buildings

            if (food < foodPrice) {
                int remaining = foodPrice - food;

                int lostPp = pointLost * remaining;

                player.addPp(lostPp * (-1));
                player.addFood(food * (-1));
            } else {
                player.addFood(foodPrice * (-1));
            }

        }
    }
}

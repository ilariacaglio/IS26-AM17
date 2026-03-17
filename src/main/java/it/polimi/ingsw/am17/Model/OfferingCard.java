package it.polimi.ingsw.am17.Model;

public class OfferingCard implements Subject {
    private int minPlayers;
    private char orderLetter;
    private int foodBonus;
    private int numCardsUpper;
    private int numCardsLower;

    public OfferingCard(int minPlayers, char orderLetter, int foodBonus, int numCardsUpper, int numCardsLower) {
        this.minPlayers = minPlayers;
        this.orderLetter = orderLetter;
        this.foodBonus = foodBonus;
        this.numCardsUpper = numCardsUpper;
        this.numCardsLower = numCardsLower;
    }

    public char getOrderLetter() {
        return orderLetter;
    }

    public int getFoodBonus() {
        return foodBonus;
    }

    public int getNumCardsLower() {
        return numCardsLower;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getNumCardsUpper() {
        return numCardsUpper;
    }

    @Override
    public void attach(Observer observer) {

    }

    @Override
    public void detach(Observer observer) {

    }

    @Override
    public void notifyObserver() {

    }
}

package it.polimi.ingsw.am17.Model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OfferingCard implements Subject {
    private final int minPlayers;
    private final char orderLetter;
    private final int foodBonus;
    private final int numCardsUpper;
    private final int numCardsLower;

    @JsonCreator
    public OfferingCard(@JsonProperty("minPlayers") int minPlayers,
    @JsonProperty("orderLetter") char orderLetter,
    @JsonProperty("foodBonus") int foodBonus,
    @JsonProperty("numCardsUpper") int numCardsUpper,
    @JsonProperty("numCardsLower") int numCardsLower) {
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

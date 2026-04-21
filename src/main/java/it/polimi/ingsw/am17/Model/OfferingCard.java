package it.polimi.ingsw.am17.Model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Model.GameCard.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class OfferingCard {
    private final int minPlayers;
    private final char orderLetter;
    private final int foodBonus;
    private final int numCardsUpper;
    private final int numCardsLower;
    private Player player;

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

    public void setPlayer(Player player) {
        if(player == null){
            // remove player from offering card
            this.player=null;
        }
        else if (this.player != null) {
            throw new IllegalStateException("Offering card already assigned to a player");
        }
        else {
            this.player = player;
        }
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

    public Player getPlayer() {
        return player;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OfferingCard that = (OfferingCard) o;
        return minPlayers == that.minPlayers && orderLetter == that.orderLetter && foodBonus == that.foodBonus && numCardsUpper == that.numCardsUpper && numCardsLower == that.numCardsLower && Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minPlayers, orderLetter, foodBonus, numCardsUpper, numCardsLower, player);
    }
}

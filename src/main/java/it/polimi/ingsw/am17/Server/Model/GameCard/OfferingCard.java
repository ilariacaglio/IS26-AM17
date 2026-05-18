package it.polimi.ingsw.am17.Server.Model.GameCard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Server.Model.Player;
import tools.jackson.databind.node.StringNode;

import java.io.Serializable;
import java.util.Objects;

public class OfferingCard implements Serializable {
    private final Integer minPlayers;
    private final Character orderLetter;
    private final Integer foodBonus;
    private final Integer numCardsUpper;
    private final Integer numCardsLower;
    private Player player;

    @JsonCreator
    public OfferingCard(@JsonProperty("minPlayers") Integer minPlayers,
    @JsonProperty("orderLetter") Character orderLetter,
    @JsonProperty("foodBonus") Integer foodBonus,
    @JsonProperty("numCardsUpper") Integer numCardsUpper,
    @JsonProperty("numCardsLower") Integer numCardsLower) {
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

    public Character getOrderLetter() {
        return orderLetter;
    }

    public Integer getFoodBonus() {
        return foodBonus;
    }

    public Integer getNumCardsLower() {
        return numCardsLower;
    }

    public Integer getMinPlayers() {
        return minPlayers;
    }

    public Integer getNumCardsUpper() {
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
        return minPlayers.equals(that.minPlayers) && orderLetter.equals(that.orderLetter) && foodBonus.equals(that.foodBonus) && numCardsUpper.equals(that.numCardsUpper)  && numCardsLower.equals(that.numCardsLower);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minPlayers, orderLetter, foodBonus, numCardsUpper, numCardsLower);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append("[").append(orderLetter).append(" ");
        if (numCardsUpper > 0) sb.append(numCardsUpper).append("↑ ");
        if (numCardsLower > 0) sb.append(numCardsLower).append("↓ ");
        if (foodBonus > 0) sb.append(foodBonus).append("F ");
        sb.append("(").append(player != null ? player.getNickname() : " ").append(")").append("] ");

        return sb.toString();
    }

    public String getImagePath()
    {
        return "/Images/OfferingCard/offeringCard_"+ orderLetter +".png";
    }

    public String getImagePath()
    {
        return "/Images/OfferingCard/offeringCard_"+ orderLetter +".png";
    }
}

package it.polimi.ingsw.am17.Model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Model.GameCard.*;

import java.util.Collections;
import java.util.List;

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
        if (this.player != null) {
            throw new IllegalStateException("Offering card already assigned to a player");
        }
        else {
            this.player = player;
        }
    }

    private void hunterFoodGain() {
        int numHunters = (int) player.getPlayerTribeCards().stream()
                .filter(c->c.getCardType().equals(CardType.HUNTER))
                .count();
        player.addFood(numHunters);
    }

    /**
     * @param tribeCards expected to be null or empty if no tribe card is picked
     * @param buildingCards expected to be null or empty if no building card is picked
     * @param game needed to remove the picked cards from the deck
     */
    public void playTurn(List<TribesCard> tribeCards, List<BuildingCard> buildingCards, Game game){
        int numHunters;

        // Character cards
        if(tribeCards != null && !tribeCards.equals(Collections.emptyList())){
            for(TribesCard card : tribeCards){
                player.addCharacter(card);
                game.removeTribeCardFromRow(card);

                // Hunter with icon triggers food gain
                if (card.getCardType().equals(CardType.HUNTER) && ((Hunter)card).isWithIcon()) hunterFoodGain();
            }
        }

        // Building cards
        if(buildingCards != null && !buildingCards.equals(Collections.emptyList())){
            for(BuildingCard card : buildingCards){
                int cost = calculateBuildingCost(card);
                player.addFood(-cost);
                player.addBuilding(card);
                game.removeBuildingCardFromRow(card);
            }
        }
    }

    public int calculateBuildingCost(BuildingCard card){
        //sum of the food discount of every builder card
        int foodDiscount = player.getPlayerTribeCards().stream()
                .filter(c->c.getCardType().equals(CardType.BUILDER))
                .map(c-> ((Builder)c).getFoodReduction())
                .reduce(0, Integer::sum);
        //return the price of the building card
        return card.getFoodCost()-foodDiscount;
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
}

package it.polimi.ingsw.am17.Model.GameCard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am17.Model.Player;

import java.util.List;

public class PaintingEvent extends EventCard {
    private final int pointsLow;
    private final int pointsMax;
    private final int numLow;
    private final int numMax;

    public int getNumLow() {
        return numLow;
    }

    public int getNumMax() {
        return numMax;
    }

    public int getPointsLow() {
        return pointsLow;
    }

    public int getPointsMax() {
        return pointsMax;
    }

    @JsonCreator
    public PaintingEvent(
            @JsonProperty("pointsLow") int pointsLow,
            @JsonProperty("pointsMax") int pointsMax,
            @JsonProperty("numLow") int numLow,
            @JsonProperty("numMax") int numMax,
            @JsonProperty("Final") boolean Final,
            @JsonProperty("era") int era) {
        super(Final, era, CardType.PAINTING_EVENT);
        this.pointsLow = pointsLow;
        this.pointsMax = pointsMax;
        this.numLow = numLow;
        this.numMax = numMax;
    }

    @Override
    public void computeScore(List<Player> list){
        for (Player player : list){
            //count number of artists
            long numArtist = player.getPlayerTribeCards().stream()
                    .filter(c-> c.getCardType().equals(CardType.ARTIST))
                    .count();
            //assign PP based on number of artists
            if(numArtist>=numMax){
                int pointsGain = Math.toIntExact(numArtist * pointsMax);
                player.addPp(pointsGain);
            }
            else {
                player.addPp(pointsLow*(-1));
            }

            int additionalFood=0;

            List<CharacterCard> characterList = player.getPlayerTribeCards().stream()
                    .map(c-> (CharacterCard)c)
                    .toList();
            //find additional food given by buildingCard
            for(BuildingCard c: player.getPlayerBuildingCards()){
                additionalFood = additionalFood + c.FoodBonusFromArtists(characterList);
            }
            //add additionalFood
            player.addFood(additionalFood);
        }
        }
    }


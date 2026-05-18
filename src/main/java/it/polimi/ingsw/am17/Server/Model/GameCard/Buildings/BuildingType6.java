package it.polimi.ingsw.am17.Server.Model.GameCard.Buildings;

import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.Builder;

import java.util.List;

/**
 * Get double the points from Builders
 * Final effect
 * SINGLETON
 */
public class BuildingType6 extends BuildingCard {
    private static final int era = 2;
    private static final int foodCost = 5;
    private static final int bonusPoints = 4;
    public BuildingType6() {
        super(era, foodCost, bonusPoints);
    }

    @Override
    public int GetAdditionalFinalPoints(List<CharacterCard> characterCards) {
        // get builders with stream magic
        List<Builder> builders = characterCards.stream()
                .filter(card -> card.getCardType() == CardType.BUILDER)
                .map(card -> (Builder) card)
                .toList();

        // return the sum of bonus points ONCE (as they should be counted once already)
        return builders.stream().mapToInt(Builder::getPointBonus).sum();
    }

    @Override
    public String toString() {
        return super.toString() + " Effect: x2PP from Builders] ";
    }

    @Override
    public String getImagePath()
    {
        return "/Images/Buildings/building2_6.png";
    }

    @Override
    public String getImagePath()
    {
        return "/Images/Buildings/building2_6.png";
    }
}

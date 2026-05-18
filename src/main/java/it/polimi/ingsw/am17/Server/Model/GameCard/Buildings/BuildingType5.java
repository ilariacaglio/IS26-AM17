package it.polimi.ingsw.am17.Server.Model.GameCard.Buildings;

import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import static it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType.ARTIST;

import java.util.List;

/**
 * Get one food during PaintingEvent for each artist
 * Event effect
 * SINGLETON
 */
public class BuildingType5 extends BuildingCard {
    private static final int era = 2;
    private static final int foodCost = 5;
    private static final int bonusPoints = 6;
    public BuildingType5() {
        super(era, foodCost, bonusPoints);
    }

    @Override
    public int AddFoodPerArtistInPaintingEvent(List<CharacterCard> playerCharacterCards) {
        return (int) playerCharacterCards.stream().filter(card -> card.getCardType() == ARTIST).count();
    }

    @Override
    public String toString() {
        return super.toString() + " Effect: +1PP/artist in PaintingEvent] ";
    }

    @Override
    public String getImagePath()
    {
        return "/Images/Buildings/building2_5.png";
    }

}

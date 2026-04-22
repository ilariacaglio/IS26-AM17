package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingType5;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BuildingType5Test extends BuildingCardTest{
    BuildingType5 building;
    Random r = new Random();
    @BeforeEach
    /**
     * Generate a BuildingType5
     */
    void setup()
    {
        building = new BuildingType5();
    }
    @Test
    /**
     * Run test of FoodBonus with different list of card generated randomly
     * and check the return value is correct
     */
    void FoodBonusTest()
    {
        for (int i = 0; i < 5; i++) {
            int numHunter = r.nextInt(5);
            int numBuilder = r.nextInt(5);
            int numArtist = r.nextInt(5);
            int numInv = r.nextInt(5);
            int numSham = r.nextInt(5);
            int numBind = r.nextInt(5);
            List<CharacterCard> cc = createCharacterCardList(numHunter, numArtist, numBuilder, numInv, numSham, numBind);
            int fp = building.AddFoodPerHunterInPaintingEvent(cc);

            assertEquals(numArtist, fp);
        }


    }
}
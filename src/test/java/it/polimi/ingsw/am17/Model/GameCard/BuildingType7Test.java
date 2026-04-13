package it.polimi.ingsw.am17.Model.GameCard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BuildingType7Test extends BuildingCardTest{
    BuildingType7 building;
    Random r = new Random();
    @BeforeEach
    /**
     * Generate a BuildingType7
     */
    void setup()
    {
        building = new BuildingType7();
    }

    @Test
    /**
     * Run test of FoodBonus with different list of card generated randomly
     * and check the return value is correct
     */
    void FoodBonusAndPointsBonusTest()
    {
        for (int i = 0; i < 5; i++) {
            int numHunter = r.nextInt(5);
            int numBuilder = r.nextInt(5);
            int numArtist = r.nextInt(5);
            int numInv = r.nextInt(5);
            int numSham = r.nextInt(5);
            int numBind = r.nextInt(5);
            List<CharacterCard> cc = createCharacterCardList(numHunter, numArtist, numBuilder, numInv, numSham, numBind);
            int fp = building.AddPointPerHunterInHuntingEvent(cc);
            int fb = building.AddFoodPerHunterInHuntingEvent(cc);

            assertEquals(numHunter, fp);
            assertEquals(numHunter, fb);
        }


    }
}
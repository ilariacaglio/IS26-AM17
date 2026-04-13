package it.polimi.ingsw.am17.Model.GameCard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BuildingType14Test extends BuildingCardTest{
    BuildingType14 building;
    Random r = new Random();
    @BeforeEach
    /**
     * Generate a BuildingType6 with random value and stores the value of pointsFromEachCharacter
     */
    void setup()
    {
        building = new BuildingType14();
    }
    @Test
    /**
     * Run test of FinalPoints with different list of card generated randomly
     * and check the return value is correct
     */
    void TestGetAdditionalFinalPoints()
    {
        for (int i = 0; i < 5; i++) {
            int numHunter = r.nextInt(2,5);
            int numBuilder = r.nextInt(2,5);
            int numArtist = r.nextInt(2,5);
            int numInv = r.nextInt(2,5);
            List<CharacterCard> ccNever = createCharacterCardList(numHunter, numArtist, numBuilder, numInv, 0, 0);

            int fpNever = building.GetFoodBonusFromCardAcquisition(ccNever, new Shaman(0,0,0));

            int numSham = r.nextInt(2,5);
            int numBind = 1;
            List<CharacterCard> cc = createCharacterCardList(numHunter, numArtist, numBuilder, numInv, numSham, numBind);
            int fpLow = building.GetFoodBonusFromCardAcquisition(cc, new Shaman(0,0,0));
            int fpHigh = building.GetFoodBonusFromCardAcquisition(cc, new Binder(0,0));



            assertEquals(0,fpNever);
            assertEquals(0,fpLow);
            assertEquals(5,fpHigh);
        }


    }

}
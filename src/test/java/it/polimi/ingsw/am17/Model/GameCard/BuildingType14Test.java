package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.Binder;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingType14;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.Shaman;
import it.polimi.ingsw.am17.Server.Model.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BuildingType14Test extends BuildingCardTest{
    BuildingType14 building;
    Random r = new Random();

    /**
     * Generate a BuildingType6 with random value and stores the value of pointsFromEachCharacter
     */
    @BeforeEach
    void setup()
    {
        building = new BuildingType14();
    }

    /**
     * Run test of FinalPoints with different list of card generated randomly
     * and check the return value is correct
     */
    @Test
    void TestGetAdditionalFinalPoints()
    {
        for (int i = 0; i < 5; i++) {
            int numHunter = r.nextInt(2,5);
            int numBuilder = r.nextInt(2,5);
            int numArtist = r.nextInt(2,5);
            int numInv = r.nextInt(2,5);
            List<CharacterCard> ccNever = createCharacterCardList(numHunter, numArtist, numBuilder, numInv, 0, 0);

            int fpNever = building.GetFoodBonusFromCardAcquisition(ccNever, new Shaman(GameState.NONE,0,0, null));

            int numSham = r.nextInt(2,5);
            int numBind = 1;
            List<CharacterCard> cc = createCharacterCardList(numHunter, numArtist, numBuilder, numInv, numSham, numBind);
            int fpLow = building.GetFoodBonusFromCardAcquisition(cc, new Shaman(GameState.NONE,0,0, null));
            int fpHigh = building.GetFoodBonusFromCardAcquisition(cc, new Binder(GameState.NONE,0, null));



            assertEquals(0,fpNever);
            assertEquals(0,fpLow);
            assertEquals(5,fpHigh);
        }


    }

}
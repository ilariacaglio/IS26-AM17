package it.polimi.ingsw.am17.Model.GameCard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static it.polimi.ingsw.am17.Model.GameCard.CardType.ARTIST;
import static org.junit.jupiter.api.Assertions.*;

class BuildingType5Test extends BuildingCardTest{
    BuildingType5 building;
    Random r = new Random();
    @BeforeEach
    /**
     * Generate a BuildingType5 with random value and stores the value of pointsFromEachCharacter
     */
    void setup()
    {
        building = new BuildingType5();
    }
    @Test
    /**
     * Run test of FinalPoints with different list of card generated randomly
     * and check the return value is correct
     */
    void FinalPointTest()
    {
        for (int i = 0; i < 5; i++) {
            int numHunter = r.nextInt(5);
            int numBuilder = r.nextInt(5);
            int numArtist = r.nextInt(5);
            int numInv = r.nextInt(5);
            int numSham = r.nextInt(5);
            int numBind = r.nextInt(5);
            List<CharacterCard> cc = createCharacterCardList(numHunter, numArtist, numBuilder, numInv, numSham, numBind);
            int fp = building.FoodBonus(cc);

            assertEquals(numArtist, fp);
        }


    }
}
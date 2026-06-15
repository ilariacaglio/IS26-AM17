package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingType4;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class BuildingType4Test extends BuildingCardTest {
    BuildingType4 building;
    Random r = new Random();

    /**
     * Generate a BuildingType4 with random value and stores the value of pointsFromEachCharacter
     */
    @BeforeEach
    void setup()
    {
        building = new BuildingType4();
    }

    /**
     * Run test of FinalPoints with different list of card generated randomly
     * and check the return value is correct
     */
    @Test
    void TestGetAdditionalFinalPoints()
    {
        for (int i = 0; i < 20; i++) {
            int numHunter = r.nextInt(5);
            int numBuilder = r.nextInt(5);
            int numArtist = r.nextInt(5);
            int numInv = r.nextInt(5);
            int numSham = r.nextInt(5);
            int numBind = r.nextInt(5);
            List<CharacterCard> cc = createCharacterCardList(numHunter, numArtist, numBuilder, numInv, numSham, numBind);

            int lowest = IntStream.of(numHunter, numBuilder, numArtist, numInv, numSham, numBind)
                    .min()
                    .getAsInt();

            int fp = building.GetAdditionalFinalPoints(cc);

            assertEquals(lowest*6, fp);
        }


    }
}
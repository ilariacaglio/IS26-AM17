package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingType3M;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BuildingType3MTest extends BuildingCardTest {
    BuildingType3M building;
    Random r = new Random();
    int pointsFromEachCharacter;

    /**
     * Generate a BuildingType3M with random value and stores the value of pointsFromEachCharacter
     */
    @BeforeEach
    void setup()
    {
        int food = r.nextInt(5);
        int bonusPoint = r.nextInt(3);
        pointsFromEachCharacter = r.nextInt(5);
        building = new BuildingType3M(food, bonusPoint, CardType.HUNTER,pointsFromEachCharacter);
    }


    /**
     * Run test of FinalPoints with different list of card generated randomly
     * and check the return value is correct
     */
    @Test
    void testFinalPoints()
    {
        for (int i = 0; i < 5; i++) {
            int numHunter = r.nextInt(5);
            int numBuilder = r.nextInt(5);
            int numArtist = r.nextInt(5);
            int numBinder = r.nextInt(5);
            int numShaman = r.nextInt(5);
            int numInventor = r.nextInt(5);
            List<CharacterCard> cc = createCharacterCardList(numHunter, numArtist, numBuilder, numInventor, numShaman, numBinder);
            int fp = building.GetAdditionalFinalPoints(cc);

            assertEquals(numHunter*pointsFromEachCharacter, fp);
        }
    }
}
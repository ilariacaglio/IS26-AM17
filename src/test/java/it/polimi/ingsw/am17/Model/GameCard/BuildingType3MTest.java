package it.polimi.ingsw.am17.Model.GameCard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BuildingType3MTest extends BuildingCardTest {
    BuildingType3M building;
    Random r = new Random();
    int pointsFromEachCharacter;
    @BeforeEach
    /**
     * Generate a BuildingType3M with random value and stores the value of pointsFromEachCharacter
     */
    void setup()
    {
        int food = r.nextInt(5);
        int bonusPoint = r.nextInt(3);
        pointsFromEachCharacter = r.nextInt(5);
        building = new BuildingType3M(food, bonusPoint, CardType.HUNTER,pointsFromEachCharacter);
    }

    @Test
    /**
     * Run test of FinalPoints with different list of card generated randomly
     * and check the return value is correct
     */
    void testFinalPoint()
    {
        for (int i = 0; i < 5; i++) {
            int numHunter = r.nextInt(5);
            int numBuilder = r.nextInt(5);
            int numArtist = r.nextInt(5);
            int numBinder = r.nextInt(5);
            int numShaman = r.nextInt(5);
            int numInventor = r.nextInt(5);
            List<CharacterCard> cc = createCharacterCardList(numHunter, numArtist, numBuilder, numInventor, numShaman, numBinder);
            int fp = building.FinalPoints(cc);

            assertEquals(numHunter*pointsFromEachCharacter, fp);
        }
    }

    /**
     * Generate and return a list of CharacterCard with number of cards as specified
     * @param hunter number of hunter you want to have in the list
     * @param art number of artist you want to have in the list
     * @param build number of builder you want to have in the list
     * @return List<CharacterCard>
     */
    private List<CharacterCard> createCharacterCard(int hunter, int art, int build)
    {
        List<CharacterCard> cc = new ArrayList<>();

        for (int i = 0; i < hunter; i++) {
            cc.add(new CharacterCard(1, 1, CardType.HUNTER));
        }
        for (int i = 0; i < art; i++) {
            cc.add(new CharacterCard(1, 1, CardType.ARTIST));
        }
        for (int i = 0; i < build; i++) {
            cc.add(new CharacterCard(1, 1, CardType.BUILDER));
        }

        return cc;
    }

}
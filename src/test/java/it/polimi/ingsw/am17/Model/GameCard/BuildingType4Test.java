package it.polimi.ingsw.am17.Model.GameCard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class BuildingType4Test {
    BuildingType4 building;
    Random r = new Random();
    @BeforeEach
    /**
     * Generate a BuildingType3M with random value and stores the value of pointsFromEachCharacter
     */
    void setup()
    {
        building = new BuildingType4();
    }

    @Test
    /**
     * Run test of FinalPoints with different list of card generated randomly
     * and check the return value is correct
     */
    void FinalPointTest()
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

            int fp = building.FinalPoints(cc);

            assertEquals(lowest*6, fp);
        }


    }

    /**
     * Generate and return a list of CharacterCard with number of cards as specified
     * @param hunter number of hunter you want to have in the list
     * @param art number of artist you want to have in the list
     * @param build number of builder you want to have in the list
     * @param inv number of inventor you want to have in the list
     * @param sham number of shaman you want to have in the list
     * @param bind number of binder you want to have in the list
     * @return List<CharacterCard>
     */
    public static List<CharacterCard> createCharacterCardList(int hunter, int art, int build, int inv, int sham, int bind)
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
        for (int i = 0; i < inv; i++) {
            cc.add(new CharacterCard(1, 1, CardType.INVENTOR));
        }
        for (int i = 0; i < sham; i++) {
            cc.add(new CharacterCard(1, 1, CardType.SHAMAN));
        }
        for (int i = 0; i < bind; i++) {
            cc.add(new CharacterCard(1, 1, CardType.BINDER));
        }

        return cc;
    }
}
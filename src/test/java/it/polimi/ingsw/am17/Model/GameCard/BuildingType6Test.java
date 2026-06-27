package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.Builder;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingType6;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BuildingType6Test extends BuildingCardTest{
    BuildingType6 building;
    Random r = new Random();

    /**
     * Generate a BuildingType6 with random value and stores the value of pointsFromEachCharacter
     */
    @BeforeEach
    void setup()
    {
        building = new BuildingType6();
    }

    /**
     * Run test of FinalPoints with different list of card generated randomly
     * and check the return value is correct
     */
    @Test
    void TestGetAdditionalFinalPoints()
    {
        for (int i = 0; i < 5; i++) {
            int numHunter = r.nextInt(5);
            int numBuilder = r.nextInt(5);
            int numArtist = r.nextInt(5);
            int numInv = r.nextInt(5);
            int numSham = r.nextInt(5);
            int numBind = r.nextInt(5);
            List<CharacterCard> cc = createCharacterCardList(numHunter, numArtist, numBuilder, numInv, numSham, numBind);
            int fp = building.GetAdditionalFinalPoints(cc);

            int sumPoints = sumOfBuilderPoints(cc);

            assertEquals(sumPoints, fp);
        }


    }

    private int sumOfBuilderPoints(List<CharacterCard> cc)
    {
        int sum=0;
        for(CharacterCard c : cc)
        {
            if(c.getCardType() == CardType.BUILDER)
            {
                sum += ((Builder)c).getPointBonus();
            }
        }
        return sum;
    }
}
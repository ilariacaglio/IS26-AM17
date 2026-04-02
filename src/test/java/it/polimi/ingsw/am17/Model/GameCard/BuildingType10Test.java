package it.polimi.ingsw.am17.Model.GameCard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BuildingType10Test extends BuildingCardTest {
    BuildingType10 building;
    Random r = new Random();
    @BeforeEach
    /**
     * Generate a BuildingType10
     */
    void setup()
    {
        building = new BuildingType10();
    }

    @Test
    /**
     * Run test of FoodBonus with different list of card generated randomly
     * and check the return value is correct
     */
    void FoodBonusTest()
    {
        for (int i = 0; i < 50; i++) {
            int numHunter = r.nextInt(5);
            int numBuilder = r.nextInt(5);
            int numArtist = r.nextInt(5);
            int numInv = r.nextInt(5);
            int numSham = r.nextInt(5);
            int numBind = r.nextInt(5);
            List<CharacterCard> cc = createCharacterCardList(numHunter, numArtist, numBuilder, numInv, numSham, numBind);
            CharacterCard card = new CharacterCard(1, 1, CardType.values()[r.nextInt(6)]);

            int fb = 0;
            //check that since the card is not inventor
            if(card.getCardType() != CardType.INVENTOR)
            {
                //if the card is not inventor check it does not add food
                fb = building.FoodBonus(cc, card);
                assertEquals(0, fb);
            }else
            {
                //if the card is inventor we create a 'real' inventor so we can test
                int icon = r.nextInt(10);
                card = new Inventor(1, 1, InventorIconType.values()[icon]);

                //check whether in the list we have an inventor with same icon
                boolean sameIcon =false;
                for (var c : cc)
                {
                    if(c.getCardType()==CardType.INVENTOR)
                    {
                        if(((Inventor) c).getIcon() == InventorIconType.values()[icon])
                        {
                            sameIcon = true;
                        }
                    }
                }

                fb = building.FoodBonus(cc, card);

                //check the result
                if(sameIcon)
                    assertEquals(3, fb);
                else
                    assertEquals(0, fb);
            }

            //add final test so we are sure at least one test is supposed to add food
            card = new Inventor(1, 1, InventorIconType.values()[r.nextInt(10)]);
            cc.add(card);

            fb = building.FoodBonus(cc, card);
            assertEquals(3, fb);
        }


    }
}
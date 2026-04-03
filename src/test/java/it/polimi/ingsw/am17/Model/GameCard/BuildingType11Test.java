package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Model.Color;
import it.polimi.ingsw.am17.Model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuildingType11Test {
    BuildingType11 buildingType11;

    @BeforeEach
    void setUp()
    {
        buildingType11 = new BuildingType11();
    }

   /* @Test
    void ShouldGiveFoodBonus(){
        Player player = new Player("Test player", Color.BLACK);

        int risultato = buildingType11.FoodBonus(player);
        assertEquals(1,risultato);
    }*/

}
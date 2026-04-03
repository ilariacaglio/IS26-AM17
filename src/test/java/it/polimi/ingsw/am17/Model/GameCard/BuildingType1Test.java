package it.polimi.ingsw.am17.Model.GameCard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuildingType1Test {
    BuildingType1 buildingType1;

    @BeforeEach
    void setUp() {
        buildingType1 = new BuildingType1();
    }

    @Test
    void ShouldGiveFinalPoints() {
        List<CharacterCard> playerCharacterCards = new ArrayList<>();

        int risultato = buildingType1.FinalPoints(playerCharacterCards);

        assertEquals(25,risultato);
    }

}
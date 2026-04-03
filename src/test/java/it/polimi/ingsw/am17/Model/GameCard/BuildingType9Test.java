package it.polimi.ingsw.am17.Model.GameCard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuildingType9Test {
    BuildingType9 buildingType9;

    @BeforeEach
    void setUp() {
        buildingType9 = new BuildingType9();
    }

    @Test
    void ShouldGiveStarBonus() {
        List<CharacterCard> playerCharacterCards = new ArrayList<>();

        int risultato = buildingType9.StarBonus(playerCharacterCards);

        assertEquals(3,risultato);

    }

}
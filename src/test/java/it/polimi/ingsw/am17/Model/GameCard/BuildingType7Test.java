package it.polimi.ingsw.am17.Model.GameCard;

import org.junit.jupiter.api.BeforeEach;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BuildingType7Test {
    BuildingType7 building;
    Random r = new Random();
    @BeforeEach
    /**
     * Generate a BuildingType7
     */
    void setup()
    {
        building = new BuildingType7();
    }
}
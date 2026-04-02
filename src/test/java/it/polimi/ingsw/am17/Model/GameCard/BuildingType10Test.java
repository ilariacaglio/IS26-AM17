package it.polimi.ingsw.am17.Model.GameCard;

import org.junit.jupiter.api.BeforeEach;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BuildingType10Test {
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
}
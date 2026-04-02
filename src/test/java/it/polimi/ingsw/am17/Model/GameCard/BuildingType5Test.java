package it.polimi.ingsw.am17.Model.GameCard;

import org.junit.jupiter.api.BeforeEach;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BuildingType5Test {
    BuildingType5 building;
    Random r = new Random();
    @BeforeEach
    /**
     * Generate a BuildingType5 with random value and stores the value of pointsFromEachCharacter
     */
    void setup()
    {
        building = new BuildingType5();
    }
}
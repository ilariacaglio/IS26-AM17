package it.polimi.ingsw.am17.Model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("TestPlayer", Color.RED);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void initialPPandFoodShouldBeZero() {
        assertEquals(0, player.getPp());
        assertEquals(0, player.getFood());

    }

    @Test
    void addPpShouldAddPp() {
        int ppChange = 5;
        player.addPp(ppChange);
        assertEquals(ppChange, player.getPp());
    }

    @Test
    void addPpShouldRemovePp() {
        int ppChange = -5;
        player.addPp(ppChange);
        assertEquals(ppChange, player.getPp());
    }

    @Test
    void addFoodShouldAddFood() {
        int foodChange = 5;
        player.addFood(foodChange);
        assertEquals(foodChange, player.getFood());
    }

    @Test
    void addFoodShouldRemoveFood() {
        int foodChange = -5;
        player.addFood(foodChange);
        assertEquals(foodChange, player.getFood());
    }

    @Test
    void setOfferingCard() {
    }

    @Test
    void freeOfferingCard() {
    }

    @Test
    void playTurn() {
    }

    @Test
    void buyBuilding() {
    }

    @Test
    void getPlayerTribeCards() {
    }

    @Test
    void getPlayerBuildingCards() {
    }

    @Test
    void calculateFinalPoints() {
    }
}
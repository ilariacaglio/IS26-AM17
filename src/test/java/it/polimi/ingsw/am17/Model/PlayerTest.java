package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.CardType;
import it.polimi.ingsw.am17.Model.GameCard.TribesCard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
    void addFoodShouldChangeFood() {
        int foodChange = 5;
        player.addFood(foodChange);
        assertEquals(foodChange, player.getFood());
        player.addFood(-foodChange);
        assertEquals(0, player.getFood());
    }

    @Test
    void addFoodShouldNotGoUnderZero() {
        assertThrows(IllegalStateException.class, () -> player.addFood(-5));
    }

    @Test
    void playTurnWithOfferingCard() {
        OfferingCard offeringCard = new OfferingCard(2,'A', 1 ,1, 1);
        TribesCard tribeCard1 = new TribesCard(1, CardType.INVENTOR);
        TribesCard tribeCard2 = new TribesCard(1, CardType.PAINTING_EVENT);
        List<TribesCard>  tribeCards = List.of(tribeCard1, tribeCard2);
        Game game = new Game(3);
        game.getUpperRow().addCard(tribeCard1);
        game.getUpperRow().addCard(tribeCard2);
        List<BuildingCard> buildingCards = new ArrayList<>();
        player.playTurn(tribeCards, buildingCards, game);
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
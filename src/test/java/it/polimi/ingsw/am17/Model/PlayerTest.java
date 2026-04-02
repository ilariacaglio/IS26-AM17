package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.*;
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
    void initialPPAndFoodShouldBeZero() {
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
    void testSetAndFreeOfferingCard() {
        OfferingCard offeringCard = new OfferingCard(2, 'A', 1, 1, 1);
        player.setOfferingCard(offeringCard);
        assertEquals(offeringCard, player.getOfferingCard());
        player.freeOfferingCard();
        assertNull(player.getOfferingCard());
    }

    @Test
    void playTurnWithTribeCards() {
        // TODO: might need offeringCard if playTurn implements checks
        Game game = new Game(3);
        TribesCard tribeCard1 = new TribesCard(1, CardType.INVENTOR);
        TribesCard tribeCard2 = new TribesCard(1, CardType.PAINTING_EVENT);
        game.getUpperRow().addCard(tribeCard1);
        game.getUpperRow().addCard(tribeCard2);

        player.playTurn(List.of(tribeCard1, tribeCard2), new ArrayList<>(), game);

        assertTrue(player.getPlayerTribeCards().contains(tribeCard1));
        assertTrue(player.getPlayerTribeCards().contains(tribeCard2));
        assertFalse(game.getUpperRow().getCards().contains(tribeCard1));
        assertFalse(game.getUpperRow().getCards().contains(tribeCard2));
    }

    @Test
    void playTurnWithBuildingCards() {
        // TODO: might need offeringCard if playTurn implements checks
        Game game = new Game(3);
        player.addFood(5);
        BuildingCard buildingCard = new BuildingCard(1, 3, 2);
        game.getUpperBuildingRow().addCard(buildingCard);

        player.playTurn(new ArrayList<>(), List.of(buildingCard), game);

        assertTrue(player.getPlayerBuildingCards().contains(buildingCard));
        assertEquals(2, player.getFood()); // 5 - 3 = 2
        assertFalse(game.getUpperBuildingRow().getCards().contains(buildingCard));
    }

    @Test
    void hunterFoodBonus() {
        // TODO: might need offeringCard if playTurn implements checks
        Game game = new Game(3);

        // First hunter (with icon) -> 0 existing hunters -> +0 food
        Hunter hunter1 = new Hunter(1, 2, true);
        game.getUpperRow().addCard(hunter1);
        player.playTurn(List.of(hunter1), new ArrayList<>(), game);
        assertEquals(0, player.getFood());

        // Second hunter (with icon) -> 1 existing hunter -> +1 food
        Hunter hunter2 = new Hunter(1, 2, true);
        game.getUpperRow().addCard(hunter2);
        player.playTurn(List.of(hunter2), new ArrayList<>(), game);
        assertEquals(1, player.getFood());

        // Third hunter (no icon) -> no extra food
        Hunter hunter3 = new Hunter(1, 2, false);
        game.getUpperRow().addCard(hunter3);
        player.playTurn(List.of(hunter3), new ArrayList<>(), game);
        assertEquals(1, player.getFood());

        // Fourth hunter (with icon) -> 3 existing hunters -> +3 food
        Hunter hunter4 = new Hunter(1, 2, true);
        game.getUpperRow().addCard(hunter4);
        player.playTurn(List.of(hunter4), new ArrayList<>(), game);
        assertEquals(4, player.getFood()); // 1 + 3 = 4
    }

    @Test
    void buildingPurchaseWithBuilderDiscount() {
        // TODO: might need offeringCard if playTurn implements checks
        Game game = new Game(3);

        Builder builder = new Builder(1, 2, 0, 1); // 1 food reduction
        game.getUpperRow().addCard(builder);
        player.playTurn(List.of(builder), new ArrayList<>(), game);

        player.addFood(2);
        BuildingCard buildingCard = new BuildingCard(1, 3, 0);
        player.buyBuilding(buildingCard); // Cost: 3 - 1 = 2

        assertEquals(0, player.getFood());
    }

    @Test
    void calculateFinalPointsTest() {
        // TODO: might need offeringCard if playTurn implements checks
        Game game = new Game(3);

        // Builders PP: 5 + 4 = 9
        Builder builder = new Builder(1, 2, 5, 0);
        Builder builder2 = new Builder(2, 2, 4, 0);
        game.getUpperRow().addCard(builder);
        game.getUpperRow().addCard(builder2);
        player.playTurn(List.of(builder, builder2), new ArrayList<>(), game);

        // Inventors PP: 3 (amount of inventors) * 2 (unique icons) = 6
        Inventor inv1 = new Inventor(1, 2, InventorIconType.FLUTE);
        Inventor inv2 = new Inventor(1, 2, InventorIconType.FLUTE);
        Inventor inv3 = new Inventor(1, 2, InventorIconType.CANOE);
        game.getUpperRow().addCard(inv1);
        game.getUpperRow().addCard(inv2);
        game.getUpperRow().addCard(inv3);
        player.playTurn(List.of(inv1, inv2, inv3), new ArrayList<>(), game);

        // Artists PP: lower(amount/2) * 10 = 20
        Artist art1 = new Artist(1, 2);
        Artist art2 = new Artist(1, 2);
        Artist art3 = new Artist(1, 2);
        Artist art4 = new Artist(1, 2);
        Artist art5 = new Artist(1, 2);
        game.getUpperRow().addCard(art1);
        game.getUpperRow().addCard(art2);
        game.getUpperRow().addCard(art3);
        game.getUpperRow().addCard(art4);
        game.getUpperRow().addCard(art5);
        player.playTurn(List.of(art1, art2, art3, art4, art5), new ArrayList<>(), game);

        // Buildings PP (no FinalPoints effects): 4+3 = 7
        BuildingCard b1 = new BuildingCard(1, 0, 4);
        BuildingCard b2 = new BuildingCard(1, 0, 3);
        game.getUpperBuildingRow().addCard(b1);
        game.getUpperBuildingRow().addCard(b2);
        player.playTurn(new ArrayList<>(), List.of(b1, b2), game);

        // 9 + 6 + 20 + 7 = 42
        player.calculateFinalPoints();
        assertEquals(42, player.getPp());
    }
}
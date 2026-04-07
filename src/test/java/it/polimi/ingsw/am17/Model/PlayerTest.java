package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
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
    void addFoodShouldNotGoUnderZero() {
        assertThrows(IllegalStateException.class, () -> player.addFood(-5));
    }

    @Test
    void calculateFinalPointsTest() {

        // Builders PP: 5 + 4 = 9
        Builder builder = new Builder(1, 2, 5, 0);
        Builder builder2 = new Builder(2, 2, 4, 0);
        player.addCards(List.of(builder, builder2), new ArrayList<>());

        // Inventors PP: 3 (amount of inventors) * 2 (unique icons) = 6
        Inventor inv1 = new Inventor(1, 2, InventorIconType.FLUTE);
        Inventor inv2 = new Inventor(1, 2, InventorIconType.FLUTE);
        Inventor inv3 = new Inventor(1, 2, InventorIconType.CANOE);
        player.addCards(List.of(inv1, inv2, inv3), new ArrayList<>());

        // Artists PP: lower(amount/2) * 10 = 20
        Artist art1 = new Artist(1, 2);
        Artist art2 = new Artist(1, 2);
        Artist art3 = new Artist(1, 2);
        Artist art4 = new Artist(1, 2);
        Artist art5 = new Artist(1, 2);
        player.addCards(List.of(art1, art2, art3, art4, art5), new ArrayList<>());

        // Buildings PP (no FinalPoints effects): 4+3 = 7
        BuildingCard b1 = new BuildingCard(1, 0, 4);
        BuildingCard b2 = new BuildingCard(1, 0, 3);
        player.addCards(new ArrayList<>(), List.of(b1, b2));

        // 9 + 6 + 20 + 7 = 42
        player.calculateFinalPoints();
        assertEquals(42, player.getPp());
    }

    @Test
    void testHasBuilding2()
    {
        BuildingCard b = new BuildingCard(0, 0, 0);
        player.addBuilding(b);
        player.addBuilding(b);
        player.addBuilding(b);

        assertFalse(player.hasBuilding2());

        BuildingType2 b2 = new BuildingType2();
        player.addBuilding(b2);

        assertTrue(player.hasBuilding2());

        player.addBuilding(b2);

        player.addBuilding(b);
        player.addBuilding(b);
        player.addBuilding(b);

        assertTrue(player.hasBuilding2());
    }

    @Test
    void testFoodEvent()
    {
        //add food and points to player
        player.addFood(4);
        player.addPp(4);

        //test event with no cards
        player.solveFoodEvent(2);
        assertEquals(4, player.getFood());
        assertEquals(4, player.getPp());

        //add cart to player
        List<CharacterCard> cards = new ArrayList<>();
        cards.add(new Hunter(0, 0, false));
        cards.add(new Hunter(0, 0, false));
        player.addCards(cards, Collections.emptyList());

        //test event with default case
        player.solveFoodEvent(2);
        assertEquals(2, player.getFood());
        assertEquals(4, player.getPp());

        player.addFood(2);

        List<BuildingCard> buildingCards = new ArrayList<>();
        buildingCards.add(new BuildingType13M(0, 0, 1, CardType.BUILDER));
        buildingCards.add(new BuildingType13M(0, 0, 1, CardType.HUNTER));

        //add binder and buildings
        cards.add(new Binder(0, 0));
        player.addCards(cards, buildingCards);

        //test with binder and buildings
        player.solveFoodEvent(2);
        assertEquals(4, player.getFood());
        assertEquals(4, player.getPp());

        player = new Player("TestPlayer", Color.RED);
        player.addPp(1);

        cards = new ArrayList<>();
        cards.add(new Artist(0, 0));
        cards.add(new Artist(0, 0));
        player.addCards(cards, Collections.emptyList());

        //test with no food and negative remaining pp
        player.solveFoodEvent(2);
        assertEquals(0, player.getFood());
        assertEquals(-3, player.getPp());
    }

    @Test
    void testHuntingEvent()
    {
        player.addPp(4);
        player.addFood(4);

        //test with no cards
        player.solveHuntingEvent(2);
        assertEquals(4, player.getFood());
        assertEquals(4, player.getPp());

        List<CharacterCard> cards = new ArrayList<>();
        cards.add(new Binder(0, 0));
        cards.add(new Binder(0, 0));
        player.addCards(cards, Collections.emptyList());

        //test with no hunter
        player.solveHuntingEvent(2);
        assertEquals(4, player.getFood());
        assertEquals(4, player.getPp());

        cards.add(new Hunter(0, 0, false));
        cards.add(new Hunter(0, 0, false));
        player.addCards(cards, Collections.emptyList());

        //test with hunter
        player.solveHuntingEvent(2);
        assertEquals(6, player.getFood());
        assertEquals(8, player.getPp());

        List<BuildingCard> bc = new ArrayList<>();
        bc.add(new BuildingType7());
        player.addCards(Collections.emptyList(), bc);

        player.solveHuntingEvent(2);
        assertEquals(10, player.getFood());
        assertEquals(14, player.getPp());
    }

    @Test
    void testPaintingEvent()
    {
        player.addPp(4);

        //test with no cards
        player.solvePaintingEvent(2, 3, 1);
        assertEquals(3, player.getPp());

        List<CharacterCard> cards = new ArrayList<>();
        cards.add(new Binder(0, 0));
        cards.add(new Binder(0, 0));
        player.addCards(cards, Collections.emptyList());

        player.solvePaintingEvent(2, 3, 1);
        assertEquals(2, player.getPp());

        cards = new ArrayList<>();
        cards.add(new Artist(0, 0));
        cards.add(new Artist(0, 0));
        player.addCards(cards, Collections.emptyList());

        player.solvePaintingEvent(2, 2, 1);
        assertEquals(6, player.getPp());
    }
}
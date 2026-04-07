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
}
package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.CommonInterfaces.InvalidOperationException;
import it.polimi.ingsw.am17.Server.Model.Color;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.*;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.*;
import it.polimi.ingsw.am17.Server.Model.GameState;
import it.polimi.ingsw.am17.Server.Model.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        assertThrows(InvalidOperationException.class, () -> player.addFood(-5));
    }

    @Test
    void calculateFinalPointsTest() {

        // Builders PP: 5 + 4 = 9
        Builder builder = new Builder(GameState.ERA1, 2, 5, 0, null);
        Builder builder2 = new Builder(GameState.ERA2, 2, 4, 0, null);
        player.addCards(List.of(builder, builder2), new ArrayList<>());

        // Inventors PP: 3 (amount of inventors) * 2 (unique icons) = 6
        Inventor inv1 = new Inventor(GameState.ERA1, 2, InventorIconType.FLUTE, null);
        Inventor inv2 = new Inventor(GameState.ERA1, 2, InventorIconType.FLUTE, null);
        Inventor inv3 = new Inventor(GameState.ERA1, 2, InventorIconType.CANOE, null);
        player.addCards(List.of(inv1, inv2, inv3), new ArrayList<>());

        // Artists PP: lower(amount/2) * 10 = 20
        Artist art1 = new Artist(GameState.ERA1, 2, null);
        Artist art2 = new Artist(GameState.ERA1, 2, null);
        Artist art3 = new Artist(GameState.ERA1, 2, null);
        Artist art4 = new Artist(GameState.ERA1, 2, null);
        Artist art5 = new Artist(GameState.ERA1, 2, null);
        player.addCards(List.of(art1, art2, art3, art4, art5), new ArrayList<>());

        // Buildings PP (no FinalPoints effects): 4+3 = 7
        BuildingCard b1 = new BuildingCard(GameState.ERA1, 0, 4);
        BuildingCard b2 = new BuildingCard(GameState.ERA1, 0, 3);
        player.addCards(new ArrayList<>(), List.of(b1, b2));

        // 9 + 6 + 20 + 7 = 42
        player.calculateFinalPoints();
        assertEquals(42, player.getPp());
    }

    @Test
    void testHasBuilding2WithMoreBuilding2()
    {
        BuildingCard b = new BuildingCard(GameState.NONE, 0, 0);
        player.addBuilding(b);
        player.addBuilding(b);
        player.addBuilding(b);
        BuildingType2 b2 = new BuildingType2();
        player.addBuilding(b2);
        player.addBuilding(b2);
        player.addBuilding(b);
        player.addBuilding(b);
        player.addBuilding(b);

        assertTrue(player.hasBuilding2());
    }

    @Test
    void testHasBuilding2(){
        BuildingCard b = new BuildingCard(GameState.NONE, 0, 0);
        player.addBuilding(b);
        player.addBuilding(b);
        player.addBuilding(b);
        BuildingType2 b2 = new BuildingType2();
        player.addBuilding(b2);

        assertTrue(player.hasBuilding2());
    }


    @Test
    void testHasBuilding2WithNoBuilding2()
    {
        BuildingCard b = new BuildingCard(GameState.NONE, 0, 0);
        player.addBuilding(b);
        player.addBuilding(b);
        player.addBuilding(b);

        assertFalse(player.hasBuilding2());
    }

    @Test
    void testFoodEventWithoutCards() {
        //add food and points to player
        player.addFood(4);
        player.addPp(4);

        //test event with no cards
        player.solveFoodEvent(2);
        assertEquals(4, player.getFood());
        assertEquals(4, player.getPp());
    }

    @Test
    void testFoodEvent()
    {
        //add food and points to player
        player.addFood(4);
        player.addPp(4);

        //add cart to player
        List<CharacterCard> cards = new ArrayList<>();
        cards.add(new Hunter(GameState.NONE, 0, false, null));
        cards.add(new Hunter(GameState.NONE, 0, false, null));
        player.addCards(cards, Collections.emptyList());

        //test event with default case
        player.solveFoodEvent(2);
        assertEquals(2, player.getFood());
        assertEquals(4, player.getPp());

    }


    @Test
    void testFoodEventWithBinderAndBuildings()
    {
        //add food and points to player
        player.addFood(4);
        player.addPp(4);

        //add cart to player
        List<CharacterCard> cards = new ArrayList<>();
        cards.add(new Hunter(GameState.NONE, 0, false, null));
        cards.add(new Hunter(GameState.NONE, 0, false, null));
        player.addCards(cards, Collections.emptyList());

        List<BuildingCard> buildingCards = new ArrayList<>();
        buildingCards.add(new BuildingType13M(GameState.NONE, 0, 1, CardType.BUILDER));
        buildingCards.add(new BuildingType13M(GameState.NONE, 0, 1, CardType.HUNTER));

        //add binder and buildings
        cards.add(new Binder(GameState.NONE, 0, null));
        player.addCards(cards, buildingCards);

        //test with binder and buildings
        player.solveFoodEvent(2);
        assertEquals(4, player.getFood());
        assertEquals(4, player.getPp());

    }

    @Test
    void testWithNoFoodAndNegativeRemainingPp()
    {
        player.addPp(1);

        List<CharacterCard> cards = new ArrayList<>();
        cards.add(new Artist(GameState.NONE, 0, null));
        cards.add(new Artist(GameState.NONE, 0, null));
        player.addCards(cards, Collections.emptyList());

        //test with no food and negative remaining pp
        player.solveFoodEvent(2);
        assertEquals(0, player.getFood());
        assertEquals(-3, player.getPp());
    }

    @Test
    void testHuntingEventWithNoCards() {
        player.addPp(4);
        player.addFood(4);

        //test with no cards
        player.solveHuntingEvent(2);
        assertEquals(4, player.getFood());
        assertEquals(4, player.getPp());
    }

    @Test
    void testHuntingEventWithNoHunter() {
        player.addPp(4);
        player.addFood(4);

        List<CharacterCard> cards = new ArrayList<>();
        cards.add(new Binder(GameState.NONE, 0, null));
        cards.add(new Binder(GameState.NONE, 0, null));
        player.addCards(cards, Collections.emptyList());

        //test with no hunter
        player.solveHuntingEvent(2);
        assertEquals(4, player.getFood());
        assertEquals(4, player.getPp());
    }
    @Test
    void testHuntingEvent() {
        player.addPp(4);
        player.addFood(4);

        List<CharacterCard> cards = new ArrayList<>();
        cards.add(new Binder(GameState.NONE, 0, null));
        cards.add(new Binder(GameState.NONE, 0, null));
        player.addCards(cards, Collections.emptyList());

        cards.add(new Hunter(GameState.NONE, 0, false, null));
        cards.add(new Hunter(GameState.NONE, 0, false, null));
        player.addCards(cards, Collections.emptyList());
    }

    @Test
    void testHuntingEventWithBuildings()
    {
        player.addPp(4);
        player.addFood(8);

        List<CharacterCard> cards = new ArrayList<>();
        cards.add(new Binder(GameState.NONE, 0, null));
        cards.add(new Hunter(GameState.NONE, 0, false, null));
        cards.add(new Hunter(GameState.NONE, 0, false, null));
        cards.add(new Binder(GameState.NONE, 0, null));
        List<BuildingCard> bc = new ArrayList<>();
        bc.add(new BuildingType7());
        player.addCards(cards, bc);

        player.solveHuntingEvent(2);
        //8-7+2*2 = 5
        assertEquals(5, player.getFood());
        //4+2*(2+1) = 10
        assertEquals(10, player.getPp());
    }

    @Test
    void testPaintingEventWithNoCards() {
        player.addPp(4);

        //test with no cards
        player.solvePaintingEvent(2, 3, 1);
        assertEquals(3, player.getPp());
    }

    @Test
    void testPaintingEventWithNotEnoughArtists() {
        player.addPp(4);

        List<CharacterCard> cards = new ArrayList<>();
        cards.add(new Binder(GameState.NONE, 0, null));
        cards.add(new Binder(GameState.NONE, 0, null));
        player.addCards(cards, Collections.emptyList());

        player.solvePaintingEvent(2, 3, 1);
        assertEquals(3, player.getPp());
    }

    @Test
    void testPaintingEvent()
    {
        player.addPp(4);

        List<CharacterCard> cards = new ArrayList<>();
        cards.add(new Binder(GameState.NONE, 0, null));
        cards.add(new Binder(GameState.NONE, 0, null));
        cards.add(new Artist(GameState.NONE, 0, null));
        cards.add(new Artist(GameState.NONE, 0, null));

        player.addCards(cards, Collections.emptyList());

        player.solvePaintingEvent(2, 2, 1);
        assertEquals(8, player.getPp());
    }

    @Test
    void testCalculateStarPointsWithNoCards()
    {
        assertEquals(0, player.calculateStarPoints());
    }

    @Test
    void testCalculateStarPointsWithNoShaman()
    {
        List<CharacterCard> cards = new ArrayList<>();
        cards.add(new Binder(GameState.NONE, 0, null));
        cards.add(new Binder(GameState.NONE, 0, null));
        cards.add(new Artist(GameState.NONE, 0, null));
        cards.add(new Artist(GameState.NONE, 0, null));
        player.addCards(cards, Collections.emptyList());
        assertEquals(0, player.calculateStarPoints());
    }

    @Test
    void testCalculateStarPoints()
    {
        List<CharacterCard> cards = new ArrayList<>();
        cards.add(new Shaman(GameState.NONE, 0, 2, null));
        cards.add(new Shaman(GameState.NONE, 0, 3, null));
        cards.add(new Artist(GameState.NONE, 0, null));
        cards.add(new Artist(GameState.NONE, 0, null));
        player.addCards(cards, Collections.emptyList());
        assertEquals(5, player.calculateStarPoints());
    }

    @Test
    void testCalculateStarPointsWithBuildings()
    {
        player.addFood(15);
        List<CharacterCard> cards = new ArrayList<>();
        cards.add(new Shaman(GameState.NONE, 0, 2, null));
        cards.add(new Shaman(GameState.NONE, 0, 3, null));
        cards.add(new Artist(GameState.NONE, 0, null));
        cards.add(new Artist(GameState.NONE, 0, null));

        List<BuildingCard> bc = new ArrayList<>();
        bc.add(new BuildingType7());
        bc.add(new BuildingType9());

        player.addCards(cards, bc);

        assertEquals(8, player.calculateStarPoints());
    }

}
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
    void testPaintingEventWithBuildingsFoodBonus() {
        Artist artist1 = new Artist(GameState.NONE, 0, null);
        Artist artist2 = new Artist(GameState.NONE, 0, null);
        player.addCards(List.of(artist1, artist2), Collections.emptyList());

        // Ensure initial food is 0
        assertEquals(0, player.getFood());

        BuildingType5 building5 = new BuildingType5();

        player.addBuilding(building5);

        // trigger the event to call method AddFoodPerArtistInPaintingEvent
        player.solvePaintingEvent(2, 3, 1);

        // The player's food must have increased to 2
        assertEquals(2, player.getFood());
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

    @Test
    void testAddCardsInsufficientFood() {
        BuildingCard expensiveBuilding = new BuildingCard(GameState.NONE, 10, 0);

        // The player starts with 0 food, so the purchase must fail and throw the exception
        assertThrows(InvalidOperationException.class, () ->
                player.addCards(new ArrayList<>(), List.of(expensiveBuilding)));
    }

    @Test
    void testAddCardsHunterWithIconAddsFood() {
        Hunter hunterWithIcon1 = new Hunter(GameState.NONE, 0, true, null);
        Hunter hunterWithIcon2 = new Hunter(GameState.NONE, 0, true, null);

        // addCards logic:
        // Add hunter1 -> add 1 food
        // Add hunter2 -> add 2 food
        // the player should have 3 food in total
        player.addCards(List.of(hunterWithIcon1, hunterWithIcon2), Collections.emptyList());
        assertEquals(3, player.getFood());
    }

    @Test
    void testCalculateBuildingCostAndCanBuy() {
        Builder b1 = new Builder(GameState.NONE, 0, 0, 1, null);
        Builder b2 = new Builder(GameState.NONE, 0, 0, 2, null);

        player.addCharacter(b1);
        player.addCharacter(b2);

        // Total food discount: 3
        BuildingCard building = new BuildingCard(GameState.NONE, 5, 0);

        // 5 - 3  = 2
        assertEquals(2, player.calculateBuildingCost(building));

        // Check canBuyBuildings (player currently has 0 food, actual cost is 2)
        assertFalse(player.canBuyBuildings(List.of(building)));

        player.addFood(2);
        assertTrue(player.canBuyBuildings(List.of(building)));
    }

    @Test
    void testAddCharacterFoodBonusFromBuilding10() {
        BuildingCard building10 = new BuildingType10();
        player.addBuilding(building10);

        // Ensure initial food is 0
        assertEquals(0, player.getFood());

        // Add the first inventor (no bonus should be applied yet, since the player doesn't have this icon)
        Inventor firstInventor = new Inventor(GameState.ERA1, 2, InventorIconType.FLUTE, null);
        player.addCharacter(firstInventor);

        assertEquals(0, player.getFood());

        // Add a second inventor with the SAME icon
        Inventor secondInventor = new Inventor(GameState.ERA1, 2, InventorIconType.FLUTE, null);
        player.addCharacter(secondInventor);

        // The player should now receive +3 food due to BuildingType10 effect
        assertEquals(3, player.getFood());
    }

    @Test
    void testAddCharacterFoodBonusFromBuilding14() {
        BuildingCard building14 = new BuildingType14();
        player.addBuilding(building14);

        // Ensure initial food is 0
        assertEquals(0, player.getFood());

        // Add 5 out of 6 different character types (the set is not complete yet)
        player.addCharacter(new Inventor(GameState.NONE, 0, InventorIconType.FLUTE, null));
        player.addCharacter(new Binder(GameState.NONE, 0, null));
        player.addCharacter(new Shaman(GameState.NONE, 0, 0, null));
        player.addCharacter(new Artist(GameState.NONE, 0, null));
        player.addCharacter(new Hunter(GameState.NONE, 0, false, null));

        // Ensure food is still 0 since the 6-card set is incomplete
        assertEquals(0, player.getFood());

        // Add the 6th missing character type (Builder) to complete the set
        player.addCharacter(new Builder(GameState.NONE, 0, 0, 0, null));

        // The player should now receive +5 food due to BuildingType14 effect
        assertEquals(5, player.getFood());
    }

    @Test
    void testAddFoodToTurnFood() {
        assertEquals(0, player.addFoodToTurnFood());

        // BuildingType11 gives food bonus
        player.addBuilding(new BuildingType11());
        assertTrue(player.addFoodToTurnFood() > 0);
    }

    @Test
    void testHasDoubleRitualEventPoints() {
        assertFalse(player.hasDoubleRitualEventPoints());

        // BuildingType8 gives double points after Ritual Event
        player.addBuilding(new BuildingType8());
        assertTrue(player.hasDoubleRitualEventPoints());
    }

    @Test
    void testHasShieldFromRitualEvent() {
        assertFalse(player.hasShieldFromRitualEvent());

        // BuildingType12 gives a shield from Ritual Event PP loss
        player.addBuilding(new BuildingType12());
        assertTrue(player.hasShieldFromRitualEvent());
    }

    @Test
    void testAddCardsSuccessfullyBuysBuildingAndCoversHunter() {
        player.addFood(5);

        BuildingCard affordableBuilding = new BuildingCard(GameState.NONE, 4, 0);

        Hunter hunterWithIcon = new Hunter(GameState.NONE, 0, true, null);

        // Call addCards
        // Adds the Hunter with icon -> adds +1 food
        // Calculates the cost of the building and "buys" the card
        player.addCards(List.of(hunterWithIcon), List.of(affordableBuilding));

        // Expected final food: 2.
        assertEquals(2, player.getFood());
        assertEquals(1, player.getBuildingCards().size());
        assertEquals(1, player.getCharacterCards().size());
    }
}
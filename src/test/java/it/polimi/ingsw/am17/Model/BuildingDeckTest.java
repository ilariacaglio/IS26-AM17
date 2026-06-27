package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Server.Model.Decks.BuildingDeck;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameState;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuildingDeckTest {
    BuildingDeck buildingDeck;

    //doesn't create an empty list
    @Test
    void shouldNotCreateEmptyList() {
        buildingDeck = new BuildingDeck(3);
        assertFalse(buildingDeck.drawAllEra1().isEmpty());
        assertFalse(buildingDeck.drawAllEra2().isEmpty());
        assertFalse(buildingDeck.drawAllEra3().isEmpty());
    }

    //in buildingCardsEra1 there are only buildings era1
    @Test
    void shouldOnlyHaveEra1Buildings() {
        buildingDeck = new BuildingDeck(3);
        List<BuildingCard> buildingCardsEra1 = buildingDeck.drawAllEra1();

        for (BuildingCard buildingCard : buildingCardsEra1) {
            GameState currentEra = buildingCard.getEra();
            assertEquals(GameState.ERA1, currentEra);
        }
    }

    //in buildingCardsEra2 there are only buildings era2
    @Test
    void shouldOnlyHaveEra2Buildings() {
        buildingDeck = new BuildingDeck(3);
        List<BuildingCard> buildingCardsEra2 = buildingDeck.drawAllEra2();

        for (BuildingCard buildingCard : buildingCardsEra2) {
            GameState currentEra = buildingCard.getEra();
            assertEquals(GameState.ERA2, currentEra);
        }
    }

    //in buildingCardsEra3 there are only buildings era3
    @Test
    void shouldOnlyHaveEra3Buildings() {
        buildingDeck = new BuildingDeck(3);
        List<BuildingCard> buildingCardsEra3 = buildingDeck.drawAllEra3();

        for (BuildingCard buildingCard : buildingCardsEra3) {
            GameState currentEra = buildingCard.getEra();
            assertEquals(GameState.ERA3, currentEra);
        }
    }

    //if numPlayers==2, buildingCardsEra1 has 1 card
    @Test
    void shouldOnlyHave1BuildingCardsEra1() {
        buildingDeck = new BuildingDeck(2);
        List<BuildingCard> buildingCardsEra1 = buildingDeck.drawAllEra1();
        assertEquals(1, buildingCardsEra1.size());
    }

    //if numPlayers!=2, buildingCardsEra1 has 2 card
    @Test
    void shouldOnlyHave2BuildingCardsEra1() {
        buildingDeck = new BuildingDeck(3);
        List<BuildingCard> buildingCardsEra1 = buildingDeck.drawAllEra1();
        assertEquals(2, buildingCardsEra1.size());
    }

    //if numPlayers<=3, buildingCardsEra2 has 2 card
    @Test
    void shouldOnlyHave2BuildingCardsEra2() {
        buildingDeck = new BuildingDeck(3);
        List<BuildingCard> buildingCardsEra2 = buildingDeck.drawAllEra2();
        assertEquals(2, buildingCardsEra2.size());
    }

    //if numPlayers>3, buildingCardsEra2 has 3 card
    @Test
    void shouldOnlyHave3BuildingCardsEra2() {
        buildingDeck = new BuildingDeck(4);
        List<BuildingCard> buildingCardsEra2 = buildingDeck.drawAllEra2();
        assertEquals(3, buildingCardsEra2.size());
    }

    //if numPlayers==2, buildingCardsEra3 has 3 card
    @Test
    void shouldOnlyHave3BuildingCardsEra3() {
        buildingDeck = new BuildingDeck(2);
        List<BuildingCard> buildingCardsEra3 = buildingDeck.drawAllEra3();
        assertEquals(3, buildingCardsEra3.size());
    }

    //if numPlayers==3, buildingCardsEra3 has 4 card
    @Test
    void shouldOnlyHave4BuildingCardsEra3PlayerNumber3() {
        buildingDeck = new BuildingDeck(3);
        List<BuildingCard> buildingCardsEra3 = buildingDeck.drawAllEra3();
        assertEquals(4, buildingCardsEra3.size());
    }

    //if numPlayers==4, buildingCardsEra3 has 4 card
    @Test
    void shouldOnlyHave4BuildingCardsEra3PlayerNumber4() {
        buildingDeck = new BuildingDeck(4);
        List<BuildingCard> buildingCardsEra3 = buildingDeck.drawAllEra3();
        assertEquals(4, buildingCardsEra3.size());
    }

    //if numPlayers==5, buildingCardsEra3 has 5 card
    @Test
    void shouldOnlyHave5BuildingCardsEra3() {
        buildingDeck = new BuildingDeck(5);
        List<BuildingCard> buildingCardsEra3 = buildingDeck.drawAllEra3();
        assertEquals(5, buildingCardsEra3.size());
    }

    //buildingCardsEra1 is unmodifiableList
    @Test
    void shouldBeUnmodifiableListEra1() {
        buildingDeck = new BuildingDeck(2);
        List<BuildingCard> buildingCardsEra1 = buildingDeck.drawAllEra1();
        assertThrows(UnsupportedOperationException.class, () ->buildingCardsEra1.add(null) );
    }

    //buildingCardsEra2 is unmodifiableList
    @Test
    void shouldBeUnmodifiableListEra2() {
        buildingDeck = new BuildingDeck(2);
        List<BuildingCard> buildingCardsEra2 = buildingDeck.drawAllEra2();
        assertThrows(UnsupportedOperationException.class, () ->buildingCardsEra2.add(null) );
    }

    //buildingCardsEra3 is unmodifiableList
    @Test
    void shouldBeUnmodifiableListEra3() {
        buildingDeck = new BuildingDeck(2);
        List<BuildingCard> buildingCardsEra3 = buildingDeck.drawAllEra3();
        assertThrows(UnsupportedOperationException.class, () ->buildingCardsEra3.add(null) );
    }

}
package it.polimi.ingsw.am17.Model.GameCard;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class BuildingType4Test {

    @Test
    public void testEmptyList() {
        BuildingType4 card = new BuildingType4();
        List<CharacterCard> cards = new ArrayList<>();
        int points = card.FinalPoints(cards);
        assertEquals(0, points);
    }

    @Test
    public void testIncompleteSet() {
        BuildingType4 card = new BuildingType4();
        List<CharacterCard> cards = new ArrayList<>();
        cards.add(new CharacterCard(1, 1, CardType.ARTIST));
        cards.add(new CharacterCard(1, 1, CardType.BINDER));
        cards.add(new CharacterCard(1, 1, CardType.BUILDER));
        cards.add(new CharacterCard(1, 1, CardType.HUNTER));
        cards.add(new CharacterCard(1, 1, CardType.INVENTOR));
        // NO SHAMAN

        int points = card.FinalPoints(cards);
        assertEquals(0, points, "Should return 0 if any character type is missing");
    }

    @Test
    public void testCompleteSet() {
        BuildingType4 card = new BuildingType4();
        List<CharacterCard> cards = new ArrayList<>();
        cards.add(new CharacterCard(1, 1, CardType.ARTIST));
        cards.add(new CharacterCard(1, 1, CardType.BINDER));
        cards.add(new CharacterCard(1, 1, CardType.BUILDER));
        cards.add(new CharacterCard(1, 1, CardType.HUNTER));
        cards.add(new CharacterCard(1, 1, CardType.INVENTOR));
        cards.add(new CharacterCard(1, 1, CardType.SHAMAN));
        cards.add(new CharacterCard(1, 1, CardType.SHAMAN)); // extra card

        int points = card.FinalPoints(cards);
        assertEquals(6, points, "Should return 6 for one complete set");
    }

    @Test
    public void testMultipleCompleteSets() {
        BuildingType4 card = new BuildingType4();
        List<CharacterCard> cards = new ArrayList<>();
        // Two sets
        for (int i = 0; i < 2; i++) {
            cards.add(new CharacterCard(1, 1, CardType.ARTIST));
            cards.add(new CharacterCard(1, 1, CardType.BINDER));
            cards.add(new CharacterCard(1, 1, CardType.BUILDER));
            cards.add(new CharacterCard(1, 1, CardType.HUNTER));
            cards.add(new CharacterCard(1, 1, CardType.INVENTOR));
            cards.add(new CharacterCard(1, 1, CardType.SHAMAN));
        }
        // One extra card
        cards.add(new CharacterCard(1, 1, CardType.ARTIST));

        int points = card.FinalPoints(cards);
        assertEquals(12, points, "Should return 12 for two complete sets");
    }
}

package it.polimi.ingsw.am17.Model.GameCard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuildingType10Test {
    BuildingType10 buildingType10;

    @BeforeEach
    void setUp() {
        buildingType10 = new BuildingType10();


    }

    @Test
    void TestFoodBonusInventor3() {
        List<CharacterCard> cards = new ArrayList<>();

        CharacterCard A = new Inventor(2, 2,InventorIconType.CANOE);
        CharacterCard B = new Inventor(3, 2,InventorIconType.CANOE);
        cards.add(A);

        int risultato = buildingType10.FoodBonus(cards, B);
        //first and second if are true (Inventor, same icon)
        assertEquals(3,risultato);
    }

    @Test
    void TestFoodBonusInventor0() {
        List<CharacterCard> cards = new ArrayList<>();

        CharacterCard A = new Inventor(2, 2,InventorIconType.IDOL);
        CharacterCard B = new Inventor(3, 2,InventorIconType.BREAD);
        cards.add(A);

        int risultato = buildingType10.FoodBonus(cards, B);
        //first if is true,second if is false (Inventor, no same icon)
        assertEquals(0,risultato);

    }

    @Test
    void TestFoodBonus0() {
        List<CharacterCard> cards = new ArrayList<>();

        CharacterCard A = new Inventor(2, 2,InventorIconType.CANOE);
        CharacterCard B = new Artist(3, 2);
        cards.add(A);

        int risultato = buildingType10.FoodBonus(cards, B);
        //first if is false, we don't enter in second if (no Inventor)
        assertEquals(0,risultato);
    }

    @Test
    void TestFoodBonusListaVuotaInventor() {
        List<CharacterCard> cards = new ArrayList<>();

        CharacterCard A = new Inventor(3, 2,InventorIconType.BREAD);

        int risultato = buildingType10.FoodBonus(cards, A);
        //first if is true, but list is void (Inventor)
        assertEquals(0,risultato);
    }

    @Test
    void TestFoodBonusListaVuotaNoInventor() {
        List<CharacterCard> cards = new ArrayList<>();

        CharacterCard B = new Shaman(2, 2, 2);

        int risultato = buildingType10.FoodBonus(cards, B);
        //first if is false and list is void (no Inventor)
        assertEquals(0,risultato);
    }



}
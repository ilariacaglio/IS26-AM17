package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingType10;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.*;
import it.polimi.ingsw.am17.Server.Model.GameState;
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

        CharacterCard A = new Inventor(GameState.ERA2, 2, InventorIconType.CANOE,null);
        CharacterCard B = new Inventor(GameState.ERA3, 2,InventorIconType.CANOE, null);
        cards.add(A);

        int result = buildingType10.GetFoodBonusFromCardAcquisition(cards, B);
        //first and second if are true (Inventor, same icon)
        assertEquals(3,result);
    }

    @Test
    void TestFoodBonusInventor0() {
        List<CharacterCard> cards = new ArrayList<>();

        CharacterCard A = new Inventor(GameState.ERA2, 2,InventorIconType.IDOL, null);
        CharacterCard B = new Inventor(GameState.ERA3, 2,InventorIconType.BREAD, null);
        cards.add(A);

        int result = buildingType10.GetFoodBonusFromCardAcquisition(cards, B);
        //first if is true,second if is false (Inventor, no same icon)
        assertEquals(0,result);

    }

    @Test
    void TestFoodBonus0() {
        List<CharacterCard> cards = new ArrayList<>();

        CharacterCard A = new Inventor(GameState.ERA2, 2,InventorIconType.CANOE, null);
        CharacterCard B = new Artist(GameState.ERA3, 2, null);
        cards.add(A);

        int result = buildingType10.GetFoodBonusFromCardAcquisition(cards, B);
        //first if is false, we don't enter in second if (no Inventor)
        assertEquals(0,result);
    }

    @Test
    void TestFoodBonusEmptyListInventor() {
        List<CharacterCard> cards = new ArrayList<>();

        CharacterCard A = new Inventor(GameState.ERA3, 2,InventorIconType.BREAD, null);

        int result = buildingType10.GetFoodBonusFromCardAcquisition(cards, A);
        //first if is true, but list is void (Inventor)
        assertEquals(0,result);
    }

    @Test
    void TestFoodBonusEmptyListNoInventor() {
        List<CharacterCard> cards = new ArrayList<>();

        CharacterCard B = new Shaman(GameState.ERA2, 2, 2, null);

        int result = buildingType10.GetFoodBonusFromCardAcquisition(cards, B);
        //first if is false and list is void (no Inventor)
        assertEquals(0,result);
    }



}
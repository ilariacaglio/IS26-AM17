package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingType13M;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.CardType;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuildingType13MTest {
    BuildingType13M buildingType13M;

    @BeforeEach
    void setUp() {
        buildingType13M = new BuildingType13M(2, 2,2, CardType.ARTIST);
    }

    @Test
    void ShouldGiveFoodDiscount() {
        List<CharacterCard> playerCharacterCards = new ArrayList<>();

        CharacterCard A = new Artist(2, 2);
        CharacterCard B = new Inventor(3,2, InventorIconType.BREAD);
        playerCharacterCards.add(A);
        playerCharacterCards.add(B);

        int foodDiscount = buildingType13M.GetFoodDiscountInFoodEvent(playerCharacterCards);

        assertEquals(1,foodDiscount);
    }

    @Test
    void ShouldNotGiveFoodDiscountWhenNoMatchingCharacterType() {
        List<CharacterCard> playerCharacterCards = new ArrayList<>();

        CharacterCard A = new Binder(2, 2);
        CharacterCard B = new Inventor(3,2, InventorIconType.BREAD);
        playerCharacterCards.add(A);
        playerCharacterCards.add(B);

        int foodDiscount = buildingType13M.GetFoodDiscountInFoodEvent(playerCharacterCards);

        assertEquals(0,foodDiscount);
    }

    @Test
    void ShouldNotGiveFoodDiscountWhenPlayerCardIsEmpty() {
        List<CharacterCard> playerCharacterCards = new ArrayList<>();

        int foodDiscount = buildingType13M.GetFoodDiscountInFoodEvent(playerCharacterCards);

        assertEquals(0,foodDiscount);
    }



}
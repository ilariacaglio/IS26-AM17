package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Server.Model.Color;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingType12;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingType8;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.Shaman;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events.RitualEvent;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class RitualEventTest {
    RitualEvent ritualEvent;

    @BeforeEach
    void setUp() {
        ritualEvent = new RitualEvent(false,2,10,5);
    }

    //everyone has the same star number and player A has BuildingType8
    @Test
    void shouldAllEqualBuildingType8(){
        Queue<Player> list = new LinkedList<>();
        Player A = new Player("playerA", Color.BLACK);
        Player B = new Player("playerB", Color.WHITE);
        list.add(A);
        list.add(B);

        TribesCard cardA = new Shaman(2, 2, 2);
        TribesCard cardB = new Shaman(2, 2, 2);
        A.addCharacter(cardA);
        B.addCharacter(cardB);

        A.addBuilding(new BuildingType8());

        ritualEvent.computeScore(list);

        assertEquals(15, A.getPp());
        assertEquals(5, B.getPp());
    }

    //everyone has the same star number and nobody has BuildingType8
    @Test
    void shouldAllEqualNoBuildingType8(){
        Queue<Player> list = new LinkedList<>();
        Player A = new Player("playerA", Color.BLACK);
        Player B = new Player("playerB", Color.WHITE);
        list.add(A);
        list.add(B);

        TribesCard cardA = new Shaman(2, 2, 2);
        TribesCard cardB = new Shaman(2, 2, 2);
        A.addCharacter(cardA);
        B.addCharacter(cardB);

        ritualEvent.computeScore(list);

        assertEquals(5, A.getPp());
        assertEquals(5, B.getPp());
    }

    //A has max stars and BuildingType8
    //B has min stars and BuildingType12
    @Test
    void shouldAMaxBMinAndBuildings(){
        Queue<Player> list = new LinkedList<>();
        Player A = new Player("playerA", Color.BLACK);
        Player B = new Player("playerB", Color.WHITE);
        list.add(A);
        list.add(B);

        TribesCard cardA = new Shaman(3, 2, 3);
        TribesCard cardB = new Shaman(2, 2, 2);
        A.addCharacter(cardA);
        B.addCharacter(cardB);

        A.addBuilding(new BuildingType8());
        B.addBuilding(new BuildingType12());

        ritualEvent.computeScore(list);

        assertEquals(20, A.getPp());
        assertEquals(0, B.getPp());
    }

    //A has max stars and  no BuildingType8
    //B has min stars and no BuildingType12
    @Test
    void shouldAMaxBMinAndNoBuildings(){
        Queue<Player> list = new LinkedList<>();
        Player A = new Player("playerA", Color.BLACK);
        Player B = new Player("playerB", Color.WHITE);
        list.add(A);
        list.add(B);

        TribesCard cardA = new Shaman(3, 2, 3);
        TribesCard cardB = new Shaman(2, 2, 2);
        A.addCharacter(cardA);
        B.addCharacter(cardB);

        ritualEvent.computeScore(list);

        assertEquals(10, A.getPp());
        assertEquals(-5, B.getPp());
    }

    //A and B max stars and A has BuildingType8
    //C min and BuildingType12
    @Test
    void shouldABMaxCMinAndBuildings(){
        Queue<Player> list = new LinkedList<>();
        Player A = new Player("playerA", Color.BLACK);
        Player B = new Player("playerB", Color.WHITE);
        Player C = new Player("playerC", Color.RED);
        list.add(A);
        list.add(B);
        list.add(C);

        TribesCard cardA = new Shaman(3, 2, 3);
        TribesCard cardB = new Shaman(3, 2, 3);
        TribesCard cardC = new Shaman(2, 2, 2);
        A.addCharacter(cardA);
        B.addCharacter(cardB);
        C.addCharacter(cardC);

        A.addBuilding(new BuildingType8());
        C.addBuilding(new BuildingType12());

        ritualEvent.computeScore(list);

        assertEquals(20, A.getPp());
        assertEquals(10, B.getPp());
        assertEquals(0, C.getPp());
    }

    //A and B max stars and no BuildingType8
    //C min and no BuildingType12
    @Test
    void shouldABMaxCMinNoBuildings(){
        Queue<Player> list = new LinkedList<>();
        Player A = new Player("playerA", Color.BLACK);
        Player B = new Player("playerB", Color.WHITE);
        Player C = new Player("playerC", Color.RED);
        list.add(A);
        list.add(B);
        list.add(C);

        TribesCard cardA = new Shaman(3, 2, 3);
        TribesCard cardB = new Shaman(3, 2, 3);
        TribesCard cardC = new Shaman(2, 2, 2);
        A.addCharacter(cardA);
        B.addCharacter(cardB);
        C.addCharacter(cardC);

        ritualEvent.computeScore(list);

        assertEquals(10, A.getPp());
        assertEquals(10, B.getPp());
        assertEquals(-5, C.getPp());
    }

    //A max stars and BuildingType8
    //B and C min and C has BuildingType12
    @Test
    void shouldAMaxBCMinAndBuildings(){
        Queue<Player> list = new LinkedList<>();
        Player A = new Player("playerA", Color.BLACK);
        Player B = new Player("playerB", Color.WHITE);
        Player C = new Player("playerC", Color.RED);
        list.add(A);
        list.add(B);
        list.add(C);

        TribesCard cardA = new Shaman(3, 2, 3);
        TribesCard cardB = new Shaman(2, 2, 2);
        TribesCard cardC = new Shaman(2, 2, 2);
        A.addCharacter(cardA);
        B.addCharacter(cardB);
        C.addCharacter(cardC);

        A.addBuilding(new BuildingType8());
        C.addBuilding(new BuildingType12());

        ritualEvent.computeScore(list);

        assertEquals(20, A.getPp());
        assertEquals(-5, B.getPp());
        assertEquals(0, C.getPp());
    }

    //A max stars and no BuildingType8
    //B and C min and no BuildingType12
    @Test
    void shouldAMaxBCMinAndNoBuildings(){
        Queue<Player> list = new LinkedList<>();
        Player A = new Player("playerA", Color.BLACK);
        Player B = new Player("playerB", Color.WHITE);
        Player C = new Player("playerC", Color.RED);
        list.add(A);
        list.add(B);
        list.add(C);

        TribesCard cardA = new Shaman(3, 2, 3);
        TribesCard cardB = new Shaman(2, 2, 2);
        TribesCard cardC = new Shaman(2, 2, 2);
        A.addCharacter(cardA);
        B.addCharacter(cardB);
        C.addCharacter(cardC);

        ritualEvent.computeScore(list);

        assertEquals(10, A.getPp());
        assertEquals(-5, B.getPp());
        assertEquals(-5, C.getPp());
    }



}
package it.polimi.ingsw.am17.Model.GameCard;

import it.polimi.ingsw.am17.Server.Model.Color;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingType12;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingType8;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.Shaman;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events.RitualEvent;
import it.polimi.ingsw.am17.Server.Model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class RitualEventTest {
    private RitualEvent ritualEvent;
    private Queue<Player> list;
    private Player playerA;
    private Player playerB;
    private Player playerC;

    @BeforeEach
    void setUp() {
        ritualEvent = new RitualEvent(false, 2, 10, 5, null);

        list = new LinkedList<>();
        playerA = new Player("playerA", Color.BLACK);
        playerB = new Player("playerB", Color.WHITE);
        playerC = new Player("playerC", Color.RED);
    }

    /**
     * Creates shaman card and adds it to player.
     * Then adds the player to te queue.
     */
    private void addPlayerWithShamanToList(Player player, int era, int minPlayers, int stars) {
        player.addCharacter(new Shaman(era, minPlayers, stars, null));
        list.add(player);
    }

    @Test
    void shouldAllEqualBuildingType8() {
        addPlayerWithShamanToList(playerA, 2, 2, 2);
        addPlayerWithShamanToList(playerB, 2, 2, 2);

        playerA.addBuilding(new BuildingType8());

        ritualEvent.computeScore(list);

        // no extra point due to tie
        assertEquals(5, playerA.getPp());
        assertEquals(5, playerB.getPp());
    }

    @Test
    void shouldAllEqualNoBuildingType8() {
        addPlayerWithShamanToList(playerA, 2, 2, 2);
        addPlayerWithShamanToList(playerB, 2, 2, 2);

        ritualEvent.computeScore(list);

        assertEquals(5, playerA.getPp());
        assertEquals(5, playerB.getPp());
    }

    @Test
    void shouldAMaxBMinAndBuildings() {
        addPlayerWithShamanToList(playerA, 3, 2, 3);
        addPlayerWithShamanToList(playerB, 2, 2, 2);

        playerA.addBuilding(new BuildingType8());
        playerB.addBuilding(new BuildingType12());

        ritualEvent.computeScore(list);

        assertEquals(20, playerA.getPp());
        assertEquals(0, playerB.getPp());
    }

    @Test
    void shouldAMaxBMinAndNoBuildings() {
        addPlayerWithShamanToList(playerA, 3, 2, 3);
        addPlayerWithShamanToList(playerB, 2, 2, 2);

        ritualEvent.computeScore(list);

        assertEquals(10, playerA.getPp());
        assertEquals(-5, playerB.getPp());
    }

    @Test
    void shouldNotApplyBuildingType8ToMinPlayer() {
        addPlayerWithShamanToList(playerA, 3, 2, 3);
        addPlayerWithShamanToList(playerB, 1, 1, 1);

        playerB.addBuilding(new BuildingType8());

        ritualEvent.computeScore(list);

        assertEquals(10, playerA.getPp());
        assertEquals(-5, playerB.getPp());
    }

    @Test
    void shouldNotApplyBuildingType12ToMaxPlayer() {
        addPlayerWithShamanToList(playerA, 3, 2, 3);
        addPlayerWithShamanToList(playerB, 1, 1, 1);

        playerA.addBuilding(new BuildingType12());

        ritualEvent.computeScore(list);

        assertEquals(10, playerA.getPp());
        assertEquals(-5, playerB.getPp());
    }

    @Test
    void shouldABMaxCMinAndBuildings() {
        addPlayerWithShamanToList(playerA, 3, 2, 3);
        addPlayerWithShamanToList(playerB, 3, 2, 3);
        addPlayerWithShamanToList(playerC, 2, 2, 2);

        playerA.addBuilding(new BuildingType8());
        playerC.addBuilding(new BuildingType12());

        ritualEvent.computeScore(list);

        // not doubling points due to tie
        assertEquals(10, playerA.getPp());
        assertEquals(10, playerB.getPp());
        assertEquals(0, playerC.getPp());
    }

    @Test
    void shouldABMaxCMinNoBuildings() {
        addPlayerWithShamanToList(playerA, 3, 2, 3);
        addPlayerWithShamanToList(playerB, 3, 2, 3);
        addPlayerWithShamanToList(playerC, 2, 2, 2);

        ritualEvent.computeScore(list);

        assertEquals(10, playerA.getPp());
        assertEquals(10, playerB.getPp());
        assertEquals(-5, playerC.getPp());
    }

    @Test
    void shouldAMaxBCMinAndBuildings() {
        addPlayerWithShamanToList(playerA, 3, 2, 3);
        addPlayerWithShamanToList(playerB, 2, 2, 2);
        addPlayerWithShamanToList(playerC, 2, 2, 2);

        playerA.addBuilding(new BuildingType8());
        playerC.addBuilding(new BuildingType12());

        ritualEvent.computeScore(list);

        assertEquals(20, playerA.getPp());
        assertEquals(-5, playerB.getPp());
        assertEquals(0, playerC.getPp());
    }

    @Test
    void shouldAMaxBCMinAndNoBuildings() {
        addPlayerWithShamanToList(playerA, 3, 2, 3);
        addPlayerWithShamanToList(playerB, 2, 2, 2);
        addPlayerWithShamanToList(playerC, 2, 2, 2);

        ritualEvent.computeScore(list);

        assertEquals(10, playerA.getPp());
        assertEquals(-5, playerB.getPp());
        assertEquals(-5, playerC.getPp());
    }

    @Test
    void shouldIgnoreIntermediatePlayers() {
        addPlayerWithShamanToList(playerA, 3, 2, 3);
        addPlayerWithShamanToList(playerB, 2, 2, 2);
        addPlayerWithShamanToList(playerC, 1, 1, 1);

        ritualEvent.computeScore(list);

        assertEquals(10, playerA.getPp());
        assertEquals(0, playerB.getPp());
        assertEquals(-5, playerC.getPp());
    }
}
package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.GameCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    Game game;
    @BeforeEach
    void setUp() {
        game = new Game(3);
    }

    //to do - metodo da sistemare nel game
    @Test
    void testGetNextPlayer(){

    }

    @Test
    void testAddPlayer(){
        // player creation
        Player p = new Player("player1",Color.BLACK);
        Player p2 = new Player("player2",Color.RED);
        Player p3 = new Player("player3",Color.YELLOW);
        Player p4 = new Player("player4",Color.BLUE);
        List<Player> players = new ArrayList<>();
        // add players to game
        game.addPlayer(p);
        game.addPlayer(p2);
        game.addPlayer(p3);
        // add players to list
        players.add(p);
        players.add(p2);
        players.add(p3);
        // check usual and unusual behavior
        assertEquals(players,game.getPlayers());
        assertThrows(IllegalStateException.class, () -> game.addPlayer(p4));
    }

    @Test
    void testStart(){
        game.start();
        //check if the game has started
        assertTrue(game.isStarted());
        //check currentEra
        assertEquals(game.getCurrentEra(),1);
        //check size lower row
        assertEquals(game.getLowerRowSize(), game.getNumPlayers()+1);
        //check size upper row
        assertEquals(game.getUpperRowSize(), game.getNumPlayers()+4);
        //check size upper building row
        if(game.getNumPlayers()>2){
            assertEquals(game.getUpperBuildingRowSize(), 2);
        }
        else {
            assertEquals(game.getUpperBuildingRowSize(), 1);
        }
    }

    @Test
    void testGetNextTurn(){
        game.start();
        // get current turn letter
        var currentLetter = game.getCurrentTurnLetter();
        //call method
        Player p = game.getNextTurn();
        // check that the player returned has the current letter
        assertEquals(Optional.of(p.getOfferingCard().getOrderLetter()),currentLetter);
        // check exception
        p = game.getNextTurn();
        p = game.getNextTurn();
        assertThrows(IllegalStateException.class, () -> game.getNextTurn());
    }

    //to do -- to fix
    @Test
    void testEndTurn(){
        game.start();
        var oldUpperRow = game.getUpperRow();
        int oldEra = game.getCurrentEra();
        game.endTurn();
        //new lower row equals to old upper row
        assertEquals(game.getLowerRow(),oldUpperRow);
        // check size new upper row
        assertEquals(game.getUpperRowSize(), game.getNumPlayers()+4);
        //capire bene quanti ne devo chiamare
        game.endTurn();
        game.endTurn();
        game.endTurn();
        // check update era
        if (oldEra != game.getCurrentEra())
            assertEquals(game.getCurrentEra(), oldEra+1);
    }

    @Test
    void testChangeEra(){
        game.start();
        var oldBuildingRow = game.getUpperBuildingRow();
        int currentEra = game.getCurrentEra();
        if(currentEra !=2 && currentEra != 3)
            assertThrows(IllegalStateException.class, () -> game.changeEra());
        game.changeEra();
        if(currentEra == 2)
        {
            //new lower building row contains old upper building row
            assertTrue(game.getLowerBuildingRow().contains(oldBuildingRow));
            //check if the new upper building row has only cards with the correct era
            for(BuildingCard card : game.getUpperBuildingRow())
                assertEquals(2, card.getEra());
        }
        else if (currentEra == 3) {
            //new lower building row equals to old upper building row
            assertEquals(game.getLowerBuildingRow(),oldBuildingRow);
            //check if the new upper building row has only cards with the correct era
            for(BuildingCard card : game.getUpperBuildingRow())
                assertEquals(3, card.getEra());
        }
    }

    @Test
    void testRemoveCardFromRow(){
        // create game card
        GameCard card;
        // draw a card from the upper row
        card = game.getCardFromUpperRow();
        game.removeCardFromRow(card);
        assertFalse(game.getUpperRow().getCards().contains(card));
        // draw a card from the lower row
        card = game.getCardFromLowerRow();
        game.removeCardFromRow(card);
        assertFalse(game.getLowerRow().getCards().contains(card));
        // create card
        card = new GameCard(1);
        GameCard finalCard = card;
        assertThrows(IllegalStateException.class, () -> game.removeCardFromRow(finalCard));
        // draw a card from the upper building row
        card = game.getCardFromUpperBuildingRow();
        game.removeCardFromRow(card);
        assertFalse(game.getUpperBuildingRow().getCards().contains(card));
        // draw a card from the lower building row
        card = game.getCardFromLowerBuildingRow();
        game.removeCardFromRow(card);
        assertFalse(game.getLowerBuildingRow().getCards().contains(card));
        // create building card
        card = new BuildingCard(1,1,1);
        GameCard finalCard1 = card;
        assertThrows(IllegalStateException.class, () -> game.removeCardFromRow(finalCard1));
    }

    // to do - metodo da sistemare in game
    @Test
    void testResolveEvent(){

    }

    // to do
    @Test
    void testEndGame(){

    }

}


package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.CardType;
import it.polimi.ingsw.am17.Model.GameCard.GameCard;
import it.polimi.ingsw.am17.Model.GameCard.TribesCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    Game game;
    @BeforeEach
    void setUp() {
        game = new Game(3);
    }

    @Test
    void testGetNextPlayer(){
        Player p1 = new Player("player1",Color.BLACK);
        Player p2 = new Player("player2",Color.RED);
        Player p3 = new Player("player3",Color.YELLOW);
        //sorted list: [p2,p1.p3]
        p1.setOfferingCard(new OfferingCard(2,'C',1,1,0));
        p2.setOfferingCard(new OfferingCard(2,'A',1,1,0));
        p3.setOfferingCard(new OfferingCard(2,'D',1,1,0));
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);
        //call method
        Player p = game.getNextPlayer();
        //check if the player has the lowest letter in the list
        assertEquals(p2, p);
        p = game.getNextPlayer();
        assertEquals(p1, p);
        p = game.getNextPlayer();
        assertEquals(p3, p);
        //if currentPlayerIndex > players.size() check if exception thrown
        assertThrows(IllegalStateException.class,() -> game.getNextPlayer());
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

    //non possibile provare perchè mancano building cards
    @Test
    void testStart_3players(){
        game.start();
        //check if the game has started
        assertTrue(game.isStarted());
        //check currentEra
        assertEquals(1, game.getCurrentEra());
        //check size lower row
        assertEquals(4,game.getLowerRowSize() );
        //check size upper row
        assertEquals( 7, game.getUpperRowSize());
        //check size upper building row
        assertEquals(2,game.getUpperBuildingRowSize());
        //check if decks are not null
        assertNotNull(game.getDeck());
        assertNotNull(game.getBuildingDeckEra1());
        assertNotNull(game.getBuildingDeckEra2());
        assertNotNull(game.getBuildingDeckEra3());
    }

    //non possibile provare perchè mancano building cards
    @Test
    void testStart_2players(){
        game = new Game(2);
        game.start();
        //check if the game has started
        assertTrue(game.isStarted());
        //check currentEra
        assertEquals(1, game.getCurrentEra());
        //check size lower row
        assertEquals(3,game.getLowerRowSize() );
        //check size upper row
        assertEquals( 6, game.getUpperRowSize());
        //check size upper building row
        assertEquals(1,game.getUpperBuildingRowSize());
        //check if decks are not null
        assertNotNull(game.getDeck());
        assertNotNull(game.getBuildingDeckEra1());
        assertNotNull(game.getBuildingDeckEra2());
        assertNotNull(game.getBuildingDeckEra3());
    }

    @Test
    void testGetNextTurn(){
        Player p1 = new Player("player1",Color.BLACK);
        Player p2 = new Player("player2",Color.RED);
        Player p3 = new Player("player3",Color.YELLOW);
        p1.setOfferingCard(new OfferingCard(2,'A',1,1,0));
        p2.setOfferingCard(new OfferingCard(2,'C',1,1,0));
        p3.setOfferingCard(new OfferingCard(2,'D',1,1,0));
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);
        // get current turn letter
        var currentLetter = game.getCurrentTurnLetter();
        //call method
        Player p = game.getNextTurn();
        // check that the player returned has the current letter
        assertEquals(p.getOfferingCard().getOrderLetter(),currentLetter);
        // check exception
        game.getNextTurn();
        game.getNextTurn();
        assertThrows(IllegalStateException.class, () -> game.getNextTurn());
    }

    //non possibile provare perchè deck è null
    //fare due test separati per scenario change era e non change era
    @Test
    void testEndTurn(){
        //game.start();
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

    //non possibile provare perchè buildingdeck null
    //dividere nei 3 scenari
    @Test
    void testChangeEra(){
        //game.start();
        var oldBuildingRow = game.getUpperBuildingRow();
        int currentEra = game.getCurrentEra();
        System.out.println("Current Era: "+currentEra);
        if(currentEra !=2 && currentEra != 3)
            assertThrows(IllegalStateException.class, () -> game.changeEra());
        game.setCurrentEra(2);
        game.changeEra();
        //new lower building row contains old upper building row
        assertTrue(game.getLowerBuildingRow().getCards().containsAll(oldBuildingRow.getCards()));
        //check if the new upper building row has only cards with the correct era
        for(GameCard card : game.getUpperBuildingRow().getCards())
            assertEquals(2, card.getEra());
        game.setCurrentEra(3);
        game.changeEra();
        //new lower building row equals to old upper building row
        assertEquals(game.getLowerBuildingRow(),oldBuildingRow);
        //check if the new upper building row has only cards with the correct era
        for(GameCard card : game.getUpperBuildingRow().getCards())
            assertEquals(3, card.getEra());
    }

    //non possibile provare perchè liste vuote
    //fai test separati
    @Test
    void testRemoveBuildingCardFromRow(){
        BuildingCard card;
        // draw a card from the upper building row
        card = (BuildingCard) game.getUpperBuildingRow().getCards().getFirst();
        game.removeBuildingCardFromRow(card);
        assertFalse(game.getUpperBuildingRow().getCards().contains(card));
        // draw a card from the lower building row
        card = (BuildingCard) game.getLowerBuildingRow().getCards().getFirst();
        game.removeBuildingCardFromRow(card);
        assertFalse(game.getLowerBuildingRow().getCards().contains(card));
        // create building card
        card = new BuildingCard(1,1,1);
        BuildingCard finalCard = card;
        assertThrows(IllegalStateException.class, () -> game.removeBuildingCardFromRow(finalCard));
    }

    //non possibile provare perchè liste vuote
    //fai test separati
    @Test
    void testRemoveTribeCardFromRow(){
        // create game card
        TribesCard card;
        // draw a card from the upper row
        card = (TribesCard) game.getUpperRow().getCards().getFirst();
        game.removeTribeCardFromRow(card);
        assertFalse(game.getUpperRow().getCards().contains(card));
        // draw a card from the lower row
        card = (TribesCard) game.getLowerRow().getCards().getFirst();
        game.removeTribeCardFromRow(card);
        assertFalse(game.getLowerRow().getCards().contains(card));
        // create card
        card = new TribesCard(1, CardType.ARTIST);
        TribesCard finalCard = card;
        assertThrows(IllegalStateException.class, () -> game.removeTribeCardFromRow(finalCard));
    }
}
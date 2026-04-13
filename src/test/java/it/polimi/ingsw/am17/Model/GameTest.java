package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    Game game;
    int numPlayers = 3;

    @BeforeEach
    void setUp() {
        game = new Game(3, numPlayers);
    }

    @Test
    void testGameCreation_0players(){
        // test exception when given invalid number of players
        assertThrows(IllegalArgumentException.class, () -> new Game(2, 0));
    }

    @Test
    void testGameCreation_6players(){
        // test exception when given invalid number of players
        assertThrows(IllegalArgumentException.class, () -> new Game(2, 6));
    }

    @Test
    void testAddPlayer_withGameStart() {
        // player creation
        Player p1 = new Player("player1", Color.BLACK);
        Player p2 = new Player("player2", Color.RED);
        Player p3 = new Player("player3", Color.YELLOW);
        // add players to game
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);
        // check that every player is in the list and the size is 3
        assertTrue(game.getPlayers().contains(p1));
        assertTrue(game.getPlayers().contains(p2));
        assertTrue(game.getPlayers().contains(p3));
        assertEquals(3, game.getPlayers().size());
        // check the game has started
        //check current era updated to 1
        assertEquals(1, game.getCurrentEra());
        //check size of upper and lower tribe rows
        assertEquals(4, game.getLowerRow().size());
        assertEquals(7, game.getUpperRow().size());
        //check that the lower row has only character cards
        for (TribesCard c : game.getLowerRow()) {
            assertTrue(c.getCardType().isCharacter());
        }
        //check the upper building row
        assertNotNull(game.getUpperBuildingRow());
        assertFalse(game.getUpperBuildingRow().isEmpty());
    }

    @Test
    void testAddPlayer_Duplicate() {
        // player creation
        Player p = new Player("player2", Color.RED);
        // add player to game
        game.addPlayer(p);
        // check duplicate player
        assertThrows(IllegalArgumentException.class, () -> game.addPlayer(p));
    }

    @Nested
    class GameStarted{
        private void setOfferingCardToPlayers(){
            // get offering card list
            List<OfferingCard> offeringCardList = game.getOfferingCards();
            // every player picks an offering card
            for (int i = 0; i < numPlayers; i++) {
                game.selectOfferingCard(game.getCurrentPlayer(), offeringCardList.get(i));
            }
        }

        private Player pickWrongPlayer(){
            return game.getPlayers().stream()
                    .filter(p -> !p.equals(game.getCurrentPlayer()))
                    .toList().getFirst();
        }

        private OfferingCard getCurrentOfferingCard(){
            return game.getOfferingCards().stream()
                    .filter(card -> card.getPlayer() != null)
                    .min(Comparator.comparing(OfferingCard::getOrderLetter))
                    .orElse(null);
        }

        private List<CharacterCard> extractLowerCharacters(int amount) {
            return game.getLowerRow().stream()
                    .filter(c -> c.getCardType().isCharacter())
                    .map(c -> (CharacterCard) c)
                    .limit(amount)
                    .toList();
        }

        private List<CharacterCard> extractUpperCharacters(int amount) {
            return game.getUpperRow().stream()
                    .filter(c -> c.getCardType().isCharacter())
                    .map(c -> (CharacterCard) c)
                    .limit(amount)
                    .toList();
        }

        private List<BuildingCard> extractUpperBuildings(int amount) {
            return game.getUpperBuildingRow().stream()
                    .limit(amount)
                    .toList();
        }

        private List<BuildingCard> extractLowerBuildings(int amount) {
            return game.getUpperBuildingRow().stream()
                    .limit(amount)
                    .toList();
        }

        @BeforeEach
        void startGame() {
            // add players to game
            game.addPlayer(new Player("player1", Color.BLACK));
            game.addPlayer(new Player("player2", Color.RED));
            game.addPlayer(new Player("player3", Color.YELLOW));
            //game started automatically
        }

        @Test
        void testAddPlayer_Exception() {
            //this checks both started and overflow cases
            assertThrows(IllegalStateException.class, () -> game.addPlayer(new Player("player4", Color.BLUE)));
        }

        @Test
        void testEndRound_Normal() {
            List<TribesCard> oldUpperRow = new ArrayList<>(game.getUpperRow());
            game.endRound();
            //check that the lower row equals the old upper row
            assertEquals(oldUpperRow.size(), game.getLowerRow().size());
            assertTrue(oldUpperRow.containsAll(game.getLowerRow()));
            //check that the new upper row has the right size
            assertEquals(7, game.getUpperRow().size());
        }

        @Test
        void testEndRound_ChangeEra_2() {
            // play 2 rounds
            game.endRound();
            game.endRound();
            //get upper building row value
            List<BuildingCard> oldUpperBuildingRow = new ArrayList<>(game.getUpperBuildingRow());
            assertEquals(1, game.getCurrentEra());
            //play one more round
            game.endRound();
            // check era changed to 2
            assertEquals(2, game.getCurrentEra());
            // check the building rows
            assertEquals(oldUpperBuildingRow.size(), game.getLowerBuildingRow().size());
            assertTrue(oldUpperBuildingRow.containsAll(game.getLowerBuildingRow()));
            assertNotNull(game.getUpperBuildingRow());
            assertFalse(game.getUpperBuildingRow().isEmpty());
        }

        @Test
        void testEndRound_ChangeEra_3() {
            //play turns
            for (int i = 0; i < 6; i++) {
                game.endRound();
            }
            //get upper building row value
            List<BuildingCard> oldUpperBuildingRow = new ArrayList<>(game.getUpperBuildingRow());
            assertEquals(2, game.getCurrentEra());
            game.endRound();
            // check era changed to 3
            assertEquals(3, game.getCurrentEra());
            // check the building rows
            assertEquals(oldUpperBuildingRow.size(), game.getLowerBuildingRow().size());
            assertTrue(oldUpperBuildingRow.containsAll(game.getLowerBuildingRow()));
            assertNotNull(game.getUpperBuildingRow());
            assertFalse(game.getUpperBuildingRow().isEmpty());
        }

        @Test
        void testEndRound_EndGame() {
            //play turns
            game.endRound();
            game.endRound();
            assertEquals(1, game.getCurrentEra());
            game.endRound();
            assertEquals(2, game.getCurrentEra());
            game.endRound();
            game.endRound();
            game.endRound();
            assertEquals(2, game.getCurrentEra());
            game.endRound();
            assertEquals(3, game.getCurrentEra());
            game.endRound();
            game.endRound();
            assertEquals(3, game.getCurrentEra());
            game.endRound();
            //check if the game has ended
            assertEquals(-1, game.getCurrentEra());
        }

        @Test
        void testSelectOfferingCard_TurnError() {
            // game starts
            Player wrongPlayer = pickWrongPlayer();
            // get offering card list
            List<OfferingCard> offeringCardList = game.getOfferingCards();
            assertThrows(IllegalStateException.class, () -> game.selectOfferingCard(wrongPlayer, offeringCardList.getFirst()));
        }

        @Test
        void testSelectOfferingCard_Normal() {
            // get offering card list
            List<OfferingCard> offeringCardList = game.getOfferingCards();
            // get the current player before the method picks another one
            Player currentPlayer = game.getCurrentPlayer();
            //call method
            game.selectOfferingCard(currentPlayer, offeringCardList.getFirst());
            assertEquals(currentPlayer, offeringCardList.getFirst().getPlayer());
        }


        @Test
        void testSelectOfferingCard_CardError() {
            // get offering card list
            List<OfferingCard> offeringCardList = game.getOfferingCards();
            // get the current player before the method picks another one
            Player currentPlayer = game.getCurrentPlayer();
            // call method
            game.selectOfferingCard(currentPlayer, offeringCardList.getFirst());
            // test card already picked error
            Player finalCurrentPlayer = game.getCurrentPlayer();
            assertThrows(IllegalStateException.class, () -> game.selectOfferingCard(finalCurrentPlayer, offeringCardList.getFirst()));
            // test card null error
            assertThrows(IllegalStateException.class, () -> game.selectOfferingCard(finalCurrentPlayer, null));
        }

        @Test
        void testSelectOfferingCard_CardNotFound(){
            // test offering card not if offeringCardList
            assertThrows(IllegalStateException.class, () -> game.selectOfferingCard(game.getCurrentPlayer(),
                    new OfferingCard(5,'A',0,0,0)));
        }

        @Test
        void testSelectOfferingCard_EmptyStack() {
            setOfferingCardToPlayers();
            // check the new order of the players
            // returns the card with the lowest letter
            OfferingCard firstCardTurn = getCurrentOfferingCard();
            //check that the current player has the card with the lowest letter
            assertEquals(game.getCurrentPlayer(), firstCardTurn.getPlayer());
        }

        @Test
        void testPlayerAction_PlayerError() {
            setOfferingCardToPlayers();
            //the wrong player tries to select cards
            Player wrongPlayer = pickWrongPlayer();
            assertThrows(IllegalStateException.class,
                    () -> game.pickTribeCards(wrongPlayer, Collections.emptyList(), Collections.emptyList()));
        }

        @Test
        void testPlayerAction_ValidateErrorCharacter() {
            setOfferingCardToPlayers();
            // get offering card list
            List<OfferingCard> offeringCardList = game.getOfferingCards();
            // offering card 0: 1 card from lower row
            // the player selects some upper cards instead of lower ones
            int lowerSize = offeringCardList.getFirst().getNumCardsLower();
            List<CharacterCard> characterList = new ArrayList<>(extractUpperCharacters(lowerSize));
            assertThrows(IllegalStateException.class,
                    () -> game.pickTribeCards(game.getCurrentPlayer(), characterList, Collections.emptyList()));
            characterList.clear();
            // offering card 1: 1 card from upper row
            int upperSize = offeringCardList.get(1).getNumCardsUpper();
            // the player selects lower cards instead of upper ones
            characterList.addAll(extractLowerCharacters(upperSize));
            assertThrows(IllegalStateException.class,
                    () -> game.pickTribeCards(
                            offeringCardList.get(1).getPlayer(),
                            characterList,
                            Collections.emptyList()));
        }

        @Test
        void testPlayerAction_ValidateErrorBuilding() {
            setOfferingCardToPlayers();
            // get offering card list
            List<OfferingCard> offeringCardList = game.getOfferingCards();
            List<BuildingCard> buildingList = new ArrayList<>();
            // offering card 0: 1 card from lower row
            int lowerSize = offeringCardList.getFirst().getNumCardsLower();
            // the player selects some upper cards instead of lower ones
            if (!game.getUpperBuildingRow().isEmpty()) {
                buildingList.addAll(extractUpperBuildings(lowerSize));
                assertThrows(IllegalStateException.class,
                        () -> game.pickTribeCards(game.getCurrentPlayer(), Collections.emptyList(), buildingList));
            }
            // clear the choice list
            buildingList.clear();
            // offering card 1: 1 card from upper row
            int upperSize = offeringCardList.get(1).getNumCardsUpper();
            // the player selects lower cards instead of upper ones
            if (!game.getLowerBuildingRow().isEmpty()) {
                buildingList.addAll(extractLowerBuildings(upperSize));
                assertThrows(IllegalStateException.class,
                        () -> game.pickTribeCards(
                                offeringCardList.get(1).getPlayer(),
                                Collections.emptyList(),
                                buildingList));
            }
        }

        @Test
        void testPlayerAction_ValidateErrorNumber(){
            setOfferingCardToPlayers();
            List<BuildingCard> buildingList = new ArrayList<>();
            int wrongNumUpper = game.getOfferingCards().getFirst().getNumCardsUpper()+1;
            int wrongNumLower = game.getOfferingCards().getFirst().getNumCardsLower()+1;
            // select the wrong number of upper cards
            if(!game.getUpperBuildingRow().isEmpty()) {
                buildingList.addAll(extractUpperBuildings(wrongNumUpper));
            }
            //select the wrong number of lower cards
            List<CharacterCard> characterList = new ArrayList<>(extractLowerCharacters(wrongNumLower));
            assertThrows(IllegalStateException.class, () -> game.pickTribeCards(game.getCurrentPlayer(),characterList, buildingList));
        }

        @Test
        void testPlayerAction_CardNotFound(){
            setOfferingCardToPlayers();
            // offering card 0: 1 card from lower row
            List<CharacterCard> characterList = new ArrayList<>();
            // add a random card to list
            characterList.add(new Binder(2,4));
            assertThrows(IllegalStateException.class, () -> game.pickTribeCards(game.getCurrentPlayer(), characterList, Collections.emptyList()));
        }

        @Test
        void testPlayerAction_FoodError(){
            setOfferingCardToPlayers();
            // get offering card list
            List<OfferingCard> offeringCardList = game.getOfferingCards();
            // play first turn without checking because the lower building row is empty
            // first offering card: 1 card from the lower row
            // the first player picks character cards
            // selection from lower row
            List<CharacterCard> characterList = new ArrayList<>(extractLowerCharacters(offeringCardList.getFirst().getNumCardsLower()));
            // call method for the first time
            game.pickTribeCards(game.getCurrentPlayer(),characterList,Collections.emptyList());
            // second offering card: 1 card from the upper row
            List<BuildingCard> buildingList = new ArrayList<>();
            int numUpper =  offeringCardList.get(1).getNumCardsUpper();
            // the player selects buildings from the upper row
            if(!game.getUpperBuildingRow().isEmpty()) {
                buildingList.addAll(extractUpperBuildings(numUpper));
            }
            assertThrows(IllegalStateException.class,
                    () -> game.pickTribeCards(game.getCurrentPlayer(), Collections.emptyList(), buildingList));
        }

        @Test
        void testPlayerAction_Normal(){
            setOfferingCardToPlayers();
            // get offering card list
            List<OfferingCard> offeringCardList = game.getOfferingCards();
            // first offering card: 1 card from the lower row
            // the first player picks character cards
            // selection from lower row
            List<CharacterCard> characterList = new ArrayList<>(extractLowerCharacters(offeringCardList.getFirst().getNumCardsLower()));
            // call method for the first time to check character
            game.pickTribeCards(game.getCurrentPlayer(),characterList,Collections.emptyList());
            // check that the player is removed from the offering card
            assertNull(offeringCardList.getFirst().getPlayer());
            // check that the game rows do not contain the selected cards
            assertTrue(Collections.disjoint(game.getLowerRow(), characterList));

            // second offering card: 1 card from the upper row
            Player nextPlayer = getCurrentOfferingCard().getPlayer();
            // the second player picks building cards
            // selection from upper row
            List<BuildingCard> buildingList = new ArrayList<>(extractUpperBuildings(offeringCardList.get(1).getNumCardsUpper()));
            // add food to buy building
            nextPlayer.addFood(10);
            // call method for testing
            game.pickTribeCards(nextPlayer,Collections.emptyList(),buildingList);
            // check that the player is removed from the offering card
            assertNull(offeringCardList.get(1).getPlayer());
            // check that the game rows do not contain the selected cards
            assertTrue(Collections.disjoint(game.getUpperBuildingRow(), buildingList));

            // third offering card: 2 cards from the lower row
            nextPlayer = getCurrentOfferingCard().getPlayer();
            // the third player picks character cards
            characterList.clear();
            // selection from lower row
            characterList.addAll(extractLowerCharacters(offeringCardList.get(2).getNumCardsLower()));
            // call method
            game.pickTribeCards(nextPlayer,characterList,Collections.emptyList());
            // asserts
            assertNull(offeringCardList.get(2).getPlayer());
            assertTrue(Collections.disjoint(game.getLowerRow(), characterList));
            OfferingCard nextOfferingCard = getCurrentOfferingCard();
            assertNull(nextOfferingCard);
        }

        @Test
        void testPlayerAction_BuildingType2(){
            setOfferingCardToPlayers();
            // get offering card list
            List<OfferingCard> offeringCardList = game.getOfferingCards();
            // first offering card: 1 card from the lower row
            // the first player picks character cards
            // selection from lower row
            List<CharacterCard> characterList = new ArrayList<>(extractLowerCharacters(offeringCardList.getFirst().getNumCardsLower()));
            // call method for the first time to check character
            game.pickTribeCards(game.getCurrentPlayer(),characterList,Collections.emptyList());
            // check that the player is removed from the offering card
            assertNull(offeringCardList.getFirst().getPlayer());
            // check that the game rows do not contain the selected cards
            assertTrue(Collections.disjoint(game.getLowerRow(), characterList));

            // second offering card: 1 card from the upper row
            Player nextPlayer = getCurrentOfferingCard().getPlayer();
            // the second player picks building cards
            // selection from upper row
            List<BuildingCard> buildingList = new ArrayList<>(extractUpperBuildings(offeringCardList.get(1).getNumCardsUpper()));
            // add food to buy building
            nextPlayer.addFood(10);
            // call method for testing
            game.pickTribeCards(nextPlayer,Collections.emptyList(),buildingList);
            // check that the player is removed from the offering card
            assertNull(offeringCardList.get(1).getPlayer());
            // check that the game rows do not contain the selected cards
            assertTrue(Collections.disjoint(game.getUpperBuildingRow(), buildingList));

            // third offering card: 2 cards from the lower row
            nextPlayer = getCurrentOfferingCard().getPlayer();
            // the third player picks character cards
            characterList.clear();
            // selection from lower row
            characterList.addAll(extractLowerCharacters(offeringCardList.get(2).getNumCardsLower()));
            // insert building type 2 into player
            nextPlayer.addBuilding(new BuildingType2());
            // call method
            game.pickTribeCards(nextPlayer,characterList,Collections.emptyList());
            // asserts
            assertNull(offeringCardList.get(2).getPlayer());
            assertTrue(Collections.disjoint(game.getLowerRow(), characterList));
            // the player can pick another card from the upper row
            characterList.clear();
            characterList.addAll(extractUpperCharacters(1));
            game.pickTribeCards(nextPlayer,characterList,Collections.emptyList());
            assertTrue(Collections.disjoint(game.getUpperRow(), characterList));
        }
    }
}
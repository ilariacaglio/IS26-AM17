package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.BuildingCard;
import it.polimi.ingsw.am17.Model.GameCard.CharacterCard;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ControllerSimulatorTest {
    @Test
    void fullGameTest() { // From Controller POV

        // client connects
        // client picks to create game (provides numPlayer)

        int numPlayers = 3;
        Game game = new Game(1, numPlayers);

        game.addPlayer(new Player("player1", Color.BLACK));
        // after each player is added, all clients are notified to show e.g. 1/3

        // client connects
        // client picks to join game
        game.addPlayer(new Player("player2", Color.RED));
        game.addPlayer(new Player("player3", Color.YELLOW));

        // games start automatically when numPlayers is reached
        // clients are notified with the new state (player whose turn is, cards on the table...) TODO

        // ROUNDS
        while (true) {
            // 1. Offering card selection
            for (Player player : game.getPlayers()) {
                Random rand = new Random();
                OfferingCard offeringCard = game.getOfferingCards().get(rand.nextInt(game.getOfferingCards().size()));
                game.selectOfferingCard(player, offeringCard);
                System.out.println(player.getNickname() + " chooses offeringCard " + offeringCard.getOrderLetter());
            }

            // 2. Player actions (pick tribe cards)
            for (Player player : game.getPlayers()) {
                List<CharacterCard> characterCards = new ArrayList<>();
                List<BuildingCard> buildingCards = new ArrayList<>();
                game.selectTribeCards(player, characterCards, buildingCards); // controller POV
            }

            // endRound will end game if no cards are available
            game.endRound();

            if (game.getCurrentEra() == -1) break; // game is ended
        }
    }
}

package it.polimi.ingsw.am17.Client.UserInterface.GUIElements;

import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Client.UserInterface.GUI;
import it.polimi.ingsw.am17.Server.Model.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

/**
 * Draws the interface that displays the local ranking at the end of the game
 */
public class LocalRankingView {
    private VBox root;
    private final GUI mainGui;
    private final ClientModel game;

    public LocalRankingView(GUI mainGui, ClientModel game) {
        this.mainGui = mainGui;
        this.game = game;
        buildUI();
    }

    /**
     * Generates the interface for the final ranking after the end of the game
     */
    private void buildUI() {
        root = new VBox(ScreenScale.size(20));
        root.setPadding(new Insets(ScreenScale.size(30)));
        root.setAlignment(Pos.CENTER);
        String encodedCss = Base64.getEncoder().encodeToString(getCustomCSS().getBytes(StandardCharsets.UTF_8));
        root.getStylesheets().add("data:text/css;base64," + encodedCss);
        root.getStyleClass().add("ranking-root");
        //title
        Label rankingTitle = new Label("FINAL GAME RANKING");
        rankingTitle.getStyleClass().add("title-label");
        //ranking area
        VBox localRankingArea = new VBox(ScreenScale.size(10));
        localRankingArea.setAlignment(Pos.CENTER);
        localRankingArea.setMaxWidth(ScreenScale.size(500));
        localRankingArea.setPadding(new Insets(ScreenScale.size(30)));
        localRankingArea.getStyleClass().add("ranking-container");

        //get players final order
        List<Player> sortedPlayers = game.getOrderedPlayers().stream()
                .sorted(Comparator.comparingInt(Player::getPp).reversed())
                .toList();


        int rank = 1;

        for (Player player : sortedPlayers) {

            Label playerLabel = new Label(rank + "° place: " + player.getNickname() +
                    " | Points: " + player.getPp());

            playerLabel.getStyleClass().add("player-label");
            // special styles for the podium
            if (rank == 1) {
                playerLabel.getStyleClass().add("player-rank-1");
            } else if (rank == 2) {
                playerLabel.getStyleClass().add("player-rank-2");
            } else if (rank == 3) {
                playerLabel.getStyleClass().add("player-rank-3");
            } else {
                playerLabel.getStyleClass().add("player-rank-other");
            }

            //center text within the label
            playerLabel.setMaxWidth(Double.MAX_VALUE);
            playerLabel.setAlignment(Pos.CENTER);

            localRankingArea.getChildren().add(playerLabel);

            rank++;
        }

        VBox.setVgrow(localRankingArea, Priority.ALWAYS);

        //button to global ranking interface
        Button goToGlobalRanking = new Button("GO TO GLOBAL RANKING");
        goToGlobalRanking.getStyleClass().add("action-button");

        goToGlobalRanking.setOnAction(_ -> mainGui.showGlobalInterface());

        root.getChildren().addAll(rankingTitle,localRankingArea, goToGlobalRanking);
    }

    /**
     * @return the custom CSS for the local ranking view
     */
    private String getCustomCSS(){
        return """
    .ranking-root {
        -fx-background-color: linear-gradient(to bottom, #141E30, #243B55);
    }
    .title-label {
        -fx-text-fill: #f4dca6;
        -fx-font-size: %dpx;
        -fx-font-weight: bold;
        -fx-font-family: 'Verdana';
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 10, 0, 0, 3);
    }
    .ranking-container {
        -fx-background-color: rgba(255, 255, 255, 0.95);
        -fx-background-radius: %dpx;
        -fx-border-color: #c76b22;
        -fx-border-radius: %dpx;
        -fx-border-width: %dpx;
        -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 15, 0, 0, 5);
    }
    .player-label {
        -fx-padding: %dpx %dpx;
        -fx-background-radius: %dpx;
        -fx-font-size: %dpx;
        -fx-font-family: "Segoe UI", "Helvetica Neue", Arial, sans-serif;
        -fx-font-weight: bold;
    }
    .player-rank-1 {
        -fx-background-color: linear-gradient(to right, #FFDF00, #D4AF37);
        -fx-text-fill: #5c4000;
    }
    .player-rank-2 {
        -fx-background-color: linear-gradient(to right, #E0E0E0, #9E9E9E);
        -fx-text-fill: #2c3e50;
    }
    .player-rank-3 {
        -fx-background-color: linear-gradient(to right, #CD7F32, #A0522D);
        -fx-text-fill: #ffffff;
    }
    .player-rank-other {
        -fx-background-color: #ecf0f1;
        -fx-text-fill: #34495e;
    }
    .action-button {
        -fx-background-color: linear-gradient(to bottom, #c76b22, #8e4713);
        -fx-text-fill: #f4dca6;
        -fx-font-size: %dpx;
        -fx-font-weight: bold;
        -fx-padding: %dpx %dpx;
        -fx-background-radius: %dpx;
        -fx-cursor: hand;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 3);
    }
    .action-button:hover {
        -fx-background-color: linear-gradient(to bottom, #d97f35, #a3561a);
        -fx-text-fill: #ffffff;
    }
    .action-button:pressed {
        -fx-background-color: #5c2c16;
        -fx-translate-y: %dpx;
        -fx-effect: none;
    }
    """.formatted(
                ScreenScale.sizeInt(42),
                ScreenScale.sizeInt(15),
                ScreenScale.sizeInt(15),
                ScreenScale.sizeInt(3),
                ScreenScale.sizeInt(12),
                ScreenScale.sizeInt(25),
                ScreenScale.sizeInt(8),
                ScreenScale.sizeInt(22),
                ScreenScale.sizeInt(20),
                ScreenScale.sizeInt(12),
                ScreenScale.sizeInt(30),
                ScreenScale.sizeInt(25),
                ScreenScale.sizeInt(2)
        );
    }

    // The main GUI will call this to put it in the Scene
    public Parent getRoot() {
        return root;
    }
}

package it.polimi.ingsw.am17.Client.UserInterface.GUIElements;

import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Client.UserInterface.GUI;
import it.polimi.ingsw.am17.Server.Model.Player;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Comparator;
import java.util.List;

public class LocalRankingView {
    private VBox root;
    private GUI mainGui;
    private ClientModel game;

    public LocalRankingView(GUI mainGui, ClientModel game) {
        this.mainGui = mainGui;
        this.game = game;
        buildUI();
    }
    //generates the interface for the final ranking after the end of the game
    private void buildUI() {
        root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        //background color
        root.setStyle("-fx-background-color: #2c3e50;");
        //title
        Label rankingTitle = new Label("--- FINAL GAME RANKING ---");
        rankingTitle.setStyle("""
            -fx-text-fill: #ecf0f1;
            -fx-font-size: 30px;
            -fx-font-weight: bold;
        """);
        //ranking area
        VBox localRankingArea = new VBox(10);
        localRankingArea.setAlignment(Pos.CENTER);
        localRankingArea.setMaxWidth(400);
        localRankingArea.setPadding(new Insets(20));
        localRankingArea.setStyle("""
            -fx-background-color: #ecf0f1;
            -fx-background-radius: 10px;
            -fx-border-color: #bdc3c7;
            -fx-border-radius: 10px;
            -fx-border-width: 2px;
        """);

        //get players final order
        List<Player> sortedPlayers = game.getOrderedPlayers().stream()
                .sorted(Comparator.comparingInt(Player::getPp).reversed())
                .toList();


        int rank = 1;

        for (Player player : sortedPlayers) {

            Label playerLabel = new Label(rank + "° place: " + player.getNickname() +
                    " - Points: " + player.getPp());

            playerLabel.setStyle("""
                -fx-font-size: 24px;
                -fx-font-family: 'Consolas';
                -fx-text-fill: #2c3e50;
            """);
            //center text within the label
            playerLabel.setMaxWidth(Double.MAX_VALUE);
            playerLabel.setAlignment(Pos.CENTER);

            localRankingArea.getChildren().add(playerLabel);

            rank++;
        }

        VBox.setVgrow(localRankingArea, Priority.ALWAYS);

        //button to global ranking interface
        Button goToGlobalRanking = new Button("GO TO GLOBAL RANKING");
        goToGlobalRanking.setStyle("""
            -fx-font-size: 18px;
            -fx-font-weight: bold;
            -fx-padding: 10px 20px;
        """);

        goToGlobalRanking.setOnAction(e -> {
            mainGui.showGlobalInterface();
        });

        root.getChildren().addAll(rankingTitle,localRankingArea, goToGlobalRanking);
    }

    // The main GUI will call this to put it in the Scene
    public Parent getRoot() {
        return root;
    }
}

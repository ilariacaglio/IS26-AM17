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

    private void buildUI() {
        root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        //title
        Label rankingTitle = new Label("--- FINAL GAME RANKING ---");
        rankingTitle.setStyle("""
            -fx-text-fill: black;
            -fx-font-size: 30px;
            -fx-font-weight: bold;
        """);
        //ranking area
        TextArea localRankingArea = new TextArea();

        localRankingArea.setEditable(false);
        localRankingArea.setWrapText(true);

        localRankingArea.setStyle("""
            -fx-font-size: 18px;
            -fx-control-inner-background: #f4f4f4;
            -fx-font-family: 'Consolas';
        """);

        //fill ranking
        List<Player> sortedPlayers = game.getOrderedPlayers().stream()
                .sorted(Comparator.comparingInt(Player::getPp).reversed())
                .toList();

        StringBuilder rankingText = new StringBuilder();

        int rank = 1;

        for (Player player : sortedPlayers) {

            rankingText.append(rank)
                    .append("° place: ")
                    .append(player.getNickname())
                    .append(" - Points: ")
                    .append(player.getPp())
                    .append("\n");

            rank++;
        }

        localRankingArea.setText(rankingText.toString());

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

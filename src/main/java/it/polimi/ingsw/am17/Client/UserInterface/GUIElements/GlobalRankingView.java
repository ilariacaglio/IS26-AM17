package it.polimi.ingsw.am17.Client.UserInterface.GUIElements;

import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Client.UserInterface.GUI;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Server.Utility.RankingEntry;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Comparator;
import java.util.List;

public class GlobalRankingView {
    private VBox root;
    private GUI mainGui;
    private ClientModel game;
    private Player localPlayer;

    public GlobalRankingView(GUI mainGui, ClientModel game, Player localPlayer) {
        this.mainGui = mainGui;
        this.game = game;
        this.localPlayer = localPlayer;
        buildUI();
    }

    private void buildUI() {
        root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        //title
        Label globalRankingTitle = new Label("\n--- YOUR POSITION IN GLOBAL RANKING ---");
        globalRankingTitle.setStyle("""
            -fx-text-fill: black;
            -fx-font-size: 30px;
            -fx-font-weight: bold;
        """);
        //ranking area
        TextArea globalRankingArea = new TextArea();

        globalRankingArea.setEditable(false);
        globalRankingArea.setWrapText(true);

        globalRankingArea.setStyle("""
            -fx-font-size: 18px;
            -fx-control-inner-background: #f4f4f4;
            -fx-font-family: 'Consolas';
        """);
        //fill ranking
        List<RankingEntry> globalRanking = game.getRanking();

        StringBuilder globalRankingText = new StringBuilder();

        if (!globalRanking.isEmpty()) {
            globalRankingText.append("--- YOUR POSITION IN GLOBAL RANKING ---\n\n");
            RankingEntry userEntry = globalRanking.stream()
                    .filter(e -> e.getGameId().equals(game.getGameId())
                            && e.getNickname().equals(localPlayer.getNickname()))
                    .findFirst()
                    .orElse(null);

            if (userEntry != null) {
                int pos = globalRanking.indexOf(userEntry) + 1;

                globalRankingText.append(pos)
                        .append(")\t")
                        .append(userEntry.getNickname())
                        .append("\t")
                        .append(userEntry.getFinalPoints())
                        .append("\n\n");
            } else {
                globalRankingText.append("Player data not found!\n\n");
            }

            // classifica globale completa
            globalRankingText.append("--- GLOBAL RANKING ---\n");
            globalRankingText.append("N.\tNICKNAME\t\tSCORE\n");

            int rank = 1;

            for (RankingEntry entry : globalRanking) {

                globalRankingText.append(rank)
                        .append(")\t")
                        .append(entry.getNickname())
                        .append("\t\t")
                        .append(entry.getFinalPoints())
                        .append("\n");

                rank++;
            }
        }

        globalRankingArea.setText(globalRankingText.toString());

        VBox.setVgrow(globalRankingArea, Priority.ALWAYS);


        //button to global ranking interface
        Button end = new Button("END");
        end.setStyle("""
            -fx-font-size: 18px;
            -fx-font-weight: bold;
            -fx-padding: 10px 20px;
        """);
        //close game
        end.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });

        root.getChildren().addAll(globalRankingTitle, globalRankingArea, end);
    }

    // The main GUI will call this to put it in the Scene
    public Parent getRoot() {
        return root;
    }
}

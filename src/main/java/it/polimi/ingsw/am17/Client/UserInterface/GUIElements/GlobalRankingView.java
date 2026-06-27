package it.polimi.ingsw.am17.Client.UserInterface.GUIElements;

import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Client.UserInterface.GUI;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Server.Utility.RankingEntry;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * Draws the interface to display the global ranking at the end of the game
 */
public class GlobalRankingView {
    private VBox root;
    private final GUI mainGui;
    private final ClientModel game;
    private final Player localPlayer;

    public GlobalRankingView(GUI mainGui, ClientModel game, Player localPlayer) {
        this.mainGui = mainGui;
        this.game = game;
        this.localPlayer = localPlayer;
        buildUI();
    }

    private void buildUI() {
        root = new VBox(ScreenScale.size(20));
        root.setPadding(new Insets(ScreenScale.size(30)));
        root.setAlignment(Pos.TOP_CENTER);
        String encodedCss = Base64.getEncoder().encodeToString(getCustomCSS().getBytes(StandardCharsets.UTF_8));
        root.getStylesheets().add("data:text/css;base64," + encodedCss);
        root.getStyleClass().add("ranking-root");
        //title
        Label globalRankingTitle = new Label("GLOBAL RANKING");
        globalRankingTitle.getStyleClass().add("title-label");

        // Ranking area container
        VBox rankingContainer = new VBox(ScreenScale.size(20));
        rankingContainer.setAlignment(Pos.CENTER);
        rankingContainer.setMaxWidth(ScreenScale.size(600));
        rankingContainer.setMaxHeight(ScreenScale.size(650));
        rankingContainer.setPadding(new Insets(ScreenScale.size(20)));
        rankingContainer.getStyleClass().add("ranking-container");

        List<RankingEntry> globalRanking = game.getRanking();

        if (!globalRanking.isEmpty()) {

            // Display local player data
            RankingEntry userEntry = globalRanking.stream()
                    .filter(e -> e.getGameId().equals(game.getGameId())
                            && e.getNickname().equals(localPlayer.getNickname()))
                    .findFirst()
                    .orElse(null);

            if (userEntry != null) {
                int pos = globalRanking.indexOf(userEntry) + 1;
                Label userLabel = new Label("YOUR POSITION: " + pos + "°  |  " + userEntry.getNickname() + "  |  Points: " + userEntry.getFinalPoints());
                userLabel.getStyleClass().addAll("player-label", "user-highlight");
                userLabel.setMaxWidth(Double.MAX_VALUE);
                userLabel.setAlignment(Pos.CENTER);
                rankingContainer.getChildren().add(userLabel);
            }

            Label separator = new Label("───────────────────────────────────");
            separator.setStyle("-fx-text-fill: #bdc3c7; -fx-font-weight: bold;");
            rankingContainer.getChildren().add(separator);

            // Global ranking display
            VBox listContainer = new VBox(10);
            listContainer.setAlignment(Pos.TOP_CENTER);

            int rank = 1;
            for (RankingEntry entry : globalRanking) {
                Label entryLabel = new Label(rank + "° Place: " + entry.getNickname() + "  |  Points: " + entry.getFinalPoints());
                entryLabel.getStyleClass().add("player-label");

                // Highlight podium
                if (rank == 1) entryLabel.getStyleClass().add("player-rank-1");
                else if (rank == 2) entryLabel.getStyleClass().add("player-rank-2");
                else if (rank == 3) entryLabel.getStyleClass().add("player-rank-3");
                else entryLabel.getStyleClass().add("player-rank-other");

                entryLabel.setMaxWidth(Double.MAX_VALUE);
                entryLabel.setAlignment(Pos.CENTER);
                listContainer.getChildren().add(entryLabel);

                rank++;
            }

            // Display list inside scroll pane
            ScrollPane scrollPane = new ScrollPane(listContainer);
            scrollPane.getStyleClass().add("pretty-scroll-pane");
            scrollPane.setFitToWidth(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

            VBox.setVgrow(scrollPane, Priority.ALWAYS);
            rankingContainer.getChildren().add(scrollPane);
        } else {
            Label noDataLabel = new Label("No ranking data available yet!");
            noDataLabel.getStyleClass().add("player-label");
            rankingContainer.getChildren().add(noDataLabel);
        }

        VBox.setVgrow(rankingContainer, Priority.ALWAYS);


        //button to global ranking interface
        Button goBackButton = new Button("GO BACK");
        goBackButton.getStyleClass().add("action-button");
        //go back to start page
        goBackButton.setOnAction(_ -> mainGui.showStartInterface());

        root.getChildren().addAll(globalRankingTitle, rankingContainer, goBackButton);
    }

    /**
     * @return the custom CSS for the global ranking view
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
    .user-highlight {
        -fx-background-color: #c76b22;
        -fx-text-fill: #ffffff;
        -fx-border-color: #8e4713;
        -fx-border-width: %dpx;
        -fx-border-radius: %dpx;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);
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
    .pretty-scroll-pane {
        -fx-background: transparent;
        -fx-background-color: transparent;
    }
    .pretty-scroll-pane .viewport {
        -fx-background-color: transparent;
    }
    .pretty-scroll-pane .scroll-bar:vertical {
        -fx-background-color: transparent;
        -fx-pref-width: %dpx;
    }
    .pretty-scroll-pane .scroll-bar:vertical .track {
        -fx-background-color: rgba(0, 0, 0, 0.1);
        -fx-background-radius: %dpx;
    }
    .pretty-scroll-pane .scroll-bar:vertical .thumb {
        -fx-background-color: rgba(199, 107, 34, 0.8);
        -fx-background-radius: %dpx;
    }
    .pretty-scroll-pane .scroll-bar:vertical .thumb:hover {
        -fx-background-color: rgba(244, 220, 166, 0.9);
    }
    .pretty-scroll-pane .scroll-bar:vertical .increment-button,
    .pretty-scroll-pane .scroll-bar:vertical .decrement-button {
        -fx-opacity: 0;
        -fx-pref-height: 0;
        -fx-padding: 0;
    }
    """.formatted(
                ScreenScale.sizeInt(36),
                ScreenScale.sizeInt(15),
                ScreenScale.sizeInt(15),
                ScreenScale.sizeInt(3),
                ScreenScale.sizeInt(12),
                ScreenScale.sizeInt(25),
                ScreenScale.sizeInt(8),
                ScreenScale.sizeInt(20),
                ScreenScale.sizeInt(2),
                ScreenScale.sizeInt(8),
                ScreenScale.sizeInt(20),
                ScreenScale.sizeInt(12),
                ScreenScale.sizeInt(30),
                ScreenScale.sizeInt(25),
                ScreenScale.sizeInt(2),
                ScreenScale.sizeInt(12),
                ScreenScale.sizeInt(10),
                ScreenScale.sizeInt(10)
        );
    }

    // The main GUI will call this to put it in the Scene
    public Parent getRoot() {
        return root;
    }
}

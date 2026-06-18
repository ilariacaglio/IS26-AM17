package it.polimi.ingsw.am17.Client.UserInterface.GUIElements;

import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Client.UserInterface.CardGUI;
import it.polimi.ingsw.am17.Client.UserInterface.GUI;
import it.polimi.ingsw.am17.CommonInterfaces.InvalidOperationException;
import it.polimi.ingsw.am17.Server.Model.Color;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.GameCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

/**
 * Draws the interface displayed when game is started.
 */
public class GameView {
    private VBox root;
    private final GUI mainGui;
    private VBox turnOverlay;
    private HBox upperCardsBox;
    private HBox lowerCardsBox;
    private HBox playerCardsBox;
    private HBox playerResourcesBox;
    private final List<CardGUI> offeringCardGUI = new ArrayList<>();
    private TurnCardGUI turnCard;
    private final ClientModel readOnlyModel;
    private final Player localPlayer;
    private Player selectedPlayer;
    private List<CharacterCard> tribesSelected = new ArrayList<>();
    private List<BuildingCard> buildingSelected = new ArrayList<>();
    private OfferingCard offeringSelected = null;

    public GameView(GUI mainGui, ClientModel readOnlyModel, Player localPlayer) {
        this.mainGui = mainGui;
        this.readOnlyModel = readOnlyModel;
        this.localPlayer = localPlayer;
        this.selectedPlayer = localPlayer;
        buildUI();
    }

    private void buildUI() {
        root = new VBox(10);

        setUpBackground();
        createTurnOverlay();

        HBox localPlayerNameBox = createLocalPlayerBox();

        // Upper cards
        upperCardsBox = new HBox(10);
        upperCardsBox.setAlignment(Pos.CENTER);

        HBox offeringCardBox = createOfferingCardBox();

        // Lower cards
        lowerCardsBox = new HBox(10);
        lowerCardsBox.setAlignment(Pos.CENTER);

        HBox sendButtonBox = createSendButtonBox();

        // Player sections
        VBox playersButtonBox = createOtherPlayersBox();
        VBox playerCardsContainer = createPersonalCardsBox();

        // Player resources
        playerResourcesBox = new HBox(10);
        createPlayerCardLabel();

        // Create a blank region to act as a spring/spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Add everything to root
        root.getChildren().addAll(
                turnOverlay, localPlayerNameBox, upperCardsBox, offeringCardBox,
                lowerCardsBox, sendButtonBox, playerResourcesBox,
                playerCardsContainer, spacer, playersButtonBox
        );
    }

    /**
     * Sets up the interface background
     */
    private void setUpBackground() {
        URL imageUrl = getClass().getResource("/Images/background_game.png");
        if (imageUrl == null) {
            throw new RuntimeException("Could not find image at /images/background_game.png inside resources!");
        }

        Image image = new Image(imageUrl.toExternalForm());

        BackgroundImage bgImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );

        root.setBackground(new Background(bgImage));
    }

    /**
     * Creates turn overlay for the interface
     */
    private void createTurnOverlay(){
        turnOverlay = new VBox();
        turnOverlay.setAlignment(Pos.CENTER);

        Label turnText = new Label("IT'S YOUR TURN!");
        turnText.setStyle("""
            -fx-background-color: rgba(0, 0, 0, 0.75);
            -fx-background-radius: 15px;
            -fx-padding: 5px 40px;
            -fx-border-color: #c76b22;
            -fx-border-radius: 15px;
            -fx-border-width: 2px;
            -fx-text-fill: #f4dca6;
            -fx-font-weight: bold;
            -fx-font-size: 36px;
            -fx-font-family: 'Verdana';
            """);

        DropShadow textShadow = new DropShadow();
        textShadow.setRadius(5.0);
        textShadow.setOffsetY(3.0);
        textShadow.setColor(javafx.scene.paint.Color.color(0, 0, 0, 0.8));
        turnText.setEffect(textShadow);

        turnOverlay.getChildren().add(turnText);
        turnOverlay.setVisible(readOnlyModel.isPlayerTurn(localPlayer));
    }

    /**
     * Creates the local player name label box
     */
    private HBox createLocalPlayerBox() {
        Label localPlayerName = new Label("Local Player: " + localPlayer.getNickname());
        localPlayerName.setStyle("""
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-font-size: 30px;
        """);
        HBox localPlayerNameBox = new HBox(10);
        localPlayerNameBox.getChildren().add(localPlayerName);
        return localPlayerNameBox;
    }

    /**
     * Creates box for turn order card and offering cards
     */
    private HBox createOfferingCardBox() {
        HBox offeringBox = new HBox(20);
        offeringBox.setAlignment(Pos.CENTER);

        // Turn Card first
        turnCard = new TurnCardGUI(readOnlyModel.getTURN_CARD_IMAGE_PATH(), readOnlyModel.getNumPlayers());
        offeringBox.getChildren().add(turnCard);

        // Offering Cards second
        for (OfferingCard card : readOnlyModel.getOfferingCards()) {
            CardGUI offeringCard;
            if (card.getPlayer() == null) {
                offeringCard = new CardGUI(card.getImagePath());
            } else {
                offeringCard = new CardGUI(card.getImagePath(), card.getPlayer().getColor().getFxColor());
            }
            offeringCard.setUserData(card);
            setOnMouseClickForOffering(offeringCard, card);
            offeringCardGUI.add(offeringCard);
            offeringBox.getChildren().add(offeringCard);
        }
        return offeringBox;
    }

    /**
     * Creates the Send button and its container
     */
    private HBox createSendButtonBox() {
        Button sendButton = new Button("SEND");
        sendButton.setStyle("""
            -fx-background-color: #5c2c16;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 5px;
            -fx-cursor: hand;
            -fx-font-size: 16px;
            -fx-padding: 10px 20px;
            """);

        sendButton.setOnAction(_ -> handleSendAction());

        HBox sendButtonBox = new HBox();
        sendButtonBox.getChildren().add(sendButton);
        sendButtonBox.setAlignment(Pos.CENTER);
        return sendButtonBox;
    }

    /**
     * Handles the logic for the Send button cleanly
     */
    private void handleSendAction() {
        try {
            if (offeringSelected != null) {
                readOnlyModel.validateOfferingCardTurnAction(localPlayer, offeringSelected.getOrderLetter());
                mainGui.pickOfferingCard(offeringSelected);
            } else {
                readOnlyModel.validateTribeCardsTurnAction(localPlayer, tribesSelected, buildingSelected);
                mainGui.pickTribeCards(tribesSelected, buildingSelected);
            }

            // Reset state on success
            offeringSelected = null;
            buildingSelected = new ArrayList<>();
            tribesSelected = new ArrayList<>();

        } catch (InvalidOperationException ex) {
            showErrorAlert(ex.getErrorType().getMessage());
        } catch (Exception ex) {
            showErrorAlert(ex.getMessage());
        }
    }

    /**
     * Helper to show error alerts
     */
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error in selection");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Creates the box containing other players' buttons and their cards
     */
    private VBox createOtherPlayersBox() {
        HBox playersCardsBox = new HBox(10);
        playersCardsBox.setAlignment(Pos.CENTER);

        HBox showCardsBox = new HBox(10);

        ScrollPane otherScrollPane = new ScrollPane(showCardsBox);
        otherScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        otherScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        otherScrollPane.setFitToHeight(true);
        otherScrollPane.setPannable(true);
        otherScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        for(Player p : readOnlyModel.getOrderedPlayers()){
            Button playerButton = new Button(p.getNickname());
            String colorName = p.getColor().name().toLowerCase();

            playerButton.setStyle("""
                -fx-background-color: linear-gradient(to bottom, #f5f7fa 0%%, #c3cfe2 100%%);
                -fx-background-radius: 25;
                -fx-padding: 12px 30px;
                -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.3), 10, 0, 0, 4);
                -fx-text-fill: %s;
                -fx-font-family: "Segoe UI", "Helvetica Neue", Arial, sans-serif;
                -fx-font-weight: bold;
                -fx-font-size: 18px;
                -fx-cursor: hand;
            """.formatted(colorName));

            playersCardsBox.getChildren().add(playerButton);
            StackPane.setAlignment(playersCardsBox, Pos.BOTTOM_CENTER);

            playerButton.setOnAction(_ -> {
                selectedPlayer = p;
                createPlayerCardLabel();
                updateSelectedPlayer();
            });
        }

        return new VBox(10, playersCardsBox, otherScrollPane);
    }

    /**
     * Creates the box containing the personal cards of the selected player
     */
    private VBox createPersonalCardsBox() {
        playerCardsBox = new HBox(10);
        playerCardsBox.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));

        ScrollPane scrollPane = new ScrollPane(playerCardsBox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(true);

        String inlineScrollBarCss = """
        .scroll-pane {
            -fx-background: transparent;
            -fx-background-color: transparent;
        }
        .scroll-pane .scroll-bar:horizontal {
            -fx-background-color: transparent;
            -fx-pref-height: 12px;
        }
        .scroll-pane .scroll-bar:horizontal .track {
            -fx-background-color: rgba(255, 255, 255, 0.15);
            -fx-background-radius: 10px;
        }
        .scroll-pane .scroll-bar:horizontal .thumb {
            -fx-background-color: rgba(199, 107, 34, 0.8);
            -fx-background-radius: 10px;
        }
        .scroll-pane .scroll-bar:horizontal .thumb:hover {
            -fx-background-color: rgba(244, 220, 166, 0.9);
        }
        .scroll-pane .scroll-bar:horizontal .increment-button,
        .scroll-pane .scroll-bar:horizontal .decrement-button {
            -fx-opacity: 0;
            -fx-pref-width: 0;
            -fx-padding: 0;
        }
        """;

        String encodedCss = Base64.getEncoder().encodeToString(inlineScrollBarCss.getBytes(StandardCharsets.UTF_8));
        scrollPane.getStylesheets().add("data:text/css;base64," + encodedCss);
        scrollPane.setMinHeight(CardGUI.getCardRectangleHeight() + 40);

        updateSelectedPlayer();

        return new VBox(10, scrollPane);
    }

    /**
     * update all graphics related to player queue
     */
    public void updatePlayerQueue(){
        // Update turn overlay visibility
        turnOverlay.setVisible(readOnlyModel.isPlayerTurn(localPlayer));
    }

    /**
     * call all other updates
     */
    public void updateGameElements() {
        this.tribesSelected.clear();
        this.buildingSelected.clear();
        this.offeringSelected = null;

        // Update turn overlay visibility
        turnOverlay.setVisible(readOnlyModel.isPlayerTurn(localPlayer));


        // Update Upper Cards
        updateUpperCards();

        //UpdateTurnCards
        List<Color> currentTurnOrder = readOnlyModel.getOrderedPlayers().stream()
                .map(Player::getColor)
                .toList();

        turnCard.placeTotems(currentTurnOrder);

        // Update Offering Cards
        updateOfferingCards();

        // Update Lower Cards
        updateLowerCards();

        // Update Player stats (Points, Food, Name) and personal board
        createPlayerCardLabel();
        updateSelectedPlayer();
    }

    /**
     * update upper and lower decks graphics by removing picked cards
     */
    public void updateGameCardDecks() {
        // Update turn overlay visibility
        turnOverlay.setVisible(readOnlyModel.isPlayerTurn(localPlayer));

        // Update Upper Cards
        upperCardsBox.getChildren().removeIf(node -> {
            GameCard cardData = (GameCard) node.getUserData();

            // Check if the card still exists in the game model
            // If it is NOT in the tribe row or NOT in the building row, remove it (return true)
            if (cardData.getIsBuilding()) {
                return !readOnlyModel.getUpperBuildingRow().contains(cardData);
            } else {
                return !readOnlyModel.getUpperTribeRow().contains(cardData);
            }
        });

        // Update Lower Cards
        lowerCardsBox.getChildren().removeIf(node -> {
            GameCard cardData = (GameCard) node.getUserData();

            // Check if the card still exists in the game model
            // If it is NOT in the tribe row or NOT in the building row, remove it (return true)
            if (cardData.getIsBuilding()) {
                return !readOnlyModel.getLowerBuildingRow().contains(cardData);
            } else {
                return !readOnlyModel.getLowerTribeRow().contains(cardData);
            }
        });

        // Update Player stats (Points, Food, Name) and personal board
        createPlayerCardLabel();
        updateSelectedPlayer();
    }

    /**
     * update offering deck's graphics
     */
    public void updateOfferingDeck(){
        // Update turn overlay visibility
        turnOverlay.setVisible(readOnlyModel.isPlayerTurn(localPlayer));

        for (CardGUI cardGUI : offeringCardGUI){
            if(cardGUI.isSelected())
                cardGUI.setVisualSelection(false);
        }
        for(int i=0; i<offeringCardGUI.size(); i++){
            if (i < readOnlyModel.getOfferingCards().size()) {
                CardGUI card = offeringCardGUI.get(i);
                Player p = readOnlyModel.getOfferingCards().get(i).getPlayer();
                if (p != null) {
                    card.updateTotem(p.getColor().getFxColor());
                    turnCard.removePlayerTotem(p.getColor());
                } else {
                    // Optional: Reset to default border if no player owns it
                    card.updateTotem(javafx.scene.paint.Color.TRANSPARENT);
                }
            }
        }
    }

    /**
     * update upper deck graphics
     */
    private void updateUpperCards() {
        upperCardsBox.getChildren().clear(); // Remove old cards
        for(TribesCard card : readOnlyModel.getUpperTribeRow()){
            CardGUI upperCard = new CardGUI(card.getImagePath());
            upperCard.setUserData(card);
            setOnMouseClickForTribes(upperCard, card);
            upperCardsBox.getChildren().add(upperCard);
        }
        for(BuildingCard card : readOnlyModel.getUpperBuildingRow()){
            CardGUI upperCard = new CardGUI(card.getImagePath());
            upperCard.setUserData(card);
            setOnMouseClickForBuilding(upperCard, card);
            upperCardsBox.getChildren().add(upperCard);
        }
    }

    /**
     * update lower deck graphics
     */
    private void updateLowerCards(){
        lowerCardsBox.getChildren().clear();
        for(TribesCard card : readOnlyModel.getLowerTribeRow()){
            CardGUI lowerCard = new CardGUI(card.getImagePath());
            lowerCard.setUserData(card);
            setOnMouseClickForTribes(lowerCard, card);
            lowerCardsBox.getChildren().add(lowerCard);
        }
        for(BuildingCard card : readOnlyModel.getLowerBuildingRow()){
            CardGUI lowerCard = new CardGUI(card.getImagePath());
            lowerCard.setUserData(card);
            setOnMouseClickForBuilding(lowerCard, card);
            lowerCardsBox.getChildren().add(lowerCard);
        }
    }


    /**
     * Resets offering card colored border and updates totem placement
     */
    private void updateOfferingCards(){

        //set all card to not selected
        for (CardGUI cardGUI : offeringCardGUI){
            if(cardGUI.isSelected())
                cardGUI.setVisualSelection(false);
        }

        //update the totem on the cards
        for (int i = 0; i < offeringCardGUI.size(); i++) {
            CardGUI card = offeringCardGUI.get(i);

            if (i < readOnlyModel.getOfferingCards().size()) {
                OfferingCard serverCard = readOnlyModel.getOfferingCards().get(i);

                card.setUserData(serverCard);

                if (serverCard.getPlayer() != null) {
                    card.updateTotem(serverCard.getPlayer().getColor().getFxColor());
                } else {
                    card.updateTotem(javafx.scene.paint.Color.TRANSPARENT);
                }
            }
        }

    }

    /**
     * set the action from click on tribes card
     */
    private void setOnMouseClickForTribes(CardGUI cardGUI, TribesCard card) {
        if(card.getCardType().isCharacter()) {
            cardGUI.setOnMouseClicked(_ -> {
                if (!readOnlyModel.isPlayerTurn(localPlayer)) {
                    showWaitTurnAlert();
                    return;
                }

                if (readOnlyModel.isPickOCPhase()) {
                    return; // Not selectable right now
                }

                // Toggle the visual state
                cardGUI.setVisualSelection(!cardGUI.isSelected());

                tribesSelected((CharacterCard) cardGUI.getUserData());
            });
        }
    }

    /**
     * set the action from click on building card
     */
    private void setOnMouseClickForBuilding(CardGUI cardGUI, BuildingCard card ) {
        cardGUI.setOnMouseClicked(_ -> {
            if (!readOnlyModel.isPlayerTurn(localPlayer)) {
                showWaitTurnAlert(); // The main GUI handles the alert, not the card!
                return;
            }

            if (readOnlyModel.isPickOCPhase()) {
                return; // Not selectable right now
            }

            // Toggle the visual state
            cardGUI.setVisualSelection(!cardGUI.isSelected());

            // Handle the game logic
            buildingSelected((BuildingCard) cardGUI.getUserData());
        });
    }

    /**
     * set the action from click on offering card
     */
    private void setOnMouseClickForOffering(CardGUI cardGUI, OfferingCard card) {
        cardGUI.setOnMouseClicked(_ -> {
            if (!readOnlyModel.isPlayerTurn(localPlayer)) {
                showWaitTurnAlert(); // The main GUI handles the alert, not the card!
                return;
            }

            if (!readOnlyModel.isPickOCPhase()) {
                return; // Not selectable right now
            }

            OfferingCard thisCard = readOnlyModel.getOfferingCards().stream()
                    .filter(c -> c.equals(card)) // Put your condition inside filter()
                    .findFirst()
                    .orElse(null);

            if(thisCard == null || thisCard.getPlayer()!=null)
                return;

            // Handle the game logic
            offeringSelected((OfferingCard) cardGUI.getUserData());

            // Toggle the visual state
            cardGUI.setVisualSelection(!cardGUI.isSelected());
        });
    }

    /**
     * Displays an alert when the user plays out of its turn
     */
    private void showWaitTurnAlert(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Wait Your Turn");
        alert.setHeaderText(null);
        alert.setContentText("It is currently the other player's turn!");
        alert.showAndWait();

    }

    /**
     * Handles tribes card selection by adding or removing the card to it
     */
    public void tribesSelected(CharacterCard card) {
        if(tribesSelected.contains(card))
            tribesSelected.remove(card);
        else
            tribesSelected.add(card);
    }

    /**
     * Handles building card selection by adding or removing the card to it
     */
    public void buildingSelected(BuildingCard card) {
        if(buildingSelected.contains(card))
            buildingSelected.remove(card);
        else
            buildingSelected.add(card);
    }

    /**
     * Handles offering card selection
     */
    public void offeringSelected(OfferingCard card) {
        offeringSelected = card;

        for (CardGUI cardGUI : offeringCardGUI){
            if(cardGUI.isSelected())
                cardGUI.setVisualSelection(false);
        }
    }

    /**
     * update the graphics related to selected player
     */
    private void updateSelectedPlayer(){
        //update selected player
        selectedPlayer = readOnlyModel.findPlayer(selectedPlayer);

        List<CharacterCard> orderedCards = new ArrayList<>(
                selectedPlayer.getCharacterCards()
        );

        orderedCards.sort(Comparator.comparing(CharacterCard::getCardType));

        playerCardsBox.getChildren().clear();

        //add personal character cards
        for(CharacterCard card : orderedCards){
            CardGUI characterCard = new CardGUI(card.getImagePath());
            playerCardsBox.getChildren().add(characterCard);
        }
        //add personal building cards
        for(BuildingCard card : selectedPlayer.getBuildingCards()){
            CardGUI buildingCard = new CardGUI(card.getImagePath());
            playerCardsBox.getChildren().add(buildingCard);
        }
    }

    /**
     * Displays the selected players data (excluding cards)
     */
    private void createPlayerCardLabel(){
        selectedPlayer = readOnlyModel.findPlayer(selectedPlayer);
        Label name = new Label("Name: " + selectedPlayer.getNickname());
        name.setStyle("""
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-font-size: 30px;
        """);
        Label food = new Label("Food: " + selectedPlayer.getFood());
        food.setStyle("""
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-font-size: 30px;
        """);
        Label points = new Label("Points: " + selectedPlayer.getPp());
        points.setStyle("""
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-font-size: 30px;
        """);
        if(playerResourcesBox != null) {
            playerResourcesBox.getChildren().clear();
            playerResourcesBox.getChildren().addAll(name, points, food);
        }
    }

    // The main GUI will call this to put it in the Scene
    public Parent getRoot() {
        return root;
    }
}

package it.polimi.ingsw.am17.Client.UserInterface;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.StrokeType;

/**
 * Represents a game card inside the game view
 */
public class CardGUI extends StackPane {

    private Rectangle border;
    private ImageView totemView; // Changed from Polygon to ImageView
    private boolean isSelected = false;

    private static final int CARD_WIDTH = 90;
    private static final int CARD_HEIGHT = 130;

    public CardGUI(String imagePath) {
        createGraphics(imagePath);
    }

    public CardGUI(String imagePath, Color playerColor) {
        createGraphics(imagePath);
        updateTotem(playerColor);
    }

    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Creates the structure of the card by drawing a rectangle and adding the image (passed as parameter) to it.
     */
    private void createGraphics(String imagePath) {

        border = new Rectangle(100, 140);
        border.setArcWidth(15);
        border.setArcHeight(15);
        border.setFill(Color.WHITE);
        border.setStroke(Color.BLACK);
        border.setStrokeWidth(1);
        this.getChildren().add(border);

        java.io.InputStream imageStream = getClass().getResourceAsStream(imagePath);

        if (imageStream != null) {
            Image img = new Image(imageStream);
            ImageView view = new ImageView(img);
            view.setFitWidth(CARD_WIDTH);
            view.setFitHeight(CARD_HEIGHT);
            view.setPreserveRatio(true);
            view.setSmooth(true);

            this.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
            this.setMinSize(CARD_WIDTH, CARD_HEIGHT);
            this.setMaxSize(CARD_WIDTH, CARD_HEIGHT);

            Rectangle clip = new Rectangle(90, 130);
            clip.setArcWidth(12);
            clip.setArcHeight(12);
            view.setClip(clip);
            this.getChildren().add(view);
        } else {
            System.err.println("Warning: Card image not found at path -> " + imagePath);
        }

        totemView = new ImageView();

        // Adjust these sizes to fit your specific PNG
        totemView.setFitWidth(35);
        totemView.setFitHeight(35);
        totemView.setPreserveRatio(true);
        totemView.setSmooth(true);
        totemView.setVisible(false); // Hidden by default

        // Position the totem at the top center, slightly overhanging the edge
        StackPane.setAlignment(totemView, Pos.TOP_CENTER);
        StackPane.setMargin(totemView, new Insets(0, 0, 0, 0));

        this.getChildren().add(totemView);
    }

    /**
     *  Changes card appearance when it is selected/unselected.
     */
    public void setVisualSelection(boolean selected) {
        this.isSelected = selected;
        if (isSelected) {
            border.setStroke(Color.LIGHTBLUE);
            border.setStrokeWidth(4);
            border.setStrokeType(StrokeType.INSIDE);
        } else {
            border.setStroke(Color.BLACK);
            border.setStrokeWidth(1);
            border.setStrokeType(StrokeType.CENTERED);
        }
    }

    /**
     * Handles totem visibility on a card
     */
    public void updateTotem(Color playerColor){
        // by default the totem should be invisible
        if (playerColor == null || playerColor.equals(Color.TRANSPARENT)) {
            totemView.setVisible(false);
            return;
        }

        // Figure out which image to load based on the JavaFX color
        String totemImagePath = getTotemImagePath(playerColor);

        // Load the image safely
        if (!totemImagePath.isEmpty()) {
            java.io.InputStream totemStream = getClass().getResourceAsStream(totemImagePath);

            if (totemStream != null) {
                Image totemImg = new Image(totemStream);
                totemView.setImage(totemImg);
                totemView.setVisible(true);
            } else {
                System.err.println("Warning: Totem image not found at path -> " + totemImagePath);
                totemView.setVisible(false); // Keep it hidden if missing
            }
        }
    }

    /**
     * @param playerColor   the color chosen by the player in the game
     * @return              the path of the totem image basing on the players color
     */
    private static String getTotemImagePath(Color playerColor) {
        String totemImagePath = "";

        if (playerColor.equals(Color.RED)) {
            totemImagePath = "/Images/totem/totem_red.png";
        } else if (playerColor.equals(Color.BLUE)) {
            totemImagePath = "/Images/totem/totem_blue.png";
        } else if (playerColor.equals(Color.BLACK)) {
            totemImagePath = "/Images/totem/totem_black.png";
        } else if (playerColor.equals(Color.YELLOW)) { // Assuming yellow is a player color
            totemImagePath = "/Images/totem/totem_yellow.png";
        } else if (playerColor.equals(Color.WHITE)) {
            totemImagePath = "/Images/totem/totem_white.png";
        }
        return totemImagePath;
    }
}
package it.polimi.ingsw.am17.Client.UserInterface.GUIElements;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import java.util.List;

/**
 * Represents the turn order card inside the game view
 */
public class TurnCardGUI extends StackPane {

    private final ImageView[] totemSlots; //saves the totem images
    List<it.polimi.ingsw.am17.Server.Model.Color> turnOrderColors;

    private static final double SCALE = 0.85;
    private static final double CARD_WIDTH = 90*SCALE;
    private static final double CARD_HEIGHT = 130*SCALE;

    public TurnCardGUI(String imagePath, int numSlots) {
        // init of the turn card and totem array

        Rectangle border = new Rectangle(100*SCALE, 140*SCALE);
        border.setArcWidth(15);
        border.setArcHeight(15);
        border.setFill(Color.WHITE);
        border.setStroke(Color.BLACK);
        border.setStrokeWidth(1);
        this.getChildren().add(border);

        if (imagePath != null) {
            // Safely check if the stream exists
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
                System.err.println("Warning: TurnCard image not found at path -> " + imagePath);
            }
        }
        // Initialize the array to match the player count
        totemSlots = new ImageView[numSlots];

        // Grab the specific Y coordinates for this number of players
        double[] yOffsets = calculateOffsetsFor(numSlots);

        for (int i = 0; i < numSlots; i++) {
            totemSlots[i] = new ImageView();
            totemSlots[i].setFitWidth(35);
            totemSlots[i].setFitHeight(35);
            totemSlots[i].setPreserveRatio(true);
            totemSlots[i].setVisible(false);

            // Move the totem to its slot
            totemSlots[i].setTranslateY(yOffsets[i]);

            this.getChildren().add(totemSlots[i]);
        }
    }

    /**
     * Define the exact vertical (Y) locations for the white boxes.
     */
    private double[] calculateOffsetsFor(int numSlots) {
        return switch (numSlots) {
            case 2 -> new double[]{-40, -20};
            case 3 -> new double[]{-40, -20, 0};
            case 4 -> new double[]{-40, -20, 0, 20};
            case 5 -> new double[]{-50, -30, -10, 10, 30};
            default -> new double[numSlots];
        };
    }

    /**
     * @return  the totem image path for the color passed as parameter.
     */
    private String getTotemImagePath(it.polimi.ingsw.am17.Server.Model.Color color) {
        if (color == null) return "";
        return switch (color) {
            case RED -> "/Images/totem/totem_red.png";
            case BLUE -> "/Images/totem/totem_blue.png";
            case BLACK -> "/Images/totem/totem_black.png";
            case YELLOW -> "/Images/totem/totem_yellow.png";
            case WHITE -> "/Images/totem/totem_white.png";
        };
    }

    /**
     * Places all the totems on the card.
     */
    public void placeTotems(List<it.polimi.ingsw.am17.Server.Model.Color> turnOrderColors) {
        this.turnOrderColors = turnOrderColors;

        // Clear all existing totems
        for (ImageView totemSlot : totemSlots) {
            totemSlot.setVisible(false);
        }

        // Fill boxes
        if (turnOrderColors != null) {
            for (int i = 0; i < turnOrderColors.size() && i < totemSlots.length; i++) {
                String path = getTotemImagePath(turnOrderColors.get(i));
                if (!path.isEmpty()) {
                    java.io.InputStream totemStream = getClass().getResourceAsStream(path);

                    if (totemStream != null) {
                        Image img = new Image(totemStream);
                        totemSlots[i].setImage(img);
                        totemSlots[i].setVisible(true);
                    } else {
                        System.err.println("Warning: Turn order totem image not found at path -> " + path);
                        totemSlots[i].setVisible(false);
                    }
                }
            }
        }
    }

    /**
     * Removes a totem from the card.
     * @param color     the color of the totem to remove
     */
    public void removePlayerTotem(it.polimi.ingsw.am17.Server.Model.Color color){
        for (int i = 0; i < turnOrderColors.size() && i < totemSlots.length; i++) {
            if(turnOrderColors.get(i) == color)
                totemSlots[i].setVisible(false);
        }

    }
}
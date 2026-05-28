package it.polimi.ingsw.am17.Client.UserInterface;

import javafx.scene.layout.Border;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class TurnCardGUI extends StackPane {

    private ImageView[] totemSlots; //saves the totem images
    List<it.polimi.ingsw.am17.Server.Model.Color> turnOrderColors;

    private final int CARD_WIDTH = 90;
    private final int CARD_HEIGHT = 130;

    public TurnCardGUI(String imagePath, int numSlots) {

        Rectangle border = new Rectangle(100, 140);
        border.setArcWidth(15);
        border.setArcHeight(15);
        border.setFill(Color.WHITE);
        border.setStroke(Color.BLACK);
        border.setStrokeWidth(1);
        this.getChildren().add(border);

        if (imagePath != null) {
            Image img = new Image(getClass().getResourceAsStream(imagePath));
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
        switch (numSlots) {
            case 2:
                return new double[] { -40, -20 };
            case 3:
                return new double[] { -40, -20, 0 };
            case 4:
                return new double[] { -40, -20, 0, 20 };
            case 5:
                return new double[] { -50, -30, -10, 10, 30 };
            default:
                return new double[numSlots];
        }
    }

    private String getTotemImagePath(it.polimi.ingsw.am17.Server.Model.Color color) {
        if (color == null) return "";
        switch (color) {
            case RED: return "/images/totem/totem_red.png";
            case BLUE: return "/images/totem/totem_blue.png";
            case BLACK: return "/images/totem/totem_black.png";
            case YELLOW: return "/images/totem/totem_yellow.png";
            case WHITE: return "/images/totem/totem_white.png";
            default: return "";
        }
    }

    public void updateTurnOrder(List<it.polimi.ingsw.am17.Server.Model.Color> turnOrderColors) {
        this.turnOrderColors = turnOrderColors;

        // Clear all existing totems
        for (int i = 0; i < totemSlots.length; i++) {
            totemSlots[i].setVisible(false);
        }

        // Fill boxes
        if (turnOrderColors != null) {
            for (int i = 0; i < turnOrderColors.size() && i < totemSlots.length; i++) {
                String path = getTotemImagePath(turnOrderColors.get(i));
                if (!path.isEmpty()) {
                    Image img = new Image(getClass().getResourceAsStream(path));
                    totemSlots[i].setImage(img);
                    totemSlots[i].setVisible(true);
                }
            }
        }
    }

    public void removePlayerTotem(it.polimi.ingsw.am17.Server.Model.Color color){
        for (int i = 0; i < turnOrderColors.size() && i < totemSlots.length; i++) {
            if(turnOrderColors.get(i) == color)
                totemSlots[i].setVisible(false);
        }

    }
}
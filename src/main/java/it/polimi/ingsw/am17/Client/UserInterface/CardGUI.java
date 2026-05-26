package it.polimi.ingsw.am17.Client.UserInterface;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.StrokeType;

public class CardGUI extends StackPane {

    private Rectangle border;
    private boolean isSelected = false;
    private final int CARD_WIDTH = 90;
    private final int CARD_HEIGHT = 130;

    public CardGUI(String imagePath) {
        createGraphics(imagePath, Color.BLACK);
    }

    public CardGUI(String imagePath, Color defaultBorderColor) {
        createGraphics(imagePath, defaultBorderColor);
    }

    private void createGraphics(String imagePath, Color defaultBorderColor) {

        border = new Rectangle(100, 140);
        border.setArcWidth(15);
        border.setArcHeight(15);
        border.setFill(Color.WHITE);
        border.setStroke(defaultBorderColor);
        border.setStrokeWidth(defaultBorderColor.equals(Color.BLACK) ? 1 : 4);

        this.getChildren().add(border);

        this.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        this.setMinSize(CARD_WIDTH, CARD_HEIGHT);
        this.setMaxSize(CARD_WIDTH, CARD_HEIGHT);


        if (imagePath != null) {
            var resourceStream = getClass().getResourceAsStream(imagePath);
            if(resourceStream!=null) {
                //Image img = new Image(getClass().getResourceAsStream(imagePath));
                Image img = new Image(resourceStream);
                ImageView view = new ImageView(img);

                view.setFitWidth(CARD_WIDTH);
                view.setFitHeight(CARD_HEIGHT);
                view.setPreserveRatio(true);
                view.setSmooth(true);


                Rectangle clip = new Rectangle(90, 130);
                clip.setArcWidth(12);
                clip.setArcHeight(12);
                view.setClip(clip);

                this.getChildren().add(view);

            }
            else {
                System.err.println("ATTENZIONE: File immagine non trovato al percorso: " + imagePath);
            }
        }
    }

    /**
     * change selected status (and border color)
     * @param selected
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
        }
    }

    public void updateBorderFromPlayer(Color playerColor){
        border.setArcWidth(15);
        border.setArcHeight(15);
        border.setFill(Color.WHITE);
        border.setStroke(playerColor);
        border.setStrokeWidth(playerColor.equals(Color.BLACK) ? 1 : 4);

    }

    public boolean isSelected() {
        return isSelected;
    }
}
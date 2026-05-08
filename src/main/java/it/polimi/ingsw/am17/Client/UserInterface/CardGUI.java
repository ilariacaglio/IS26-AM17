package it.polimi.ingsw.am17.Client.UserInterface;


import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CardGUI extends StackPane {

    public CardGUI(String imagePath) {
        // 1. Background of the card
        Rectangle border = new Rectangle(100, 140);
        border.setArcWidth(15);
        border.setArcHeight(15);
        //border.setFill(Color.WHITE);
        //border.setStroke(Color.BLACK);

        // 2. The Image
        Image img = new Image(getClass().getResourceAsStream(imagePath));
        ImageView view = new ImageView(img);

        // 3. Scale the image to fit the card
        view.setFitWidth(90);  // Leave a small margin
        view.setPreserveRatio(true);
        view.setSmooth(true);

        // Add everything to the StackPane
        this.getChildren().addAll(border, view);
    }
}

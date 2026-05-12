package it.polimi.ingsw.am17.Client.UserInterface;


import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.StrokeType;

public class CardGUI extends StackPane {

    private final TribesCard tribesCard;
    private final BuildingCard buildingCard;
    private final OfferingCard offeringCard;
    private boolean selected = false;
    private boolean selectable;

    private Rectangle border;

    public CardGUI(String path)
    {
        tribesCard = null;
        buildingCard = null;
        offeringCard = null;
        selectable = false;
        createCardGUI(path);
    }

    public CardGUI(TribesCard card)
    {
        if(card.getCardType().isCharacter()) {
            tribesCard = card;
            selectable = true;
        }
        else {
            tribesCard = null;
            selectable = false;
        }
        buildingCard = null;
        offeringCard = null;

        createCardGUI(card.getImagePath());
    }

    public CardGUI(BuildingCard card)
    {
        selectable = true;
        tribesCard = null;
        buildingCard = card;
        offeringCard = null;

        createCardGUI(card.getImagePath());
    }

    public CardGUI(OfferingCard card)
    {
        tribesCard = null;
        buildingCard = null;
        offeringCard = card;
        selectable = true;
        createCardGUI(card.getImagePath());
    }

    private void createCardGUI(String imagePath) {
        //card form and size
        border = new Rectangle(100, 140);
        border.setArcWidth(15);
        border.setArcHeight(15);
        border.setFill(javafx.scene.paint.Color.WHITE);
        border.setStroke(javafx.scene.paint.Color.BLACK);

        if(!selectable){
            border.setStroke(Color.RED);
            border.setStrokeWidth(2);
        }
        if(offeringCard != null)
        {
            Player p =offeringCard.getPlayer();
            if(p != null)
            {
                selectable = false;
                Color c = p.getColor().getFxColor();

                border.setStroke(c);
                border.setStrokeWidth(2);
            }
        }
        //add border
        this.getChildren().add(border);
//if is used only for test purposes
        if (imagePath != null) {
        //load image
        Image img = new Image(getClass().getResourceAsStream(imagePath));
        ImageView view = new ImageView(img);

        //fit the card in rectangle
        view.setFitWidth(90);
        view.setPreserveRatio(true);
        view.setSmooth(true);

        // Add everything to the StackPane
        this.getChildren().add(view);
        }
        //click the card
        if(selectable) {
            this.setOnMouseClicked(e -> {
                if(!GUI.isPlayerTurn())
                {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Wait Your Turn");
                    alert.setHeaderText(null);
                    alert.setContentText("It is currently the other player's turn!");
                    alert.showAndWait();
                    return;
                }
                selected = !selected;
                setSelected(selected);
                if (tribesCard != null)
                    GUI.tribesSelected((CharacterCard) tribesCard);
                if (offeringCard != null)
                    GUI.offeringSelected(offeringCard);
                if (buildingCard != null)
                    GUI.buildingSelected(buildingCard);
                System.out.println("carta cliccata");
            });
        }
    }

    public void setSelected(boolean selected) {
        if (selected) {
            // Light blue border
            border.setStroke(Color.LIGHTBLUE);
            border.setStrokeWidth(4);
            // Optional: Makes the border grow inward so it doesn't clip
            border.setStrokeType(StrokeType.INSIDE);
        } else {
            // Reset to default
            border.setStroke(Color.BLACK);
            border.setStrokeWidth(1);
        }
    }
}

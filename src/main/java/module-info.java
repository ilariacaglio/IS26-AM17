module it.polimi.ingsw.am17 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.smartcardio;
    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.databind;
    requires java.rmi;
    requires java.logging;
    requires io.github.cdimascio.dotenv.java;
    requires java.sql;
    requires javafx.graphics;

    // Add this line to allow RMI to access your client interfaces
    exports it.polimi.ingsw.am17.Client.RMI to java.rmi;

    // If your Server-side RMI interfaces are in a different package, export that too
    exports it.polimi.ingsw.am17.Server.RMI to java.rmi;
    opens it.polimi.ingsw.am17.Server.Model to tools.jackson.databind;
    exports it.polimi.ingsw.am17;
    exports it.polimi.ingsw.am17.Server.Model;
    opens it.polimi.ingsw.am17 to javafx.fxml, tools.jackson.databind;
    exports it.polimi.ingsw.am17.CommonInterfaces;
    opens it.polimi.ingsw.am17.CommonInterfaces to javafx.fxml, tools.jackson.databind;
    exports it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters;
    opens it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters to tools.jackson.databind;
    exports it.polimi.ingsw.am17.Server.Model.GameCard.Buildings;
    opens it.polimi.ingsw.am17.Server.Model.GameCard.Buildings to tools.jackson.databind;
    exports it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events;
    opens it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Events to tools.jackson.databind;
    exports it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards;
    opens it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards to tools.jackson.databind;
    exports it.polimi.ingsw.am17.Server.Model.GameCard;
    opens it.polimi.ingsw.am17.Server.Model.GameCard to tools.jackson.databind;
    exports it.polimi.ingsw.am17.Server.Model.Decks;
    opens it.polimi.ingsw.am17.Server.Model.Decks to tools.jackson.databind;
    opens it.polimi.ingsw.am17.Client.UserInterface to javafx.graphics;
    exports it.polimi.ingsw.am17.Server.Utility;
    opens it.polimi.ingsw.am17.Server.Utility to tools.jackson.databind;
}
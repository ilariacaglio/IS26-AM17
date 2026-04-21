module it.polimi.ingsw.am17 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.smartcardio;
    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.databind;
    requires java.rmi;

// Add this line to allow RMI to access your client interfaces
    exports it.polimi.ingsw.am17.RMI.Client to java.rmi;

    // If your Server-side RMI interfaces are in a different package, export that too
    exports it.polimi.ingsw.am17.RMI.Server to java.rmi;
    opens it.polimi.ingsw.am17 to javafx.fxml;
    opens it.polimi.ingsw.am17.Model.GameCard to tools.jackson.databind;
    opens it.polimi.ingsw.am17.Model to tools.jackson.databind;
    exports it.polimi.ingsw.am17;
    exports it.polimi.ingsw.am17.Model;
    exports it.polimi.ingsw.am17.Model.GameCard;
}
module it.polimi.ingsw.am17 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.smartcardio;
    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.databind;
    requires java.rmi;


    opens it.polimi.ingsw.am17 to javafx.fxml;
    opens it.polimi.ingsw.am17.Model.GameCard to tools.jackson.databind;
    opens it.polimi.ingsw.am17.Model to tools.jackson.databind;
    exports it.polimi.ingsw.am17;
}
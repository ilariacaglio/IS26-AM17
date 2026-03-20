module it.polimi.ingsw.am17 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.smartcardio;
    requires com.fasterxml.jackson.annotation;


    opens it.polimi.ingsw.am17 to javafx.fxml;
    exports it.polimi.ingsw.am17;
}
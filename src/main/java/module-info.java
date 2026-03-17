module it.polimi.ingsw.am17 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.smartcardio;


    opens it.polimi.ingsw.am17 to javafx.fxml;
    exports it.polimi.ingsw.am17;
}
module it.polimi.ingsw.am17 {
    requires javafx.controls;
    requires javafx.fxml;


    opens it.polimi.ingsw.am17 to javafx.fxml;
    exports it.polimi.ingsw.am17;
}
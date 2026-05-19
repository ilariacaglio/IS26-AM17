package it.polimi.ingsw.am17.Server.Model;

public enum Color {
    RED,
    BLUE,
    WHITE,
    BLACK,
    YELLOW;

    public javafx.scene.paint.Color getFxColor() {
        return switch (this) {
            case RED    -> javafx.scene.paint.Color.RED;
            case BLUE   -> javafx.scene.paint.Color.BLUE;
            case WHITE  -> javafx.scene.paint.Color.WHITE;
            case BLACK  -> javafx.scene.paint.Color.BLACK;
            case YELLOW -> javafx.scene.paint.Color.GOLD; // Gold  looks better
        };
    }
}

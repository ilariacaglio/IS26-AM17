package it.polimi.ingsw.am17.Client.UserInterface.GUIElements;

import javafx.stage.Screen;

/**
 * Utility class to dynamically scale GUI elements based on the screen resolution.
 */
public class ScreenScale {
    // The global scale calculated at application startup
    public static final double SCALE;

    // This static block calculates the scale factor based on the screen height
    // It is executed only once when the game starts.
    static {
        // Read the height of the player's screen
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        // Calculate the global scale using a 1080p monitor as a baseline
        SCALE = (screenHeight / 1080.0) *1.1 ;
    }

    /**
     * Scales a pixel value (Double).
     * @param originalPixelSize the original size in pixels
     * @return the scaled size as a double
     */
    public static double size(double originalPixelSize) {
        return originalPixelSize * SCALE;
    }

    /**
     * Scales a pixel value rounding it to Integer (useful for Fonts and base Spacing).
     * @param originalPixelSize the original size in pixels
     * @return the scaled size as an integer
     */
    public static int sizeInt(double originalPixelSize) {
        return (int) Math.round(originalPixelSize * SCALE);
    }
}

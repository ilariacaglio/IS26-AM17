package it.polimi.ingsw.am17.CommonInterfaces;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LauncherUtility {
    private static final Logger logger = Logger.getLogger(LauncherUtility.class.getName());
    public static void handleLoggingOption(boolean debug) {
        // logging configuration
        Logger rootLogger = Logger.getLogger("");

        // set default level higher than INFO to disable log printing
        // we used INFO level to print the majority of messages
        Level targetLevel = debug ? Level.ALL : Level.WARNING;
        rootLogger.setLevel(targetLevel);

        // remove all existing handlers
        for (Handler handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }

        // set console handler logging level
        ConsoleHandler customConsoleHandler = new ConsoleHandler();
        customConsoleHandler.setLevel(Level.ALL);
        rootLogger.addHandler(customConsoleHandler);

        // print message of logging enabled
        if (debug) {
            logger.info("Debug mode enabled: logs will be printed");
        }
    }
}

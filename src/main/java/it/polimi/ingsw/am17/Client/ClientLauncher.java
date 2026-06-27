package it.polimi.ingsw.am17.Client;

import it.polimi.ingsw.am17.Client.RMI.ClientRMI;
import it.polimi.ingsw.am17.Client.Socket.ClientSocket;
import it.polimi.ingsw.am17.CommonInterfaces.LauncherUtility;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.Arrays;
import java.util.List;

/**
 * Launches a client.
 * Use --host to specify the host.
 * Use --portRMI to specify the port for RMI.
 * Use --portSocket to specify the port for Socket.
 * Use --debug to raise the logging level.
 * Use --socket to use socket communication (instead or RMI).
 * Use --gui to launch a graphical interface.
 */
public class ClientLauncher {

    static void main(String[] args) throws IOException, NotBoundException {
        List<String> argsList = Arrays.asList(args);

        boolean gui = argsList.contains("--gui");
        boolean socket = argsList.contains("--socket");
        boolean debug = argsList.contains("--debug");

        if (gui) {
            handleOSGUIScale();
        }

        LauncherUtility.handleLoggingOption(debug);

        String host;
        if (argsList.contains("--host")) {
            host = argsList.get(argsList.indexOf("--host") + 1);
        }
        else {
            host = "127.0.0.1";
        }

        int portRMI;
        if (argsList.contains("--portRMI")) {
            portRMI = Integer.parseInt(argsList.get(argsList.indexOf("--portRMI") + 1));
        }
        else {
            portRMI = 1099;
        }

        int portSocket;
        if (argsList.contains("--portSocket")) {
            portSocket = Integer.parseInt(argsList.get(argsList.indexOf("--portSocket") + 1));
        }
        else {
            portSocket = 24312;
        }

        if (socket) {
            new ClientSocket(host, portSocket, gui);
        } else {
            new ClientRMI(host,portRMI, "MesosRMIServer", gui);
        }
    }

    /**
     * Sets the scale of the GUI to 100% on Windows.
     * If the OS is not Windows, this method does nothing.
     */
    private static void handleOSGUIScale() {
        // Retrieve the operating system name
        String osName = System.getProperty("os.name").toLowerCase();

        // Check if the OS is Windows
        if (osName.contains("win")) {
            // Force the Windows UI scale to 1.0 (100%)
            System.setProperty("glass.win.uiScale", "1.0");
            // Disable JavaFX High-DPI support to prevent anomalous scaling
            System.setProperty("prism.allowhidpi", "false");
        }
    }
}

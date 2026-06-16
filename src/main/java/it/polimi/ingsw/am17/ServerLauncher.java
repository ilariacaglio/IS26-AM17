package it.polimi.ingsw.am17;

import it.polimi.ingsw.am17.CommonInterfaces.LauncherUtility;
import it.polimi.ingsw.am17.Server.Controller.ControllerInterface;
import it.polimi.ingsw.am17.Server.Controller.GamesController;
import it.polimi.ingsw.am17.Server.RMI.ServerRMI;
import it.polimi.ingsw.am17.Server.Socket.ServerSocketMultiplexer;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

/**
 * Launches both the RMI and Socket servers with the same controller.
 */
public class ServerLauncher {
    private static final Logger logger = Logger.getLogger(ServerLauncher.class.getName());

    /**
     * Launches a server.
     * Use --serverName to specify the server name for RMI.
     * Use --host to specify the host.
     * Use --portRMI to specify the port for RMI.
     * Use --portSocket to specify the port for Socket.
     * Use --debug to raise logging level.
     */
    static void main(String[] args) {
        List<String> argsList = Arrays.asList(args);

        boolean debug = argsList.contains("--debug");
        LauncherUtility.handleLoggingOption(debug);

        String serverName;
        if (argsList.contains("--serverName")) {
            serverName = argsList.get(argsList.indexOf("--serverName") + 1);
        }
        else {
            serverName = "MesosRMIServer";
        }

        String host;
        if (argsList.contains("--host")) {
            host = argsList.get(argsList.indexOf("--host") + 1);
        }
        else {
            host = getLocalIpAddress();
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

        // create the main controller
        ControllerInterface controller = new GamesController();

        // Start an RMI server in its own thread
        new Thread(() -> {
            try {
                System.setProperty("java.rmi.server.hostname", host);
                new ServerRMI(controller, portRMI, serverName);
            } catch (Exception e) {
                logger.severe("RMI server failed to start: " + e.getMessage());
            }
        }).start();

        // Start a Socket server in its own thread
        new Thread(() -> {
            try {
                new ServerSocketMultiplexer(controller, portSocket);
            } catch (Exception e) {
                logger.severe("Socket server failed to start: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Gets local IP on active LAN
     * @return  the found IP address or "127.0.0.1" if not found
     */
    private static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iFace = interfaces.nextElement();

                // ignore loopback and unused interfaces
                if (iFace.isLoopback() || !iFace.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = iFace.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    // get only IPv4 addresses
                    if (addr instanceof Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            logger.warning("Cannot get network IP address. Using localhost. Error: " + e.getMessage());
        }

        return "127.0.0.1";
    }
}

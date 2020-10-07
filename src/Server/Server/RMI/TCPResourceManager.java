package Server.RMI;

import Server.Interface.*;
import Server.Common.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;
import java.net.*;


public class TCPResourceManager extends ResourceManager {
    private ServerSocket serverSocket;
    private static String s_serverHost = "localhost";
    private static int s_serverPort;
    private static String s_serverName;
    private ExecutorService executor = Executors.newFixedThreadPool(15);
    private PrintStream out;

    public static void main(String args[]) {
        if (args.length > 1) {
            s_serverName = args[0];
            s_serverPort = Integer.parseInt(args[1]);
        }
        TCPResourceManager server = new TCPResourceManager(s_serverName);
        try {
            server.serverSocket = new ServerSocket(s_serverPort);

            while (true) {
                Socket conn = server.serverSocket.accept();
                server.executor.submit(new HandleRequest(conn, server));
            }

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public TCPResourceManager(String p_name) {
        super(p_name);

    }

}

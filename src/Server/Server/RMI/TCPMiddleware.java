package Server.RMI;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;
import java.net.*;
import Server.Interface.*;
import Server.Common.*;

public class TCPMiddleware extends ResourceManager {
    private ServerSocket serSocket;
    private Socket flightSocket;
    private Socket carSocket;
    private Socket roomSocket;

    private static int flightPort = 10031;
    private static int carPort = 10031;
    private static int roomPort = 10031;
    private static int s_serverPort = 9031;
    private String flightHostName;
    private String carHostName;
    private String roomHostName;
    private ExecutorService executor = Executors.newFixedThreadPool(15);
    private PrintStream out;

    public TCPMiddleware(String p_name) {
        super(p_name);
    }

    public static void main(String args[]) {
        TCPMiddleware middleware = new TCPMiddleware("Middleware");

        if (args.length > 2) {
            middleware.flightHostName = args[0];
            middleware.carHostName = args[1];
            middleware.roomHostName = args[2];

        } else {
            System.out.println("Not enough arguments for TCPMiddleware!");
            System.exit(0);
        }
        try {
            middleware.serSocket = new ServerSocket(s_serverPort);
            middleware.flightSocket = new Socket(middleware.flightHostName, flightPort);
            middleware.carSocket = new Socket(middleware.carHostName, carPort);
            middleware.roomSocket = new Socket(middleware.roomHostName, roomPort);

            while (true) {
                Socket conn = middleware.serSocket.accept();
                middleware.executor.submit(new HandleMiddlewareRequest(conn, middleware.flightSocket,
                        middleware.carSocket, middleware.roomSocket, middleware));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

    }

}

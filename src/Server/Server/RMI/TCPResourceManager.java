package Server.RMI;

import Server.Interface.*;
import Server.Common.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;
import java.net.*;

class HandleRequest implements Runnable {
    private Socket socket;
    private ResourceManager rm;
    private Scanner in;
    private PrintWriter out;

    public HandleRequest(Socket socket, ResourceManager rm) {
        this.socket = socket;
        this.rm = rm;
        try {
            this.in = new Scanner(socket.getInputStream());
            this.out = new PrintWriter(socket.getOutputStream());
            this.run();
        } catch (Exception e) {
            // TODO: kill just thread
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        String query = readLineFromSocket();
        // command=addflights&xid=1&location=mtl&flightnum=1&numseats=2&price=100
        decodeQuery(query);

    }

    private static void decodeQuery(String query) {
        // command=addflights&xid=1&location=mtl&flightnum=1&numseats=2&price=100
        try {
            String cmd = "";
            Vector<String> arguments = new Vector<String>();
            String[] tokenized = query.split("&");

            System.out.println(Arrays.toString(tokenized));

            cmd = tokenized[0].substring(tokenized[0].indexOf('=') + 1);
            System.out.println(cmd);
            for (int i = 1; i < tokenized.length; i++) {
                arguments.add(tokenized[i].substring(tokenized[i].indexOf('=') + 1));
            }

        } catch (IndexOutOfBoundsException e) {
            System.out.println("Cannot decode the query!");
        }

    }

    private String readLineFromSocket() {
        try {
            return this.in.nextLine();
        } catch (Exception e) {
            return "";
        }
    }
}

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
        } else {
            System.out.println("Not enough arguments for TCPResourceManager!");
            System.exit(0);
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

package Server.RMI;

import Server.Interface.*;
import Server.Common.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;
import java.net.*;

class HandleMiddlewareRequest implements Runnable {
    private Socket conn;
    private Socket flightSocket;
    private Socket carSocket;
    private Socket roomSocket;
    private ResourceManager rm;
    private Scanner in;
    private PrintWriter out;

    public HandleMiddlewareRequest(Socket conn, Socket flightSocket, Socket carSocket, Socket roomSocket,
            ResourceManager rm) {
        this.conn = conn;
        this.flightSocket = flightSocket;
        this.carSocket = carSocket;
        this.roomSocket = roomSocket;
        this.rm = rm;
        try {
            this.in = new Scanner(conn.getInputStream());
            this.out = new PrintWriter(conn.getOutputStream(), true);
            this.run();
        } catch (Exception e) {
            // TODO: kill just thread
            e.printStackTrace();
            System.exit(1);
        }

    }

    private static Vector<String> decodeQuery(String query) {
        // command=addflights&xid=1&location=mtl&flightnum=1&numseats=2&price=100
        try {
            String cmd = "";
            Vector<String> arguments = new Vector<String>();
            String[] tokenized = query.split("&");

            System.out.println(Arrays.toString(tokenized));

            cmd = (tokenized[0].substring(tokenized[0].indexOf('=') + 1));
            System.out.println(cmd);
            arguments.add(cmd);
            for (int i = 1; i < tokenized.length; i++) {
                arguments.add(tokenized[i].substring(tokenized[i].indexOf('=') + 1));
            }

            return arguments;

        } catch (IndexOutOfBoundsException e) {
            System.out.println("Cannot decode the query!");
            return null;
        }

    }

    @Override
    public void run() {
        String query = readLineFromSocket();
        // command=addflights&xid=1&location=mtl&flightnum=1&numseats=2&price=100
        Vector<String> decodedQuery = decodeQuery(query);
        try {
            switch (decodedQuery.get(0)) {
                case "AddFlight": {

                    PrintWriter p1 = new PrintWriter(this.flightSocket.getOutputStream());
                    p1.println(query);
                    p1.flush();

                    Scanner scan1 = new Scanner(this.flightSocket.getInputStream());
                    out.println(scan1.nextLine());
                    out.flush();
                    break;
                }
                case "AddCars": {
                    PrintWriter p1 = new PrintWriter(this.carSocket.getOutputStream());
                    p1.println(query);
                    p1.flush();

                    Scanner scan1 = new Scanner(this.carSocket.getInputStream());
                    out.println(scan1.nextLine());
                    out.flush();
                    break;

                }
                case "AddRooms": {
                    PrintWriter p1 = new PrintWriter(this.roomSocket.getOutputStream());
                    p1.println(query);
                    p1.flush();

                    Scanner scan1 = new Scanner(this.roomSocket.getInputStream());
                    out.println(scan1.nextLine());
                    out.flush();
                    break;

                }

                case "AddCustomer": {
                    checkArgumentsCount(2, decodedQuery.size());
                    int id = toInt(decodedQuery.elementAt(1));
                    try {
                        int customer = rm.newCustomer(id);
                        break;
                    } catch (Exception e) {
                        break;
                    }

                }

                case "AddCustomerID": {
                    checkArgumentsCount(3, decodedQuery.size());
                    int id = toInt(decodedQuery.elementAt(1));
                    int cid = toInt(decodedQuery.elementAt(2));

                    try {
                        rm.newCustomer(id, cid);
                        break;
                    } catch (Exception e) {
                        break;
                    }

                }

                case "DeleteFlight": {
                    PrintWriter p1 = new PrintWriter(this.flightSocket.getOutputStream());
                    p1.println(query);
                    p1.flush();

                    Scanner scan1 = new Scanner(this.flightSocket.getInputStream());
                    out.println(scan1.nextLine());
                    out.flush();
                    break;

                }
                case "DeleteCars": {
                    PrintWriter p1 = new PrintWriter(this.carSocket.getOutputStream());
                    p1.println(query);
                    p1.flush();

                    Scanner scan1 = new Scanner(this.carSocket.getInputStream());
                    out.println(scan1.nextLine());
                    out.flush();
                    break;

                }
                case "DeleteRooms": {
                    PrintWriter p1 = new PrintWriter(this.roomSocket.getOutputStream());
                    p1.println(query);
                    p1.flush();

                    Scanner scan1 = new Scanner(this.roomSocket.getInputStream());
                    out.println(scan1.nextLine());
                    out.flush();
                    break;

                }
                // TO DO: CaseDelete customer is not implemented
                case "DeleteCustomer": {
                    break;
                }
                case "QueryFlight": {
                    PrintWriter p1 = new PrintWriter(this.flightSocket.getOutputStream());
                    p1.println(query);
                    p1.flush();

                    Scanner scan1 = new Scanner(this.flightSocket.getInputStream());
                    out.println(scan1.nextLine());
                    out.flush();
                    break;
                }
                case "QueryCars": {
                    PrintWriter p1 = new PrintWriter(this.carSocket.getOutputStream());
                    p1.println(query);
                    p1.flush();

                    Scanner scan1 = new Scanner(this.carSocket.getInputStream());
                    out.println(scan1.nextLine());
                    out.flush();
                    break;
                }
                case "QueryRooms": {
                    PrintWriter p1 = new PrintWriter(this.roomSocket.getOutputStream());
                    p1.println(query);
                    p1.flush();

                    Scanner scan1 = new Scanner(this.roomSocket.getInputStream());
                    out.println(scan1.nextLine());
                    out.flush();
                    break;
                }
                case "QueryCustomer": {
                    checkArgumentsCount(3, decodedQuery.size());
                    int id = toInt(decodedQuery.elementAt(1));
                    int customerId = toInt(decodedQuery.elementAt(2));
                    String result = rm.queryCustomerInfo(id, customerId);
                    out.println(result);
                    break;
                }

                case "QueryFlightPrice": {
                    PrintWriter p1 = new PrintWriter(this.flightSocket.getOutputStream());
                    p1.println(query);
                    p1.flush();

                    Scanner scan1 = new Scanner(this.flightSocket.getInputStream());
                    out.println(scan1.nextLine());
                    out.flush();
                    break;
                }
                case "QueryCarsPrice": {
                    PrintWriter p1 = new PrintWriter(this.carSocket.getOutputStream());
                    p1.println(query);
                    p1.flush();

                    Scanner scan1 = new Scanner(this.carSocket.getInputStream());
                    out.println(scan1.nextLine());
                    out.flush();
                    break;
                }
                case "QueryRoomsPrice": {
                    PrintWriter p1 = new PrintWriter(this.roomSocket.getOutputStream());
                    p1.println(query);
                    p1.flush();

                    Scanner scan1 = new Scanner(this.roomSocket.getInputStream());
                    out.println(scan1.nextLine());
                    out.flush();
                    break;
                }
                case "reserveFlight": {
                    break;
                }
                case "reserveCar": {
                    break;
                }
                case "reserveRoom": {
                    break;
                }

                case "Bundle": {
                    break;
                }

            }
        }

        catch (Exception e) {

        }

    }

    private String readLineFromSocket() {
        try {
            return this.in.nextLine();
        } catch (Exception e) {
            return "";
        }
    }

    public static void checkArgumentsCount(Integer expected, Integer actual) throws IllegalArgumentException {
        if (expected != actual) {
            throw new IllegalArgumentException("Invalid number of arguments. Expected " + (expected - 1) + ", received "
                    + (actual - 1) + ". Location \"help,<CommandName>\" to check usage of this command");
        }
    }

    public static int toInt(String string) throws NumberFormatException {
        return (Integer.valueOf(string)).intValue();
    }

    public static boolean toBoolean(String string)// throws Exception
    {
        return (Boolean.valueOf(string)).booleanValue();
    }
}

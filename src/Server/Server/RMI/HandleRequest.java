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

            cmd = (tokenized[0].substring(tokenized[0].indexOf('=') + 1)).toLowerCase();
            System.out.println(cmd);
            for (int i = 1; i < tokenized.length; i++) {
                arguments.add(tokenized[i].substring(tokenized[i].indexOf('=') + 1));
            }

            execute(cmd, arguments);

        } catch (IndexOutOfBoundsException e) {
            System.out.println("Cannot decode the query!");
        }

    }

    private void execute(String cmd, Vector<String> arguments) {
        cmd = cmd.toLowerCase();

        switch (cmd) {
            case "addflight": {
                checkArgumentsCount(4, arguments.size());
                int id = toInt(arguments.elementAt(0));
                int flightNum = toInt(arguments.elementAt(1));
                int flightSeats = toInt(arguments.elementAt(2));
                int flightPrice = toInt(arguments.elementAt(3));
                //TODO : sent result to middleware
                boolean result = rm.addFlight(id, flightNum, flightSeats, flightPrice);
                break;
            }
            case "addrooms": {
                checkArgumentsCount(4, arguments.size());
                int id = toInt(arguments.elementAt(0));
                String location = arguments.elementAt(1);
                int numRooms = toInt(arguments.elementAt(2));
                int price = toInt(arguments.elementAt(3));

                boolean result = rm.addRooms(id, location, numRooms, price);
                break;
            }

            case "addcustomer": {
                //will be use dn middleware
                checkArgumentsCount(1, arguments.size());
                int id = toInt(arguments.elementAt(0));
                try {
                    int customer = rm.newCustomer(id);
                    break;
                } catch (Exception e) {
                    break;
                }
            }

            case "addcustomerid": {
                checkArgumentsCount(2, arguments.size());
                int id = toInt(arguments.elementAt(0));
                int cid = toInt(arguments.elementAt(1));

                try {
                    rm.newCustomer(id, cid);
                    break;
                } catch (Exception e) {
                    break;
                }

            }

            case "deleteflight": {
                checkArgumentsCount(2, arguments.size());
                int id = toInt(arguments.elementAt(0));
                int flightNum = toInt(arguments.elementAt(1));
                //TODO : sent result to middleware
                boolean result = rm.deleteFlight(id, flightNum);
                break;
            }

            case "deletecars": {
                checkArgumentsCount(2, arguments.size());
                int id = toInt(arguments.elementAt(0));
                String location = toInt(arguments.elementAt(1));
                //TODO : sent result to middleware
                boolean result = rm.deleteCars(id, location);
                break;
            }

            case "deleterooms": {
                checkArgumentsCount(2, arguments.size());
                int id = toInt(arguments.elementAt(0));
                String location = toInt(arguments.elementAt(1));
                //TODO : sent result to middleware
                boolean result = rm.deleteRooms(id, location);
                break;
            }

            case "deletecustomer": {

            }

            case "queryflight": {
                checkArgumentsCount(2, arguments.size());
                int id = toInt(arguments.elementAt(0));
				int flightNum = toInt(arguments.elementAt(1));
                //TODO : sent result to middleware
                int result = rm.queryFlight(id, flightNum);
                break;
            }

            case "querycars": {
                checkArgumentsCount(2, arguments.size());
                int id = toInt(arguments.elementAt(0));
				String location = arguments.elementAt(1);
                //TODO : sent result to middleware
                int result = rm.queryCars(id, location);
                break;
            }

            case "queryrooms": {
                checkArgumentsCount(2, arguments.size());
                int id = toInt(arguments.elementAt(0));
				String location = arguments.elementAt(1);
                //TODO : sent result to middleware
                int result = rm.queryRooms(id, location);
                break;
            }

            case "querycustomer": {

            }

            case "queryflightprice": {
                checkArgumentsCount(2, arguments.size());
				int id = toInt(arguments.elementAt(0));
                int flightNum = toInt(arguments.elementAt(1));
                //TODO : sent result to middleware
				int result = rm.queryFlightPrice(id, flightNum);
				break;
            }

            case "querycarsprice": {
                checkArgumentsCount(2, arguments.size());
				int id = toInt(arguments.elementAt(0));
                String location = toInt(arguments.elementAt(1));
                //TODO : sent result to middleware
				int result = rm.queryCarsPrice(id, location);
				break;
            }

            case "queryroomsprice": {
                checkArgumentsCount(2, arguments.size());
				int id = toInt(arguments.elementAt(0));
                String location = toInt(arguments.elementAt(1));
                //TODO : sent result to middleware
				int result = rm.queryRoomsPrice(id, location);
				break;
            }

            case "reserveflight": {
                checkArgumentsCount(3, arguments.size());
                int id = toInt(arguments.elementAt(0));
				int customerID = toInt(arguments.elementAt(1));
				int flightNum = toInt(arguments.elementAt(2));
                //TODO : sent result to middleware
				int result = rm.reserveFlight(id, customerID, flightNum);
            }

            case "reservecar": {
                checkArgumentsCount(3, arguments.size());
				int id = toInt(arguments.elementAt(0));
				int customerID = toInt(arguments.elementAt(1));
                String location = arguments.elementAt(2);
                //TODO : sent result to middleware
				boolean result = rm.reserveCar(id, customerID, location)) {
            }

            case "reserveroom": {
                checkArgumentsCount(3, arguments.size());
				int id = toInt(arguments.elementAt(0));
				int customerID = toInt(arguments.elementAt(1));
				String location = arguments.elementAt(2);
                //TODO : sent result to middleware
				boolean result = rm.reserveRoom(id, customerID, location)) {
            }

            case "bundle": {

            }

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
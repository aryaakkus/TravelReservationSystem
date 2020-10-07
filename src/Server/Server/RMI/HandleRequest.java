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
    private static ResourceManager rm;
    private Scanner in;
    private static PrintWriter out;

    public HandleRequest(Socket socket, ResourceManager rm) {
        this.socket = socket;
        this.rm = rm;
        try {
            this.in = new Scanner(socket.getInputStream());
            this.out = new PrintWriter(socket.getOutputStream(), true);
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

    private static void execute(String cmd, Vector<String> arguments) {
        cmd = cmd.toLowerCase();

        switch (cmd) {
            case "addflight": {
                checkArgumentsCount(4, arguments.size());
                int id = toInt(arguments.elementAt(0));
                int flightNum = toInt(arguments.elementAt(1));
                int flightSeats = toInt(arguments.elementAt(2));
                int flightPrice = toInt(arguments.elementAt(3));
                //TODO : sent result to middleware
                Boolean result = rm.addFlight(id, flightNum, flightSeats, flightPrice);
                out.println(result.toString());
                break;
            }
            case "addrooms": {
                checkArgumentsCount(4, arguments.size());
                int id = toInt(arguments.elementAt(0));
                String location = arguments.elementAt(1);
                int numRooms = toInt(arguments.elementAt(2));
                int price = toInt(arguments.elementAt(3));

                Boolean result = rm.addRooms(id, location, numRooms, price);
                out.println(result.toString());
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
                Boolean result = rm.deleteFlight(id, flightNum);
                out.println(result.toString());
                break;
            }

            case "deletecars": {
                checkArgumentsCount(2, arguments.size());
                int id = toInt(arguments.elementAt(0));
                String location =arguments.elementAt(1);
                //TODO : sent result to middleware
                Boolean result = rm.deleteCars(id, location);
                out.println(result.toString());
                break;
            }

            case "deleterooms": {
                checkArgumentsCount(2, arguments.size());
                int id = toInt(arguments.elementAt(0));
                String location = arguments.elementAt(1);
                //TODO : sent result to middleware
                Boolean result = rm.deleteRooms(id, location);
                out.println(result.toString());
                break;
            }

            case "deletecustomer": {
                checkArgumentsCount(2, arguments.size());
                int id = toInt(arguments.elementAt(0));
				int customerID = toInt(arguments.elementAt(1));
				Boolean result = rm.deleteCustomer(id, customerID);
                out.println(result.toString());
                break;
            }

            case "queryflight": {
                checkArgumentsCount(2, arguments.size());
                int id = toInt(arguments.elementAt(0));
				int flightNum = toInt(arguments.elementAt(1));
                //TODO : sent result to middleware
                Integer result = rm.queryFlight(id, flightNum);
                out.println(result.toString());
                break;
            }

            case "querycars": {
                checkArgumentsCount(2, arguments.size());
                int id = toInt(arguments.elementAt(0));
				String location = arguments.elementAt(1);
                //TODO : sent result to middleware
                Integer result = rm.queryCars(id, location);
                out.println(result.toString());
                break;
            }

            case "queryrooms": {
                checkArgumentsCount(2, arguments.size());
                int id = toInt(arguments.elementAt(0));
				String location = arguments.elementAt(1);
                //TODO : sent result to middleware
                Integer result = rm.queryRooms(id, location);
                out.println(result.toString());
                break;
            }

            case "querycustomer": {
                checkArgumentsCount(2, arguments.size());
                int id = toInt(arguments.elementAt(0));
				int customerId = toInt(arguments.elementAt(1));
                //TODO : sent result to middleware
                String result = rm.queryCustomerInfo(id, customerId);
                out.println(result);
                break;
            }

            case "queryflightprice": {
                checkArgumentsCount(2, arguments.size());
				int id = toInt(arguments.elementAt(0));
                int flightNum = toInt(arguments.elementAt(1));
                //TODO : sent result to middleware
                Integer result = rm.queryFlightPrice(id, flightNum);
                out.println(result.toString());
				break;
            }

            case "querycarsprice": {
                checkArgumentsCount(2, arguments.size());
				int id = toInt(arguments.elementAt(0));
                String location = arguments.elementAt(1);
                //TODO : sent result to middleware
                Integer result = rm.queryCarsPrice(id, location);
                out.println(result.toString());
				break;
            }

            case "queryroomsprice": {
                checkArgumentsCount(2, arguments.size());
				int id = toInt(arguments.elementAt(0));
                String location = arguments.elementAt(1);
                //TODO : sent result to middleware
                Integer result = rm.queryRoomsPrice(id, location);
                out.println(result.toString());
				break;
            }

            case "reserveflight": {
                checkArgumentsCount(3, arguments.size());
                int id = toInt(arguments.elementAt(0));
				int customerID = toInt(arguments.elementAt(1));
				int flightNum = toInt(arguments.elementAt(2));
                //TODO : sent result to middleware
                Boolean result = rm.reserveFlight(id, customerID, flightNum);
                out.println(result.toString());
                break;
            }

            case "reservecar": {
                checkArgumentsCount(3, arguments.size());
				int id = toInt(arguments.elementAt(0));
				int customerID = toInt(arguments.elementAt(1));
                String location = arguments.elementAt(2);
                //TODO : sent result to middleware
                Boolean result = rm.reserveCar(id, customerID, location);
                out.println(result.toString());
                break;
            }

            case "reserveroom": {
                checkArgumentsCount(3, arguments.size());
				int id = toInt(arguments.elementAt(0));
				int customerID = toInt(arguments.elementAt(1));
				String location = arguments.elementAt(2);
                //TODO : sent result to middleware
                Boolean result = rm.reserveRoom(id, customerID, location)) 
                out.println(result.toString());
                break;
            }

            case "bundle": {
                if (arguments.size() < 6) {
                    throw new IllegalArgumentException("Invalid number of arguments");
                }
                int id = toInt(arguments.elementAt(0));
				int customerID = toInt(arguments.elementAt(1));
                Vector<String> flightNumbers = new Vector<String>();
                String [] flights = arguments.elementAt(2).split(",");
				for (String flight : flights) {
					flightNumbers.addElement(flight);
				}
				String location = arguments.elementAt(arguments.size() - 3);
				boolean car = toBoolean(arguments.elementAt(arguments.size() - 2));
                boolean room = toBoolean(arguments.elementAt(arguments.size() - 1));
                
                Boolean result = rm.bundle(id, customerID, flightNumbers, location, car, room);
                out.println(result.toString());
                break;

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
package Client;

import Server.Interface.*;

import java.util.*;
import java.io.*;
import java.net.*;

public class TCPClient extends Client {
    private static String s_serverHost = "localhost";
    private static int s_serverPort = 3031;
    private PrintWriter out;
    private Socket socket;
    // private static String s_rmiPrefix = "group_31_";

    public static void main(String args[]) {
        if (args.length > 0) {
            s_serverHost = args[0];
        }

        if (args.length > 1) {
            System.err.println((char) 27 + "[31;1mClient exception: " + (char) 27
                    + "[0mUsage: java client.TCPClient [server_hostname [server_rmiobject]]");
            System.exit(1);
        }
        try {
            TCPClient client = new TCPClient();
            client.connectServer();
            client.start();
        } catch (Exception e) {
            System.err.println((char) 27 + "[31;1mClient exception: " + (char) 27 + "[0mUncaught exception");
            e.printStackTrace();
            System.exit(1);
        }

    }

    @Override
    public void connectServer() {
        connectServer(s_serverHost, s_serverPort);
    }

    public void connectServer(String server, int port) {
        try (Socket socket = new Socket(server, port)) {
            this.socket = socket;

            PrintWriter out = new PrintWriter(socket.getOutputStream());
            this.out = out;

        } catch (Exception ex) {

            System.out.println("Server not found: " + ex.getMessage());

        }

    }

    public TCPClient() {
        super();
    }

    @Override
    @SuppressWarnings("serial")
    public void execute(Command cmd, Vector<String> arguments) {

        switch (cmd) {
            case Help: {
                if (arguments.size() == 1) {
                    System.out.println(Command.description());
                } else if (arguments.size() == 2) {
                    Command l_cmd = Command.fromString((String) arguments.elementAt(1));
                    System.out.println(l_cmd.toString());
                } else {
                    System.err.println((char) 27 + "[31;1mCommand exception: " + (char) 27
                            + "[0mImproper use of help command. Location \"help\" or \"help,<CommandName>\"");
                }
                break;
            }
            case AddFlight: {
                checkArgumentsCount(5, arguments.size());

                System.out.println("Adding a new flight [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Flight Number: " + arguments.elementAt(2));
                System.out.println("-Flight Seats: " + arguments.elementAt(3));
                System.out.println("-Flight Price: " + arguments.elementAt(4));
                HashMap<String, String> mymap = new HashMap<String, String>() {
                    {
                        put("command", arguments.elementAt(0));
                        put("xid", arguments.elementAt(1));
                        put("flightNum", arguments.elementAt(2));
                        put("flightSeats", arguments.elementAt(3));
                        put("flightPrice", arguments.elementAt(4));
                    }
                };
                String queryMessage = getDataString(mymap);
                this.out.println(queryMessage);
                if (readInput().equals("true")) {
                    System.out.println("Flight added");
                } else {
                    System.out.println("Flight could not be added");
                }
                break;

            }
            case AddCars: {
                checkArgumentsCount(5, arguments.size());

                System.out.println("Adding new cars [xid=" + arguments.elementAt(1) + "]");
                System.out.println("-Car Location: " + arguments.elementAt(2));
                System.out.println("-Number of Cars: " + arguments.elementAt(3));
                System.out.println("-Car Price: " + arguments.elementAt(4));
                HashMap<String, String> mymap = new HashMap<String, String>() {
                    {
                        put("command", arguments.elementAt(0));
                        put("xid", arguments.elementAt(1));
                        put("location", arguments.elementAt(2));
                        put("numCars", arguments.elementAt(3));
                        put("price", arguments.elementAt(4));
                    }
                };
                String queryMessage = getDataString(mymap);
                this.out.println(queryMessage);
                if (readInput().equals("true")) {
                    System.out.println("Cars added");
                } else {
                    System.out.println("Cars could not be added");
                }
                break;
            }case AddRooms:

    {
        checkArgumentsCount(5, arguments.size());

        System.out.println("Adding new rooms [xid=" + arguments.elementAt(1) + "]");
        System.out.println("-Room Location: " + arguments.elementAt(2));
        System.out.println("-Number of Rooms: " + arguments.elementAt(3));
        System.out.println("-Room Price: " + arguments.elementAt(4));
        HashMap<String, String> mymap = new HashMap<String, String>() {
            {
                put("command", arguments.elementAt(0));
                put("xid", arguments.elementAt(1));
                put("location", arguments.elementAt(2));
                put("numRooms", arguments.elementAt(3));
                put("price", arguments.elementAt(4));
            }
        };
        String queryMessage = getDataString(mymap);
        this.out.println(queryMessage);
        if (readInput().equals("true")) {
            System.out.println("Rooms added");
        } else {
            System.out.println("Rooms could not be added");
        }
        break;
    }

    case AddCustomer:
    {
        checkArgumentsCount(2, arguments.size());

        System.out.println("Adding a new customer [xid=" + arguments.elementAt(1) + "]");
        HashMap<String, String> mymap = new HashMap<String, String>() {
            {
                put("command", arguments.elementAt(0));
                put("xid", arguments.elementAt(1));

            }
        };
        String queryMessage = getDataString(mymap);
        this.out.println(queryMessage);
        int customer = Integer.parseInt(readInput());
        System.out.println("Add customer ID: " + customer);
        break;
    }case AddCustomerID:
    {
        checkArgumentsCount(3, arguments.size());

        System.out.println("Adding a new customer [xid=" + arguments.elementAt(1) + "]");
        System.out.println("-Customer ID: " + arguments.elementAt(2));
        HashMap<String, String> mymap = new HashMap<String, String>() {
            {
                put("command", arguments.elementAt(0));
                put("xid", arguments.elementAt(1));
                put("customerID", arguments.elementAt(2));

            }
        };
        String queryMessage = getDataString(mymap);
        this.out.println(queryMessage);
        if (readInput().equals("true")) {
            System.out.println("Add customer ID: " + arguments.elementAt(2));
        } else {
            System.out.println("Customer could not be added");
        }
        break;

    }case DeleteFlight:
    {
        checkArgumentsCount(3, arguments.size());

        System.out.println("Deleting a flight [xid=" + arguments.elementAt(1) + "]");
        System.out.println("-Flight Number: " + arguments.elementAt(2));

        HashMap<String, String> mymap = new HashMap<String, String>() {
            {
                put("command", arguments.elementAt(0));
                put("xid", arguments.elementAt(1));
                put("flightNum", arguments.elementAt(1));

            }
        };
        String queryMessage = getDataString(mymap);
        this.out.println(queryMessage);

        if (readInput().equals("true")) {
            System.out.println("Flight Deleted");
        } else {
            System.out.println("Flight could not be deleted");
        }
        break;
    }case DeleteCars:
    {
        checkArgumentsCount(3, arguments.size());

        System.out.println("Deleting all cars at a particular location [xid=" + arguments.elementAt(1) + "]");
        System.out.println("-Car Location: " + arguments.elementAt(2));
        HashMap<String, String> mymap = new HashMap<String, String>() {
            {
                put("command", arguments.elementAt(0));
                put("xid", arguments.elementAt(1));
                put("location", arguments.elementAt(2));

            }
        };
        String queryMessage = getDataString(mymap);
        this.out.println(queryMessage);

        if (readInput().equals("true")) {
            System.out.println("Cars Deleted");
        } else {
            System.out.println("Cars could not be deleted");
        }
        break;
    }case DeleteRooms:
    {
        checkArgumentsCount(3, arguments.size());

        System.out.println("Deleting all rooms at a particular location [xid=" + arguments.elementAt(1) + "]");
        System.out.println("-Room Location: " + arguments.elementAt(2));
        HashMap<String, String> mymap = new HashMap<String, String>() {
            {
                put("command", arguments.elementAt(0));
                put("xid", arguments.elementAt(1));
                put("location", arguments.elementAt(1));

            }
        };
        String queryMessage = getDataString(mymap);
        this.out.println(queryMessage);

        if (readInput().equals("true")) {
            System.out.println("Rooms Deleted");
        } else {
            System.out.println("Rooms could not be deleted");
        }
        break;
    }case DeleteCustomer:
    {
        checkArgumentsCount(3, arguments.size());

        System.out.println("Deleting a customer from the database [xid=" + arguments.elementAt(1) + "]");
        System.out.println("-Customer ID: " + arguments.elementAt(2));
        HashMap<String, String> mymap = new HashMap<String, String>() {
            {
                put("command", arguments.elementAt(0));
                put("xid", arguments.elementAt(1));
                put("customerID", arguments.elementAt(2));

            }
        };
        String queryMessage = getDataString(mymap);
        this.out.println(queryMessage);

        if (readInput().equals("true")) {
            System.out.println("Customer Deleted");
        } else {
            System.out.println("Customer could not be deleted");
        }
        break;
    }case QueryFlight:
    {
        checkArgumentsCount(3, arguments.size());

        System.out.println("Querying a flight [xid=" + arguments.elementAt(1) + "]");
        System.out.println("-Flight Number: " + arguments.elementAt(2));

        HashMap<String, String> mymap = new HashMap<String, String>() {
            {
                put("command", arguments.elementAt(0));
                put("xid", arguments.elementAt(1));
                put("flightNum", arguments.elementAt(2));

            }
        };
        String queryMessage = getDataString(mymap);
        this.out.println(queryMessage);

        int seats = Integer.parseInt(readInput());
        System.out.println("Number of seats available: " + seats);
        break;
    }case QueryCars:
    {
        checkArgumentsCount(3, arguments.size());

        System.out.println("Querying cars location [xid=" + arguments.elementAt(1) + "]");
        System.out.println("-Car Location: " + arguments.elementAt(2));

        HashMap<String, String> mymap = new HashMap<String, String>() {
            {
                put("command", arguments.elementAt(0));
                put("xid", arguments.elementAt(1));
                put("location", arguments.elementAt(2));

            }
        };
        String queryMessage = getDataString(mymap);
        this.out.println(queryMessage);

        int numCars = Integer.parseInt(readInput());
        System.out.println("Number of cars at this location: " + numCars);
        break;
    }case QueryRooms:
    {
        checkArgumentsCount(3, arguments.size());

        System.out.println("Querying rooms location [xid=" + arguments.elementAt(1) + "]");
        System.out.println("-Room Location: " + arguments.elementAt(2));

        HashMap<String, String> mymap = new HashMap<String, String>() {
            {
                put("command", arguments.elementAt(0));
                put("xid", arguments.elementAt(1));
                put("location", arguments.elementAt(2));

            }
        };
        String queryMessage = getDataString(mymap);
        this.out.println(queryMessage);

        int numRoom = Integer.parseInt(readInput());
        System.out.println("Number of rooms at this location: " + numRoom);
        break;
    }

    case QueryCustomer:
    {
        checkArgumentsCount(3, arguments.size());

        System.out.println("Querying customer information [xid=" + arguments.elementAt(1) + "]");
        System.out.println("-Customer ID: " + arguments.elementAt(2));

        HashMap<String, String> mymap = new HashMap<String, String>() {
            {
                put("command", arguments.elementAt(0));
                put("xid", arguments.elementAt(1));
                put("customerID", arguments.elementAt(2));

            }
        };
        String queryMessage = getDataString(mymap);
        this.out.println(queryMessage);
        String bill = readInput();
        System.out.print(bill);
        break;
    }case QueryFlightPrice:
    {
        checkArgumentsCount(3, arguments.size());

        System.out.println("Querying a flight price [xid=" + arguments.elementAt(1) + "]");
        System.out.println("-Flight Number: " + arguments.elementAt(2));
        HashMap<String, String> mymap = new HashMap<String, String>() {
            {
                put("command", arguments.elementAt(0));
                put("xid", arguments.elementAt(1));
                put("flightNum", arguments.elementAt(2));

            }
        };
        String queryMessage = getDataString(mymap);
        this.out.println(queryMessage);

        int price = Integer.parseInt(readInput());
        System.out.println("Price of a seat: " + price);
        break;
    }case QueryCarsPrice:
    {
        checkArgumentsCount(3, arguments.size());

        System.out.println("Querying cars price [xid=" + arguments.elementAt(1) + "]");
        System.out.println("-Car Location: " + arguments.elementAt(2));

        HashMap<String, String> mymap = new HashMap<String, String>() {
            {
                put("command", arguments.elementAt(0));
                put("xid", arguments.elementAt(1));
                put("location", arguments.elementAt(2));

            }
        };
        String queryMessage = getDataString(mymap);
        this.out.println(queryMessage);

        int price = Integer.parseInt(readInput());
        System.out.println("Price of cars at this location: " + price);
        break;
    }case QueryRoomsPrice:
    {
        checkArgumentsCount(3, arguments.size());

        System.out.println("Querying rooms price [xid=" + arguments.elementAt(1) + "]");
        System.out.println("-Room Location: " + arguments.elementAt(2));

        HashMap<String, String> mymap = new HashMap<String, String>() {
            {
                put("command", arguments.elementAt(0));
                put("xid", arguments.elementAt(1));
                put("location", arguments.elementAt(2));

            }
        };
        String queryMessage = getDataString(mymap);
        this.out.println(queryMessage);

        int price = Integer.parseInt(readInput());
        System.out.println("Price of rooms at this location: " + price);
        break;
    }case ReserveFlight:
    {
        checkArgumentsCount(4, arguments.size());

        System.out.println("Reserving seat in a flight [xid=" + arguments.elementAt(1) + "]");
        System.out.println("-Customer ID: " + arguments.elementAt(2));
        System.out.println("-Flight Number: " + arguments.elementAt(3));

        HashMap<String, String> mymap = new HashMap<String, String>() {
            {
                put("command", arguments.elementAt(0));
                put("xid", arguments.elementAt(1));
                put("customerID", arguments.elementAt(2));
                put("flightNum", arguments.elementAt(3));

            }
        };
        String queryMessage = getDataString(mymap);
        this.out.println(queryMessage);

        if (readInput().equals("true")) {
            System.out.println("Flight Reserved");
        } else {
            System.out.println("Flight could not be reserved");
        }
        break;
    }case ReserveCar:
    {
        checkArgumentsCount(4, arguments.size());

        System.out.println("Reserving a car at a location [xid=" + arguments.elementAt(1) + "]");
        System.out.println("-Customer ID: " + arguments.elementAt(2));
        System.out.println("-Car Location: " + arguments.elementAt(3));

        HashMap<String, String> mymap = new HashMap<String, String>() {
            {
                put("command", arguments.elementAt(0));
                put("xid", arguments.elementAt(1));
                put("customerID", arguments.elementAt(2));
                put("location", arguments.elementAt(3));

            }
        };
        String queryMessage = getDataString(mymap);
        this.out.println(queryMessage);

        if (readInput().equals("true")) {
            System.out.println("Car Reserved");
        } else {
            System.out.println("Car could not be reserved");
        }
        break;
    }case ReserveRoom:
    {
        checkArgumentsCount(4, arguments.size());

        System.out.println("Reserving a room at a location [xid=" + arguments.elementAt(1) + "]");
        System.out.println("-Customer ID: " + arguments.elementAt(2));
        System.out.println("-Room Location: " + arguments.elementAt(3));

        HashMap<String, String> mymap = new HashMap<String, String>() {
            {
                put("command", arguments.elementAt(0));
                put("xid", arguments.elementAt(1));
                put("customerID", arguments.elementAt(2));
                put("location", arguments.elementAt(3));

            }
        };
        String queryMessage = getDataString(mymap);
        this.out.println(queryMessage);

        if (readInput().equals("true")) {
            System.out.println("Room Reserved");
        } else {
            System.out.println("Room could not be reserved");
        }
        break;
    }case Bundle:
    {
        if (arguments.size() < 7) {
            System.err.println((char) 27 + "[31;1mCommand exception: " + (char) 27
                    + "[0mBundle command expects at least 7 arguments. Location \"help\" or \"help,<CommandName>\"");
            break;
        }

        System.out.println("Reserving an bundle [xid=" + arguments.elementAt(1) + "]");
        System.out.println("-Customer ID: " + arguments.elementAt(2));
        for (int i = 0; i < arguments.size() - 6; ++i) {
            System.out.println("-Flight Number: " + arguments.elementAt(3 + i));
        }
        System.out.println("-Location for Car/Room: " + arguments.elementAt(arguments.size() - 3));
        System.out.println("-Book Car: " + arguments.elementAt(arguments.size() - 2));
        System.out.println("-Book Room: " + arguments.elementAt(arguments.size() - 1));

        StringBuilder bld = new StringBuilder();
        for (int i = 0; i < arguments.size() - 6; ++i) {
            bld.append(arguments.elementAt(3 + i));
            if (i < arguments.size() - 7) {
                bld.append(",");
            }
        }
        String str = bld.toString();

        HashMap<String, String> mymap = new HashMap<String, String>() {
            {
                put("command", arguments.elementAt(0));
                put("xid", arguments.elementAt(1));
                put("customerID", arguments.elementAt(2));
                put("flightNumbers", str);
                put("location", arguments.elementAt(arguments.size() - 3));
                put("car", arguments.elementAt(arguments.size() - 2));
                put("room", arguments.elementAt(arguments.size() - 1));

            }
        };

        this.out.println(getDataString(mymap));

        if (readInput().equals("true")) {
            System.out.println("Bundle Reserved");
        } else {
            System.out.println("Bundle could not be reserved");
        }
        break;
    }case Quit:

    checkArgumentsCount(1, arguments.size());

                System.out.println("Quitting client");
                System.exit(0);

        }

    }

    private String getDataString(HashMap<String, String> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }
        return result.toString();
    }

    private String readInput() {
        try {
            Scanner scan1 = new Scanner(this.socket.getInputStream());
            // scan.close();
            return scan1.nextLine();

        } catch (Exception e) {
            return "";
        }
    }}

    
    
    
        
    
    

    
    

    

    
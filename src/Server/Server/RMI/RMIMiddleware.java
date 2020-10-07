
package Server.RMI;

import Server.Interface.*;
import Server.Common.*;

import java.rmi.NotBoundException;
import java.util.*;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIMiddleware extends ResourceManager {
	private static String s_serverName = "Middleware";
	// TODO: ADD YOUR GROUP NUMBER TO COMPLETE
	private static String s_rmiPrefix = "group_31_";

	// add three different resource managers as attributes
	private static IResourceManager carManager = null;
	private static IResourceManager flightManager = null;
	private static IResourceManager roomManager = null;

	// calls addFlight on the flightmanager s
	@Override
	public boolean addFlight(int xid, int flightNum, int flightSeats, int flightPrice) throws RemoteException {

		flightManager.addFlight(xid, flightNum, flightSeats, flightPrice);
		System.out.println("Middleware connected to Flight Server to add flight!");
		return true;

	}

	@Override
	public boolean addCars(int xid, String location, int count, int price) throws RemoteException {

		carManager.addCars(xid, location, count, price);
		System.out.println("Middleware connected to Car Server to add car!");
		return true;

	}

	@Override
	public boolean addRooms(int xid, String location, int count, int price) throws RemoteException {

		roomManager.addRooms(xid, location, count, price);
		System.out.println("Middleware connected to Room server to add rooms!");
		return true;
	}

	@Override
	public boolean deleteFlight(int xid, int flightNum) throws RemoteException {
		flightManager.deleteFlight(xid, flightNum);
		System.out.println("Middleware connected to Flights server to delete flight");
		return true;
	}

	@Override
	public boolean deleteCars(int xid, String location) throws RemoteException {
		carManager.deleteCars(xid, location);
		System.out.println("Middleware connected to Cars server to delete cars");
		return true;
	}

	@Override
	public boolean deleteRooms(int xid, String location) throws RemoteException {
		roomManager.deleteRooms(xid, location);
		System.out.println("Middleware connected to Rooms server to delete rooms");
		return true;
	}

	@Override
	public int queryFlight(int xid, int flightNum) throws RemoteException {
		System.out.println("Middleware connected to Flights server to query flights.");
		return flightManager.queryFlight(xid, flightNum);

	}

	@Override
	public int queryCars(int xid, String location) throws RemoteException {
		System.out.println("Middleware connected to Cars server to query cars.");
		return carManager.queryCars(xid, location);
	}

	@Override
	public int queryRooms(int xid, String location) throws RemoteException {
		System.out.println("Middleware connected to Rooms server to query rooms.");
		return roomManager.queryRooms(xid, location);
	}

	@Override
	public int queryFlightPrice(int xid, int flightNum) throws RemoteException {
		System.out.println("Middleware connected to Flights server to query flight price.");
		return flightManager.queryFlightPrice(xid, flightNum);

	}

	@Override
	public int queryCarsPrice(int xid, String location) throws RemoteException {
		System.out.println("Middleware connected to Cars server to query cars price.");
		return carManager.queryCarsPrice(xid, location);
	}

	@Override
	public int queryRoomsPrice(int xid, String location) throws RemoteException {
		System.out.println("Middleware connected to Rooms server to query rooms price.");
		return roomManager.queryRoomsPrice(xid, location);
	}

	@Override
	public boolean reserveFlight(int xid, int customerID, int flightNum) throws RemoteException {
		// Read customer object if it exists (and read lock it)
		Customer customer = (Customer) readData(xid, Customer.getKey(customerID));
		if (customer == null) {
			Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ")  failed--customer doesn't exist");
			return false;
		} else {
			boolean b = flightManager.reserveFlight(xid, customerID, flightNum);
			if (b) {
				// if item was successfully reserved, reserve it for customer
				customer.reserve(Flight.getKey(flightNum), String.valueOf(flightNum),
						flightManager.queryFlightPrice(xid, flightNum));
				writeData(xid, customer.getKey(), customer);
				return true;

			}
			return false;
		}
	}

	@Override
	public boolean reserveCar(int xid, int customerID, String location) throws RemoteException {
		// Read customer object if it exists (and read lock it)
		Customer customer = (Customer) readData(xid, Customer.getKey(customerID));
		if (customer == null) {
			Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ")  failed--customer doesn't exist");
			return false;
		} else {
			boolean b = carManager.reserveCar(xid, customerID, location);
			if (b) {
				customer.reserve(Car.getKey(location), location, carManager.queryCarsPrice(xid, location));
				writeData(xid, customer.getKey(), customer);
				return true;

			}
			return false;
		}

	}

	@Override
	public boolean reserveRoom(int xid, int customerID, String location) throws RemoteException {
		// Read customer object if it exists (and read lock it)
		Customer customer = (Customer) readData(xid, Customer.getKey(customerID));
		if (customer == null) {
			Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ")  failed--customer doesn't exist");
			return false;
		} else {
			boolean b = roomManager.reserveRoom(xid, customerID, location);
			if (b) {
				customer.reserve(Room.getKey(location), location, roomManager.queryRoomsPrice(xid, location));
				writeData(xid, customer.getKey(), customer);
				return true;

			}
			return false;
		}
	}

	@Override
	public boolean bundle(int xid, int customerId, Vector<String> flightNumbers, String location, boolean car,
			boolean room) throws RemoteException {
		
		Customer customer = (Customer) readData(xid, Customer.getKey(customerId));
		if (customer == null) {
			Trace.warn("RM::reserveItem(" + xid + ", " + customerId + ")  failed--customer doesn't exist");
			return false;
		}
		boolean reservable = false;
		for (int index = 0; index < flightNumbers.size(); index++) {
			reservable = (flightManager.queryFlight(xid, Integer.valueOf(flightNumbers.get(index))) > 0);
			if (!reservable) {
				break;
			}

		}

		if (reservable) {
			for (int index = 0; index < flightNumbers.size(); index++) {
				reserveFlight(xid, customerId, Integer.valueOf(flightNumbers.get(index)));
			}

			if (car && carManager.queryCars(xid, location) > 0) {
				reserveCar(xid, customerId, location);
			}

			if (room && roomManager.queryRooms(xid, location) > 0) {
				reserveRoom(xid, customerId, location);
			}

			return true;
		} else {
			return false;
		}

	}

	// @Override
	// public boolean deleteCustomer(int xid, int customerID) throws RemoteException
	// {
	// Customer customer = (Customer)readData(xid, Customer.getKey(customerID));
	// if (customer == null)
	// {
	// Trace.warn("RM::deleteCustomer(" + xid + ", " + customerID + ")
	// failed--customer doesn't exist");
	// return false;
	// }
	// else
	// {
	// RMHashMap reservations = customer.getReservations();
	// for (String reservedKey : reservations.keySet())
	// {
	// if()
	// // ReservedItem reserveditem = customer.getReservedItem(reservedKey);
	// // Trace.info("RM::deleteCustomer(" + xid + ", " + customerID + ") has
	// reserved " + reserveditem.getKey() + " " + reserveditem.getCount() + "
	// times");
	// // ReservableItem item = (ReservableItem)readData(xid,
	// reserveditem.getKey());
	// // Trace.info("RM::deleteCustomer(" + xid + ", " + customerID + ") has
	// reserved " + reserveditem.getKey() + " which is reserved " +
	// item.getReserved() + " times and is still available " + item.getCount() + "
	// times");
	// // item.setReserved(item.getReserved() - reserveditem.getCount());
	// // item.setCount(item.getCount() + reserveditem.getCount());
	// // writeData(xid, item.getKey(), item);
	// }
	// removeData(xid, customer.getKey());
	// Trace.info("RM::deleteCustomer(" + xid + ", " + customerID + ") succeeded");
	// return true;

	// }
	// }

	public static IResourceManager connectServer(String server, int port, String name) {
		try {
			boolean first = true;
			while (true) {
				try {
					Registry registry = LocateRegistry.getRegistry(server, port);
					IResourceManager resourceManager = (IResourceManager) registry.lookup(s_rmiPrefix + name);
					System.out.println("Connected to '" + name + "' server [" + server + ":" + port + "/" + s_rmiPrefix
							+ name + "]");
					return resourceManager;
				} catch (NotBoundException | RemoteException e) {
					if (first) {
						System.out.println("Waiting for '" + name + "' server [" + server + ":" + port + "/"
								+ s_rmiPrefix + name + "]");
						first = false;
					}
				}
				Thread.sleep(500);
			}
		} catch (Exception e) {
			System.err.println((char) 27 + "[31;1mServer exception: " + (char) 27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	// connect three servers to middleware
	public static void connectServers() {
		// all connect to same registry
		flightManager = connectServer(Const.REGISTRY_HOST_NAME, Const.REGISTRY_PORT, Const.FLIGHT_SERVER_NAME);
		carManager = connectServer(Const.REGISTRY_HOST_NAME, Const.REGISTRY_PORT, Const.CAR_SERVER_NAME);
		roomManager = connectServer(Const.REGISTRY_HOST_NAME, Const.REGISTRY_PORT, Const.ROOM_SERVER_NAME);
	}

	public static void main(String args[]) {
		if (args.length > 0) {
			s_serverName = args[0];
		}

		// Create the RMI server entry
		try {
			// Create a new Server object
			RMIMiddleware middlewareServer = new RMIMiddleware(s_serverName);
			connectServers();

			// Dynamically generate the stub (client proxy)
			IResourceManager resourceManager = (IResourceManager) UnicastRemoteObject.exportObject(middlewareServer, 0);

			// Bind the remote object's stub in the registry
			Registry l_registry;
			try {
				l_registry = LocateRegistry.createRegistry(3031);
			} catch (RemoteException e) {
				l_registry = LocateRegistry.getRegistry(3031);
			}
			final Registry registry = l_registry;
			registry.rebind(s_rmiPrefix + s_serverName, resourceManager);

			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
						registry.unbind(s_rmiPrefix + s_serverName);
						System.out.println("'" + s_serverName + "' resource manager unbound");
					} catch (Exception e) {
						System.err
								.println((char) 27 + "[31;1mServer exception: " + (char) 27 + "[0mUncaught exception");
						e.printStackTrace();
					}
				}
			});
			System.out.println("'" + s_serverName + "' resource manager server ready and bound to '" + s_rmiPrefix
					+ s_serverName + "'");
		} catch (Exception e) {
			System.err.println((char) 27 + "[31;1mServer exception: " + (char) 27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
		}

		// Create and install a security manager
		// if (System.getSecurityManager() == null)
		// {
		// System.setSecurityManager(new SecurityManager());
		// }
	}

	public RMIMiddleware(String name) {
		super(name);
	}
}

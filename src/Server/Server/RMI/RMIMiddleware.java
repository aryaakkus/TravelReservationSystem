
package Server.RMI;

import Server.Interface.*;
import Server.Common.*;

import java.rmi.NotBoundException;
import java.util.*;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;



public class RMIMiddleware extends ResourceManager
{
    private static String s_serverName = "Middleware";
	//TODO: ADD YOUR GROUP NUMBER TO COMPLETE
    private static String s_rmiPrefix = "group_31_";

    //add three different resource managers as fields
    private static IResourceManager carManager = null;
    private static IResourceManager flightManager  = null;
    private static IResourceManager roomManager = null;

    @Override
    public boolean addFlight(int xid, int flightNum, int flightSeats, int flightPrice) throws RemoteException{
      
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





	

    public static IResourceManager connectServer(String server, int port, String name)
	{
		try {
			boolean first = true;
			while (true) {
				try {
					Registry registry = LocateRegistry.getRegistry(server, port);
					IResourceManager resourceManager = (IResourceManager)registry.lookup(s_rmiPrefix + name);
					System.out.println("Connected to '" + name + "' server [" + server + ":" + port + "/" + s_rmiPrefix + name + "]");
					return resourceManager;
				}
				catch (NotBoundException|RemoteException e) {
					if (first) {
						System.out.println("Waiting for '" + name + "' server [" + server + ":" + port + "/" + s_rmiPrefix + name + "]");
						first = false;
					}
				}
				Thread.sleep(500);
			}
		}
		catch (Exception e) {
			System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
        }
        return null;
	}


    //connect three servers to middleware
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
			IResourceManager resourceManager = (IResourceManager)UnicastRemoteObject.exportObject(middlewareServer, 0);

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
					}
					catch(Exception e) {
						System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
						e.printStackTrace();
					}
				}
			});                                       
			System.out.println("'" + s_serverName + "' resource manager server ready and bound to '" + s_rmiPrefix + s_serverName + "'");
		}
		catch (Exception e) {
			System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
		}

		// Create and install a security manager
		//if (System.getSecurityManager() == null)
		//{
		//	System.setSecurityManager(new SecurityManager());
		//}
	}

	public RMIMiddleware(String name)
	{
		super(name);
	}
}



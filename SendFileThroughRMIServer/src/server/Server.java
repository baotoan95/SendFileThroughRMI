package server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
	public static void main(String[] args) {
		try {
			LocateRegistry.createRegistry(12345);
			Registry registry = LocateRegistry.getRegistry(12345);
			
			registry.bind("FileHandler", new FileHandler());
			System.out.println("Server is ready...");
		} catch (RemoteException | AlreadyBoundException e) {
			e.printStackTrace();
		}
	}
}

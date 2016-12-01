


import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Enumeration;

public class DeProp_main {
	
	private static final int TOTAL_PROCESSES = 2;

    public static void main(String args[]) {

        try {
        	try {
        		java.rmi.registry.LocateRegistry.createRegistry(1099);
        		} catch (RemoteException e) {
        		e.printStackTrace();
        		}
        	
        	
        	System.setProperty("java.security.policy","./my.policy");
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new RMISecurityManager());
            }
            
            System.err.println("Server ready");
            
            new ProcessStarter().spawnProcesses(TOTAL_PROCESSES);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
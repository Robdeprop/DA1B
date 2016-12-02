


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
	
	private static final int TOTAL_LOCAL_PROCESSES = 4;
	private static final String[] ipAddressesInNetwork = {"192.168.1.25"};

    public static void main(String args[]) {
    	System.out.println("IP Addresses in the network that should run this code:");
    	for(String ip : ipAddressesInNetwork)
    	{
    		System.out.println(ip);
    	}
    	System.out.println("--------------");
    	
        try {
        	try {
        		java.rmi.registry.LocateRegistry.createRegistry(1099);
        		}catch (RemoteException e) {
        		e.printStackTrace();
        		}
        	
        	
        	System.setProperty("java.security.policy","./my.policy");
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new RMISecurityManager());
            }
            
            System.out.println("Server ready!");
            
            new ProcessStarter().spawnProcesses(TOTAL_LOCAL_PROCESSES, ipAddressesInNetwork);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
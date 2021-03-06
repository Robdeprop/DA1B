


import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

public class DeProp_main {

	private static final String[] ipAddressesInNetwork = {"localhost", "localhost", "localhost", "localhost"};

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
            
            ProcessStarter p = new ProcessStarter();
            p.start(ipAddressesInNetwork, true);
            
            System.out.println("Program will keep executing for 30s");
	        try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
	        
	        p.logRandInt();
	        
	        
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
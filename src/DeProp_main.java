


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
    private static final String RMI_PREFIX = "rmi://";
    short ipNetworkPrefixLength = 22;

    private ArrayList<DeProp_RMI> processes;
    private Enumeration<NetworkInterface> networkInterfaces;
    private InetAddress inetAddress;
    private static final int INSTANTIATION_DELAY = 10000;

    public static void main(String args[]) {

        try {
        	try {
        		java.rmi.registry.LocateRegistry.createRegistry(1099);
        		} catch (RemoteException e) {
        		e.printStackTrace();
        		}
        	
            /*if (System.getSecurityManager() == null) {
                System.setSecurityManager(new RMISecurityManager());
            }*/
            
            System.err.println("Server ready");
            
            new ProcessStarter().spawnProcesses();
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
    
    public void start() {

    	try{
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e){
        	System.err.println("Cannot instantiate IP resolver");
            throw new RuntimeException(e);
        }
    	
    	System.out.println(inetAddress);
    	
    	for (String ipAddress: this.getIpAddresses()){
    		//isLocal = isLocal || url.startsWith(RMI_PREFIX + ipAddress);
    		System.out.println(ipAddress.toString());
    	}
    }
    
    
    
    private boolean isProcessLocal(String url){    
    	
    	boolean isLocal = false;
    	
    	//LOGGER.debug("======================================================================");
    	for (String ipAddress: this.getIpAddresses()){
    		isLocal = isLocal || url.startsWith(RMI_PREFIX + ipAddress);
    	//	LOGGER.debug(ipAddress.toString());
    	}
    	//LOGGER.debug(inetAddress.getHostAddress());
    	//LOGGER.debug("----------------------------------------------------------------------");
    	
    	// special cases.
    	isLocal = isLocal || url.startsWith(RMI_PREFIX + "localhost");
    	isLocal = isLocal || url.startsWith(RMI_PREFIX + "127.0.0.1");
    	isLocal = isLocal || url.startsWith(RMI_PREFIX + "127.0.1.1");
    	isLocal = isLocal || url.startsWith(RMI_PREFIX + inetAddress.getHostAddress());
    	
    	System.out.println("url: " + url + " isLocal: " + isLocal);
    	//LOGGER.debug("======================================================================");

        return isLocal;
    }
    	
    private String[] getIpAddresses() {
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e5) {
            System.err.println("Cannot instantiate IP resolver");
            throw new RuntimeException(e5);
        }

        ArrayList<String> ipAddresses = new ArrayList<String>();

        while (networkInterfaces.hasMoreElements()) {
        	NetworkInterface networkInterface = networkInterfaces.nextElement();

			for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
			{
				//if (ipNetworkPrefixLength == interfaceAddress.getNetworkPrefixLength())
				//{
					ipAddresses.add( interfaceAddress.getAddress().getHostAddress().toString() );						
				//}
			}
        }

        String[] result = new String[ipAddresses.size()];

        return ipAddresses.toArray(result);
    }
}
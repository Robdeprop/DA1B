

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;

public class ProcessStarter {

    private static final String RMI_PREFIX = "rmi://";
    short ipNetworkPrefixLength = 22;

    private ArrayList<DeProp_RMI> processes;
    private Enumeration<NetworkInterface> networkInterfaces;
    private InetAddress inetAddress;
    private static final int INSTANTIATION_DELAY = 10000;
    
    private ArrayList<String> ipAddressesInNetwork;

    /**
     * Launches server instance
     */
    public void spawnProcesses(int numOfLocalProcesses) {
        
        //instantiating InetAddress to resolve local IP
        try{
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e){
            System.err.println("Cannot instantiate IP resolver");
            throw new RuntimeException(e);
        }
        
        System.out.println(inetAddress);
        
        String[] processURLs = new String[numOfLocalProcesses]; // ADD A + 1 HERE FOR HARDCODED IP
        
        for(int i = 0; i < numOfLocalProcesses; i++)
        {
        	processURLs[i] = "rmi://localhost/SESprocess" + i;
        }
        
        // TO HARDCODE IP ADDRESSES:
        //processURLs[numOfLocalProcesses] = "rmi://192.168.43.213/SESprocess0";
        
        
        processes = new ArrayList<DeProp_RMI>();

        //bind local processes and locate remote ones
        try {
            DeProp_RMI process;
            int processIndex = 0;
            for (String url : processURLs) {
                if (isProcessLocal(url)) {
                    process = new DeProp(processURLs.length, processIndex);
                    System.out.println("Process " + processIndex + " is local.");
                    new Thread((DeProp) process).start();
                    Naming.bind(url, process);
                    processes.add(process);
                }
                processIndex++;

            }

            //sleep is needed to instantiate local processes at all machines
            System.out.println("Waiting for remote processes to instantiate...");
            try {
                Thread.sleep(INSTANTIATION_DELAY);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("And looking them up.");

            for (String url : processURLs) {
                if (!isProcessLocal(url)) {
                    process = (DeProp_RMI) Naming.lookup(url);
                    System.out.println("Found remote process with URL " + url);
                    processes.add(process);
                }
            }

        } catch (RemoteException e1) {
            e1.printStackTrace();
        } catch (AlreadyBoundException e2) {
            e2.printStackTrace();
        } catch (NotBoundException e3) {
            e3.printStackTrace();
        } catch (MalformedURLException e4) {
            e4.printStackTrace();
        }
    }

    private boolean isProcessLocal(String url){    
    	
    	boolean isLocal = false;
    	
    	//LOGGER.debug("======================================================================");
    	
    	ipAddressesInNetwork = this.getIpAddressesInNetwork();
    	for (String ip: ipAddressesInNetwork)
    	{
    		isLocal = isLocal || url.startsWith(RMI_PREFIX + ip);
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

    private ArrayList<String> getIpAddressesInNetwork() {
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e5) {
            System.out.println("Cannot instantiate IP resolver");
            throw new RuntimeException(e5);
        }

        ArrayList<String> ipAddresses = new ArrayList<String>();

        while (networkInterfaces.hasMoreElements()) {
        	NetworkInterface networkInterface = networkInterfaces.nextElement();

			for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
			{
				String hostAddress = interfaceAddress.getAddress().getHostAddress();
				ipAddresses.add(hostAddress.toString());			
				System.out.println("Found IP: " + hostAddress.toString());
			}
        }
        
        return ipAddresses;
    }

}

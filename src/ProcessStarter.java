

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
import java.util.Map;
import java.util.TreeMap;

public class ProcessStarter {

    private static final String RMI_PREFIX = "rmi://";
    short ipNetworkPrefixLength = 22;

    private TreeMap<String, DeProp_RMI> processes;
    private ArrayList<DeProp_RMI> localProcesses;
    private Enumeration<NetworkInterface> networkInterfaces;
    private InetAddress inetAddress;
    String localIpAddress;
    private static final String PROCESS_PREFIX = "DePropSESProcess";
    private static final int INSTANTIATION_DELAY = 10000;
    
    private ArrayList<String> ipAddressesInNetwork;

    /**
     * Launches server instance
     */
    public void spawnProcesses(int numOfLocalProcesses, String[] ipAddressesInNetwork) {
        
        //instantiating InetAddress to resolve local IP
        try{
            inetAddress = InetAddress.getLocalHost();
            localIpAddress = inetAddress.getHostAddress();
        } catch (UnknownHostException e){
            System.err.println("Cannot instantiate IP resolver");
            throw new RuntimeException(e);
        }
        
        int totalProcesses = ipAddressesInNetwork.length * numOfLocalProcesses;
        
        System.out.println("Your own IP is: " + localIpAddress);
        
        String[] processURLs = new String[numOfLocalProcesses]; // ADD A + 1 HERE FOR HARDCODED IP
        
        
        processes = new TreeMap<String, DeProp_RMI>();
        localProcesses = new ArrayList<DeProp_RMI>();
        DeProp_RMI process;
        try {
	        for(int i = 0; i < numOfLocalProcesses; i++)
	        {
	        	String processURL = "rmi://" + localIpAddress + "/" + PROCESS_PREFIX + i;
	        	process = new DeProp(totalProcesses, processURL);
	        	
				Naming.bind(processURL, process);
				
	        	processes.put(processURL, process);
	        	localProcesses.add(process);
	            
	            /*System.out.println("Process " + processURL + " is local.");
	            new Thread((DeProp) process).start();*/
	        }
	        
	        
	        if(ipAddressesInNetwork.length > 1)
	        {
	        	System.out.println("Sleeping for " + INSTANTIATION_DELAY + "ms to wait for other servers");;
	        	try {
	                Thread.sleep(INSTANTIATION_DELAY);
	            } catch (InterruptedException e) {
	                throw new RuntimeException(e);
	            }
	        	
		        ArrayList<String> remoteURLs = new ArrayList<String>();
		        for(String ip : ipAddressesInNetwork)
		        {
		        	if(!ip.equals(localIpAddress))
		        	{
		        		for(int i = 0; i < numOfLocalProcesses; i++)
		        		{
		        			String remoteURL = "rmi://" + ip + "/" + PROCESS_PREFIX + i;
		        			remoteURLs.add(remoteURL);
		        		}
		        	}
		        	for (String url : remoteURLs)
		        	{
		        		System.out.println("Connecting to remote process " + url + "...");
			           
						process = (DeProp_RMI) Naming.lookup(url);
						
			            System.out.println("Connected to remote process " + url + "!");
			            processes.put(url, process);
			        }
		        }
	        }
	        
	        
	        ArrayList<DeProp_RMI> processList = new ArrayList<DeProp_RMI>();
	        
	        for(Map.Entry<String, DeProp_RMI> entry : processes.entrySet()) {
	        	  String key = entry.getKey();
	        	  DeProp_RMI value = entry.getValue();
	        	  System.out.println(key + " => " + value);
	        	  processList.add(value);
	        }
	        
	        for(DeProp_RMI localProcess : localProcesses)
	        {
	        	((DeProp) localProcess).setProcesses(processList);
	        }
	        
	        try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
	        
	        System.out.println("Whole system is connected, so start threads now");
	        for(DeProp_RMI localProcess : localProcesses)
	        {
	        	new Thread((DeProp) localProcess).start();
	        }
	
	        
	    } catch (MalformedURLException | RemoteException | AlreadyBoundException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
}

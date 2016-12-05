

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
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ProcessStarter {

    private static final String RMI_PREFIX = "rmi://";
    short ipNetworkPrefixLength = 22;

    private ArrayList<DeProp_RMI> processes;
    private ArrayList<DeProp_RMI> localProcesses;
    private Enumeration<NetworkInterface> networkInterfaces;
    private InetAddress inetAddress;
    String localIpAddress;
    private static final String PROCESS_PREFIX = "DePropSESProcess";
    private static final int INSTANTIATION_DELAY = 10000;
    
    private ArrayList<String> ipAddressesInNetwork;
    
    private String ownProcessURL;
    private int ownProcessIndex;
    private DeProp_RMI ownProcess = null;

    /**
     * Launches server instance
     */
    
    public void start(String[] ipAddressesInNetwork, Boolean spawnIfPossible)
    {
    	start(ipAddressesInNetwork, spawnIfPossible, false);
    }
    
    public void print(Boolean silent, String message)
    {
    	if(silent) { return; }
    	System.out.println(message);
    }
    
    public void start(String[] ipAddressesInNetwork, Boolean spawnIfPossible, Boolean silent) {
        
        //instantiating InetAddress to resolve local IP
        try{
            inetAddress = InetAddress.getLocalHost();
            localIpAddress = inetAddress.getHostAddress();
        } catch (UnknownHostException e){
            System.err.println("Cannot instantiate IP resolver");
            throw new RuntimeException(e);
        }
        
        int totalProcesses = ipAddressesInNetwork.length;
        
        print(silent, "Your own IP is: " + localIpAddress);
        
        //String[] processURLs = new String[totalProcesses];
        
        for(int i = 0; i < totalProcesses; i++)
        {
        	if(ipAddressesInNetwork[i].equals("localhost"))
        	{
        		ipAddressesInNetwork[i] = localIpAddress;
        	}
        }
        
        Arrays.sort(ipAddressesInNetwork);
        
        HashMap<String, Boolean> takenProcesses = new HashMap<String, Boolean>();
        ArrayList<String> localProcesses = new ArrayList<String>();
        ArrayList<String> processURLs = new ArrayList<String>();
        String ownProcessURL = null;
        
        for(String ip : ipAddressesInNetwork)
        {
        	int procNum = 1;
        	String processURL = "rmi://" + ip + "/" + PROCESS_PREFIX + procNum;
        	while(takenProcesses.containsKey(processURL))
        	{
        		procNum++;
        		processURL = "rmi://" + ip + "/" + PROCESS_PREFIX + procNum;
        		print(silent, processURL + " can be instantiated");
        	}
        	takenProcesses.put(processURL, false);
        	processURLs.add(processURL);
        	if(ip.equals(localIpAddress))
        	{
        		// Local process
        		localProcesses.add(processURL);
        	}
        }
        
        
        try {
        	//print(silent, "Names bound to RMI registry at host " + host + " and port " + port + ":");
        	Registry registry;
        	String[] boundNames;
        	for(String ip : ipAddressesInNetwork)
	        {
	        	registry = LocateRegistry.getRegistry(ip, 1099);
	        	boundNames = registry.list();
	        	print(silent, "Bound:");
	            for (final String name : boundNames)
	            {
	            	String processName = "rmi://" + ip + "/" + name;
	            	if(takenProcesses.containsKey(processName))
	            	{
	            		takenProcesses.put(processName, true);
	            		print(silent, "\t" + processName + " is taken");
	            	}
	            }
	        }
        	
        	
        	print(silent, "Unbound local processes:");
        	
        	boolean boundAProcess = false;
        	for(String localProcessURL : localProcesses)
        	{
        		if(!takenProcesses.get(localProcessURL))
        		{
        			print(silent, localProcessURL);
        			// Bind this URL
        			if(!boundAProcess && spawnIfPossible)
        			{
        				boundAProcess = true;
	        			ownProcess = new DeProp(totalProcesses, localProcessURL, processURLs.indexOf(localProcessURL));
	        			new Thread((DeProp) ownProcess).start();
	        			Naming.bind(localProcessURL, (DeProp_RMI) UnicastRemoteObject.exportObject(ownProcess));
	        			ownProcessURL = localProcessURL;
	                    print(silent, "Process " + localProcessURL + " instantiated!");
        			}
        		}
        	}
        	
            // Now wait until all processes are bound
        	print(silent, "Waiting for all processes to be instantiated...");
        	while(takenProcesses.containsValue(false))
        	{
        		
        		for(String ip : ipAddressesInNetwork)
    	        {
    	        	registry = LocateRegistry.getRegistry(ip, 1099);
    	        	boundNames = registry.list();
    	            for (final String name : boundNames)
    	            {
    	            	String processName = "rmi://" + ip + "/" + name;
    	            	if(takenProcesses.containsKey(processName) && !takenProcesses.get(processName))
    	            	{
    	            		takenProcesses.put(processName, true);
    	            		print(silent, "\tNew remote process bound: " + processName);
    	            	}
    	            }
    	        }
        		
        		System.out.print(".");
        		try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
        	}
        	
        	
        	
        	
        	
        	processes = new ArrayList<DeProp_RMI>();
        	for(String processURL : processURLs)
        	{
        		if(processURL.equals(ownProcessURL))
        		{
        			// Own process, start thread
        			print(silent, "Added own process " + processURL);
        			processes.add(ownProcess);
        		}
        		else
        		{
        			// Not own process, look up
        			print(silent, "Connecting to remote process " + processURL + "...");
        			processes.add((DeProp_RMI) Naming.lookup(processURL));
        		}
        	}
        	
        	print(silent, "Connected to all remote processes!");

        	if(ownProcess != null)
        	{
        		((DeProp) ownProcess).setProcesses(processURLs);
        	}

	        
	        try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
	        
	        

		    print(silent, "Whole system is connected now");
	
	        
	    } catch (MalformedURLException | RemoteException | NotBoundException | AlreadyBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    public void logRandInt()
    {
    	if(ownProcess != null)
    	{
    		int getter = -1;
			
				try {
					getter = ownProcess.getRandomInt();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

    		System.out.println("Exit. Rand int was " + getter);
    	}
    }
    
    public ArrayList<DeProp_RMI> getProcesses()
    {
    	return this.processes;
    }
}

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class Setup {

	private static final int TOTAL_LOCAL_PROCESSES = 4;
	private static final String[] ipAddressesInNetwork = {"192.168.1.25"};
	private static final String PROCESS_PREFIX = "DePropSESProcess";
	
	private ArrayList<DeProp_RMI> processes;
	
	public Setup()
	{
		 ArrayList<String> remoteURLs = new ArrayList<String>();
		 processes = new ArrayList<DeProp_RMI>();
		 try {
		     for(String ip : ipAddressesInNetwork)
		     {
		     	for(int i = 0; i < TOTAL_LOCAL_PROCESSES; i++)
		     	{
		     		String remoteURL = "rmi://" + ip + "/" + PROCESS_PREFIX + i;
		     		System.out.println("Adding remoteURL " + remoteURL);
		     		DeProp_RMI process;
					process = (DeProp_RMI) Naming.lookup(remoteURL);
		            processes.add(process);
		     	}
		     }
		 } catch (MalformedURLException | RemoteException | NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		for(DeProp_RMI process : processes)
		{
			((DeProp) process).setProcesses(processes);
		}
		
		for(DeProp_RMI process : processes)
        {
        	new Thread((DeProp) process).start();
        }
		
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<DeProp_RMI> getProcesses()
	{
		return this.processes;
	}
}

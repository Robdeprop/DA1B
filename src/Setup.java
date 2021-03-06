import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class Setup {

	private static final String[] ipAddressesInNetwork = {"localhost", "localhost", "localhost", "localhost"};
	private ArrayList<DeProp_RMI> processes;
	
	public Setup()
	{
		this(false);
	}
	
	public Setup(Boolean silent)
	{
		System.setProperty("java.security.policy","./my.policy");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        
		ProcessStarter p = new ProcessStarter();
		p.start(ipAddressesInNetwork, true, silent);
		this.processes = p.getProcesses();
		
		// RESET all processes
		for(DeProp_RMI process : processes)
		{
			try {
				process.reset();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList<DeProp_RMI> getProcesses()
	{
		return this.processes;
	}
}

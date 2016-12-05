import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class TestSetup {

	private static final int TOTAL_LOCAL_PROCESSES = 2;
	private static final String[] ipAddressesInNetwork = {"localhost", "localhost"};
	private static final String PROCESS_PREFIX = "DePropSESProcess";
	
	public static void main(String args[]) {
		ProcessStarter p = new ProcessStarter();
		p.start(ipAddressesInNetwork, true);
		ArrayList<DeProp_RMI> processes = p.getProcesses();
		
		int index = -1;
		try {
			index = processes.get(0).getIndex();
		} catch (RemoteException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		int myRand = -1;
		System.out.println("GOT INDEX: " + index);
		
		try {
			myRand = processes.get(0).getRandomInt();
		} catch (RemoteException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		System.out.println("GOT RAND: " + myRand);
		
		
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
		
		DeProp_RMI process1 = processes.get(0);
        DeProp_RMI process2 = processes.get(1);
        try{
            Message message1 = new Message(process1.getIndex(),process2.getIndex(), 1);
            process1.send(process2.getIndex(), message1, 10);

            Message message2 = new Message(process1.getIndex(), process2.getIndex(), 2);
            process1.send(process2.getIndex(), message2, 0);

            // Sleep at least the sum of all delays to be sure all messages have arrived.
            Thread.sleep(20);
        }
        catch (RemoteException e1){
            e1.printStackTrace();
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class My_class extends UnicastRemoteObject
implements My_interface_RMI {
protected My_class() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

@Override
public String sayHello() throws RemoteException {
	// TODO Auto-generated method stub
	return "Hello world!";
	
}

	public static void main(String args[]) 
	{ 
	    try 
	    { 
	        My_class obj = new My_class(); 
	        // Bind this object instance to the name "HelloServer" 
	        Naming.rebind("TestServer", obj); 
	    } 
	    catch (Exception e) 
	    { 
	        System.out.println("My_class err: " + e.getMessage()); 
	        e.printStackTrace(); 
	    } 
	} 
}
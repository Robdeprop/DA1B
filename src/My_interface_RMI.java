import java.rmi.Remote;

public interface My_interface_RMI extends Remote {
public String sayHello() throws java.rmi.RemoteException;
}
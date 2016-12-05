import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DeProp implements DeProp_RMI, Runnable, Serializable {
	
	private ArrayList<DeProp_RMI> processes;
	private String processURL;
	
	private HashMap<Integer, ArrayList<Integer>> latestSentMessages;
	private ArrayList<Message> pendingMessages;
	private ArrayList<Message> receivedMessages;
	
	private ArrayList<Integer> localVectorClock;
	
	private int index;
	
	private int myRand;

    public DeProp(int totalProcesses, String processURL, int index) {
    	this.processURL = processURL;
    	this.index = index;
    	Random rand = new Random();
    	myRand = rand.nextInt(50) + 1;
    	
    	System.out.println("Initiated DeProp with index " + index + " and rand " + myRand);
    }
    
	@Override
	public void reset() {		
		this.latestSentMessages = new HashMap<Integer, ArrayList<Integer>>();
		this.pendingMessages = new ArrayList<Message>();
		this.receivedMessages = new ArrayList<Message>();
		this.localVectorClock = new ArrayList<Integer>();
		for(int i = 0; i < this.processes.size(); i++)
		{
			this.localVectorClock.add(0);
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Thread of process " + this.processURL + " started");
		this.reset();
		
		/*try {
			this.sendSomethingToEveryone();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	@Override
	public ArrayList<Message> getReceivedMessages()
	{
		System.out.println("Somebody requested my received messages, which there are currently " + this.receivedMessages.size() + " of.");
		return this.receivedMessages;
	}
	
	public void incrementLocalClock()
	{
		System.out.println("local Clock size: " + this.localVectorClock.size());
		localVectorClock.set(this.index, localVectorClock.get(this.index) + 1);
	}

	@Override
	public void send(int receiverIndex, Message message, int delayTime) throws RemoteException {
		
		this.incrementLocalClock();
		
		System.out.println("Contents of latestSentMessages at process " + index + " before sending message " + message.getId() + ": " + this.latestSentMessages);
		
		
		System.out.println(this.processURL + " (index " + this.index + ") is sending message " + message.toString());
		message.setSendersLocalVectorClock(this.localVectorClock);
		message.setSendersLatestSentMessages(this.latestSentMessages);
		MessageDelayer delayedMessage = new MessageDelayer(this.processes.get(receiverIndex), message, delayTime);
		new Thread(delayedMessage).start();
		this.latestSentMessages.put(receiverIndex, new ArrayList<Integer>(this.localVectorClock));
	}

	@Override
	public void receive(Message message) throws RemoteException {
		System.out.println(this.processURL + " got message " + message.toString());
		if (this.canBeDelivered(message))
		{
			System.out.println("Process " + this.index + " accepted message: " + message.toString());
            this.acceptMessage(message);
            
        } else {
            this.pendingMessages.add(message);
            System.out.println("However, "+message.toString()+" was added to the pending messages...");
        }
	}
	
	public void attemptPendingMessageDelivery()
	{
        for (Message currentMessage : this.pendingMessages)
        {
        	System.out.println("CHECK NOW: Can " + currentMessage.toString() + " be accepted now?");
            if (this.canBeDelivered(currentMessage))
            {
            	this.pendingMessages.remove(currentMessage);
            	System.out.println("Removed this message: " + currentMessage.toString());
                this.acceptMessage(currentMessage);
                return;
            }
            System.out.println("Nope...");
        }
        if(this.pendingMessages.size() > 0)
        {
        	System.out.println("There are " + this.pendingMessages.size() + " pending messages in " + this.processURL + ", but none can be delivered right now");
        }
    }
	
	private ArrayList<Integer> mergeLocalVectorClocks(List<Integer> localVectorClock1, List<Integer> localVectorClock2)
	{
        ArrayList<Integer> resultingLocalClock = new ArrayList<Integer>();
        for (int i = 0; i < this.processes.size(); i++)
        {
        	int value1 = 0;
        	int value2 = 0;
        	if(localVectorClock1.size() > i)
        	{
        		value1 = localVectorClock1.get(i);
        	}
        	if(localVectorClock2 != null && localVectorClock2.size() > i)
        	{
        		value2 = localVectorClock2.get(i);
        	}
        	resultingLocalClock.add(Math.max(value1, value2));
        }
        return resultingLocalClock;
    }
	
	public void acceptMessage(Message message)
	{
		// Message actually gets delivered
		this.incrementLocalClock();
		
		this.receivedMessages.add(message);
		
		this.localVectorClock = mergeLocalVectorClocks(this.localVectorClock, message.getSendersLocalVectorClock());
		
		Iterator it = message.getSendersLatestSentMessages().entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Integer, ArrayList<Integer>> pair = (Map.Entry<Integer, ArrayList<Integer>>)it.next();
	        //System.out.println(pair.getKey() + " = " + pair.getValue());
	        ArrayList<Integer> latestMessageLocalVectorClock = pair.getValue();
	        int receiverId = pair.getKey();
	        
	        ArrayList<Integer> storeLocalVectorClock;
	        if(latestMessageLocalVectorClock == null)
	        {
	        	storeLocalVectorClock = latestMessageLocalVectorClock;
	        }
	        else
	        {
	        	storeLocalVectorClock = this.mergeLocalVectorClocks(latestMessageLocalVectorClock, this.latestSentMessages.get(receiverId));
	        }
	        this.latestSentMessages.put(receiverId, storeLocalVectorClock);
	    }
	    
	    System.out.println("-- Message " + message.toString() + " was accepted --");
		
		// Now that the a new message has been received, pending messages might be able to be received now
		attemptPendingMessageDelivery();
	}
	
	public boolean canBeDelivered(Message message)
	{
		if (!message.getSendersLatestSentMessages().containsKey(index))
		{
			// This is supposed to be the first message to this process from that sender, so accept it
	        return true;
	    }
		
		List<Integer> localVectorClockClone = new ArrayList<Integer>(this.localVectorClock);
		localVectorClockClone.set(this.index, localVectorClockClone.get(this.index) + 1);
		List<Integer> messageClock = message.getSendersLatestSentMessages().get(this.index);
		for (int i = 0; i < processes.size(); i++) {
            if (localVectorClockClone.get(i) < messageClock.get(i))
            {
            	// The local clock isn't updated enough yet to receive this message
            	System.err.println("Cannot accept message because localVectorClockClone.get("+i+") = " + localVectorClockClone.get(i) + " which is smaller than messageClock.get(" + i+") = " + messageClock.get(i));
                return false;
            }
        }
		return true;
	}

	@Override
	public int getIndex() throws RemoteException {
		// TODO Auto-generated method stub
		return this.index;
	}

	public ArrayList<DeProp_RMI> getProcesses() {
		return processes;
	}

	public void setProcesses(ArrayList<String> processURLs) {
		this.processes = new ArrayList<DeProp_RMI>();
		for(int i = 0; i < processURLs.size(); i++)
		{
			if(processURLs.get(i).equals(this.processURL))
			{
				this.index = i;
				System.out.println("Process " + processURL + " found that its own ID is " + this.index);
				this.processes.add(this);
			}
			else
			{
				try {
					this.processes.add((DeProp_RMI) Naming.lookup(processURLs.get(i)));
				} catch (MalformedURLException | RemoteException | NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		this.reset();
	}

	public void sendSomethingToEveryone() throws RemoteException {
		for(int i = 0; i < processes.size(); i++)
		{
	        if(i != this.index)
	        {
	        	this.send(i, new Message(this.index, i, 1), 1000);
	        }
	        //it.remove(); // avoids a ConcurrentModificationException
	    }
	}

	@Override
	public int getRandomInt() throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("Somebody requested my random int of " + this.myRand);
		this.myRand++;
		return this.myRand;
	}
 
}
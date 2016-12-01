/*
 * Copyright (c) 2004, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 * -Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the
 *  distribution.
 *
 * Neither the name of Oracle nor the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that Software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 */


import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.io.Serializable;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class DeProp implements DeProp_RMI, Runnable, Serializable {
	
	private HashMap<String, DeProp_RMI> processes;
	private String processURL;

    public DeProp(int totalProcesses, String processURL) {
    	this.processURL = processURL;
    }

    public String sayHello() {
        return "Hello, pizza!";
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Thread of process " + this.processURL + " started");
		
		try {
			this.sendSomethingToEveryone();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void send(String url, Message message) throws RemoteException {
		try {
    		System.out.println(this.processURL + " is sending message " + message.toString());
            processes.get(url).receive(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
	}

	@Override
	public void receive(Message message) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println(this.processURL + " got message " + message.toString());
	}

	@Override
	public int getIndex() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public LinkedList<Message> getMessages() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public HashMap<String, DeProp_RMI> getProcesses() {
		return processes;
	}

	public void setProcesses(HashMap<String, DeProp_RMI> processes) {
		this.processes = processes;
		Iterator it = processes.entrySet().iterator();
	    while (it.hasNext()) {
	    	Map.Entry pair = (Map.Entry)it.next();
	        //System.out.println("Process " + this.processURL + " knows there is a process with URL " + pair.getKey() + ":");
	        //System.out.println(pair.getValue());
	        //it.remove(); // avoids a ConcurrentModificationException
	    }
	}

	public void sendSomethingToEveryone() throws RemoteException {
		Iterator it = processes.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        if(pair.getKey() != this.processURL)
	        {
	        	this.send((String) pair.getKey(), new Message(this.processURL, (String) pair.getKey(), 1));
	        }
	        //it.remove(); // avoids a ConcurrentModificationException
	    }
	}
 
}
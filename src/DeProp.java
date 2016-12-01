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
	
	private ArrayList<DeProp_RMI> processes;
	private String processURL;
	
	private int index;

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
	public void send(int receiverIndex, Message message, int delayTime) throws RemoteException {
		System.out.println(this.processURL + " (index " + this.index + ") is sending message " + message.toString());
		MessageDelayer delayedMessage = new MessageDelayer(processes.get(receiverIndex), message, delayTime);
		new Thread(delayedMessage).start();
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

	public ArrayList<DeProp_RMI> getProcesses() {
		return processes;
	}

	public void setProcesses(ArrayList<DeProp_RMI> processes) {
		this.processes = processes;
		for(int i = 0; i < processes.size(); i++)
		{
			if(processes.get(i) == this)
			{
				this.index = i;
				System.out.println("Process " + processURL + " found that its own ID is " + this.index);
			}
		}
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
 
}
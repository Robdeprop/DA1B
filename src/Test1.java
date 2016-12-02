

import static org.junit.Assert.*;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.junit.Test;

public class Test1 {
	
	private Setup setup;

	@Before
    public void init(){
        setup = new Setup();
    }
    
    @Test
    /**
     * P1 sends m1 to P2
     * P1 sends m2 to P2 but m2 arrives before m1
     */
    public void testSystem(){
    	System.err.println("Start Test: testSystem");
        DeProp_RMI process1 = setup.getProcesses().get(0);
        DeProp_RMI process2 = setup.getProcesses().get(1);
        try{
            Message message1 = new Message(process1.getIndex(),process2.getIndex(), 1);
            process1.send(process2.getIndex(), message1, 10);

            Message message2 = new Message(process1.getIndex(), process2.getIndex(), 2);
            process1.send(process2.getIndex(), message2, 0);

            // Sleep at least the sum of all delays to be sure all messages have arrived.
            Thread.sleep(20);

            ArrayList<Message> messages = process2.getReceivedMessages();
            Assert.assertEquals(2, messages.size());
            Assert.assertEquals(message1.getId(), messages.get(0).getId());
            Assert.assertEquals(message2.getId(), messages.get(1).getId());

        } catch (RemoteException e){
            e.printStackTrace();
            Assert.fail();
        } catch (InterruptedException e){
            e.printStackTrace();
            Assert.fail();
        }

    }
    
    @Test
    /**
     * P1 sends m1 to P2 
     * P2 sends m2 to P3 
     * P1 sends m3 to P3
     */
    public void testcase1(){
    	System.err.println("Start Test: testcase1");
        DeProp_RMI process1 = setup.getProcesses().get(0);
        DeProp_RMI process2 = setup.getProcesses().get(1);
        DeProp_RMI process3 = setup.getProcesses().get(2);

        try{
            Message message1 = new Message(process1.getIndex(),process2.getIndex(), 1);
            process1.send(process2.getIndex(),message1, 0);

            Message message2  = new Message(process2.getIndex(), process3.getIndex(), 2);
            process2.send(process3.getIndex(), message2, 10);
            
            Message message3  = new Message(process2.getIndex(), process3.getIndex(), 3);
            process1.send(process3.getIndex(),message3, 20);
            
            // Sleep at least the sum of all delays to be sure all messages have arrived.
            Thread.sleep(50);

            ArrayList<Message> messagesProcess2 = process2.getReceivedMessages();            
            Assert.assertEquals(1, messagesProcess2.size());
            Assert.assertEquals(message1.getId(), messagesProcess2.get(0).getId());
            
            ArrayList<Message> messagesProcess3 = process3.getReceivedMessages();
            Assert.assertEquals(2, messagesProcess3.size());
            Assert.assertEquals(message2.getId(), messagesProcess3.get(0).getId());
            Assert.assertEquals(message3.getId(), messagesProcess3.get(1).getId());

        } catch (RemoteException e){
            e.printStackTrace();
            Assert.fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
		}
    }
    
    @Test
    /**
     * Figure 3.5 of lecture notes.
     */
    public void testcase2(){
    	System.err.println("Start Test: testcase2");
        DeProp_RMI process1 = setup.getProcesses().get(0);
        DeProp_RMI process2 = setup.getProcesses().get(1);
        DeProp_RMI process3 = setup.getProcesses().get(2);

        try{
            Message message1 = new Message(process1.getIndex(), process3.getIndex(), 1);
            process1.send(process3.getIndex(), message1, 30);

            Message message2 = new Message(process1.getIndex(), process2.getIndex(), 2);
            process1.send(process2.getIndex(), message2, 10);
            
            Thread.sleep(20);
            
            Message message3 = new Message(process2.getIndex(), process3.getIndex(), 3);
            process2.send(process3.getIndex(), message3, 0);
            
            // Sleep at least the sum of all delays to be sure all messages have arrived.
            Thread.sleep(1500);

            ArrayList<Message> messagesProcess2 = process2.getReceivedMessages();
            Assert.assertEquals(1, messagesProcess2.size());
            Assert.assertEquals(message2.getId(), messagesProcess2.get(0).getId());
            
            ArrayList<Message> messagesProcess3 = process3.getReceivedMessages();
            Assert.assertEquals(2, messagesProcess3.size());
            
            for (int i=0; i < messagesProcess3.size(); i++)
            {
            	System.out.println("messagesProcess3.get(" + i + ").getId(): " + messagesProcess3.get(i).getId());
            }
            
            Assert.assertEquals(message1.getId(), messagesProcess3.get(0).getId());
            Assert.assertEquals(message3.getId(), messagesProcess3.get(1).getId());

        } catch (RemoteException e){
            e.printStackTrace();
            Assert.fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
		}
    }
    
    @Test
    /**
     * P1 sends m1 to P2
	 * P2 sends m2 to P3
	 * P1 sends m3 to P3 but m3 arrives before m2
     */
    public void testcase3(){
    	System.err.println("Start Test: testcase3");
        DeProp_RMI process1 = setup.getProcesses().get(0);
        DeProp_RMI process2 = setup.getProcesses().get(1);
        DeProp_RMI process3 = setup.getProcesses().get(2);

        try{
            Message message1 = new Message(process1.getIndex(),process2.getIndex(), 1);
            process1.send(process2.getIndex(), message1, 0);

            Message message2 = new Message(process2.getIndex(), process3.getIndex(), 2);
            process2.send(process3.getIndex(), message2, 10);
            
            Message message3 = new Message(process1.getIndex(), process3.getIndex(), 3);
            process1.send(process3.getIndex(), message3, 20);
            
            // Sleep atleast the sum of all delays to be sure all messages have arrived.
            Thread.sleep(150);

            ArrayList<Message> messagesProcess2 = process2.getReceivedMessages();

            Assert.assertEquals(1, messagesProcess2.size());
            Assert.assertEquals(message1.getId(), messagesProcess2.get(0).getId());
            
            ArrayList<Message> messagesProcess3 = process3.getReceivedMessages();

            Assert.assertEquals(2, messagesProcess3.size());
            Assert.assertEquals(message2.getId(), messagesProcess3.get(0).getId());
            Assert.assertEquals(message3.getId(), messagesProcess3.get(1).getId());

        } catch (RemoteException e){
            e.printStackTrace();
            Assert.fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
		}
    }
    
    @Test
    /**
     * P1 sends m1 to P2
	 * P1 sends m2 to P2 but m2 arrives before m1
     */
    public void testcase4(){
    	System.err.println("Start Test: testcase4");
        DeProp_RMI process1 = setup.getProcesses().get(0);
        DeProp_RMI process2 = setup.getProcesses().get(1);

        try{
        	// This message, m1, will arrive after, m2, thus late.
            Message message1 = new Message(process1.getIndex(),process2.getIndex(), 1);
            process1.send(process2.getIndex(), message1, 20);

            Message message2 = new Message(process1.getIndex(), process2.getIndex(), 2);
            process1.send(process2.getIndex(), message2, 0);
            
            // Sleep at least the sum of all delays to be sure all messages have arrived.
            Thread.sleep(150);

            ArrayList<Message> messagesProcess2 = process2.getReceivedMessages();

            Assert.assertEquals(2, messagesProcess2.size());
            Assert.assertEquals(message1.getId(), messagesProcess2.get(0).getId());
            Assert.assertEquals(message2.getId(), messagesProcess2.get(1).getId());
            
        } catch (RemoteException e){
            e.printStackTrace();
            Assert.fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
		}
    }

    @Test
    /**
     * P1 sends m1 to P2
	 * P1 sends m2 to P2 but m2 arrives before m1 and m2 depends on m1
	 * P2 sends m3 to P3
     */
    public void testcase5(){
    	System.err.println("Start Test: testcase5");
        DeProp_RMI process1 = setup.getProcesses().get(0);
        DeProp_RMI process2 = setup.getProcesses().get(1);
        DeProp_RMI process3 = setup.getProcesses().get(2);

        try{
        	// This message, m1, will arrive after, m2, thus late.
            Message message1 = new Message(process1.getIndex(),process2.getIndex(), 1);
            process1.send(process2.getIndex(), message1, 20);

            Message message2 = new Message(process1.getIndex(), process2.getIndex(), 2);
            process1.send(process2.getIndex(), message2, 0);
            
            Message message3 = new Message(process2.getIndex(), process3.getIndex(), 3);
            process2.send(process3.getIndex(), message3, 10);
            
            // Sleep at least the sum of all delays to be sure all messages have arrived.
            Thread.sleep(150);

            ArrayList<Message> messagesProcess2 = process2.getReceivedMessages();

            Assert.assertEquals(2, messagesProcess2.size());
            Assert.assertEquals(message1.getId(), messagesProcess2.get(0).getId());
            Assert.assertEquals(message2.getId(), messagesProcess2.get(1).getId());
            
            ArrayList<Message> messagesProcess3 = process3.getReceivedMessages();

            Assert.assertEquals(1, messagesProcess3.size());
            Assert.assertEquals(message3.getId(), messagesProcess3.get(0).getId());

        } catch (RemoteException e){
            e.printStackTrace();
            Assert.fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
		}
    }
    
    @Test
    /**
	 * See Figure 5 in "A New Algorithm to Implement Causal Ordering."
     */
    public void testcase6(){
    	System.err.println("Start Test: testcase6");
        DeProp_RMI process1 = setup.getProcesses().get(0);
        DeProp_RMI process2 = setup.getProcesses().get(1);
        DeProp_RMI process3 = setup.getProcesses().get(2);
        DeProp_RMI process4 = setup.getProcesses().get(3);

        try{
        	// Messages of S1 (= process1)
            Message message1 = new Message(process1.getIndex(),process3.getIndex(), 1);
            process1.send(process3.getIndex(), message1, 60);

            Message message2 = new Message(process1.getIndex(), process2.getIndex(), 2);
            process1.send(process2.getIndex(), message2, 10);
            
            Message message3 = new Message(process1.getIndex(), process4.getIndex(), 3);
            process1.send(process4.getIndex(), message3, 40);
            
            Thread.sleep(20);
            
            // Messages of S2 (= process2)
            Message message4 = new Message(process2.getIndex(),process3.getIndex(), 4);
            process2.send(process3.getIndex(), message4, 0);

            Message message5 = new Message(process2.getIndex(), process4.getIndex(), 5);
            process2.send(process4.getIndex(), message5, 0);
            
            Thread.sleep(20);
            
            // Messages of S4 (= process4)
            Message message6 = new Message(process4.getIndex(), process3.getIndex(), 6);
            process4.send(process3.getIndex(), message6, 0);
            
            // Sleep at least the sum of all delays to be sure all messages have arrived.
            Thread.sleep(300);

            ArrayList<Message> messagesProcess2 = process2.getReceivedMessages();
            Assert.assertEquals(1, messagesProcess2.size());
            Assert.assertEquals(message2.getId(), messagesProcess2.get(0).getId());
            
            ArrayList<Message> messagesProcess3 = process3.getReceivedMessages();
            Assert.assertEquals(3, messagesProcess3.size());
            Assert.assertEquals(message1.getId(), messagesProcess3.get(0).getId());
            Assert.assertTrue(message4.getId() == messagesProcess3.get(1).getId());
            Assert.assertEquals(message6.getId(), messagesProcess3.get(2).getId());
                        
            ArrayList<Message> messagesProcess4 = process4.getReceivedMessages();
            Assert.assertEquals(2, messagesProcess4.size());
            Assert.assertEquals(message5.getId(), messagesProcess4.get(0).getId());
            Assert.assertEquals(message3.getId(), messagesProcess4.get(1).getId());
            
        } catch (RemoteException e){
            e.printStackTrace();
            Assert.fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
		}
    }
}

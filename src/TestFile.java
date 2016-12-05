

import java.rmi.RemoteException;
import java.util.ArrayList;

public class TestFile {
	
	private Setup setup;
	private int testsPassed = 0;
	private int testsDone = 0;
	private int currentErrors = 0;
	
	public static void main(String args[]) {
		new TestFile();
	}
	
	public TestFile() {
		testSystem();
		testcase1();
		testcase2();
		testcase3();
		testcase4();
		testcase5();
		testcase6();
		
		System.out.println(testsPassed + "/" + testsDone + " tests passed.");
	}
	
	public void assertEquals(int int1, int int2)
	{
		if(int1 == int2)
		{
			return;
		}
		currentErrors++;
		System.err.println("ERROR: " + int1 + " was expected, but got " + int2);
	}
	
	public void assertTrue(Boolean bool)
	{
		if(bool)
		{
			return;
		}
		currentErrors++;
		System.err.println("ERROR: true was expected, but got " + bool);
	}
	
	public void testCompleted()
	{
		testsDone++;
		if(currentErrors == 0)
		{
			testsPassed++;
			System.out.println("Test succesfully passed!");
		}
		else
		{
			System.out.println("Test failed with " + currentErrors + " errors.");
		}
		currentErrors = 0;
		
		System.out.println("---");
	}
	
	public void startTest(String testName)
	{
		init();
		System.out.println("Starting test " +testName + ":");
	}
	
	public void fail()
	{
		System.err.println("EXECUTION FAILED!");
	}

	
    public void init(){
        setup = new Setup(true);
    }
    
    
    /**
     * P1 sends m1 to P2
     * P1 sends m2 to P2 but m2 arrives before m1
     */
    public void testSystem(){
    	startTest("Start Test: testSystem");
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
            assertEquals(2, messages.size());
            assertEquals(message1.getId(), messages.get(0).getId());
            assertEquals(message2.getId(), messages.get(1).getId());

        } catch (RemoteException e){
            e.printStackTrace();
            fail();
        } catch (InterruptedException e){
            e.printStackTrace();
            fail();
        }
        testCompleted();
    }
    
    
    /**
     * P1 sends m1 to P2 
     * P2 sends m2 to P3 
     * P1 sends m3 to P3
     */
    public void testcase1(){
    	startTest("Start Test: testcase1");
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
            assertEquals(1, messagesProcess2.size());
            assertEquals(message1.getId(), messagesProcess2.get(0).getId());
            
            ArrayList<Message> messagesProcess3 = process3.getReceivedMessages();
            assertEquals(2, messagesProcess3.size());
            assertEquals(message2.getId(), messagesProcess3.get(0).getId());
            assertEquals(message3.getId(), messagesProcess3.get(1).getId());

        } catch (RemoteException e){
            e.printStackTrace();
            fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
		}
        testCompleted();
    }
    
    
    /**
     * Figure 3.5 of lecture notes.
     */
    public void testcase2(){
    	startTest("Start Test: testcase2");
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
            assertEquals(1, messagesProcess2.size());
            assertEquals(message2.getId(), messagesProcess2.get(0).getId());
            
            ArrayList<Message> messagesProcess3 = process3.getReceivedMessages();
            assertEquals(2, messagesProcess3.size());
            
            for (int i=0; i < messagesProcess3.size(); i++)
            {
            	System.out.println("messagesProcess3.get(" + i + ").getId(): " + messagesProcess3.get(i).getId());
            }
            
            assertEquals(message1.getId(), messagesProcess3.get(0).getId());
            assertEquals(message3.getId(), messagesProcess3.get(1).getId());

        } catch (RemoteException e){
            e.printStackTrace();
            fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
		}
        testCompleted();
    }
    
    
    /**
     * P1 sends m1 to P2
	 * P2 sends m2 to P3
	 * P1 sends m3 to P3 but m3 arrives before m2
     */
    public void testcase3(){
    	startTest("Start Test: testcase3");
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

            assertEquals(1, messagesProcess2.size());
            assertEquals(message1.getId(), messagesProcess2.get(0).getId());
            
            ArrayList<Message> messagesProcess3 = process3.getReceivedMessages();

            assertEquals(2, messagesProcess3.size());
            assertEquals(message2.getId(), messagesProcess3.get(0).getId());
            assertEquals(message3.getId(), messagesProcess3.get(1).getId());

        } catch (RemoteException e){
            e.printStackTrace();
            fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
		}
        testCompleted();
    }
    
    
    /**
     * P1 sends m1 to P2
	 * P1 sends m2 to P2 but m2 arrives before m1
     */
    public void testcase4(){
    	startTest("Start Test: testcase4");
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

            assertEquals(2, messagesProcess2.size());
            assertEquals(message1.getId(), messagesProcess2.get(0).getId());
            assertEquals(message2.getId(), messagesProcess2.get(1).getId());
            
        } catch (RemoteException e){
            e.printStackTrace();
            fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
		}
        testCompleted();
    }

    
    /**
     * P1 sends m1 to P2
	 * P1 sends m2 to P2 but m2 arrives before m1 and m2 depends on m1
	 * P2 sends m3 to P3
     */
    public void testcase5(){
    	startTest("Start Test: testcase5");
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

            assertEquals(2, messagesProcess2.size());
            assertEquals(message1.getId(), messagesProcess2.get(0).getId());
            assertEquals(message2.getId(), messagesProcess2.get(1).getId());
            
            ArrayList<Message> messagesProcess3 = process3.getReceivedMessages();

            assertEquals(1, messagesProcess3.size());
            assertEquals(message3.getId(), messagesProcess3.get(0).getId());

        } catch (RemoteException e){
            e.printStackTrace();
            fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
		}
        testCompleted();
    }
    
    
    /**
	 * See Figure 5 in "A New Algorithm to Implement Causal Ordering."
     */
    public void testcase6(){
    	startTest("Start Test: testcase6");
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
            assertEquals(1, messagesProcess2.size());
            assertEquals(message2.getId(), messagesProcess2.get(0).getId());
            
            ArrayList<Message> messagesProcess3 = process3.getReceivedMessages();
            assertEquals(3, messagesProcess3.size());
            assertEquals(message1.getId(), messagesProcess3.get(0).getId());
            assertTrue(message4.getId() == messagesProcess3.get(1).getId());
            assertEquals(message6.getId(), messagesProcess3.get(2).getId());
                        
            ArrayList<Message> messagesProcess4 = process4.getReceivedMessages();
            assertEquals(2, messagesProcess4.size());
            assertEquals(message5.getId(), messagesProcess4.get(0).getId());
            assertEquals(message3.getId(), messagesProcess4.get(1).getId());
            
            
        } catch (RemoteException e){
            e.printStackTrace();
            fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
		}
        testCompleted();
    }
}

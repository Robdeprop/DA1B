

import java.util.List;

public class Message {
	private String senderURL;
	private String receiverURL;
	private List<Integer> clocks;
	private long arrivalTime;
	private int id;
	private int delay;
	
	public Message(String senderURL, String receiverURL, int messageId)
	{
		this.senderURL = senderURL;
		this.receiverURL = receiverURL;
		this.setId(messageId);
	}

	public List<Integer> getClocks() {
		return clocks;
	}

	public void setClocks(List<Integer> clocks) {
		this.clocks = clocks;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String toString()
	{
		return "Message{ sender=\"" + this.senderURL + "\", receiver=\"" + this.receiverURL + "\", id=\"" + this.id + "\"}";
	}
}

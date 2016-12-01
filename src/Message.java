

import java.util.List;

public class Message {
	private int senderIndex;
	private int receiverIndex;
	private List<Integer> clocks;
	private long arrivalTime;
	private int id;
	private int delay;
	
	public Message(int senderIndex, int receiverIndex, int messageId)
	{
		this.senderIndex = senderIndex;
		this.receiverIndex = receiverIndex;
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
		return "Message{ sender=\"" + this.senderIndex + "\", receiver=\"" + this.receiverIndex + "\", id=\"" + this.id + "\"}";
	}
}

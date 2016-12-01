

import java.util.List;

public class Message {
	private int sender;
	private int receiver;
	private List<Integer> clocks;
	private long arrivalTime;
	private int id;
	
	public Message(int sender, int receiver, int messageId)
	{
		this.sender = sender;
		this.receiver = receiver;
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
}

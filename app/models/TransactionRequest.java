package models;

import java.util.Date;

import javax.inject.Inject;

public class TransactionRequest {
	
	public String id;
	public Bank bank;
	public int amount;
	public String sender_id;
	public String receiver_id;
	public String category;
	public Date timestamp;
	public boolean conditionsMet;
	public String rejectedRule;
	
	@Inject
	public TransactionRequest(String senderID, String receiverID, String bankID, String category, 
			int amount, String requestID) {
		this.sender_id = senderID;
		this.receiver_id = receiverID;
		this.category = category;
		this.amount = amount;
		this.id = requestID;
		this.timestamp = new Date();
		this.conditionsMet = true;
		this.rejectedRule = null;
	}
}

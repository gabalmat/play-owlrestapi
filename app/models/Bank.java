package models;

public class Bank {
	
	public String id;
	public String nationality;
	public boolean isBlacklisted;
	public int totalAmount;
	public int averageAmount;
	public int numTrans;
	public int numTrustedTrans;
	public int numRejections;
	public int numRejectionsInRow;
	public boolean participateNontrusted;
	
	public Bank(String id, String nationality) {
		this.id = id;
		this.nationality = nationality;
		this.isBlacklisted = false;
		this.totalAmount = 0;
		this.numTrans = 0;
		this.numTrustedTrans = 0;
		this.averageAmount = 0;
		this.participateNontrusted = true;
		this.numRejections = 0;
		this.numRejectionsInRow = 0;
	}
}

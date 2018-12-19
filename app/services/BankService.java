package services;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import models.Bank;

@Singleton
public class BankService {
	
	private Map<String, Bank> bankList = new HashMap<>();
	
	public Bank getBank(String bankID) {
		if (bankList.containsKey(bankID)) {
			return bankList.get(bankID);
		}
		
		return null;
	}
	
	public void addBank(Bank bank) {
		bankList.put(bank.id, bank);
	}
	
	public void clearBankList() {
//		for (String bankId : bankList.keySet()) {
//			Bank bank = bankList.get(bankId);
//			bank = null;
//		}
		bankList.clear();
		int test = bankList.size();
	}

}

package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;

import models.Bank;
import models.TransactionRequest;
import play.libs.Json;
import play.mvc.*;
import plugins.Drools;
import services.BankService;
import services.LoggerService;
import utilities.Ontology;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {
	
	@Inject Ontology ontology;
	@Inject Drools drools;
	@Inject BankService bankService;
	@Inject LoggerService loggerService;

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
    	
    	return ok("rules are running... check the console");
    	//views.html.index.render()
    }
    
    public Result handleTransactionRequest(String senderId, String receiverId, String bankId, 
    		String category, int amount, String transId) 
    {	
    	ObjectNode response = Json.newObject();
    	Bank currentBank;
    	
    	TransactionRequest tr = new TransactionRequest(senderId, receiverId, bankId, 
    			category, amount, transId);
    	
    	// get the Bank object and add it to the transaction request
    	try {
    		currentBank = bankService.getBank(bankId);
    		tr.bank = currentBank;
    	} catch (NullPointerException e) {
    		response.put("status", "failure");
    		response.put("reason", "Bank does not exist yet! Please create the Bank and try again.");
    		
    		return ok(response.toString());
    	}
    	
    	drools.kieSession.insert(tr);
    	drools.kieSession.insert(tr.bank);
    	drools.kieSession.insert(ontology);
    	drools.kieSession.fireAllRules();
    	
    	// If all the conditions have been met, add the transaction
    	if (tr.conditionsMet) {
    		// log the accepted transaction request
    		loggerService.acceptedLogger.log(Level.INFO, "Transaction Request accepted - transaction request ID: " + tr.id + ", bank ID: " + tr.bank.id 
    				+ ", sender ID: " + tr.sender_id + ", receiver ID: " + tr.receiver_id + ", amount: " + tr.amount 
    				+ ", category ID: " + tr.category + ", timestamp: " + tr.timestamp);
    		
    		// add the transaction
    		if (ontology.addTransaction(senderId, receiverId, transId)) {
    			// Transaction added successfully
    			response.put("status", "success");
    			
    			// Update the transaction statistics for the bank
    			tr.bank.numTrans += 1;
    			tr.bank.totalAmount += amount;
    			tr.bank.averageAmount = tr.bank.totalAmount / tr.bank.numTrans;
    			
    			// Reset the numRejectionsInRow value for the bank
    			tr.bank.numRejectionsInRow = 0;
    			
    			if (Ontology.isTrustedBoolean(senderId) || Ontology.isTrustedBoolean(receiverId)) {
    				tr.bank.numTrustedTrans += 1;
    			}
    		} else {
    			// Unable to add Transaction
    			response.put("status", "failure");
    			response.put("reason", "Transaction Request was accepted, but there was an error adding the Transaction.");
    		}
    	} else {
    		// Transaction Request was rejected
    		tr.bank.numRejections += 1;
    		tr.bank.numRejectionsInRow +=1;
    		response.put("status", "failure");
    		response.put("reason", tr.rejectedRule);
    		
    		// log the rejected transaction request
    		loggerService.rejectedLogger.log(Level.INFO, "Transaction Request rejected due to " + tr.rejectedRule + " - transaction request ID: " 
    				+ tr.id + ", bank ID: " + tr.bank.id + ", sender ID: " + tr.sender_id + ", receiver ID: " + tr.receiver_id 
    				+ ", amount: " + tr.amount + ", category ID: " + tr.category + ", timestamp: " + tr.timestamp);
    	}
    	
    	return ok(response.toString());
    }
    
    public Result addBank(String nationality, String bankId) {
    	ObjectNode response = Json.newObject();
    	
    	Bank bank = new Bank(bankId, nationality);
    	bankService.addBank(bank);
    	
    	response.put("status", "success");
    	return ok(response.toString());
    }
    
    public Result addMerchant(String id) {
    	String result = ontology.addMerchant(id);
    	
    	ObjectNode response = Json.newObject();
    	response.put("status", result);
    	
    	return ok(response.toString());
    }
    
    public Result addConsumer(String id) {
    	String result = ontology.addConsumer(id);
    	
    	ObjectNode response= Json.newObject();
    	response.put("status", result);
    	
    	return ok(response.toString());
    }
    
//    public Result addTransaction(String senderId, String receiverId, String transactionId) {
//    	String result = ontology.addTransaction(senderId, receiverId, transactionId);
//    	
//    	ObjectNode response = Json.newObject();
//    	response.put("status", result);
//    	
//    	return ok(response.toString());
//    }
    
    public Result isPersonal(String transactionId) {
    	return ok(ontology.isClass(transactionId, "Personal"));
    }
    
    public Result isCommercial(String transactionId) {  	
    	return ok(ontology.isClass(transactionId, "Commercial"));
    }
    
    public Result isPurchase(String transactionId) {
    	return ok(ontology.isClass(transactionId, "Purchase"));
    }
    
    public Result isRefund(String transactionId) {
    	return ok(ontology.isClass(transactionId, "Refund"));
    }
    
    public Result isTrusted(String merchantId) {
    	return ok(Ontology.isTrusted(merchantId));
    }
    
    public Result reset() {
    	// reset the ontology;
    	String result = ontology.loadOntology();
    	
    	// delete all banks
    	bankService.clearBankList();
    	
    	// delete the log files
    	for (Handler handler : loggerService.acceptedLogger.getHandlers()) {
    		handler.close();
    		loggerService.acceptedLogger.removeHandler(handler);
    		
    		File f = new File("accepted.log");
    		f.delete();
    	}
    	
    	for (Handler handler : loggerService.rejectedLogger.getHandlers()) {
    		handler.close();
    		loggerService.rejectedLogger.removeHandler(handler);
    		
    		File f = new File("rejected.log");
    		f.delete();
    	}
    	
    	ObjectNode response = Json.newObject();
    	response.put("result", result);
    	
    	return ok(response.toString());
    }
    
    public Result isBlacklisted(String bankId) {
    	Bank bank = null;
    	ObjectNode response = Json.newObject();
    	
    	if (bankService.getBank(bankId) == null) {
    		response.put("status", "failure");
    		response.put("reason", "not a bank");
    		
    		return ok(response.toString());
    	}
    	
    	bank = bankService.getBank(bankId);
    	response.put("status", "success");
    	response.put("result", bank.isBlacklisted);
    	
    	return ok(response.toString());
    }
    
    public Result bankRejections(String bankId) {
    	Bank bank = null;
    	ObjectNode response = Json.newObject();
    	
    	if (bankService.getBank(bankId) == null) {
    		response.put("status", "failure");
    		response.put("reason", "not a bank");
    		
    		return ok(response.toString());
    	}
    	
    	bank = bankService.getBank(bankId);
    	response.put("status", "success");
    	response.put("rejections", bank.numRejections);
    	
    	return ok(response.toString());
    }
    
    public Result rejectionLog() throws IOException {
    	ObjectNode response = Json.newObject();
    	String logContents;
    	
    	BufferedReader br = null;
    	try {
			br = new BufferedReader(new FileReader("rejected.log"));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			
			logContents = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
			response.put("error", "There was an error, check the console for stack trace");
			return ok(response.toString());
		} finally {
			br.close();
		}
    	
    	response.put("", logContents);
    	
    	return ok(response.toString());
    }
    
    public Result acceptanceLog() throws IOException {
    	ObjectNode response = Json.newObject();
    	String logContents;
    	
    	BufferedReader br = null;
    	try {
			br = new BufferedReader(new FileReader("accepted.log"));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			
			logContents = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
			response.put("error", "There was an error, check the console for stack trace");
			return ok(response.toString());
		} finally {
			br.close();
		}
    	
    	response.put("", logContents);
    	
    	return ok(response.toString());
    }

}

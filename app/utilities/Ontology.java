package utilities;

import openllet.jena.PelletReasonerFactory;
import play.libs.Json;

import java.io.InputStream;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.jena.shared.JenaException;

public class Ontology {
	
	public static OntModel ontReasoned;
	private static final String source_file = "owl/transaction.owl";
	private static final String source_url = "http://www.semanticweb.org/gabalmat/ontologies/transaction.owl";
	private static final String NS = source_url + "#";;
	
	public Ontology() {
		loadOntology();
	}
	
	public String addMerchant(String id) {
		
		// Get the classes we need
		OntClass merchantOntClass = ontReasoned.getOntClass(NS + "Merchant");
		
		// Create the individuals we need
		Individual merchant = ontReasoned.createIndividual(NS + id, merchantOntClass);
		
		if (merchant == null) {
			return "error: could not create merchant";
		}
		
		return "success";
	}
	
	public String addConsumer(String id) {
		
		// Get the classes we need
		OntClass consumerOntClass = ontReasoned.getOntClass(NS + "Consumer");
		
		// Create the individuals we need
		Individual consumer = ontReasoned.createIndividual(NS + id, consumerOntClass);
		
		if (consumer == null) {
			return "error: could not create consumer";
		}
		
		return "success";
	}
	
	public boolean addTransaction(String senderId, String receiverId, String transactionId) {
		
		// Get the Sender
		Individual sender = ontReasoned.getIndividual(NS + senderId);
		
		// Get the receiver
		Individual receiver = ontReasoned.getIndividual(NS + receiverId);
		
		// Get the classes we need
		OntClass transOntClass = ontReasoned.getOntClass(NS + "Transaction");
		
		// Get the properties we need
		OntProperty hasSender = ontReasoned.getObjectProperty(NS + "has_sender");
		OntProperty hasReciever = ontReasoned.getObjectProperty(NS + "has_receiver");
		OntProperty participatesIn = ontReasoned.getObjectProperty(NS + "participates_in");
		
		// Create the individual
		Individual transaction = ontReasoned.createIndividual(NS + transactionId, transOntClass);
		
		if (transaction == null) {
			return false;
		}
		
		// Add the properties to each individual
		transaction.addProperty(hasSender, sender);
		transaction.addProperty(hasReciever, receiver);
		sender.addProperty(participatesIn, transaction);
		receiver.addProperty(participatesIn, transaction);
		
		return true;
	}
	
	public ObjectNode isClass(String transactionId, String className) {
		boolean isClass = false;
		ObjectNode response = Json.newObject();
		
		// Get the transaction
		Individual transaction = ontReasoned.getIndividual(NS + transactionId);
		
		if (transaction == null) {
			response.put("status", "failure");
			response.put("reason", "not a transaction");
			
			return response;
		}
		
		ExtendedIterator<OntClass> classIter = transaction.listOntClasses(false);
		
		while (classIter.hasNext()) {
			OntClass ontClass = classIter.next();
			
			if (ontClass.hasURI(NS + className)) {
				isClass = true;
				break;
			}
		}
		
		response.put("status", "success");
		response.put("result", isClass);
		
		return response;
	}
	
	public static ObjectNode isTrusted(String merchantID) {
		boolean isMerchant = false;
		boolean isTrusted = false;
		ObjectNode response = Json.newObject();
		
		// Get the individual
		Individual indiv = ontReasoned.getIndividual(NS + merchantID);
		
		if (indiv == null) {
			response.put("status", "failure");
			response.put("reason", "not a merchant");
			return response;
		}
		
		// Check to see if the individual is a Merchant
		ExtendedIterator<OntClass> classIter = indiv.listOntClasses(false);
		
		while (classIter.hasNext()) {
			OntClass ontClass = classIter.next();
			
			if (ontClass.hasURI(NS + "Merchant")) {
				isMerchant = true;
			}
			
			if (ontClass.hasURI(NS + "Trusted")) {
				isTrusted = true;
			}
		}
		
		if (isMerchant == false) {
			response.put("status", "failure");
			response.put("reason", "not a merchant");
			return response;
		}
		
		response.put("status", "success");
		response.put("result", isTrusted);
		return response;
		
	}
	
	public String loadOntology() {
		// Read the ontology. No reasoner yet.
		OntModel baseOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		try
		{
		    InputStream in = FileManager.get().open(source_file);
		   
		    try
		    {
		        baseOntology.read(in, null);
		    }
		    catch (Exception e)
		    {
		        e.printStackTrace();
		    }
		}
		catch (JenaException je)
		{
		    System.err.println("ERROR" + je.getMessage());
		    je.printStackTrace();
		    System.exit(0);
		}

		baseOntology.setNsPrefix( "csc750", NS ); // Just for compact printing; doesn't really matter


		// This will create an ontology that has a reasoner attached.
		// This means that it will automatically infer classes an individual belongs to, according to restrictions, etc.
		ontReasoned = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC, baseOntology);
		
		return "success";
	}
	
	public static boolean isTrustedBoolean(String id) {
		ObjectNode node = isTrusted(id);
		
		if (node.has("result")) {
			return node.get("result").asBoolean();
		}
		
		return false;
	}

}

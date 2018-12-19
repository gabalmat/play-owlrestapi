package utilities;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class AcceptedLogger {
	
	private static FileHandler acceptedLog = null;
	
	public static void init() throws SecurityException, IOException {
		
		try {
			acceptedLog = new FileHandler("accepted.log", true);
		} catch (SecurityException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		
		Logger l = Logger.getLogger(AcceptedLogger.class.getName());
		acceptedLog.setFormatter(new SimpleFormatter());
		l.addHandler(acceptedLog);
		l.setLevel(Level.INFO);
	}
}

package utilities;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class RejectedLogger {
	
	private static FileHandler rejectedLog = null;
	
	public static void init() throws SecurityException, IOException {
		
		try {
			rejectedLog = new FileHandler("rejected.log", true);
		} catch (SecurityException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		
		Logger l = Logger.getLogger(RejectedLogger.class.getName());
		rejectedLog.setFormatter(new SimpleFormatter());
		l.addHandler(rejectedLog);
		l.setLevel(Level.INFO);
	}
}
